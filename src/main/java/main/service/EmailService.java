package main.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.zxing.WriterException;
import main.model.ResetPassword;
import main.model.Ticket;
import main.model.User;
import main.model.Voucher;
import main.repository.ResetPasswordRepository;
import org.hibernate.annotations.AttributeAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.UUID;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    @Autowired
    private IQRCodeService qrCodeService;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final String BODY_HTML = "</h1><body></html>";
    public void sendTicketEmail(String receiverEmail) throws MessagingException, GeneralSecurityException, IOException {
        try {
            Drive driveService = CloudService.getDriveService();

            // Upload a file.
            File fileMetadata = new File();
            fileMetadata.setName("Ticket.pdf");

            java.io.File filePath = new java.io.File("ticket.pdf");
            FileContent mediaContent = new FileContent("application/pdf", filePath);

            File file = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            String fileId = file.getId();
            Permission permission = new Permission()
                    .setType("anyone")
                    .setRole("reader");

            driveService.permissions().create(fileId, permission)
                    .setFields("id")
                    .execute();

            String downloadUrl = "https://drive.google.com/uc?export=download&id=" + fileId;

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(receiverEmail);
            helper.setText("<html><body><h1>This is your ticket! Enjoy your flight!</h1><body></html>", true);


            FileSystemResource fileEmail = new FileSystemResource(new java.io.File("C:\\Internship\\InternshipProject\\BackEnd\\FlightAgencyReset\\FlightAgencyReset\\FlightAgency\\FlightAgency\\ticket.pdf"));
            FileSystemResource qrEmail = new FileSystemResource(new java.io.File("qrCode.png"));
            helper.addAttachment("Ticket.pdf", fileEmail);
            helper.addAttachment("QRCode.png", qrEmail);
            helper.setSubject("Ticket");

            javaMailSender.send(message);

            System.out.println("File ID: " + file.getId());
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendReservedTicketEmail(Ticket ticket, String receiverEmail) throws MessagingException {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(receiverEmail);

            helper.setText("");

            // Set Subject: header field
            message.setSubject("Testing Subject");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String msg = "Your ticket reservation was made. Please access the following link to buy the ticket "
                    + "http://localhost:4200/reserveticket?id=" + ticket.getId();


            messageBodyPart.setContent(msg, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            javaMailSender.send(message);

            logger.info("Sent message successfully....");

        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRouteChangeToPersonEmail(String receiverEmail) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(receiverEmail);
        helper.setText("<html><body><h1>There has been some changes made to a route for one of your flights!</h1><body></html>", true);
        helper.setSubject("Warning");
        javaMailSender.send(message);
    }

    public void sendFlightCancelToPersonEmail(String receiverEmail) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(receiverEmail);
        helper.setText("<html><body><h1>Due to unfortunate circumstances your flight has been cancelled.</h1><body></html>", true);
        helper.setSubject("Cancelled flight");
        javaMailSender.send(message);
    }

    public void sendFlightChangeToPersonEmail(String receiverEmail) throws MessagingException {
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//        helper.setTo(receiverEmail);
//        helper.setText("<html><body><h1>There has been some changes made to a flight for one of your tickets!</h1><body></html>", true);
//        helper.setSubject("Warning");
//        javaMailSender.send(message);
    }

    public void sendPasswordOAuthEmail(String receiverEmail, String username, String password) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(receiverEmail);
        helper.setText("<html><body><h1>Here are your user details to log in our application without OAuth! Username:" + username + "Password: " + password + BODY_HTML, true);
        helper.setSubject("Your username and password");
        javaMailSender.send(message);
    }

    public void sendResetPasswordEmail(User user, String receiverEmail) {
        try {
            logger.info("Reseting password email.. {}", user);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(receiverEmail);

            helper.setText("");

            // Set Subject: header field
            message.setSubject("Testing Subject");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            UUID token = UUID.randomUUID(); //a random UUID token
            saveInResetPasswordTable(user.getUsername(), token.toString());
            String msg = "Dear " + user.getUsername() + ",<br><br>"
                    + "We have received a request to reset your password. Please click the following link to reset your password:<br><br>"
                    + "<a href=\"http://localhost:8080/resetpassword/token?token=" + token + "\">Reset Password</a>";

            messageBodyPart.setContent(msg, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            javaMailSender.send(message);

            logger.info("Sent message successfully....");

        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendVoucherEmail(Voucher voucher, String receiverEmail) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(receiverEmail);
        switch (voucher.getType()) {
            case BIRTHDAY_VOUCHER:
                helper.setText("<html><body><h1>Happy birthday! Here is a voucher for your next flight: " + voucher.getCode() + "</h1><body></html>", true);
                break;
            case FIVE_YEAR_VOUCHER:
                helper.setText("<html><body><h1>Congratulations for flying for 5 years with us! Here is a voucher for your next flight: " + voucher.getCode() + BODY_HTML, true);
                break;
        }
        helper.setSubject("Voucher");
        javaMailSender.send(message);
    }

    public void saveInResetPasswordTable(String username, String token) {
        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setToken(token);
        resetPassword.setUsername(username);
        resetPasswordRepository.save(resetPassword);

    }
}
