package mpho_chatapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;

public class MPHO_CHATAPP {
    private String username;
    private String password;
    private String cellNumber;
    private String firstName;
    private String lastName;
    private boolean isLoggedIn = false;

    // === GUI Entry Point ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MPHO_CHATAPP app = new MPHO_CHATAPP();
            app.showMainMenu();
        });
    }

    // === GUI: Main Menu ===
    private void showMainMenu() {
        JFrame frame = new JFrame("QuickChat Messaging Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to QuickChat.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton sendBtn = new JButton("Send Messages");
        JButton recentBtn = new JButton("Show Recently Sent Messages");
        JButton storedBtn = new JButton("View Stored Messages");
        JButton arrayOpsBtn = new JButton("Array Operations & Reports");
        JButton testDataBtn = new JButton("Load Test Data");
        JButton quitBtn = new JButton("Quit");

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        buttonPanel.add(sendBtn);
        buttonPanel.add(recentBtn);
        buttonPanel.add(storedBtn);
        buttonPanel.add(arrayOpsBtn);
        buttonPanel.add(testDataBtn);
        buttonPanel.add(quitBtn);

        frame.add(welcomeLabel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // === Event Listeners ===
        sendBtn.addActionListener(e -> sendMessagesGUI());
        recentBtn.addActionListener(e -> showRecentMessagesGUI());
        storedBtn.addActionListener(e -> viewStoredMessagesGUI());
        arrayOpsBtn.addActionListener(e -> showArrayOperationsGUI());
        testDataBtn.addActionListener(e -> {
            Message.populateWithTestData();
            JOptionPane.showMessageDialog(frame, "Test data loaded successfully!\n\n" +
                    "Sent Messages: " + Message.getSentMessages().size() + "\n" +
                    "Stored Messages: " + Message.getStoredMessages().size() + "\n" +
                    "Disregarded Messages: " + Message.getDisregardedMessages().size());
        });
        quitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Thank you for using QuickChat. Goodbye!");
            frame.dispose();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // === GUI: Send Messages ===
    private void sendMessagesGUI() {
        JFrame sendFrame = new JFrame("Send Message");
        sendFrame.setSize(400, 400);
        sendFrame.setLayout(new GridLayout(7, 1, 10, 10));

        JTextField recipientField = new JTextField();
        JTextArea messageArea = new JTextArea();
        JButton sendBtn = new JButton("Send Message");
        JButton storeBtn = new JButton("Store Message");
        JButton discardBtn = new JButton("Discard Message");

        sendFrame.add(new JLabel("Recipient Cell Number (with +code):"));
        sendFrame.add(recipientField);
        sendFrame.add(new JLabel("Message (max 250 characters):"));
        sendFrame.add(new JScrollPane(messageArea));
        sendFrame.add(sendBtn);
        sendFrame.add(storeBtn);
        sendFrame.add(discardBtn);

        sendFrame.setLocationRelativeTo(null);
        sendFrame.setVisible(true);

        // Message object
        Message message = new Message();

        // === Action Listeners ===
        sendBtn.addActionListener(e -> {
            String recipient = recipientField.getText().trim();
            String text = messageArea.getText().trim();

            if (message.checkRecipientCell(recipient) == 0) {
                JOptionPane.showMessageDialog(sendFrame, "Invalid phone number. Must include international code.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (text.length() > 250) {
                JOptionPane.showMessageDialog(sendFrame, "Message exceeds 250 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            message.setRecipient(recipient);
            message.setMessage(text);
            String result = message.sentMessage(1); // Send
            JOptionPane.showMessageDialog(sendFrame, result + "\n\n" + message.printMessages(), "Message Sent", JOptionPane.INFORMATION_MESSAGE);
        });

        storeBtn.addActionListener(e -> {
            String recipient = recipientField.getText().trim();
            String text = messageArea.getText().trim();

            if (message.checkRecipientCell(recipient) == 0 || text.isEmpty()) {
                JOptionPane.showMessageDialog(sendFrame, "Please fill in all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            message.setRecipient(recipient);
            message.setMessage(text);
            String result = message.sentMessage(3); // Store
            JOptionPane.showMessageDialog(sendFrame, result, "Stored", JOptionPane.INFORMATION_MESSAGE);
        });

        discardBtn.addActionListener(e -> {
            String recipient = recipientField.getText().trim();
            String text = messageArea.getText().trim();
            
            if (!recipient.isEmpty() && !text.isEmpty()) {
                message.setRecipient(recipient);
                message.setMessage(text);
                String result = message.sentMessage(2); // Discard
                JOptionPane.showMessageDialog(sendFrame, result, "Discarded", JOptionPane.INFORMATION_MESSAGE);
            } else {
                recipientField.setText("");
                messageArea.setText("");
                JOptionPane.showMessageDialog(sendFrame, "Message discarded.");
            }
        });
    }

    // === GUI: View Stored Messages ===
    private void viewStoredMessagesGUI() {
        JFrame viewFrame = new JFrame("Stored Messages");
        viewFrame.setSize(400, 300);

        JTextArea messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setText(JSONHandler.getAllMessages());

        viewFrame.add(new JScrollPane(messagesArea));
        viewFrame.setLocationRelativeTo(null);
        viewFrame.setVisible(true);
    }

    // === GUI: Show Recent Messages ===
    private void showRecentMessagesGUI() {
        JFrame recentFrame = new JFrame("Recently Sent Messages");
        recentFrame.setSize(500, 400);

        JTextArea messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setText(Message.displaySentMessagesSendersRecipients());

        recentFrame.add(new JScrollPane(messagesArea));
        recentFrame.setLocationRelativeTo(null);
        recentFrame.setVisible(true);
    }

    // === GUI: Array Operations & Reports ===
    private void showArrayOperationsGUI() {
        JFrame arrayFrame = new JFrame("Array Operations & Reports");
        arrayFrame.setSize(600, 500);
        arrayFrame.setLayout(new GridLayout(7, 1, 10, 10));

        JButton sentRecipientsBtn = new JButton("2a. Display Senders & Recipients of Sent Messages");
        JButton longestMsgBtn = new JButton("2b. Display Longest Sent Message");
        JButton searchByIdBtn = new JButton("2c. Search Message by ID");
        JButton searchByRecipientBtn = new JButton("2d. Search Messages by Recipient");
        JButton deleteByHashBtn = new JButton("2e. Delete Message by Hash");
        JButton fullReportBtn = new JButton("2f. Display Full Report");
        JButton closeBtn = new JButton("Close");

        arrayFrame.add(sentRecipientsBtn);
        arrayFrame.add(longestMsgBtn);
        arrayFrame.add(searchByIdBtn);
        arrayFrame.add(searchByRecipientBtn);
        arrayFrame.add(deleteByHashBtn);
        arrayFrame.add(fullReportBtn);
        arrayFrame.add(closeBtn);

        // === Event Listeners for Array Operations ===
        sentRecipientsBtn.addActionListener(e -> {
            JTextArea resultArea = new JTextArea(Message.displaySentMessagesSendersRecipients());
            resultArea.setEditable(false);
            JOptionPane.showMessageDialog(arrayFrame, new JScrollPane(resultArea), 
                    "Sent Messages - Senders & Recipients", JOptionPane.INFORMATION_MESSAGE);
        });

        longestMsgBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(arrayFrame, Message.displayLongestSentMessage(), 
                    "Longest Sent Message", JOptionPane.INFORMATION_MESSAGE);
        });

        searchByIdBtn.addActionListener(e -> {
            String searchID = JOptionPane.showInputDialog(arrayFrame, "Enter Message ID to search:");
            if (searchID != null && !searchID.trim().isEmpty()) {
                String result = Message.searchMessageByID(searchID.trim());
                JTextArea resultArea = new JTextArea(result);
                resultArea.setEditable(false);
                JOptionPane.showMessageDialog(arrayFrame, new JScrollPane(resultArea), 
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        searchByRecipientBtn.addActionListener(e -> {
            String recipient = JOptionPane.showInputDialog(arrayFrame, "Enter recipient number:");
            if (recipient != null && !recipient.trim().isEmpty()) {
                String result = Message.searchMessagesByRecipient(recipient.trim());
                JTextArea resultArea = new JTextArea(result);
                resultArea.setEditable(false);
                JOptionPane.showMessageDialog(arrayFrame, new JScrollPane(resultArea), 
                        "Messages for Recipient", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        deleteByHashBtn.addActionListener(e -> {
            String hash = JOptionPane.showInputDialog(arrayFrame, "Enter message hash to delete:");
            if (hash != null && !hash.trim().isEmpty()) {
                String result = Message.deleteMessageByHash(hash.trim());
                JOptionPane.showMessageDialog(arrayFrame, result, 
                        "Delete Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        fullReportBtn.addActionListener(e -> {
            JTextArea resultArea = new JTextArea(Message.displayFullReport());
            resultArea.setEditable(false);
            JOptionPane.showMessageDialog(arrayFrame, new JScrollPane(resultArea), 
                    "Full Message Report", JOptionPane.INFORMATION_MESSAGE);
        });

        closeBtn.addActionListener(e -> arrayFrame.dispose());

        arrayFrame.setLocationRelativeTo(null);
        arrayFrame.setVisible(true);
    }

    // === Validation Methods ===
    public boolean checkUsername(String username) {
        return username.length() <= 5 && username.contains("_");
    }

    public boolean checkPasswordComplexity(String password) {
        if (password.length() < 8) return false;
        if (!Pattern.compile("[A-Z]").matcher(password).find()) return false;
        if (!Pattern.compile("[0-9]").matcher(password).find()) return false;
        return Pattern.compile("[^A-Za-z0-9]").matcher(password).find();
    }

    public boolean checkCellPhoneNumber(String cellNumber) {
        String pattern = "^\\+\\d{1,3}\\d{7,10}$";
        return Pattern.matches(pattern, cellNumber);
    }

    // === Login ===
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return enteredUsername.equals(this.username) && enteredPassword.equals(this.password);
    }

    public String returnLoginStatus(boolean isSuccessful) {
        if (isSuccessful) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}