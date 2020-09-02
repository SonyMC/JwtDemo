package com.sonymathew.jwtdemo.user;

import java.io.Serializable;

//This class is required for storing the username and password we recieve from the client.
// Thsi class will serve as teh model for the user 

public class User implements Serializable{


		private static final long serialVersionUID = 5926468583005150707L; // canbe nay long :-)
		
		private String username;
		private String password;
		
		//need default constructor for JSON Parsing
		public User()
		{
			
		}
		
		// constructor based dependency injection
		public User(String username, String password) {
			this.setUsername(username);
			this.setPassword(password);
		}

		public String getUsername() {
			return this.username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return this.password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	

}
