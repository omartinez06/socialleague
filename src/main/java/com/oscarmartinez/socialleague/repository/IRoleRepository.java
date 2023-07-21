package com.oscarmartinez.socialleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.Role;

@Repository("roleRepository")
public interface IRoleRepository extends JpaRepository<Role, Long> {
	
	public Role findRoleByName(String name);

}
