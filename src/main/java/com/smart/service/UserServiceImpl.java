package com.smart.service;

import java.util.Properties;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.smart.entities.User;
import com.smart.repository.UserRepositary;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepositary userRepositary;

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public User registerForm(User user, String url) {

		user.setRole("ROLE_USER");
		user.setImageUrl("default.png");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnable(false);
		user.setVerificationCode(UUID.randomUUID().toString());

		User save = userRepositary.save(user);

		if (save != null) {
			verificationSendEmail(save, url);
		}

		return save;
	}

	@Override
	public void verificationSendEmail(User user, String url) {
		// TODO Auto-generated method stub

		// rest of the code
		String from = "fahadhussai863@gmail.com";
		String to = user.getEmail();
		String subject = "Account Verification";
		String content = "Dear [[name]], <br>" + "Please click the link below to verify your registration : <br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3> " + "Thank you, <br>" + "Faisal Ali";

		try {

			// Step 2: compose the message [text, multi media]
			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom(from, "Faisal Ali");
			helper.setTo(to);
			helper.setSubject(subject);

			content = content.replace("[[name]]", user.getName());
			String siteURL = url + "/verify?code=" + user.getVerificationCode();
			content = content.replace("[[URL]]", siteURL);
			helper.setText(content, true);
			mailSender.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean verifyAccount(String verificationCode) {
		// TODO Auto-generated method stub

		User user = userRepositary.findByVerificationCode(verificationCode);

		if (user == null) {
			return false;
		} else {

			user.setEnable(true);
			user.setVerificationCode(null);
			userRepositary.save(user);

			return true;
		}

	}

	@Override
	public void removeSessionMessage() {
		HttpSession sessions = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
				.getSession();
		sessions.removeAttribute("message");

	}

	@Override
	public boolean sendEmail(String subject, String message, String to) {
		boolean f = false;

		// rest of the code
		String from = "fahadhussai863@gmail.com";

		// variable for gmail
		String host = "smtp.gmail.com";

		// get the system properties
		Properties properties = new Properties();
		System.out.println("Properties : " + properties);

		// setting important information to properties object

		// host set
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.host", host);

		String userName = "fahadhussai863";
		String password = "tvpaaokzlosxnydj";

		// Step 1: to get the session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(userName, password);
			}

		});

		try {

			// Step 2: compose the message [text, multi media]
			Message message2 = new MimeMessage(session);

			// add recipient to message
			message2.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// from email
			message2.setFrom(new InternetAddress(from));

			// adding subject to message
			message2.setSubject(subject);

			// adding text to message
			/* message2.setText(message); */
			message2.setContent(message, "text/html");

			// send
			// Step 3: send the message using Transport class
			Transport.send(message2);

			System.out.println("Send success............");

			f = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return f;
	}

	@Override
	public void changePassword(String newPassword, HttpSession session) {
		// TODO Auto-generated method stub

		String email = (String) session.getAttribute("email");
		User user1 = this.userRepositary.getUserByUserName(email);
		user1.setPassword(this.passwordEncoder.encode(newPassword));
		User save = this.userRepositary.save(user1);

	}

}
