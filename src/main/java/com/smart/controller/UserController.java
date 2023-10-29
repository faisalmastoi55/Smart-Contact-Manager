package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.ContacRepositary;
import com.smart.repository.UserRepositary;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepositary userRepositary;

	@Autowired
	private ContacRepositary contacRepositary;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("Email : " + userName);

		// get the user using username
		User user = userRepositary.getUserByUserName(userName);
		System.out.println("User : " + user);

		model.addAttribute("user", user);

	}

	// Dashboard Home
	@GetMapping("/index")
	public String dashboard(Model model) {

		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open Add Form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact";
	}

	@PostMapping("/process-contact")
	public String proccessContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {

			String name = principal.getName();
			User user = this.userRepositary.getUserByUserName(name);

			if (file.isEmpty()) {
				// if the file is empty then try our message
				System.out.println("File is empty");
				contact.setImage("default.png");

			} else {
				// file the file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded");

			}

			user.getContacts().add(contact);
			contact.setUser(user);

			User save = this.userRepositary.save(user);
			// System.out.println("DATA : " + contact);

			// message success
			session.setAttribute("message", new Message("Your contact is added !! Add more...", "success"));

		} catch (Exception e) {
			System.out.println("ERROR : " + e.getMessage());
			e.printStackTrace();

			// message error
			session.setAttribute("message", new Message("Something went wrong !! Try Again...", "danger"));
		}

		return "redirect:/user/add-contact";
	}

	// show contact handler
	@GetMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contact");

		String name = principal.getName();
		User user = this.userRepositary.getUserByUserName(name);

		// CurrentPage-Page
		// Contact Per Page - 5
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contact = this.contacRepositary.findContactByUser(user.getId(), pageable);

		model.addAttribute("contacts", contact);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contact.getTotalPages());

		return "normal/show_contact";

	}

	@GetMapping("/{cid}/contact")
	public String showContactDetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		model.addAttribute("title", "Contact Details");

		Optional<Contact> optional = this.contacRepositary.findById(cid);
		Contact contact = optional.get();

		String name = principal.getName();
		User user = this.userRepositary.getUserByUserName(name);

		if (user.getId() == contact.getUser().getId())
			model.addAttribute("contact", contact);

		return "normal/contact_detail";
	}

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model, Principal principal,
			HttpSession session) {

		try {
			Contact contact = this.contacRepositary.findById(cid).get();
			User user = this.userRepositary.getUserByUserName(principal.getName());

			File deleteFile = new ClassPathResource("static/img").getFile();
			File file1 = new File(deleteFile, contact.getImage());
			file1.delete();

			user.getContacts().remove(contact);
			this.userRepositary.save(user);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// session.setAttribute("message", new Message("Contact delete successfully",
		// "success"));

		return "redirect:/user/show-contact/0";
	}

	// open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title", "Update Form");

		Contact contact = this.contacRepositary.findById(cid).get();
		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {

		try {

			// old contact detail fetch
			Contact oldContactDetails = this.contacRepositary.findById(contact.getCid()).get();

			if (!file.isEmpty()) {
				// rewrite
				// file work

				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetails.getImage());
				file1.delete();

				// update new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContactDetails.getImage());
			}

			User user = this.userRepositary.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contacRepositary.save(contact);

			session.setAttribute("message", new Message("Your Contact is updated", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/user/" + contact.getCid() + "/contact";
	}

	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");

		return "normal/profile";
	}

	// open setting handler
	@GetMapping("/settings")
	public String openSettings(Model model) {
		model.addAttribute("title", "Settings Page");

		return "normal/settings";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal,
			HttpSession session) {
		
		User currentUser = this.userRepositary.getUserByUserName(principal.getName());
		if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			
			//Change the password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepositary.save(currentUser);
			
			session.setAttribute("message", new Message("Your Password is successfully changed..", "success"));
		}else {
			session.setAttribute("message", new Message("Please Enter correct old password !!", "danger"));
		}
		
		return "redirect:/user/settings";
	}

}
