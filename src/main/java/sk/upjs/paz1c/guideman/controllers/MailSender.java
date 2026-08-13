package sk.upjs.paz1c.guideman.controllers;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender {

	public static void send() {
		final String login = "guidemanupjs2022@gmail.com";
		final String password = "Guideman123.";

		System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
		Properties prop = new Properties();
		prop.put("mail.smtp.user", login);
		prop.put("mail.smtp.password", password);
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "465");
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		// * smtp.gmail.com, port 587, 465
		Session session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(login, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(login));
			// vytvorit mail
			System.out.println(SignupController.emailReceiver);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(SignupController.emailReceiver));
			message.setSubject("Sign up - Guideman");
			String msg = "ahoj";
			MimeBodyPart bodyPart = new MimeBodyPart();
			bodyPart.setContent(msg, "text/html; charset=utf-8");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(bodyPart);
			message.setContent(multipart);
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}