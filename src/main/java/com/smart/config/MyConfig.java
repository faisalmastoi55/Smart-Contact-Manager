package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig{

	@Bean
	public UserDetailsService getUserDetailsService()
	{
		return new UserDetailServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider()
	{
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(getUserDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		
		return provider;
	}
	
	
	// configure method
		
		@Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	        	.csrf()
	        	.disable()
	        	.authorizeHttpRequests()
	            .requestMatchers("/admin/**")
	            .hasRole("ADMIN")
	            .requestMatchers("/user/**")
	            .hasRole("USER")
	            .requestMatchers("/**")
	            .permitAll()
	            .anyRequest()
	            .authenticated()
	            .and()
	            .formLogin()
	            .loginPage("/signin")
	            .loginProcessingUrl("/doLogin")
	            .defaultSuccessUrl("/user/index");
	        
	        return http.build();
	    }

		
}
