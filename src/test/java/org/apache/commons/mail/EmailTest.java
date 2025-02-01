package org.apache.commons.mail;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.io.IOException;
import java.util.Date;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.Properties;

import org.subethamail.wiser.Wiser;

public class EmailTest {

    private Email email;
    private Wiser wiser; 

    @Before
    public void setUp() throws Exception {
        wiser = new Wiser();
        wiser.setPort(2500);
        wiser.start();

        // Initialize the Email object
        email = new SimpleEmail();
        email.setHostName("localhost"); // Use localhost as the SMTP host
        email.setSmtpPort(2500); // Use the same port as Wiser
    }

    @After
    public void tearDown() throws Exception {
        // Stop the Wiser server after each test
        wiser.stop();
        email = null; // Clean up the Email object
    }

    // Test for addBcc(String... email)
    @Test
    public void testAddBcc() throws EmailException {
        email.addBcc("test1@example.com", "test2@example.com");
        assertEquals(2, email.getBccAddresses().size()); // Ensure 2 addresses were added
    }

    // Test for addBcc with a single address
    @Test
    public void testAddBccWithOneEmail() throws EmailException {
        email.addBcc("test1@example.com");
        assertEquals(1, email.getBccAddresses().size()); // Ensure 1 address was added
    }

    // Test for addBcc with invalid email addresses
    @Test(expected = EmailException.class)
    public void testAddBccWithInvalidEmail() throws EmailException {
        email.addBcc("invalid-email"); // Should throw EmailException due to invalid format
    }

    // Test for addBcc with null input
    @Test(expected = EmailException.class)
    public void testAddBccWithNullInput() throws EmailException {
        email.addBcc((String[]) null); // Should throw EmailException
    }

    // Test for addBcc with empty input
    @Test(expected = EmailException.class)
    public void testAddBccWithEmptyInput() throws EmailException {
        email.addBcc(); // Should throw EmailException
    }

    // Test for addCc
    @Test
    public void testAddCc() throws Exception {
        email.addCc("test@example.com");
        List<InternetAddress> ccAddresses = email.getCcAddresses();
        assertEquals(1, ccAddresses.size());
        assertTrue(ccAddresses.stream().anyMatch(addr -> addr.getAddress().equals("test@example.com")));
    }

    // Test add Header
    @Test
    public void testAddHeader() throws Exception {
        // Set the "From" address
        email.setFrom("sender@example.com");

        // Add a recipient
        email.addTo("recipient@example.com");

        // Add a custom header
        email.addHeader("X-Custom-Header", "Value");

        // Build the MimeMessage
        email.buildMimeMessage();

        // Retrieve the MimeMessage
        MimeMessage mimeMessage = email.getMimeMessage();

        // Verify the header
        String[] headerValue = mimeMessage.getHeader("X-Custom-Header");
        assertNotNull(headerValue);
        assertEquals("Value", headerValue[0]);
    }

    // Test for addHeader with invalid inputs
    @Test
    public void testAddHeaderWithInvalidInputs() {
        // Test empty header name
        assertThrows(IllegalArgumentException.class, () -> {
            email.addHeader("", "ValidValue"); // Should throw IllegalArgumentException for empty header name
        });
    }

    @Test
    public void testAddHeaderWithEmptyValue() {
        // Test empty header value
        assertThrows(IllegalArgumentException.class, () -> {
            email.addHeader("ValidName", ""); // Should throw IllegalArgumentException for empty header value
        });
    }

    // Test for buildMimeMessage
    @Test
    public void testBuildMimeMessage_AlreadyBuilt() throws EmailException {
        // Set the "From" address
        email.setFrom("sender@example.com");

        // Add a recipient
        email.addTo("recipient@example.com");

        // Build the MimeMessage once
        email.buildMimeMessage();

        // Attempt to build it again
        assertThrows(IllegalStateException.class, () -> email.buildMimeMessage());
    }

    @Test
    public void testBuildMimeMessage_SubjectWithCharset() throws EmailException, MessagingException {
        // Set the "From" address
        email.setFrom("sender@example.com");

        // Add a recipient
        email.addTo("recipient@example.com");

        email.setSubject("Test Subject");
        email.buildMimeMessage();

        MimeMessage message = email.getMimeMessage();
        assertEquals("Test Subject", message.getSubject());
    }

    @Test
    public void testBuildMimeMessage_SubjectWithoutCharset() throws EmailException, MessagingException {
        // Set the "From" address
        email.setFrom("sender@example.com");

        // Add a recipient
        email.addTo("recipient@example.com");

        email.setSubject("Test Subject");
        email.buildMimeMessage();

        MimeMessage message = email.getMimeMessage();
        assertEquals("Test Subject", message.getSubject());
    }

    @Test
    public void testBuildMimeMessage_ContentTextPlain() throws EmailException, MessagingException, IOException {
        // Set the "From" address
        email.setFrom("sender@example.com");

        // Add a recipient
        email.addTo("recipient@example.com");

        email.setContent("Test Content", EmailConstants.TEXT_PLAIN);
        email.buildMimeMessage();

        MimeMessage message = email.getMimeMessage();
        assertEquals("Test Content", message.getContent());
    }

    @Test
    public void testBuildMimeMessage_ContentTextHtml() throws EmailException, MessagingException, IOException {
        // Set the "From" address
        email.setFrom("sender@example.com");

        // Add a recipient
        email.addTo("recipient@example.com");

        // Set the content and content type
        email.setContent("<html><body>Test Content</body></html>", EmailConstants.TEXT_HTML);

        // Build the MimeMessage
        email.buildMimeMessage();

        // Retrieve the MimeMessage
        MimeMessage message = email.getMimeMessage();

        // Verify the content
        assertEquals("<html><body>Test Content</body></html>", message.getContent());
    }

    @Test
    public void testBuildMimeMessage_FromAddress() throws EmailException, MessagingException {
        email.setFrom("sender@example.com");

        // Add a recipient
        email.addTo("recipient@example.com");

        email.buildMimeMessage();

        MimeMessage message = email.getMimeMessage();
        assertEquals("sender@example.com", ((InternetAddress) message.getFrom()[0]).getAddress());
    }

    @Test
    public void testBuildMimeMessage_NoFromAddress() throws EmailException {
        // Add a recipient
        email.addTo("recipient@example.com");

        assertThrows(EmailException.class, () -> email.buildMimeMessage());
    }

    @Test
    public void testBuildMimeMessage_NoRecipients() throws EmailException {
        // Set the "From" address
        email.setFrom("sender@example.com");

        assertThrows(EmailException.class, () -> email.buildMimeMessage());
    }

    @Test
    public void testBuildMimeMessage_Recipients() throws EmailException, MessagingException {
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.addCc("cc@example.com");
        email.addBcc("bcc@example.com");
        email.buildMimeMessage();

        MimeMessage message = email.getMimeMessage();
        assertEquals(1, message.getRecipients(Message.RecipientType.TO).length);
        assertEquals(1, message.getRecipients(Message.RecipientType.CC).length);
        assertEquals(1, message.getRecipients(Message.RecipientType.BCC).length);
    }

    @Test
    public void testBuildMimeMessage_Headers() throws EmailException, MessagingException {
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.addHeader("X-Custom-Header", "Value");
        email.buildMimeMessage();

        MimeMessage message = email.getMimeMessage();
        assertEquals("Value", message.getHeader("X-Custom-Header")[0]);
    }

    @Test
    public void testBuildMimeMessage_SentDate() throws EmailException, MessagingException {
        // Set the "From" address
        email.setFrom("sender@example.com");

        // Add a recipient
        email.addTo("recipient@example.com");

        // Set a custom sent date
        Date sentDate = new Date();
        long sentDateMillis = sentDate.getTime();
        long truncatedSentDateMillis = (sentDateMillis / 1000) * 1000; // Truncate milliseconds
        Date truncatedSentDate = new Date(truncatedSentDateMillis);
        email.setSentDate(truncatedSentDate);

        // Build the MimeMessage
        email.buildMimeMessage();

        // Retrieve the MimeMessage
        MimeMessage message = email.getMimeMessage();

        // Verify the sent date
        assertEquals(truncatedSentDate.getTime(), message.getSentDate().getTime());
    }

    // Test for getSentDate
    @Test
    public void testGetSentDate() throws Exception {
        // Default sent date should not be null
        assertNotNull(email.getSentDate());

        // Set a custom sent date
        Date customDate = new Date();
        email.setSentDate(customDate);
        assertEquals(customDate, email.getSentDate());

        // Ensure sent date remains unchanged if set again
        Date anotherDate = new Date(customDate.getTime() + 10000);
        email.setSentDate(anotherDate);
        assertEquals(anotherDate, email.getSentDate());
    }

    // Test for send
    @Test
    public void testSend() throws Exception {
        email.setFrom("from@example.com");
        email.addTo("to@example.com");
        email.setSubject("Test Subject");
        email.setMsg("Test Message");

        // Mocking the send process
        try {
            String messageId = email.send();
            assertNotNull(messageId);
            assertTrue(messageId.startsWith("<") && messageId.endsWith(">"));
        } catch (EmailException e) {
            fail("Unexpected EmailException: " + e.getMessage());
        }

        // Missing recipient should cause failure
        email = new SimpleEmail();
        email.setFrom("from@example.com");
        email.setSubject("No Recipient Test");
        email.setMsg("This should fail");

        try {
            email.send();
            fail("Expected EmailException due to missing recipient");
        } catch (EmailException e) {
            // Expected behavior
        }
    }

    // Test for updateContentType
    @Test
    public void testUpdateContentType() throws Exception {
        // Set the "From" address
        email.setFrom("sender@example.com");

        // Add a recipient (required for building the MimeMessage)
        email.addTo("recipient@example.com");

        // Case 1: Valid content type with charset
        email.setCharset("UTF-8"); // Set charset explicitly
        email.updateContentType("text/html");
        String contentType1 = getMimeMessageContentType(email);
        assertTrue("Content type should contain 'charset=UTF-8'", contentType1.contains("charset=UTF-8"));

        // Case 2: Content type without charset
        email.updateContentType("text/plain");
        String contentType2 = getMimeMessageContentType(email);
        assertEquals("Content type should be 'text/plain'", "text/plain", contentType2);

        // Case 3: Null content type input should reset content type
        email.updateContentType(null);
        String contentType3 = getMimeMessageContentType(email);
        assertNull("Content type should be null", contentType3);

        // Case 4: Empty content type input should reset content type
        email.updateContentType("");
        String contentType4 = getMimeMessageContentType(email);
        assertNull("Content type should be null", contentType4);

        // Case 5: Valid content type without charset but existing charset in email
        email.setCharset("ISO-8859-1");
        email.updateContentType("text/xml");
        String contentType5 = getMimeMessageContentType(email);
        assertEquals("Content type should be 'text/xml; charset=ISO-8859-1'", "text/xml; charset=ISO-8859-1", contentType5);

        // Case 6: Non-text content type should remain unchanged
        email.updateContentType("application/json");
        String contentType6 = getMimeMessageContentType(email);
        assertEquals("Content type should be 'application/json'", "application/json", contentType6);

        // Case 7: Content type with extra spaces in charset declaration
        email.updateContentType("text/html; charset=UTF-8  ");
        String contentType7 = getMimeMessageContentType(email);
        assertEquals("Content type should be 'text/html; charset=UTF-8'", "text/html; charset=UTF-8", contentType7);
    }
    
    // Helper method to extract content type from MimeMessage
    private String getMimeMessageContentType(Email email) throws Exception {
        // Ensure the "From" address and recipient are set
        if (email.getFromAddress() == null) {
            email.setFrom("sender@example.com");
        }
        if (email.getToAddresses().isEmpty()) {
            email.addTo("recipient@example.com");
        }

        email.buildMimeMessage();  // Ensure MimeMessage is built
        return email.getMimeMessage().getContentType();
    }

    // Test Get Host Name
    @Test
    public void testGetHostName_FromSession() throws Exception {
        // Create a session with a hostname property
        Properties properties = new Properties();
        properties.setProperty(EmailConstants.MAIL_HOST, "smtp.session.com");
        Session session = Session.getInstance(properties);

        // Set the session in the email object
        email.setMailSession(session);

        // Verify the hostname is retrieved from the session
        assertEquals("smtp.session.com", email.getHostName());
    }

    @Test
    public void testGetHostName_FromHostNameProperty() throws Exception {
        // Set the hostname directly
        email.setHostName("smtp.example.com");

        // Verify the hostname is retrieved from the hostName property
        assertEquals("smtp.example.com", email.getHostName());
    }

    @Test
    public void testGetHostName_NullSessionAndEmptyHostName() throws Exception {
        // Create a new Email object without setting the hostname
        Email testEmail = new SimpleEmail();

        // Verify that the hostname is null
        assertNull(testEmail.getHostName());
    }

    @Test
    public void testGetMailSession() throws Exception {
        email.setHostName("localhost");
        Session session = email.getMailSession();
        assertNotNull(session);
    }

    @Test
    public void testGetSocketConnectionTimeout() throws Exception {
        email.setSocketConnectionTimeout(5000);
        assertEquals(5000, email.getSocketConnectionTimeout());
    }

    @Test
    public void testSetFrom() throws Exception {
        email.setFrom("from@example.com");
        assertEquals("from@example.com", email.getFromAddress().toString());
    }

    // Test for addReplyTo
    @Test
    public void testAddReplyTo() throws Exception {
        email.addReplyTo("reply@example.com", "Reply To");
        List<InternetAddress> replyToAddresses = email.getReplyToAddresses();
        assertEquals(1, replyToAddresses.size());
        assertTrue(replyToAddresses.stream().anyMatch(addr -> addr.getAddress().equals("reply@example.com")));
    }
}