package com.oscarmartinez.socialleague.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.socialleague.entity.BackupLines;
import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.entity.Player;
import com.oscarmartinez.socialleague.entity.Team;
import com.oscarmartinez.socialleague.repository.IBackupLinesRepository;
import com.oscarmartinez.socialleague.repository.ICategoryRepository;
import com.oscarmartinez.socialleague.repository.IPlayerRepository;
import com.oscarmartinez.socialleague.repository.ITeamRepository;
import com.oscarmartinez.socialleague.resource.BackUpLinesDTO;
import com.oscarmartinez.socialleague.resource.BackUpPointsDTO;
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
	private IPlayerRepository playerRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private IBackupLinesRepository backupRepository;

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
		newTeam.setPoints(team.getPoints());
		newTeam.setPines(team.getPines());
		newTeam.setAddedDate(new Date());
		newTeam.setAddedBy(jwtProvider.getUserName());
		Category category = categoryRepository.findById(team.getCategory())
				.orElseThrow(() -> new Exception("Category does not exist with id: " + team.getCategory()));
		newTeam.setCategory(category);

		if (team.getCaptain() != 0) {
			Player player = playerRepository.findById(team.getCaptain())
					.orElseThrow(() -> new Exception("Player does not exist with id: " + team.getCaptain()));
			newTeam.setCaptain(player);
		}

		if (isExist(team.getName()))
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
		team.setPoints(teamDetail.getPoints());
		team.setPines(teamDetail.getPines());

		if (teamDetail.getCaptain() != 0) {
			Player player = playerRepository.findById(teamDetail.getCaptain())
					.orElseThrow(() -> new Exception("Player does not exist with id: " + teamDetail.getCaptain()));
			team.setCaptain(player);
		}

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

	@Override
	public ResponseEntity<Team> addPoints(long id, int points) throws Exception {
		final String methodName = "addPoints()";
		logger.debug("{} - Begin", methodName);
		Team team = teamRepository.findById(id).orElseThrow(() -> new Exception("Team does not exist with id: " + id));

		List<BackupLines> currentBackups = backupRepository.findByTeam(team);

		if (!currentBackups.isEmpty()) {
			for (BackupLines backup : currentBackups) {
				if (isLastWeek(backup.getAddedDate())) {
					backupRepository.delete(backup);
				} else {
					backup.setFirstLine(points);
				}
			}
		} else {
			currentBackups = new ArrayList<>();
			BackupLines backupPoints = new BackupLines();
			backupPoints.setAddedDate(LocalDateTime.now());
			backupPoints.setFirstLine(points);
			backupPoints.setTeam(team);
			backupRepository.save(backupPoints);
		}

		int currentPoints = team.getPoints();
		team.setPoints(currentPoints + points);

		teamRepository.save(team);
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(team);
	}

	private boolean isLastWeek(LocalDateTime fecha) {
		LocalDateTime fechaActual = LocalDateTime.now();

		// Obtener el primer día de la semana actual (lunes)
		LocalDateTime primerDiaSemanaActual = fechaActual.with(DayOfWeek.MONDAY);

		// Obtener el primer día de la semana pasada
		LocalDateTime primerDiaSemanaPasada = primerDiaSemanaActual.minus(1, ChronoUnit.WEEKS);

		// Obtener el último día de la semana pasada (domingo)
		LocalDateTime ultimoDiaSemanaPasada = primerDiaSemanaActual.minus(1, ChronoUnit.SECONDS);

		return fecha.isAfter(primerDiaSemanaPasada) && fecha.isBefore(ultimoDiaSemanaPasada);
	}

	@Override
	public ResponseEntity<BackUpPointsDTO> getBackUpPoints(long id) throws Exception {
		final String methodName = "getBackUpPoints()";
		logger.debug("{} - Begin", methodName);
		Team team = teamRepository.findById(id).orElseThrow(() -> new Exception("Team does not exist with id: " + id));
		List<BackupLines> currentBackup = backupRepository.findByTeam(team);
		BackUpPointsDTO response = new BackUpPointsDTO();
		for (BackupLines points : currentBackup) {
			response.setPoints(points.getFirstLine());
		}
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<Team> editPoints(long id, int points) throws Exception {
		final String methodName = "editPoints()";
		logger.debug("{} - Begin", methodName);
		Team team = teamRepository.findById(id).orElseThrow(() -> new Exception("Team does not exist with id: " + id));

		List<BackupLines> currentBackup = backupRepository.findByTeam(team);

		int pointsToRemove = currentBackup.stream().filter(backup -> backup.getFirstLine() > 0)
				.mapToInt(BackupLines::getFirstLine).sum();

		int currentPoints = team.getPoints();
		int tempNewPoints = currentPoints - pointsToRemove;
		team.setPoints(tempNewPoints + points);
		
		teamRepository.save(team);
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(team);
	}

}
