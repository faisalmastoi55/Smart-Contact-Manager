package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session, HttpServletRequest request) {

		try {

			if (result1.hasErrors()) {

				model.addAttribute("user", user);
				return "signup";
			}

			if (!agreement) {
				System.out.println("You have not agreed the terms and conditions");

				throw new Exception("You have not agreed the terms and conditions");
			}

			String url = request.getRequestURL().toString();
			url = url.replace(request.getServletPath(), "");
			
			User result = this.userService.registerForm(user, url);

			session.setAttribute("message", new Message("Check and confirm your email id then you are successfully registered!!", "alert-success"));

			return "redirect:/signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));

			return "signup";

		}

	}
	
	@GetMapping("verify")
	public String verifyAccount(@Param("code") String code, Model m)
	{
		boolean f = userService.verifyAccount(code);
		
		if (f) {
			m.addAttribute("message","Successfully your account is verify");
		} else {
			m.addAttribute("message","may be your verification code is incorrect or already verified");

		}
		
		return "message";
	}

	//handler for custom login
	
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title", "Login - Smart Contact Manager");
		
		return "login";
	}
	
}
