package com.smart.service;

import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.User;

import jakarta.servlet.http.HttpSession;

public interface UserService {

	public void removeSessionMessage();
	
	public User registerForm(User user, String url);
	
	public void verificationSendEmail(User user, String path);
	
	public boolean verifyAccount(String verificationCode);
	
	public boolean sendEmail(String subject, String message, String to);
	
	public void changePassword(@RequestParam("newPassword") String newPassword,HttpSession session);

}
