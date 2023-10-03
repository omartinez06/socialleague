package com.oscarmartinez.socialleague.service;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.oscarmartinez.socialleague.entity.User;
import com.oscarmartinez.socialleague.entity.Role;
import com.oscarmartinez.socialleague.repository.IRoleRepository;
import com.oscarmartinez.socialleague.repository.IUserRepository;
import com.oscarmartinez.socialleague.resource.JwtResponse;
import com.oscarmartinez.socialleague.resource.LoginDTO;
import com.oscarmartinez.socialleague.resource.UserDTO;
import com.oscarmartinez.socialleague.security.JwtProvider;

@Service
@Transactional
public class UserServiceImp implements IUserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImp.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private IRoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private IUserRepository userRepository;

	@Override
	public ResponseEntity<?> createUser(UserDTO user) {
		Set<Role> authorities = new HashSet<Role>();
		Role defaultRole = roleRepository.findRoleByName("ADMIN");
		authorities.add(defaultRole);

		User newUser = User.builder().username(user.getUsername()).password(passwordEncoder.encode(user.getPassword()))
				.roles(authorities).build();

		userRepository.save(newUser);
		return ResponseEntity.ok("Usuario registrado");
	}

	@Override
	public ResponseEntity<?> authenticateUser(LoginDTO login) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(login.getUser(), login.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtProvider.generateJwtToken(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		log.debug("{} logged user: {}", "authenticateUser", jwtProvider.getUserName());
		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities(),
				jwtProvider.getJwtTokenExpiration(jwt)));
	}
}
