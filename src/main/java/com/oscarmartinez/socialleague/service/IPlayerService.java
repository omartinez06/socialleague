package com.oscarmartinez.socialleague.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oscarmartinez.socialleague.entity.Player;
import com.oscarmartinez.socialleague.resource.BackUpLinesDTO;
import com.oscarmartinez.socialleague.resource.PlayerDTO;

import net.sf.jasperreports.engine.JRException;

public interface IPlayerService {

	List<Player> listPlayers();

	ResponseEntity<HttpStatus> addPlayer(PlayerDTO player) throws Exception;

	ResponseEntity<Player> editPlayer(long id, PlayerDTO playerDetail) throws Exception;

	ResponseEntity<HttpStatus> deletePlayer(long id) throws Exception;

	ResponseEntity<Player> getPlayerById(long id) throws Exception;

	ResponseEntity<Player> addLine(long id, List<Integer> lines) throws Exception;
	
	ResponseEntity<Player> addSerie(long id, int serieValue) throws Exception;

	List<Player> findAllByTeam(long teamId) throws Exception;
	
	String exportReport(String reportFormat) throws JRException, IOException;
	
	ResponseEntity<HttpStatus> updateHandicap() throws Exception;
	
	ResponseEntity<Player> updateSingleHandicap(long id) throws Exception;
	
	ResponseEntity<BackUpLinesDTO> getBackUpLines(long id) throws Exception;

}
