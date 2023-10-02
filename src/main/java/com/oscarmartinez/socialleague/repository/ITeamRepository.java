package com.oscarmartinez.socialleague.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.entity.Team;

@Repository("teamRepository")
public interface ITeamRepository extends JpaRepository<Team, Long> {

	List<Team> findByCategory(Category category);

}
