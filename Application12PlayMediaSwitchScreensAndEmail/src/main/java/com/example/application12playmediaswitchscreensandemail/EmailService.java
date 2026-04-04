package com.example.application12playmediaswitchscreensandemail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    // PTR: use maven dependency com.sun.mail > javax.mail > 1.6.2
    public static List<String> sendMail(String fromMailAddress, String appPassword, String toMailAddress, String subject, String body) {
        List<String> errors = new ArrayList<>();
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromMailAddress, appPassword);
            }
        };

        Session session = Session.getInstance(props, auth);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromMailAddress));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toMailAddress));
            message.setSentDate(new Date());
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
            errors.add("Authentication failed. Please enter valid app password.");
        } catch (Exception e) {
            e.printStackTrace();
            errors.add(e.getMessage());
        }
        return errors;
    }
}