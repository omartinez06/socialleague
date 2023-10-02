package com.oscarmartinez.socialleague.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oscarmartinez.socialleague.entity.Team;
import com.oscarmartinez.socialleague.resource.BackUpPointsDTO;
import com.oscarmartinez.socialleague.resource.TeamDTO;

public interface ITeamService {

	List<Team> listTeams();

	ResponseEntity<HttpStatus> addTeam(TeamDTO team) throws Exception;

	ResponseEntity<Team> editTeam(long id, TeamDTO teamDetail) throws Exception;

	ResponseEntity<HttpStatus> deleteTeam(long id) throws Exception;

	ResponseEntity<Team> getTeamById(long id) throws Exception;

	ResponseEntity<Team> addPoints(long id, int points) throws Exception;

	ResponseEntity<BackUpPointsDTO> getBackUpPoints(long id) throws Exception;

	ResponseEntity<Team> editPoints(long id, int points) throws Exception;

}
