package com.oscarmartinez.socialleague.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.entity.Player;
import com.oscarmartinez.socialleague.entity.Team;

@Repository("playerRepository")
public interface IPlayerRepository extends JpaRepository<Player, Long> {

	List<Player> findAllByTeam(Team team);
	
	List<Player> findByCategory(Category category);
	
	List<Player> findByNameAndLastName(String name, String lastName);
	
	List<Player> findBySendReportMail(boolean sendReportMail);

}
