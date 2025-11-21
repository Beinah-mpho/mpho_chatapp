
package mpho_chatapp;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class JSONHandler {
    private static final String FILE_NAME = "messages.json";

    public static void storeMessage(Message message) {
        try {
            // Read existing messages
            List<String> existingMessages = readAllMessages();
            
            // Create JSON object for new message
            String messageJson = createMessageJSON(message);
            
            // Add new message to list
            existingMessages.add(messageJson);
            
            // Write back to file
            writeMessagesToFile(existingMessages);
            
            System.out.println("Message stored in JSON file: " + FILE_NAME);
            
        } catch (IOException e) {
            System.err.println("Error storing message: " + e.getMessage());
        }
    }

    private static String createMessageJSON(Message message) {
        StringBuilder json = new StringBuilder();
        json.append("  {\n");
        json.append("    \"messageID\": \"").append(escapeJSON(message.getMessageID())).append("\",\n");
        json.append("    \"messageCount\": ").append(message.getMessageCount()).append(",\n");
        json.append("    \"recipient\": \"").append(escapeJSON(message.getRecipient())).append("\",\n");
        json.append("    \"message\": \"").append(escapeJSON(message.getMessage())).append("\",\n");
        json.append("    \"messageHash\": \"").append(escapeJSON(message.getMessageHash())).append("\",\n");
        json.append("    \"status\": \"").append(escapeJSON(message.getStatus())).append("\",\n");
        json.append("    \"timestamp\": \"").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\"\n");
        json.append("  }");
        return json.toString();
    }

    private static String escapeJSON(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private static List<String> readAllMessages() throws IOException {
        List<String> messages = new ArrayList<>();
        
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return messages;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder currentMessage = new StringBuilder();
            boolean inMessage = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.equals("[")) continue;
                if (line.equals("]")) break;
                
                if (line.equals("{")) {
                    inMessage = true;
                    currentMessage = new StringBuilder();
                    currentMessage.append("{\n");
                } else if (line.equals("},") || line.equals("}")) {
                    if (inMessage) {
                        currentMessage.append("}");
                        messages.add(currentMessage.toString());
                        inMessage = false;
                    }
                } else if (inMessage) {
                    currentMessage.append(line).append("\n");
                }
            }
        }
        
        return messages;
    }

    private static void writeMessagesToFile(List<String> messages) throws IOException {
        try (FileWriter file = new FileWriter(FILE_NAME)) {
            file.write("[\n");
            
            for (int i = 0; i < messages.size(); i++) {
                file.write(messages.get(i));
                if (i < messages.size() - 1) {
                    file.write(",");
                }
                file.write("\n");
            }
            
            file.write("]");
            file.flush();
        }
    }

    public static String getAllMessages() {
        try {
            List<String> messages = readAllMessages();
            if (messages.isEmpty()) {
                return "No messages stored.";
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Stored Messages:\n");
            sb.append("================\n");
            
            for (String messageJson : messages) {
                // Simple parsing to extract key information
                String[] lines = messageJson.split("\n");
                String messageID = extractValue(lines, "messageID");
                String recipient = extractValue(lines, "recipient");
                String status = extractValue(lines, "status");
                String timestamp = extractValue(lines, "timestamp");
                
                sb.append("ID: ").append(messageID)
                  .append(" | To: ").append(recipient)
                  .append(" | Status: ").append(status)
                  .append(" | Time: ").append(timestamp)
                  .append("\n");
            }
            return sb.toString();
            
        } catch (IOException e) {
            return "Error reading messages: " + e.getMessage();
        }
    }

    private static String extractValue(String[] lines, String key) {
        for (String line : lines) {
            if (line.contains("\"" + key + "\":")) {
                int start = line.indexOf(":") + 1;
                String value = line.substring(start).trim();
                if (value.startsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                // Remove trailing comma if present
                if (value.endsWith(",")) {
                    value = value.substring(0, value.length() - 1);
                }
                return unescapeJSON(value);
            }
        }
        return "N/A";
    }

    private static String unescapeJSON(String text) {
        return text.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t")
                  .replace("\\b", "\b")
                  .replace("\\f", "\f");
    }

   
}
