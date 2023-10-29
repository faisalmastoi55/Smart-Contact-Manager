package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.repository.ContacRepositary;
import com.smart.repository.UserRepositary;

@RestController
public class SearchController {

	@Autowired
	private UserRepositary userRepositary;
	
	@Autowired
	private ContacRepositary contacRepositary;
	
	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal)
	{
		
		System.out.println(query);		
		User user = userRepositary.getUserByUserName(principal.getName());		
		List<Contact> contactList = this.contacRepositary.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contactList);
		
	}
}
