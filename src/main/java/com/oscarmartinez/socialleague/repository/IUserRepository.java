package com.oscarmartinez.socialleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oscarmartinez.socialleague.entity.User;

public interface IUserRepository extends JpaRepository<User, Long> {
	
	public User findByUsername(String username);
	
}
