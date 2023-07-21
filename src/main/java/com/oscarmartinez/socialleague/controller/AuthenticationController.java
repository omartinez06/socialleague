package com.oscarmartinez.socialleague.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oscarmartinez.socialleague.resource.LoginDTO;
import com.oscarmartinez.socialleague.resource.UserDTO;
import com.oscarmartinez.socialleague.service.IUserService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

	@Autowired
	private IUserService userService;

	@PostMapping
	public ResponseEntity<?> createUser(@RequestBody UserDTO user) {
		return userService.createUser(user);
	}

	@PostMapping("/auth")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO login) {
		return userService.authenticateUser(login);
	}

}
