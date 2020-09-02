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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


import com.sonymathew.jwtdemo.user.UserService;



/*
 * The JwtRequestFilter extends the Spring Web Filter OncePerRequestFilterClass.
 * For any incoming request this Filter class gets requested.
 * It checks if the request has a valid JWT token.If it has a valid token thenit sets the Authentication in context.
 * i.e. to specify that the current user is authenticated 
 */

// This is the second in chain of command as defined in JwtAPiSecurityConfig.java 

public class JwtAuthorizationFilter extends BasicAuthenticationFilter  {  // Note: BasicAuthenticationFilter extends OncePerRequestFilter

	// create logger
	private static Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
		
	}	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// get the header which we had set while generating the JWT token earlier in JwtAuthenticationFilter class.
		final String requestTokenHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);    



		// In case there is no authorization header or there is one but it does not contain the JWT we had provided
		// then ignore header & return control back to filter back to filter chain 
		if(requestTokenHeader == null	|| ! requestTokenHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)){
			filterChain.doFilter(request, response);
			if(requestTokenHeader == null){
				logger.warn("JWT Token is empty!!!");
			}else{
			
				logger.warn("JWT Token does not begin with Bearer String");
			}
			return;
		}		
		
		
		// if everything is ok,then :
		 //1. then fetch the authentication details from the header 
		// 2. verify the JWT token 
		// 3. set the security context using the authentication token
		UsernamePasswordAuthenticationToken authenticationToken= getAuthentication(request,requestTokenHeader);
		

		// After setting the Authentication in the context, we specify
		// that the current user is authenticated. So it passes the
		// Spring Security Configurations successfully.
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		
		// finally continue with the filter chain 
		filterChain.doFilter(request, response);		

	}
		
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,String authorizationHeader) {
		
		String jwtToken = null;
		String userNameFromJwt = null;
		
		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		if (authorizationHeader != null && authorizationHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {
			jwtToken = authorizationHeader.replace(SecurityConstants.BEARER_TOKEN_PREFIX, "");
		}else{
			logger.error("JWT Token does not begin with Bearer String");
		}
		
		//Finally, do an extra level of validation by trying to get the user name from the token, va;idating the 
		try {
			userNameFromJwt = jwtTokenUtil.getUsernameFromToken(authorizationHeader);  
			// now if the usr anme is succesfully extrac.
			if(userNameFromJwt != null){
				// 1. UseDetails belongs to Spring security core framework
				// 2. We are loading the user dwetails ( includign the encrypted pwd from our application/db
				UserDetails userdetails = this.userService.loadUserByUsername(userNameFromJwt); 
				
				//3. Validate the jwt token and with teh user details extracted above
				if (jwtTokenUtil.validateToken(jwtToken, userdetails)) {
					
					// 4. Generate the Authentication token 
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userdetails, null, userdetails.getAuthorities());
					
					// 5. Also set the Wenb Authentication details 
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					// 6. Retun the authentintication token 
					return usernamePasswordAuthenticationToken;
					
					// 7. An optional alternative is to simply do the followiugn instead of steps 4 & 5 above
					//return new UsernamePasswordAuthenticationToken(userNameFromJwt, null, new ArrayList<>());
					}
				}
			}catch(BadCredentialsException bex){
			logger.error("Authorization Failed!!",bex.getMessage());
			throw bex;
			}catch(Error ex){
			logger.error("Error while authorizing!!!",ex.getMessage());
			throw new RuntimeErrorException(ex);
			}
		
		return null;
		
	}

}
