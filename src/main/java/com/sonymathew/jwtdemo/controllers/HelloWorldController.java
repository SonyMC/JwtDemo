package com.sonymathew.jwtdemo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/v1/jwt")
public class HelloWorldController {
	

//	@GetMapping(path = "/{hello}")      //This indicates hello is  parameter whose value will be supplie din the request
//	@GetMapping(path = "hello")         // Indicates that "hello" is a string constant that will be passes in the request 
	@GetMapping(path = { "/hello" })    // Same as above bit technically more precise. Indicates that "hello" is a string constant that will be passes in the request 
	public ResponseEntity<?> firstPage(){
		return new ResponseEntity<>("Hello ..all of you awesome folks!!!", HttpStatus.I_AM_A_TEAPOT);
	}

}
