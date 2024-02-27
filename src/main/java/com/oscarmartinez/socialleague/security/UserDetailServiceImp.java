package com.oscarmartinez.socialleague.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oscarmartinez.socialleague.entity.User;
import com.oscarmartinez.socialleague.repository.IUserRepository;

@Service
public class UserDetailServiceImp implements UserDetailsService {

	@Autowired
	private IUserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		return UserImp.build(user);
	}

}
