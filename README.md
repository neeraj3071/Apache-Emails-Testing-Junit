# JUnit Test Class for Apache Commons Email

This project contains a JUnit test class (`EmailTest`) designed to test the functionality of the `Email` class from the `org.apache.commons.mail` package. The test class includes test methods to verify key functionalities such as setting the sender, recipient, subject, message, and content type of an email.

The project is structured as a Maven project and includes all necessary dependencies (JUnit, Apache Commons Email, and JavaMail API) to run the tests.

---

## Key Features

- **Test Coverage**: The test class covers the following functionalities of the `Email` class:
  - Setting the sender (`setFrom`).
  - Adding recipients (`addTo`).
  - Setting the subject (`setSubject`).
  - Setting the message content (`setMsg`).
  - Setting the sent date (`setSentDate`).
  - Building the MIME message (`buildMimeMessage`).
  - Sending the email (`send`).
  - Updating the content type (`updateContentType`).

- **Dependencies**:
  - JUnit 4.13.2 for testing.
  - Apache Commons Email 1.5 for email functionality.
  - JavaMail API 1.6.2 for MIME message handling.

- **Comments**: The test class is thoroughly commented to explain the purpose of each test method and assertion.

