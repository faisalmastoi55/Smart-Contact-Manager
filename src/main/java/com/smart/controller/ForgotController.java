package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.User;
import com.smart.repository.UserRepositary;
import com.smart.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	Random random = new Random();

	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepositary userRepositary;
	
	// email if form open handler
	@GetMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		System.out.println("Email : " + email);

		// generating 4 digit OTP
		// Define the lower and upper bounds for the random number
        int min = 1000;
        int max = 9999;
        
        int otp = random.nextInt(max - min + 1) + min;
		System.out.println("OTP : " + otp);

		// write code for send otp to email
		String subject = "OTP From SCM";
		String message = "" + "<div>" + "<h1>" + "OTP is : " + "<b>" + otp + "</b>" + "</h1>" + "</div>";
		String to = email;

		boolean sendEmail = this.userService.sendEmail(subject, message, to);

		if (sendEmail) {

			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";

		} else {

			session.setAttribute("message", "check your email id !!");
			return "forgot_email_form";
		}

	}

	@PostMapping("/verify-otp")
	public String verify(@RequestParam("otp1") int otp1, @RequestParam("otp2") int otp2,
			@RequestParam("otp3") int otp3, @RequestParam("otp4") int otp4, HttpSession session) {

		String otp = String.valueOf(otp1) + String.valueOf(otp2) + String.valueOf(otp3) + String.valueOf(otp4);

		int myOtp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");

		if (String.valueOf(myOtp).equals(otp)) {

			User user = this.userRepositary.getUserByUserName(email);
			
			if (user==null) {
				
				//send error message
				session.setAttribute("message", "User does not exist with this email !!");
				return "forgot_email_form";
				
			} else {
				
				//send change password form

			}
			
			return "password_change_form";
		} else {

			session.setAttribute("message", "You have entered wrong otp");
			return "verify_otp";
		}
	}
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session)
	{
		this.userService.changePassword(newPassword, session);
		
		return "redirect:/signin?change=Password change successfully..";
	}
}
