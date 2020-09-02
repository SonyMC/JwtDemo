package com.sonymathew.jwtdemo.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/*
 * The JwtTokenUtil is responsible for performing JWT operations like creation and validation.
 * It makes use of the io.jsonwebtoken.Jwts for achieving this.
 */
@Component
public class JwtTokenUtil implements Serializable{
	
	private static final long serialVersionUID = -256243549L;


	private static long jwt_token_validity = SecurityConstants.JWT_TOKEN_VALIDITY;
	
	private String secret = SecurityConstants.JWT_SECRET_KEY;
	
	
	//generate token for user
	public String generateToken(UserDetails userDetails) {// User Details is a Spring Security core component
		Map<String, Object> claims = new HashMap<>();
		claims.put("UserName", userDetails.getUsername());
		claims.put("Secret", "YipeeYippeYay");
		return doGenerateToken(claims, userDetails.getUsername());
	}	
	
	
	//while creating the token -
	//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
	//2. Sign the JWT using the HS512 algorithm and secret key.
	//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	//   compaction of the JWT to a URL-safe string 
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		
		String jwtToken = Jwts.builder()
				          .setClaims(claims)  // passed in method arg
				          .setSubject(subject) // passed in method arg which is the user name
				          .setIssuedAt(new Date(System.currentTimeMillis()))  // issued time
				          .setExpiration(new Date(System.currentTimeMillis() + jwt_token_validity * 1000)) // set Expiration
				          .signWith(SignatureAlgorithm.HS512, secret) // Sign using HS5112 algorithm and secret key defined in application.properties
				          .compact() ;   // compaction of the JWT to a URL-safe string 

				          
				       
	   return jwtToken;
	}	
	
	// get one particular claim - > this will be extracted from all claims method. 
	// Note : We pas steh actual function used in this function which is pretty cool!!!
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	
	
    //for retrieving any information from token we will need the secret key
	//retrieve all claims from the passed token
	private Claims getAllClaimsFromToken(String token) {
			
		Claims allClaims = Jwts.parser()
								.setSigningKey(secret)
								.parseClaimsJws(token)
								.getBody();
		
		return allClaims;
		
	}

	

	
	//validate token using user Name
	public Boolean validateToken(String token, UserDetails userDetails) {  // User Details is a Spring Security core component
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}	
	

	//retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject); // Note : We pass the method reference to be used. Wow!!
	}	
	
	
	//check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
		
	
	//retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);  // Note : We pass the method reference to be used. Wow!!
	}	
 

	
}
