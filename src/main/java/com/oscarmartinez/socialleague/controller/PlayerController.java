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

import com.oscarmartinez.socialleague.entity.Player;
import com.oscarmartinez.socialleague.resource.PlayerDTO;
import com.oscarmartinez.socialleague.service.IPlayerService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/player")
public class PlayerController {

	@Autowired
	private IPlayerService playerService;

	@GetMapping
	public List<Player> getPlayers() {
		return playerService.listPlayers();
	}

	@PostMapping
	public void addPlayer(@RequestBody PlayerDTO player) throws Exception {
		playerService.addPlayer(player);
	}

	@PutMapping("{id}")
	public ResponseEntity<Player> editPlayer(@PathVariable long id, @RequestBody PlayerDTO playerDetail)
			throws Exception {
		return playerService.editPlayer(id, playerDetail);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<HttpStatus> deletePlayer(@PathVariable long id) throws Exception {
		return playerService.deletePlayer(id);
	}

	@GetMapping("{id}")
	public ResponseEntity<Player> getPlayerById(@PathVariable long id) throws Exception {
		return playerService.getPlayerById(id);
	}

}
