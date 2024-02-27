package com.oscarmartinez.socialleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
	
	public User findByUsername(String username);
	
}
