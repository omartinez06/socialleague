package com.oscarmartinez.socialleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.Tournament;

@Repository("tournamentRepository")
public interface ITournamentRepository extends JpaRepository<Tournament, Long> {
	
	public Tournament findByActive(boolean active);

}
