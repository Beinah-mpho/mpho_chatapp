package mpho_chatapp;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Message {
    private String messageID;
    private int messageCount;
    private String recipient;
    private String message;
    private String messageHash;
    private String status; // "sent", "stored", "discarded"
    private static int totalMessagesSent = 0;
    private static int messageCounter = 0;
    
    // Arrays for storing messages as per requirements
    private static List<Message> sentMessages = new ArrayList<>();
    private static List<Message> disregardedMessages = new ArrayList<>();
    private static List<Message> storedMessages = new ArrayList<>();
    private static List<String> messageHashes = new ArrayList<>();
    private static List<String> messageIDs = new ArrayList<>();

    public Message() {
        this.messageID = generateMessageID();
        this.messageCount = ++messageCounter;
        this.status = "pending";
        
        // Add to messageIDs array
        messageIDs.add(this.messageID);
    }

    // Generate random 10-digit message ID
    private String generateMessageID() {
        Random rand = new Random();
        long id = 1000000000L + (long)(rand.nextDouble() * 9000000000L);
        return String.valueOf(id);
    }

    public boolean checkMessageID() {
        return this.messageID.length() == 10;
    }

    public int checkRecipientCell(String recipient) {
        // Check if number starts with international code and has proper length
        if (recipient.startsWith("+") && recipient.length() <= 13 && recipient.length() >= 11) {
            String numberPart = recipient.substring(1);
            if (numberPart.matches("\\d+")) {
                return 1; // Success
            }
        }
        return 0; // Failure
    }

    public String createMessageHash() {
        String firstTwo = messageID.substring(0, 2);
        
        // Extract first and last words from message
        String[] words = message.split(" ");
        String firstWord = words.length > 0 ? words[0].toUpperCase() : "";
        String lastWord = words.length > 1 ? words[words.length - 1].toUpperCase() : firstWord;
        
        String hash = firstTwo + ":" + messageCount + ":" + firstWord + lastWord;
        
        // Add to messageHashes array
        messageHashes.add(hash);
        
        return hash;
    }

    public String sentMessage(int choice) {
        switch (choice) {
            case 1: // Send Message
                totalMessagesSent++;
                this.status = "sent";
                // Add to sentMessages array
                sentMessages.add(this);
                // Store in JSON
                JSONHandler.storeMessage(this);
                return "Message successfully sent.";
            case 2: // Disregard Message
                this.status = "discarded";
                // Add to disregardedMessages array
                disregardedMessages.add(this);
                return "Message disregarded.";
            case 3: // Store Message
                this.status = "stored";
                // Add to storedMessages array
                storedMessages.add(this);
                // Store in JSON
                JSONHandler.storeMessage(this);
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    public String printMessages() {
        return "MessageID: " + messageID + 
               "\nMessage Hash: " + messageHash + 
               "\nRecipient: " + recipient + 
               "\nMessage: " + message +
               "\nStatus: " + status;
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }
    
    // === ARRAY OPERATIONS AS PER REQUIREMENTS ===
    
    // 2a. Display sender and recipient of all sent messages
    public static String displaySentMessagesSendersRecipients() {
        if (sentMessages.isEmpty()) {
            return "No sent messages found.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Sent Messages - Senders & Recipients:\n");
        sb.append("=====================================\n");
        
        for (Message msg : sentMessages) {
            sb.append("To: ").append(msg.getRecipient())
              .append(" | Message: ").append(msg.getMessage())
              .append("\n");
        }
        return sb.toString();
    }
    
    // 2b. Display the longest sent message
    public static String displayLongestSentMessage() {
        if (sentMessages.isEmpty()) {
            return "No sent messages found.";
        }
        
        Message longest = sentMessages.get(0);
        for (Message msg : sentMessages) {
            if (msg.getMessage().length() > longest.getMessage().length()) {
                longest = msg;
            }
        }
        
        return "Longest Sent Message:\n" +
               "Message: " + longest.getMessage() + "\n" +
               "Length: " + longest.getMessage().length() + " characters\n" +
               "Recipient: " + longest.getRecipient();
    }
    
    // 2c. Search for message ID and display recipient and message
    public static String searchMessageByID(String searchID) {
        for (Message msg : sentMessages) {
            if (msg.getMessageID().equals(searchID)) {
                return "Message Found:\n" +
                       "Message ID: " + msg.getMessageID() + "\n" +
                       "Recipient: " + msg.getRecipient() + "\n" +
                       "Message: " + msg.getMessage();
            }
        }
        
        // Also check stored messages
        for (Message msg : storedMessages) {
            if (msg.getMessageID().equals(searchID)) {
                return "Message Found (Stored):\n" +
                       "Message ID: " + msg.getMessageID() + "\n" +
                       "Recipient: " + msg.getRecipient() + "\n" +
                       "Message: " + msg.getMessage();
            }
        }
        
        return "No message found with ID: " + searchID;
    }
    
    // 2d. Search for all messages sent to a particular recipient
    public static String searchMessagesByRecipient(String recipient) {
        List<Message> foundMessages = new ArrayList<>();
        
        // Check sent messages
        for (Message msg : sentMessages) {
            if (msg.getRecipient().equals(recipient)) {
                foundMessages.add(msg);
            }
        }
        
        // Check stored messages
        for (Message msg : storedMessages) {
            if (msg.getRecipient().equals(recipient)) {
                foundMessages.add(msg);
            }
        }
        
        if (foundMessages.isEmpty()) {
            return "No messages found for recipient: " + recipient;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Messages for ").append(recipient).append(":\n");
        sb.append("==========================\n");
        
        for (Message msg : foundMessages) {
            sb.append("Message: ").append(msg.getMessage())
              .append(" | Status: ").append(msg.getStatus())
              .append(" | ID: ").append(msg.getMessageID())
              .append("\n");
        }
        
        return sb.toString();
    }
    
    // 2e. Delete a message using message hash
    public static String deleteMessageByHash(String hash) {
        // Search in sent messages
        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).getMessageHash().equals(hash)) {
                Message removed = sentMessages.remove(i);
                messageHashes.remove(hash);
                return "Message successfully deleted: " + removed.getMessage();
            }
        }
        
        // Search in stored messages
        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).getMessageHash().equals(hash)) {
                Message removed = storedMessages.remove(i);
                messageHashes.remove(hash);
                return "Message successfully deleted: " + removed.getMessage();
            }
        }
        
        return "No message found with hash: " + hash;
    }
    
    // 2f. Display report with full details of all sent messages
    public static String displayFullReport() {
        if (sentMessages.isEmpty()) {
            return "No sent messages to display in report.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("FULL MESSAGE REPORT\n");
        sb.append("===================\n\n");
        
        for (Message msg : sentMessages) {
            sb.append("Message ID: ").append(msg.getMessageID()).append("\n");
            sb.append("Message Hash: ").append(msg.getMessageHash()).append("\n");
            sb.append("Recipient: ").append(msg.getRecipient()).append("\n");
            sb.append("Message: ").append(msg.getMessage()).append("\n");
            sb.append("Status: ").append(msg.getStatus()).append("\n");
            sb.append("----------------------------------------\n");
        }
        
        sb.append("\nTotal Sent Messages: ").append(sentMessages.size());
        return sb.toString();
    }
    
    // Method to populate arrays with test data
    public static void populateWithTestData() {
        // Clear existing data
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        
        // Test Data Message 1
        Message msg1 = new Message();
        msg1.setRecipient("+2783457896");
        msg1.setMessage("Did you get the cake?");
        msg1.setStatus("sent");
        sentMessages.add(msg1);
        messageHashes.add(msg1.getMessageHash());
        
        // Test Data Message 2
        Message msg2 = new Message();
        msg2.setRecipient("+27838884567");
        msg2.setMessage("Where are you? You are late! I have asked you to be on time.");
        msg2.setStatus("stored");
        storedMessages.add(msg2);
        messageHashes.add(msg2.getMessageHash());
        
        // Test Data Message 3
        Message msg3 = new Message();
        msg3.setRecipient("+27834484567");
        msg3.setMessage("Yohoooo, I am at your gate.");
        msg3.setStatus("disregarded");
        disregardedMessages.add(msg3);
        messageHashes.add(msg3.getMessageHash());
        
        // Test Data Message 4
        Message msg4 = new Message();
        msg4.setRecipient("0838884567");
        msg4.setMessage("It is dinner time!");
        msg4.setStatus("sent");
        sentMessages.add(msg4);
        messageHashes.add(msg4.getMessageHash());
        
        // Test Data Message 5
        Message msg5 = new Message();
        msg5.setRecipient("+2783884567");
        msg5.setMessage("Ok, I am leaving without you.");
        msg5.setStatus("stored");
        storedMessages.add(msg5);
        messageHashes.add(msg5.getMessageHash());
    }
    
    // Getters for arrays (for testing)
    public static List<Message> getSentMessages() { return sentMessages; }
    public static List<Message> getDisregardedMessages() { return disregardedMessages; }
    public static List<Message> getStoredMessages() { return storedMessages; }
    public static List<String> getMessageHashes() { return messageHashes; }
    public static List<String> getMessageIDs() { return messageIDs; }

    // Getters and Setters
    public String getMessageID() { return messageID; }
    public int getMessageCount() { return messageCount; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getMessage() { return message; }
    public void setMessage(String message) { 
        this.message = message;
        this.messageHash = createMessageHash();
    }
    public String getMessageHash() { return messageHash; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}