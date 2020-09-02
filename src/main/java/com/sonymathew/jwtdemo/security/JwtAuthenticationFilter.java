
package com.sonymathew.jwtdemo.security;


import java.io.IOException;
import javax.management.RuntimeErrorException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonymathew.jwtdemo.user.User;
import com.sonymathew.jwtdemo.user.UserService;

// 1.This class is first in chain of authorisation responsible for authentcating the input usser & pwd and returnign a jwt token
// 2.In this case the username and pwd will need to be set using a login REST API as we do not have web form UI
// 3.Spring automatically figues where this filter should fit in the whole security chain though if we want we can define the trigerrign point. We won't do this here.
// 4. Once this class is executed, the user will take the JWT token returned and call the JWTAuthorizationFilter class which will be the second in command to this class. 

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	// create logger
	private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userService;
	
	// Authentication Manager does the authentication using whatever functionality we define.
	private AuthenticationManager authenticationManager;
	
	
	// constructor based dependency injection
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	
	
	
	
	// We generate the authentication token for input user name & password in the request
	// Login request: http://localhost:8083/login . Provide username and pwd as json in request.
	// The authenticate() function validates the user name and pwd stored in db which is retrieved by the UserDetailsServiceImpl class.
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		
		User inputUser = null;
		Authentication authenticate = null;
		
		try{			
			// Construct User object using Jackson mapper on the input request with reference to User class 
			inputUser = new ObjectMapper().readValue(request.getInputStream(), User.class);
			logger.warn("************ INput user !!! "+ inputUser.getUsername());
			//Now generate authentication token for User name & pwd 
			//Once the authenticate() function is called successfully , the Authenticate instance will contain teh details of the user as loaded from the DB.
			authenticate =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(inputUser.getUsername(), bCryptPasswordEncoder.encode(inputUser.getPassword())));
			logger.warn("************ Authentication Succesful!!! ");
		}catch(BadCredentialsException badcrex){
			// Authentication Has failed if control reaches this point
			logger.error("Authentication Failed for UserName:{}!!!", inputUser.getUsername(),badcrex.getMessage());
			throw badcrex;
		}catch(Error ex){
			logger.error("Error while authentication the userName:{}!!!",inputUser.getUsername(),ex.getMessage());
			throw new RuntimeErrorException(ex);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return authenticate;
		
		
	}
	
	
	// Now  on successful authentication above , generate the JWT token for the user
	// the authentication chain is automatically redirected to this function from the above attemptAuthentication funnction
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
											HttpServletResponse response,
											FilterChain chain,
											Authentication auth) 
	throws IOException, ServletException{
		
		logger.warn("************ Inside successfulAuthentication!!! ");
				
		// Get user from authentication token.
		// Here the principal object in the authentcaltion token is user as loaded from the DB in the above function 
	    //User authUser = (User) auth.getPrincipal();
		User authUser = (User) auth.getPrincipal();
		 
		
		final UserDetails userDetails = userService.loadUserByUsername(authUser.getUsername());   //User Details is Spring Core Secuirty Package

		final String jwtToken = jwtTokenUtil.generateToken(userDetails);  // this will geenrate the token
		
		logger.warn("************ jwtToken!!! " + jwtToken );
		// Now attach this token to the http response header
		response.addHeader(SecurityConstants.AUTHORIZATION_HEADER, SecurityConstants.BEARER_TOKEN_PREFIX + jwtToken);
		
		logger.warn("************ response!!! " + response.getHeader(SecurityConstants.AUTHORIZATION_HEADER) );		
	}
			
	
	
	
}
	
	

	


