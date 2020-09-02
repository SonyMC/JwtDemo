package com.sonymathew.jwtdemo.user;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sonymathew.jwtdemo.security.JwtApiSecurityConfig;
import com.sonymathew.jwtdemo.security.SecurityConstants;

/*
 * JWTUserDetailsService implements the Spring Security UserDetailsService interface. 
 * It overrides the loadUserByUsername for fetching user details from the database using the username. 
 * The Spring Security Authentication Manager calls this method for getting the user details from the database when authenticating the user details provided by the user. 
 * Here we are getting the user details from a hardcoded User List. 
 */

@Service
public class UserService implements UserDetailsService {
	
	// create logger
	private static Logger logger = LoggerFactory.getLogger(JwtApiSecurityConfig.class);
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	String userNameInApp = SecurityConstants.USER_NAME_APP;	
	
	String userPwd = SecurityConstants.USER_NAME_PWD;	
	
			
	// Note : We are using values loaded from Application.properties instead of loading from DB. This is just for demonstration purposes		
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		logger.warn("** userNameInApp:" + userNameInApp);
		logger.warn("** userPwd:" + userPwd);
	    String encodedPwd = bCryptPasswordEncoder.encode(userPwd);
		
		if(userName.equals(userNameInApp)) { // If user name matches, create the user using the User class in Spring Security
	
			UserDetails userDetails = new User(userName,  // user
											   encodedPwd, // pwd
											   true, // user is enabled 
											   true, // accountNonExpired
											   true, // credentialsNonExpired
											   true,  // accountNonLocked
											   new ArrayList<>());  // authority or roles list which presently is empty 

			return 	userDetails;
		}else{
			throw new UsernameNotFoundException("User not found with username: " + userName);
		}
			
			
	}
	

}
