package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.smart.entities.User;
import com.smart.repository.UserRepositary;

@Component
public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepositary userRepositary;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		//fetching user from database
		
		User user = userRepositary.getUserByUserName(username);

		if (user == null) {
			throw new UsernameNotFoundException("Could not found user");
		}

		CustomUserDetails details = new CustomUserDetails(user);

		return details;
	}

}
