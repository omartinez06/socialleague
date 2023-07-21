package com.oscarmartinez.socialleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.Player;

@Repository("playerRepository")
public interface IPlayerRepository extends JpaRepository<Player, Long> {

}
