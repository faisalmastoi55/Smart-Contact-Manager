package com.smart.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContacRepositary extends JpaRepository<Contact, Integer>{
	//Pagination
	
	@Query("from Contact as c where c.user.id =:userId")
	//CurrentPage-Page
	//Contact Per Page - 5
	public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);
	
	//Search
	public List<Contact> findByNameContainingAndUser(String name, User user);
}
