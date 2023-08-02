package com.oscarmartinez.socialleague.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oscarmartinez.socialleague.entity.Team;
import com.oscarmartinez.socialleague.resource.TeamDTO;
import com.oscarmartinez.socialleague.service.ITeamService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/team")
public class TeamController {

	@Autowired
	private ITeamService teamService;

	@GetMapping
	public List<Team> getTeams() {
		return teamService.listTeams();
	}

	@PostMapping
	public void addTeam(@RequestBody TeamDTO team) throws Exception {
		teamService.addTeam(team);
	}

	@PutMapping("{id}")
	public ResponseEntity<Team> editTeam(@PathVariable long id, @RequestBody TeamDTO teamDetail) throws Exception {
		return teamService.editTeam(id, teamDetail);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<HttpStatus> deleteTeam(@PathVariable long id) throws Exception {
		return teamService.deleteTeam(id);
	}

	@GetMapping("{id}")
	public ResponseEntity<Team> getTeamById(@PathVariable long id) throws Exception {
		return teamService.getTeamById(id);
	}

}
