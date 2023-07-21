package com.oscarmartinez.socialleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.Team;

@Repository("teamRepository")
public interface ITeamRepository extends JpaRepository<Team, Long>{

}
