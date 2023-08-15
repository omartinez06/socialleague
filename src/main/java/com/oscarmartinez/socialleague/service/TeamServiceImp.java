package com.oscarmartinez.socialleague.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.entity.Team;
import com.oscarmartinez.socialleague.repository.ICategoryRepository;
import com.oscarmartinez.socialleague.repository.ITeamRepository;
import com.oscarmartinez.socialleague.resource.TeamDTO;
import com.oscarmartinez.socialleague.security.JwtProvider;

@Service
public class TeamServiceImp implements ITeamService {

	private static final Logger logger = LoggerFactory.getLogger(TeamServiceImp.class);

	@Autowired
	private ITeamRepository teamRepository;

	@Autowired
	private ICategoryRepository categoryRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Override
	public List<Team> listTeams() {
		return teamRepository.findAll();
	}

	@Override
	public ResponseEntity<HttpStatus> addTeam(TeamDTO team) throws Exception {
		final String methodName = "addTeam()";
		logger.debug("{} - Begin", methodName);
		Team newTeam = new Team();
		newTeam.setName(team.getName());
		newTeam.setAddedDate(new Date());
		newTeam.setAddedBy(jwtProvider.getUserName());
		Category category = categoryRepository.findById(team.getCategory())
				.orElseThrow(() -> new Exception("Category does not exist with id: " + team.getCategory()));
		newTeam.setCategory(category);
		
		if(isExist(team.getName()))
			throw new Exception("ALLREADY_EXIST");
		
		teamRepository.save(newTeam);
		logger.debug("{} - End", methodName);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Team> editTeam(long id, TeamDTO teamDetail) throws Exception {
		final String methodName = "editPlayer()";
		logger.debug("{} - Begin", methodName);
		Team team = teamRepository.findById(id).orElseThrow(() -> new Exception("Team does not exist with id: " + id));
		team.setUpdatedBy(jwtProvider.getUserName());
		team.setUpdatedDate(new Date());
		Category category = categoryRepository.findById(teamDetail.getCategory())
				.orElseThrow(() -> new Exception("Category does not exist with id: " + team.getCategory()));
		team.setCategory(category);
		team.setName(teamDetail.getName());

		teamRepository.save(team);
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(team);
	}

	@Override
	public ResponseEntity<HttpStatus> deleteTeam(long id) throws Exception {
		final String methodName = "deleteTeam()";
		logger.debug("{} - Begin", methodName);
		Team team = teamRepository.findById(id).orElseThrow(() -> new Exception("Team does not exist with id: " + id));
		teamRepository.delete(team);
		logger.debug("{} - End", methodName);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Team> getTeamById(long id) throws Exception {
		final String methodName = "getTeamById()";
		logger.debug("{} - Begin", methodName);
		Team team = teamRepository.findById(id).orElseThrow(() -> new Exception("Team does not exist with id: " + id));
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(team);
	}
	
	public boolean isExist(String name) {
		List<Team> teams = teamRepository.findAll();
		for (Team team : teams) {
			if (team.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

}
