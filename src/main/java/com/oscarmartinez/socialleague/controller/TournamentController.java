package com.oscarmartinez.socialleague.controller;

import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oscarmartinez.socialleague.entity.Tournament;
import com.oscarmartinez.socialleague.resource.TournamentDTO;
import com.oscarmartinez.socialleague.service.ITournamentService;

import net.sf.jasperreports.engine.JRException;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

	@Autowired
	private ITournamentService tournamentService;

	@GetMapping
	public ResponseEntity<Tournament> getActiveTournament() throws Exception {
		return tournamentService.getActiveTournament();
	}

	@PostMapping
	public ResponseEntity<HttpStatus> startNewTournament(@RequestBody TournamentDTO newTournament) throws Exception {
		return tournamentService.startNewTournament(newTournament);
	}

	@PatchMapping("{id}")
	public ResponseEntity<Tournament> updateTournamentInformation(@PathVariable long id,
			@RequestBody Map<String, Object> tournamentInfo) throws Exception {
		return tournamentService.updateTournamentInformation(id, tournamentInfo);
	}

	@GetMapping("/finish/{id}")
	public ResponseEntity<Tournament> finishTournament(@PathVariable long id) throws Exception {
		return tournamentService.finishTournament(id);
	}

	@GetMapping("report/{format}")
	public String generateReport(@PathVariable String format) throws JRException, IOException {
		return tournamentService.exportReport(format);
	}

	@GetMapping("report/send")
	public ResponseEntity<?> sendEmailWithAttachment() throws MessagingException, IOException {
		return tournamentService.sendEmailWithAttachment();
	}

}
