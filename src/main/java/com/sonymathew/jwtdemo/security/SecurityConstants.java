package com.sonymathew.jwtdemo.security;

public class SecurityConstants {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_TOKEN_PREFIX = "Bearer ";
	public static final String API_BASE_URL = "http://localhost:";	
	public static final String AUTHENTICATE_URL = "/v1/jwt/authenticate"; 	
	public static final String USER_NAME_APP = "CaptainHaddock"; 
	public static final String USER_NAME_PWD = "BlisteringBarnacles"; 
	public static final String JWT_SECRET_KEY = "ThunderingTyphoons";//
//	#validity of the token = 5 minutes ; viz . 5 * 60 * 60 = 18000 milliseconds
	public static final Integer JWT_TOKEN_VALIDITY= 18000;
	
		
	
}
