package com.oscarmartinez.socialleague.service;

import java.text.SimpleDateFormat;
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
import com.oscarmartinez.socialleague.entity.ClubGift;
import com.oscarmartinez.socialleague.entity.Player;
import com.oscarmartinez.socialleague.entity.Team;
import com.oscarmartinez.socialleague.entity.Tournament;
import com.oscarmartinez.socialleague.entity.enums.CategoryLevel;
import com.oscarmartinez.socialleague.entity.enums.CategoryType;
import com.oscarmartinez.socialleague.repository.IBackupLinesRepository;
import com.oscarmartinez.socialleague.repository.ICategoryRepository;
import com.oscarmartinez.socialleague.repository.IClubGiftRepository;
import com.oscarmartinez.socialleague.repository.IPlayerRepository;
import com.oscarmartinez.socialleague.repository.ITeamRepository;
import com.oscarmartinez.socialleague.repository.ITournamentRepository;
import com.oscarmartinez.socialleague.resource.BackUpLinesDTO;
import com.oscarmartinez.socialleague.resource.PlayerDTO;
import com.oscarmartinez.socialleague.security.JwtProvider;

@Service
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

	@Autowired
	private IClubGiftRepository clubGiftRepository;

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
		newPlayer.setSendReportMail(player.isSendMail());
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
		player.setSendReportMail(playerDetail.isSendMail());

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

		BackupLines currentBackup = backupRepository.findByPlayer(player);

		int serie = 0;
		if (currentBackup != null) {
			for (int x = 0; x < lines.size(); x++) {
				serie = serie + lines.get(x);
				if (x == 0) {
					currentBackup.setFirstLine(lines.get(x));
				}
				if (x == 1) {
					currentBackup.setSecondLine(lines.get(x));
				}
				if (x == 2) {
					currentBackup.setThirdLine(lines.get(x));
				}
			}
			currentBackup.setPlayer(player);
			backupRepository.save(currentBackup);
		} else {
			BackupLines backupLines = new BackupLines();
			backupLines.setAddedDate(LocalDateTime.now());
			for (int x = 0; x < lines.size(); x++) {
				serie = serie + lines.get(x);
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

		if (linesSummation > player.getMaxSerie()) {
			player.setMaxSerie(linesSummation);
		}

		double tempAverage = (double) player.getLastSummation() / (double) player.getLinesQuantity();
		double average = Math.round(tempAverage * 100) / 100;
		player.setAverage(average);

		/* caculate gift serie */
		Tournament activeTournament = tournamentRepository.findByActive(true);

		if (activeTournament.isApplyClubes()) {
			int club1 = activeTournament.getFirstClubValue();
			int club2 = activeTournament.getSecondClubValue();
			int club3 = activeTournament.getThirdClubValue();

			ClubGift club = clubGiftRepository.findByPlayerId(player.getId());

			if (club == null) {
				club = new ClubGift();
				club.setPlayerId(player.getId());
			}

			if (club1 > 0 && serie >= club1) {
				updateClubGift(club, 1, activeTournament.getFirstClubQuota());
				club.setUpdatedDate(LocalDateTime.now());
				clubGiftRepository.save(club);
			} else if (club2 > 0 && serie >= club2) {
				updateClubGift(club, 2, activeTournament.getSecondClubQuota());
				club.setUpdatedDate(LocalDateTime.now());
				clubGiftRepository.save(club);
			} else if (club3 > 0 && serie >= club3 && CategoryType.FEMALE.equals(player.getCategory().getType())) {
				updateClubGift(club, 3, activeTournament.getThirdClubQuota());
				club.setUpdatedDate(LocalDateTime.now());
				clubGiftRepository.save(club);
			}
		}
		/*--------------------*/

		Team team = player.getTeam();
		int currentPines = team.getPines();
		team.setPines(currentPines + linesSummation + (player.getHandicap() * linesQuantity));
		teamRepository.save(team);

		player.setLineAverage((player.getLinesQuantity() * 100) / (activeTournament.getNumberDays() * 3));

		// If is new player set category
		if (player.getLinesQuantity() >= 9 && player.getCategory().getLevel().equals(CategoryLevel.N)) {
			List<Category> categories = categoryRepository.findAll();
			for (Category category : categories) {
				if (player.getAverage() >= category.getMinAverage()
						&& player.getAverage() <= category.getMaxAverage()) {
					player.setCategory(category);
				}
			}
		}

		playerRepository.save(player);

		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(player);
	}

	private void updateClubGift(ClubGift club, int clubNumber, double quota) {
		int currentVal;
		switch (clubNumber) {
		case 1:
			currentVal = club.getClub1();
			break;
		case 2:
			currentVal = club.getClub2();
			break;
		case 3:
			currentVal = club.getClub3();
			break;
		default:
			throw new IllegalArgumentException("Invalid club number");
		}

		double currentGift = club.getTotalGift();

		switch (clubNumber) {
		case 1:
			club.setClub1(currentVal + 1);
			club.setTotalGift(currentGift + quota);
			break;
		case 2:
			club.setClub2(currentVal + 1);
			club.setTotalGift(currentGift + quota);
			break;
		case 3:
			club.setClub3(currentVal + 1);
			club.setTotalGift(currentGift + quota);
			break;
		default:
			throw new IllegalArgumentException("Invalid club number");
		}
	}

	public boolean isExist(String name, String lastName) {
		List<Player> players = playerRepository.findByNameAndLastName(name, lastName);
		return !players.isEmpty();
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

			if (player.getLinesQuantity() < 9) {
				player.setAverage(player.getLastSummation() / player.getLinesQuantity());
			} else {
				double handicapDouble = (activeTournament.getPointsForHDCP() - player.getAverage())
						* activeTournament.getAverageForHDCP();
				int handicap = handicapDouble < 0 ? 0 : (int) handicapDouble;

				if (handicap < activeTournament.getMinHDCP()) {
					player.setHandicap(0);
				} else if (handicap > activeTournament.getMaxHDCP()) {
					player.setHandicap(activeTournament.getMaxHDCP());
				} else {
					player.setHandicap(handicap);
				}

				player.setAverage(player.getLastSummation() / player.getLinesQuantity());
			}

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

		/*
		 * if(player.getLinesQuantity() < 9) throw new Exception("INSUFICIENT_LINES");
		 */

		Tournament activeTournament = tournamentRepository.findByActive(true);
		double handicapDouble = (activeTournament.getPointsForHDCP() - player.getAverage())
				* activeTournament.getAverageForHDCP();
		int handicap = handicapDouble < 0 ? 0 : (int) handicapDouble;

		if (handicap < activeTournament.getMinHDCP()) {
			player.setHandicap(0);
		} else if (handicap > activeTournament.getMaxHDCP()) {
			player.setHandicap(activeTournament.getMaxHDCP());
		} else {
			player.setHandicap(handicap);
		}

		player.setAverage(player.getLastSummation() / player.getLinesQuantity());

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
		BackupLines backUp = backupRepository.findByPlayer(player);
		BackUpLinesDTO response = new BackUpLinesDTO();
		response.setLine1(backUp.getFirstLine());
		response.setLine2(backUp.getSecondLine());
		response.setLine3(backUp.getThirdLine());
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<Player> editLines(long id, List<Integer> lines) throws Exception {
		final String methodName = "editLines()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));
		// VOY AQUI TENGO QUE VALIDAR EL CAMBIO DE LINEAS PARA EMPAREJAR PROMEDIOS Y
		// BACKUP
		BackupLines backUp = backupRepository.findByPlayer(player);

		int linesQuantity = (int) lines.stream().filter(line -> line > 0).count();

		int linesSummation = lines.stream().filter(line -> line > 0).mapToInt(Integer::intValue).sum();

		int totalToRemove = backUp.getFirstLine() + backUp.getSecondLine() + backUp.getThirdLine();

		int lastPlayedLinesQuantity = 0;
		if (backUp.getFirstLine() > 0) {
			lastPlayedLinesQuantity++;
		}
		if (backUp.getSecondLine() > 0) {
			lastPlayedLinesQuantity++;
		}
		if (backUp.getThirdLine() > 0) {
			lastPlayedLinesQuantity++;
		}

		backUp.setFirstLine(0);
		backUp.setSecondLine(0);
		backUp.setThirdLine(0);

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

		for (int x = 0; x < lines.size(); x++) {
			if (x == 0) {
				backUp.setFirstLine(lines.get(x));
			}
			if (x == 1) {
				backUp.setSecondLine(lines.get(x));
			}
			if (x == 2) {
				backUp.setThirdLine(lines.get(x));
			}
		}

		backupRepository.save(backUp);

		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(player);
	}

}
