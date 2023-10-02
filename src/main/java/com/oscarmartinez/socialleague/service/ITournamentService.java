package com.oscarmartinez.socialleague.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oscarmartinez.socialleague.entity.Tournament;
import com.oscarmartinez.socialleague.resource.TournamentDTO;

public interface ITournamentService {

	ResponseEntity<Tournament> getActiveTournament() throws Exception;

	ResponseEntity<HttpStatus> startNewTournament(TournamentDTO newTournament) throws Exception;

	ResponseEntity<Tournament> updateTournamentInformation(long id, Map<String, Object> tournamentInfo) throws Exception;
	
	ResponseEntity<Tournament> finishTournament(long id) throws Exception;

}
