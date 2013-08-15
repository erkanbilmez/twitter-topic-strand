package twittertopicstrand.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
	
	public static void send(String to, String subject, String body) throws AddressException, MessagingException{
		String host = "smtp.gmail.com";	
		String from = "sehir.tweet.logging@gmail.com";
		String pass = "sehir.tweet.logging123";
	    
		Properties props = System.getProperties();
	    
		props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.user", from);
	    props.put("mail.smtp.password", pass);
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.auth", "true");

	    String[] tos = { to };

	    Session session = Session.getDefaultInstance(props, null);
	    MimeMessage message = new MimeMessage(session);
	    message.setFrom(new InternetAddress(from));

	    InternetAddress[] toAddress = new InternetAddress[tos.length];

	    for( int i=0; i < tos.length; i++ ) { 
	        toAddress[i] = new InternetAddress(tos[i]);
	    }

	    for( int i=0; i < toAddress.length; i++) { 
	        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
	    }
	    message.setSubject(subject);
	    message.setText(body);
	    
	    Transport transport = session.getTransport("smtp");
	    transport.connect(host, from, pass);
	    transport.sendMessage(message, message.getAllRecipients());
	    transport.close();
	}
}
