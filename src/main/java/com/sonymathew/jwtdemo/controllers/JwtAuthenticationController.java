//package com.sonymathew.jwtdemo.controllers;
//
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.sonymathew.jwtdemo.user.User;
//
//
//
///*
// * Expose a POST API /authenticate using the JwtAuthenticationController. 
// * The POST API gets username and password in the body- 
// * Using Spring Authentication Manager we authenticate the username and password.
// * If the credentials are valid, a JWT token is created using the JWTTokenUtil and provided to the client 
// * in response header.
// */
//
//
//@RestController
//@CrossOrigin  // indicates the client request origin is different to the server 
//@RequestMapping(path="/v1/jwt")
//public class JwtAuthenticationController {
//
//
//	@PostMapping(value = "/authenticate")
//	public ResponseEntity<?> createAuthenticationToken(@RequestBody User inputUser) throws Exception {
//
//		
//		return new ResponseEntity<>(inputUser.getUsername(), HttpStatus.OK);
//			
//		
//	
//	}
//
//	
//}
