package com.oscarmartinez.socialleague.service;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.socialleague.entity.BackupLines;
import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.entity.ClubGift;
import com.oscarmartinez.socialleague.entity.Player;
import com.oscarmartinez.socialleague.entity.Team;
import com.oscarmartinez.socialleague.entity.Tournament;
import com.oscarmartinez.socialleague.entity.enums.CategoryType;
import com.oscarmartinez.socialleague.repository.IBackupLinesRepository;
import com.oscarmartinez.socialleague.repository.ICategoryRepository;
import com.oscarmartinez.socialleague.repository.IPlayerRepository;
import com.oscarmartinez.socialleague.repository.ITeamRepository;
import com.oscarmartinez.socialleague.repository.ITournamentRepository;
import com.oscarmartinez.socialleague.resource.BackUpLinesDTO;
import com.oscarmartinez.socialleague.resource.PlayerDTO;
import com.oscarmartinez.socialleague.security.JwtProvider;



@Service
@Transactional
public class PlayerServiceImp implements IPlayerService {

	private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImp.class);

	@Autowired
	private IPlayerRepository playerRepository;

	@Autowired
	private ICategoryRepository categoryRepository;

	@Autowired
	private ITeamRepository teamRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private IBackupLinesRepository backupRepository;

	@Autowired
	private ITournamentRepository tournamentRepository;

	@Override
	public List<Player> listPlayers() {
		return playerRepository.findAll();
	}

	@Override
	public ResponseEntity<HttpStatus> addPlayer(PlayerDTO player) throws Exception {
		final String methodName = "addPlayer()";
		logger.debug("{} - Begin", methodName);
		Player newPlayer = new Player();
		newPlayer.setAddedBy(jwtProvider.getUserName());
		newPlayer.setAddedDate(new Date());
		newPlayer.setAverage(player.getAverage());
		Category category = categoryRepository.findById(player.getCategory())
				.orElseThrow(() -> new Exception("Category does not exist with id: " + player.getCategory()));
		newPlayer.setCategory(category);
		newPlayer.setBirth(new SimpleDateFormat("dd/MM/yyyy").parse(player.getBirth()));
		newPlayer.setHandicap(player.getHandicap());
		newPlayer.setLastName(player.getLastName());
		newPlayer.setName(player.getName());
		newPlayer.setPhone(player.getPhone());
		newPlayer.setLastSummation(player.getLastSummation());
		newPlayer.setLinesQuantity(player.getLinesQuantity());
		newPlayer.setMaxLine(player.getMaxLine());
		newPlayer.setMaxSerie(player.getMaxSerie());
		newPlayer.setMail(player.getMail());
		newPlayer.setLineAverage(player.getLineAverage());
		Team team = teamRepository.findById(player.getTeam())
				.orElseThrow(() -> new Exception("Team does not exist with id: " + player.getTeam()));

		newPlayer.setTeam(team);

		if (isExist(newPlayer.getName(), newPlayer.getLastName()))
			throw new Exception("ALLREADY_EXIST");

		playerRepository.save(newPlayer);
		logger.debug("{} - End", methodName);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Player> editPlayer(long id, PlayerDTO playerDetail) throws Exception {
		final String methodName = "editPlayer()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));
		player.setUpdatedBy(jwtProvider.getUserName());
		player.setUpdatedDate(new Date());
		player.setAverage(playerDetail.getAverage());
		Category category = categoryRepository.findById(playerDetail.getCategory())
				.orElseThrow(() -> new Exception("Category does not exist with id: " + playerDetail.getCategory()));
		player.setCategory(category);
		player.setBirth(new SimpleDateFormat("dd/MM/yyyy").parse(playerDetail.getBirth()));
		player.setLastName(playerDetail.getLastName());
		player.setName(playerDetail.getName());
		player.setPhone(playerDetail.getPhone());
		player.setLinesQuantity(playerDetail.getLinesQuantity());
		player.setLastSummation(playerDetail.getLastSummation());
		player.setHandicap(playerDetail.getHandicap());
		player.setMaxLine(playerDetail.getMaxLine());
		player.setMaxSerie(playerDetail.getMaxSerie());
		player.setMail(playerDetail.getMail());
		player.setLineAverage(playerDetail.getLineAverage());

		Team team = teamRepository.findById(playerDetail.getTeam())
				.orElseThrow(() -> new Exception("Team does not exist with id: " + player.getTeam()));

		player.setTeam(team);

		playerRepository.save(player);
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(player);
	}

	@Override
	public ResponseEntity<HttpStatus> deletePlayer(long id) throws Exception {
		final String methodName = "deletePlayer()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));
		playerRepository.delete(player);
		logger.debug("{} - End", methodName);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Player> getPlayerById(long id) throws Exception {
		final String methodName = "getPlayerById()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(player);
	}

	@Override
	public ResponseEntity<Player> addLine(long id, List<Integer> lines) throws Exception {
		final String methodName = "addLine()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));

		List<BackupLines> currentBackups = backupRepository.findByPlayer(player);
		if (!currentBackups.isEmpty()) {
			for (BackupLines backup : currentBackups) {
				if (isLastWeek(backup.getAddedDate())) {
					backupRepository.delete(backup);
				} else {
					for (int x = 0; x < lines.size(); x++) {
						if (x == 0) {
							backup.setFirstLine(lines.get(x));
						}
						if (x == 1) {
							backup.setSecondLine(lines.get(x));
						}
						if (x == 2) {
							backup.setThirdLine(lines.get(x));
						}
					}
				}
			}

			// currentBackups.forEach(backupRepository::delete);
		} else {
			currentBackups = new ArrayList<>();
			BackupLines backupLines = new BackupLines();
			backupLines.setAddedDate(LocalDateTime.now());
			for (int x = 0; x < lines.size(); x++) {
				if (x == 0) {
					backupLines.setFirstLine(lines.get(x));
				}
				if (x == 1) {
					backupLines.setSecondLine(lines.get(x));
				}
				if (x == 2) {
					backupLines.setThirdLine(lines.get(x));
				}
			}
			backupLines.setPlayer(player);
			backupRepository.save(backupLines);
		}

		int linesQuantity = (int) lines.stream().filter(line -> line > 0).count();

		int linesSummation = lines.stream().filter(line -> line > 0).mapToInt(Integer::intValue).sum();

		int currentQuantity = player.getLinesQuantity();
		long lastSummation = player.getLastSummation();
		player.setLinesQuantity(currentQuantity + linesQuantity);
		player.setLastSummation(lastSummation + linesSummation);

		// If some line is grater than max line, change it.
		lines.stream().filter(line -> line > player.getMaxLine()).forEach(player::setMaxLine);

		double tempAverage = (double) player.getLastSummation() / (double) player.getLinesQuantity();
		double average = Math.round(tempAverage * 100) / 100;
		player.setAverage(average);

		Team team = player.getTeam();
		int currentPines = team.getPines();
		team.setPines(currentPines + linesSummation + (player.getHandicap() * linesQuantity));
		teamRepository.save(team);

		playerRepository.save(player);

		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(player);
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
	public ResponseEntity<Player> addSerie(long id, int serieValue) throws Exception {
		final String methodName = "addSerie()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));

		Tournament activeTournament = tournamentRepository.findByActive(true);

		int club1 = activeTournament.getFirstClubValue();
		int club2 = activeTournament.getSecondClubValue();
		int club3 = activeTournament.getThirdClubValue();

		if (player.getClubGift() == null) {
			player.setClubGift(new ClubGift());
		}

		if (serieValue >= club3) {
			player.getClubGift().setUpdatedDate(LocalDateTime.now());
			int currentVal = player.getClubGift().getClub3();
			double currentGift = player.getClubGift().getTotalGift();
			player.getClubGift().setClub3(currentVal + 1);
			player.getClubGift().setTotalGift(currentGift + activeTournament.getThirdClubQuota());
			playerRepository.save(player);
		} else if (serieValue >= club2) {
			player.getClubGift().setUpdatedDate(LocalDateTime.now());
			int currentVal = player.getClubGift().getClub2();
			double currentGift = player.getClubGift().getTotalGift();
			player.getClubGift().setClub2(currentVal + 1);
			player.getClubGift().setTotalGift(currentGift + activeTournament.getSecondClubQuota());
			playerRepository.save(player);
		} else if (serieValue >= club1 && CategoryType.FEMALE.equals(player.getCategory().getType())) {
			player.getClubGift().setUpdatedDate(LocalDateTime.now());
			int currentVal = player.getClubGift().getClub1();
			double currentGift = player.getClubGift().getTotalGift();
			player.getClubGift().setClub1(currentVal + 1);
			player.getClubGift().setTotalGift(currentGift + activeTournament.getFirstClubQuota());
			playerRepository.save(player);
		}

		if (serieValue > player.getMaxSerie()) {
			player.setMaxSerie(serieValue);
		} else {
			return ResponseEntity.ok(player);
		}

		playerRepository.save(player);
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(player);
	}

	public boolean isExist(String name, String lastName) {
		List<Player> players = playerRepository.findAll();
		for (Player player : players) {
			if (player.getLastName().equalsIgnoreCase(lastName) && player.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	@Override
	public List<Player> findAllByTeam(long teamId) throws Exception {
		Team team = teamRepository.findById(teamId)
				.orElseThrow(() -> new Exception("Team does not exist with id: " + teamId));

		return playerRepository.findAllByTeam(team);
	}

	@Override
	public ResponseEntity<HttpStatus> updateHandicap() throws Exception {
		final String methodName = "updateHandicap()";
		logger.debug("{} - Begin", methodName);

		List<Player> players = playerRepository.findAll();
		Tournament activeTournament = tournamentRepository.findByActive(true);

		for (Player player : players) {
			double handicapDouble = (activeTournament.getPointsForHDCP() - player.getAverage())
					* activeTournament.getAverageForHDCP();
			int handicap = handicapDouble < 0 ? 0 : (int) handicapDouble;
			player.setHandicap(handicap);

			playerRepository.save(player);
		}

		logger.debug("{} - End", methodName);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@Override
	public ResponseEntity<Player> updateSingleHandicap(long id) throws Exception {
		final String methodName = "updateSingleHandicap()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));

		Tournament activeTournament = tournamentRepository.findByActive(true);
		double handicapDouble = (activeTournament.getPointsForHDCP() - player.getAverage())
				* activeTournament.getAverageForHDCP();
		int handicap = handicapDouble < 0 ? 0 : (int) handicapDouble;
		player.setHandicap(handicap);

		playerRepository.save(player);

		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(player);
	}

	@Override
	public ResponseEntity<BackUpLinesDTO> getBackUpLines(long id) throws Exception {
		final String methodName = "getBackUpLines()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));
		List<BackupLines> backUp = backupRepository.findByPlayer(player);
		BackUpLinesDTO response = new BackUpLinesDTO();
		for (BackupLines line : backUp) {
			response.setLine1(line.getFirstLine());
			response.setLine2(line.getSecondLine());
			response.setLine3(line.getThirdLine());
		}
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<Player> editLines(long id, List<Integer> lines) throws Exception {
		final String methodName = "editLines()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));

		List<BackupLines> backUps = backupRepository.findByPlayer(player);

		int linesQuantity = (int) lines.stream().filter(line -> line > 0).count();

		int linesSummation = lines.stream().filter(line -> line > 0).mapToInt(Integer::intValue).sum();

		int totalToRemove = backUps.stream()
				.mapToInt(backup -> backup.getFirstLine() + backup.getSecondLine() + backup.getThirdLine()).sum();
		
		int lastPlayedLinesQuantity = 0;
		for(BackupLines backup: backUps) {
			if(backup.getFirstLine() > 0) {
				lastPlayedLinesQuantity++;
			}
			if(backup.getSecondLine() > 0) {
				lastPlayedLinesQuantity++;
			}
			if(backup.getThirdLine() > 0) {
				lastPlayedLinesQuantity++;
			}
		}
		
		int currentQuantity = player.getLinesQuantity();
		long lastSummation = player.getLastSummation();
		
		player.setLinesQuantity(currentQuantity - lastPlayedLinesQuantity);
		currentQuantity = player.getLinesQuantity();
		player.setLastSummation(lastSummation - totalToRemove);
		lastSummation = player.getLastSummation();
		
		player.setLinesQuantity(currentQuantity + linesQuantity);
		player.setLastSummation(lastSummation + linesSummation);

		// If some line is grater than max line, change it.
		lines.stream().filter(line -> line > player.getMaxLine()).forEach(player::setMaxLine);

		double tempAverage = (double) player.getLastSummation() / (double) player.getLinesQuantity();
		double average = Math.round(tempAverage * 100) / 100;
		player.setAverage(average);

		playerRepository.save(player);
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(player);
	}

}
