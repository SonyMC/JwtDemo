package com.sonymathew.jwtdemo.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/*
 * Note : this class is only put here to demonstrate nabling CORS(Cross Origin Resource Sharing) for a browser based session. Here in our APIs thsi class is not relevant as it is service based and not browser based.
 * Also we will need to remove enable cors in our LibraryApiSecurityConfig.java
 */

@Configuration
public class WebSecurityConfig  extends WebMvcConfigurationSupport{
	
	 @Override
	    public void addCorsMappings(CorsRegistry registry) {
		 
	        registry.addMapping("/**")
            .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
	 	}
		 
		 

	
	

}
