
package com.sonymathew.jwtdemo.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;




// This class is used to configure our Authentication(JwtAuthenticationFilter.java) and Authorization(JwtAuthorizationFilter.java) classes to teh Spring Security Filter Chain 

@Configuration
@EnableWebSecurity // Allows Spring to find configs & components for security & automatically applies to Global Web Security
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class JwtApiSecurityConfig extends WebSecurityConfigurerAdapter{
// Note : 	WebSecurityConfigurerAdapter allows us to customize Spring Security Framework
	
	// create logger
	private static Logger logger = LoggerFactory.getLogger(JwtApiSecurityConfig.class);
	

	
    @Autowired
    Environment environment;	
	
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private UserDetailsService jwtUserDetailsService;    // User Details Service is a Spring Security Core Package 

	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// configure AuthenticationManager so that it knows from where to load
		// user for matching credentials
		// Use BCryptPasswordEncoder
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

    // We want to encrypt the pwd in JwtUserDetailsService.class by hashing it rather than storing it as plain text
   @Bean
   public BCryptPasswordEncoder bCryptPasswordEncoder(){
	   return new BCryptPasswordEncoder();
   }	
   
	
	/// This function will be used to configure which requests shoud not challenged , which should be challenged and the authentication and authization filters appplied. Will also specify the session configuration.
	public void configure(HttpSecurity httpSecurity) throws Exception   {
		try {
			httpSecurity.cors().and().csrf().disable()  // disable cors(Cross Origin Resource Sharing) and csrf(Cross site resource forgery) is disabled for non browser clients
						.authorizeRequests()  // now authorize the following request matching th below criteria
				//		.antMatchers(HttpMethod.GET).permitAll()  // sample - permit all requests  
				//		.antMatchers(HttpMethod.GET,SecurityConstants.GET_PUBLISHER_BY_ID).permitAll()//  sample 
				//		.antMatchers(HttpMethod.POST,SecurityConstants.AUTHENTICATE_URL).permitAll()// allow authenticate url 
						.anyRequest().authenticated()   // apart from new user request all other requests will be authenticated
						.and()  // add our customized authentication & authorization fiters 
						.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint) // Exception Handling
						.and()
						.sessionManagement()
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS);// Since we are using Rest services, we will not need a session. By declaring the policy as Stateless, Spring Security will never create an HttpSession and it will never use it to obtain the SecurityContext
		
			   // Add a filter to validate the tokens with every request
			httpSecurity.addFilter(new JwtAuthenticationFilter(authenticationManager()))
					    .addFilter(new JwtAuthorizationFilter(authenticationManager()));
			
			// We need thsi so that the header is auto-injected
			httpSecurity.headers().httpStrictTransportSecurity().disable();

		} catch (Exception ex) {
			logger.error("Failed to configure security in class {}!!!", JwtApiSecurityConfig.class,ex.getMessage());
			throw ex;
		} 
			
		
	}
	
	
	// Will be used to specify the User Details retrieval implmentation and pwd encoder.
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(bCryptPasswordEncoder()); // let the authentication manager know what is the user details service implmentation used to get teh user details and also teh type of pwd encoder used. 

		}
	
	
	/*
	 * This method is added only to demonstrate how to configure CORS in a browser based scenario. In our Library API - THIS CAN BE WHOLLY IGNORED OR LEFT OUT!!!!
	 */
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
        //The port on which the server started.
        String port = environment.getProperty("local.server.port");
        
        String allowedUriGeneric = SecurityConstants.API_BASE_URL + port + "/v1/*" ;
        String allowedUriLogin = SecurityConstants.API_BASE_URL + port + "/login" ;
		
        // Define an immutable list as we dont want this to keep changing 
        final List<String> allowedUris = new ArrayList<>(java.util.Arrays.asList(allowedUriGeneric,allowedUriLogin));
		
        //Define an immutable list of methods
        final List<String> allowedMethods = new ArrayList<>(java.util.Arrays.asList("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        
       //Define an immutable list of headers
        final List<String> allowedHeaders = new ArrayList<>(java.util.Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        
        // Configure CORS  
		final CorsConfiguration corsConfiguration = new CorsConfiguration();
		
		//set allowed uris 
		corsConfiguration.setAllowedOrigins(allowedUris);
		
		
		//set allowed methods
		corsConfiguration.setAllowedHeaders(allowedMethods);
		
		// setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        corsConfiguration.setAllowCredentials(true);		
        
        
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        corsConfiguration.setAllowedHeaders(allowedHeaders);
        
        // define a uri based CORS configuration source 
        final UrlBasedCorsConfigurationSource urlCorsSource = new UrlBasedCorsConfigurationSource();
        
        // register the uri based CORS configuration source using the corsConfiguration details
         urlCorsSource.registerCorsConfiguration("/**", corsConfiguration);
         
        return urlCorsSource;
        
		
	}
	
	


}

