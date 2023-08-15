package com.oscarmartinez.socialleague.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.Player;
import com.oscarmartinez.socialleague.entity.Team;

@Repository("playerRepository")
public interface IPlayerRepository extends JpaRepository<Player, Long> {

	List<Player> findAllByTeam(Team team);

}
