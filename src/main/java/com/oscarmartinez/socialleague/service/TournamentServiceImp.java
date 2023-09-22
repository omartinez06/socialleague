package com.oscarmartinez.socialleague.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.oscarmartinez.socialleague.entity.Tournament;
import com.oscarmartinez.socialleague.repository.ITournamentRepository;
import com.oscarmartinez.socialleague.resource.Constants;
import com.oscarmartinez.socialleague.resource.TournamentDTO;

@Service
public class TournamentServiceImp implements ITournamentService {

	private static final Logger logger = LoggerFactory.getLogger(TournamentServiceImp.class);

	@Autowired
	private ITournamentRepository tournamentRepository;

	@Override
	public ResponseEntity<HttpStatus> startNewTournament(TournamentDTO newTournament) throws Exception {
		final String methodName = "updateTournamentInformation()";
		logger.debug("{} - Begin", methodName);

		Tournament tournament = new Tournament();
		tournament.setTournamentName(newTournament.getTournamentName());
		tournament.setDedicateTo(newTournament.getDedicateTo());
		tournament.setApplyClubes(newTournament.isApplyClubes());
		tournament.setFirstClubValue(newTournament.getFirstClubValue());
		tournament.setSecondClubValue(newTournament.getSecondClubValue());
		tournament.setThirdClubValue(newTournament.getThirdClubValue());
		tournament.setFirstClubQuota(newTournament.getFirstClubQuota());
		tournament.setSecondClubQuota(newTournament.getSecondClubQuota());
		tournament.setThirdClubQuota(newTournament.getThirdClubQuota());
		tournament.setLinesAverage(newTournament.getLinesAverage());
		tournament.setActive(true);

		tournamentRepository.save(tournament);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Tournament> getActiveTournament() throws Exception {
		final String methodName = "getActiveTournament()";
		logger.debug("{} - Begin", methodName);

		Tournament tournament = tournamentRepository.findByActive(true);

		if (tournament == null)
			throw new Exception("Doesnt exist active tournament.");

		return ResponseEntity.ok(tournament);
	}

	@Override
	public ResponseEntity<Tournament> updateTournamentInformation(long id, Map<String, Object> tournamentInfo)
			throws Exception {
		final String methodName = "updateTournamentInformation()";
		logger.debug("{} - Begin", methodName);

		Tournament currentTournament = tournamentRepository.findById(id)
				.orElseThrow(() -> new Exception("Tournament does not exist with id: " + id));

		tournamentInfo.forEach((key, value) -> {
			switch (key) {
			case Constants.TOURNAMENT_NAME:
				currentTournament.setTournamentName(value.toString());
				break;
			case Constants.TOURNAMENT_DEDICATED_TO:
				currentTournament.setDedicateTo(value.toString());
				break;
			case Constants.TOURNAMENT_APPLY_CLUBES:
				currentTournament.setApplyClubes((boolean) value);
				break;
			case Constants.TOURNAMENT_FIRST_CLUB_VALUE:
				currentTournament.setFirstClubValue((int) value);
				break;
			case Constants.TOURNAMENT_SECOND_CLUB_VALUE:
				currentTournament.setSecondClubValue((int) value);
				break;
			case Constants.TOURNAMENT_THIRD_CLUB_VALUE:
				currentTournament.setThirdClubValue((int) value);
				break;
			case Constants.TOURNAMENT_FIRST_CLUB_QUOTA:
				currentTournament.setFirstClubQuota((double) value);
				break;
			case Constants.TOURNAMENT_SECOND_CLUB_QUOTA:
				currentTournament.setSecondClubQuota((double) value);
				break;
			case Constants.TOURNAMENT_THIRD_CLUB_QUOTA:
				currentTournament.setThirdClubQuota((double) value);
				break;
			case Constants.TOURNAMENT_LINES_AVERAGE:
				currentTournament.setLinesAverage((double) value);
				break;
			case Constants.TOURNAMENT_ACTIVE:
				currentTournament.setActive((boolean) value);
				break;
			default:
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Fields");
			}
		});

		tournamentRepository.save(currentTournament);

		return ResponseEntity.ok(currentTournament);
	}

}
