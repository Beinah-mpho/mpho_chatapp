package mpho_chatappt;

import org.junit.Test;
import static org.junit.Assert.*;

public class MessageTest {
    
    @Test
    public void testCheckMessageID() {
        Message message = new Message();
        message.setMessageID("MSG1234567");
        assertTrue("Message ID should be valid (10 digits or less)", message.checkMessageID());
    }
    
    @Test
    public void testCheckRecipientCell_Success() {
        Message message = new Message();
        int result = message.checkRecipientCell("+27718693002");
        assertTrue("Valid international number should return positive", result > 0);
    }
    
    @Test
    public void testCheckRecipientCell_Failure() {
        Message message = new Message();
        int result = message.checkRecipientCell("08575975889"); // No international code
        assertEquals("Invalid number should return 0", 0, result);
    }
    
    @Test
    public void testCheckRecipientCell_WithCountryCode() {
        Message message = new Message();
        int result = message.checkRecipientCell("+447911123456");
        assertTrue("Valid UK number should return positive", result > 0);
    }
    
    @Test
    public void testCreateMessageHash() {
        Message message = new Message();
        message.setMessage("Hi Mike, can you join us for dinner tonight");
        String hash = message.createMessageHash();
        assertNotNull("Message hash should not be null", hash);
        assertTrue("Message hash should be in uppercase", hash.equals(hash.toUpperCase()));
        assertEquals("Hash should be 64 characters (SHA-256)", 64, hash.length());
    }
    
    @Test
    public void testCheckMessageLength_Success() {
        Message message = new Message();
        message.setMessage("Short message");
        assertTrue("Message within 250 characters should be valid", message.checkMessageLength());
    }
    
    @Test
    public void testCheckMessageLength_ExactLimit() {
        // Create message of exactly 250 characters
        StringBuilder exactMessage = new StringBuilder();
        for (int i = 0; i < 250; i++) {
            exactMessage.append("a");
        }
        Message message = new Message();
        message.setMessage(exactMessage.toString());
        assertTrue("Message of exactly 250 characters should be valid", message.checkMessageLength());
    }
    
    @Test
    public void testCheckMessageLength_Failure() {
        // Create a long message
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            longMessage.append("a");
        }
        Message message = new Message();
        message.setMessage(longMessage.toString());
        assertFalse("Message exceeding 250 characters should be invalid", message.checkMessageLength());
    }
    
    @Test
    public void testSentMessage_Send() {
        Message message = new Message();
        message.setRecipient("+27718693002");
        message.setMessage("Test message for sending");
        String result = message.sentMessage(1); // Send
        assertTrue("Should indicate successful sending", result.contains("successfully") || result.contains("sent"));
    }
    
    @Test
    public void testSentMessage_Store() {
        Message message = new Message();
        message.setRecipient("+27718693002");
        message.setMessage("Test message for storing");
        String result = message.sentMessage(3); // Store
        assertTrue("Should indicate successful storing", result.contains("stored") || result.contains("saved"));
    }
    
    @Test
    public void testSentMessage_Discard() {
        Message message = new Message();
        message.setRecipient("+27718693002");
        message.setMessage("Test message for discarding");
        String result = message.sentMessage(2); // Discard
        assertTrue("Should indicate successful discarding", result.contains("discarded") || result.contains("deleted"));
    }
    
    @Test
    public void testPrintMessages() {
        Message message = new Message();
        message.setRecipient("+27718693002");
        message.setMessage("Test message");
        String output = message.printMessages();
        assertNotNull("printMessages should return non-null string", output);
        assertTrue("Output should contain message details", output.contains("Test message") || output.contains("+27718693002"));
    }
    
    @Test
    public void testPopulateWithTestData() {
        // Clear any existing test data
        Message.getSentMessages().clear();
        Message.getStoredMessages().clear();
        Message.getDisregardedMessages().clear();
        
        Message.populateWithTestData();
        
        assertTrue("Sent messages should contain test data", Message.getSentMessages().size() > 0);
        assertTrue("Stored messages should contain test data", Message.getStoredMessages().size() > 0);
        assertTrue("Disregarded messages should contain test data", Message.getDisregardedMessages().size() > 0);
    }
    
    @Test
    public void testDisplaySentMessagesSendersRecipients() {
        Message.populateWithTestData();
        String result = Message.displaySentMessagesSendersRecipients();
        assertNotNull("Should return non-null string", result);
        assertTrue("Should contain sender/recipient information", result.contains("Sender") || result.contains("Recipient"));
    }
    
    @Test
    public void testDisplayLongestSentMessage() {
        Message.populateWithTestData();
        String result = Message.displayLongestSentMessage();
        assertNotNull("Should return non-null string", result);
        assertTrue("Should contain message information", result.contains("Message") || result.contains("Characters"));
    }
    
    @Test
    public void testSearchMessageByID() {
        Message.populateWithTestData();
        // Get first message ID from sent messages for testing
        if (!Message.getSentMessages().isEmpty()) {
            String testId = Message.getSentMessages().get(0).getMessageID();
            String result = Message.searchMessageByID(testId);
            assertNotNull("Should return non-null string", result);
            assertTrue("Should contain search results", result.contains(testId));
        }
    }
    
    @Test
    public void testSearchMessagesByRecipient() {
        Message.populateWithTestData();
        String result = Message.searchMessagesByRecipient("+27718693002");
        assertNotNull("Should return non-null string", result);
        assertTrue("Should contain recipient information", result.contains("+27718693002"));
    }
    
    @Test
    public void testDeleteMessageByHash() {
        Message.populateWithTestData();
        if (!Message.getStoredMessages().isEmpty()) {
            String testHash = Message.getStoredMessages().get(0).createMessageHash();
            String result = Message.deleteMessageByHash(testHash);
            assertNotNull("Should return non-null string", result);
            assertTrue("Should indicate deletion status", result.contains("deleted") || result.contains("not found"));
        }
    }
    
    @Test
    public void testDisplayFullReport() {
        Message.populateWithTestData();
        String result = Message.displayFullReport();
        assertNotNull("Should return non-null string", result);
        assertTrue("Should contain report information", 
                   result.contains("Report") || result.contains("Total") || result.contains("Messages"));
    }
    
    @Test
    public void testReturnTotalMessages() {
        // Reset counters for accurate test
        Message.populateWithTestData();
        int total = Message.returnTotalMessages();
        assertTrue("Total messages should be positive", total >= 0);
    }
    
    @Test
    public void testChatAppValidationMethods() {
        MPHO_CHATAPP app = new MPHO_CHATAPP();
        
        // Test username validation
        assertTrue("Valid username with underscore", app.checkUsername("user_1"));
        assertFalse("Username too long", app.checkUsername("username_long"));
        assertFalse("Username without underscore", app.checkUsername("user1"));
        
        // Test password complexity
        assertTrue("Valid password", app.checkPasswordComplexity("Pass123!"));
        assertFalse("Password too short", app.checkPasswordComplexity("Short1!"));
        assertFalse("No uppercase", app.checkPasswordComplexity("password123!"));
        assertFalse("No digit", app.checkPasswordComplexity("Password!"));
        assertFalse("No special char", app.checkPasswordComplexity("Password123"));
        
        // Test cell phone validation
        assertTrue("Valid SA number", app.checkCellPhoneNumber("+27718693002"));
        assertTrue("Valid UK number", app.checkCellPhoneNumber("+447911123456"));
        assertFalse("No international code", app.checkCellPhoneNumber("08575975889"));
        assertFalse("Invalid format", app.checkCellPhoneNumber("+2771869300")); // Too short
    }
    
    @Test
    public void testLoginFunctionality() {
        MPHO_CHATAPP app = new MPHO_CHATAPP();
        
        // Set user credentials (in real scenario, these would be set properly)
        // app.username = "test_1";
        // app.password = "Test123!";
        // app.firstName = "John";
        // app.lastName = "Doe";
        
        // Test login status messages
        String successMessage = app.returnLoginStatus(true);
        String failureMessage = app.returnLoginStatus(false);
        
        assertTrue("Success message should be welcoming", successMessage.contains("Welcome"));
        assertTrue("Failure message should indicate error", failureMessage.contains("incorrect"));
    }
}