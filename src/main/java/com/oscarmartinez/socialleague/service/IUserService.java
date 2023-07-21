package com.oscarmartinez.socialleague.service;

import org.springframework.http.ResponseEntity;

import com.oscarmartinez.socialleague.resource.LoginDTO;
import com.oscarmartinez.socialleague.resource.UserDTO;

public interface IUserService {
	
	ResponseEntity<?> createUser(UserDTO user);
	
	ResponseEntity<?> authenticateUser(LoginDTO login);

}
