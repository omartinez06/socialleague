package com.oscarmartinez.socialleague.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.entity.Player;
import com.oscarmartinez.socialleague.entity.Team;
import com.oscarmartinez.socialleague.entity.Tournament;
import com.oscarmartinez.socialleague.entity.enums.CategoryLevel;
import com.oscarmartinez.socialleague.entity.enums.CategoryType;
import com.oscarmartinez.socialleague.repository.ICategoryRepository;
import com.oscarmartinez.socialleague.repository.IPlayerRepository;
import com.oscarmartinez.socialleague.repository.ITeamRepository;
import com.oscarmartinez.socialleague.repository.ITournamentRepository;
import com.oscarmartinez.socialleague.resource.AverageInformation;
import com.oscarmartinez.socialleague.resource.AverageReportDTO;
import com.oscarmartinez.socialleague.resource.Constants;
import com.oscarmartinez.socialleague.resource.TournamentDTO;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Service
@Transactional
public class TournamentServiceImp implements ITournamentService {

	private static final Logger logger = LoggerFactory.getLogger(TournamentServiceImp.class);

	@Autowired
	private ITournamentRepository tournamentRepository;

	@Autowired
	private IPlayerRepository playerRepository;

	@Autowired
	private ICategoryRepository categoryRepository;

	@Autowired
	private ITeamRepository teamRepository;

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
		tournament.setPointsForHDCP(newTournament.getPointsForHDCP());
		tournament.setAverageForHDCP(newTournament.getAverageForHDCP());
		tournament.setNumberDays(newTournament.getNumberDays());
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
				currentTournament.setFirstClubQuota((int) value);
				break;
			case Constants.TOURNAMENT_SECOND_CLUB_QUOTA:
				currentTournament.setSecondClubQuota((int) value);
				break;
			case Constants.TOURNAMENT_THIRD_CLUB_QUOTA:
				currentTournament.setThirdClubQuota((int) value);
				break;
			case Constants.TOURNAMENT_LINES_AVERAGE:
				currentTournament.setLinesAverage(Double.parseDouble(value.toString()));
				break;
			case Constants.TOURNAMENT_ACTIVE:
				currentTournament.setActive((boolean) value);
				break;
			case Constants.TOURNAMENT_POINTS_FOR_HDCP:
				currentTournament.setPointsForHDCP((int) value);
				break;
			case Constants.TOURNAMENT_AVERAGE_FOR_HDCP:
				currentTournament.setAverageForHDCP(Double.parseDouble(value.toString()));
				break;
			case Constants.TOURNAMENT_NUMBER_DAYS:
				currentTournament.setNumberDays((int) value);
				break;
			default:
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Fields");
			}
		});

		tournamentRepository.save(currentTournament);

		return ResponseEntity.ok(currentTournament);
	}

	@Override
	public ResponseEntity<Tournament> finishTournament(long id) throws Exception {
		final String methodName = "finishTournament()";
		logger.debug("{} - Begin", methodName);
		Tournament tournament = tournamentRepository.findById(id)
				.orElseThrow(() -> new Exception("Tournament does not exist with id: " + id));

		if (!tournament.isActive()) {
			throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "Tournament was already finished");
		}

		tournament.setActive(false);
		tournamentRepository.save(tournament);
		endTournament();
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(tournament);
	}

	public void endTournament() {
		final String methodName = "endTournament()";
		logger.debug("{} - Begin", methodName);
		evaluateCategoryToUp();
		evaluateCategoryToDown();
		evaluateCategoryToDownTeam();
		evaluateCategoryToUpTeam();
		logger.debug("{} - End", methodName);
	}

	public void evaluateCategoryToDown() {
		final String methodName = "evaluateCategoryToDown()";
		logger.debug("{} - Begin", methodName);
		List<Category> categories = categoryRepository.findAll();

		for (Category category : categories) {
			if (category.getLevel().equals(CategoryLevel.D) || category.getType().equals(CategoryType.TEAM))
				continue;

			List<Player> players = playerRepository.findByCategory(category);
			Collections.sort(players, new Comparator<Player>() {
				@Override
				public int compare(Player player1, Player player2) {
					return Double.compare(player1.getAverage(), player2.getAverage());
				}
			});

			// Get 3 people with the lowest average
			List<Player> playersLowestAverage = players.subList(0, Math.min(3, players.size()));

			for (Player player : playersLowestAverage) {
				player.setCategory(getDownCategory(player.getCategory()));
				playerRepository.save(player);
			}
		}
		logger.debug("{} - End", methodName);
	}

	public Category getDownCategory(Category category) {
		switch (category.getLevel()) {
		case A:
			return categoryRepository.findByLevelAndType(CategoryLevel.B, category.getType());
		case B:
			return categoryRepository.findByLevelAndType(CategoryLevel.C, category.getType());
		case C:
			return categoryRepository.findByLevelAndType(CategoryLevel.D, category.getType());
		default:
			return category;
		}
	}

	public Category getUpCategory(Category category) {
		switch (category.getLevel()) {
		case B:
			return categoryRepository.findByLevelAndType(CategoryLevel.A, category.getType());
		case C:
			return categoryRepository.findByLevelAndType(CategoryLevel.B, category.getType());
		default:
			return category;
		}
	}

	public void evaluateCategoryToUp() {
		final String methodName = "evaluateCategoryToUp()";
		logger.debug("{} - Begin", methodName);
		List<Category> categories = categoryRepository.findAll();
		List<Player> players = playerRepository.findAll();
		if (!players.isEmpty()) {
			for (Player player : players) {
				if (player.getCategory().getLevel().equals(CategoryLevel.A))
					continue;

				for (Category category : categories) {
					if (category.getType().equals(player.getCategory().getType())
							&& category.getMinAverage() >= player.getAverage()
							&& category.getMaxAverage() <= player.getAverage()) {
						logger.debug("{} - player {} goes to category {}", methodName, player.getName(),
								category.getLevel());
						player.setCategory(category);
					}
				}
				playerRepository.save(player);
			}
		}
		logger.debug("{} - End", methodName);
	}

	public void evaluateCategoryToDownTeam() {
		final String methodName = "evaluateCategoryToDownTeam()";
		logger.debug("{} - Begin", methodName);
		List<Category> categories = categoryRepository.findAll();

		for (Category category : categories) {
			if (category.getLevel().equals(CategoryLevel.C) || !category.getType().equals(CategoryType.TEAM))
				continue;

			List<Team> teams = teamRepository.findByCategory(category);
			Collections.sort(teams, new Comparator<Team>() {
				@Override
				public int compare(Team team1, Team team2) {
					return Double.compare(team1.getPoints(), team2.getPoints());
				}
			});

			// Get 2 teams with the lowest average
			List<Team> teamsLowestPoints = teams.subList(0, Math.min(2, teams.size()));

			for (Team team : teamsLowestPoints) {
				team.setCategory(getDownCategory(team.getCategory()));
				teamRepository.save(team);
			}
		}

		logger.debug("{} - End", methodName);
	}

	public void evaluateCategoryToUpTeam() {
		final String methodName = "evaluateCategoryToUpTeam()";
		logger.debug("{} - Begin", methodName);
		List<Category> categories = categoryRepository.findAll();

		for (Category category : categories) {
			if (category.getLevel().equals(CategoryLevel.A) || !category.getType().equals(CategoryType.TEAM))
				continue;

			List<Team> teams = teamRepository.findByCategory(category);

			// sort desc list by point using Comparator
			Collections.sort(teams, Comparator.comparingInt(Team::getPoints).reversed());

			// Get 2 teams with the highest average
			List<Team> teamsHighestPoints = teams.subList(0, Math.min(2, teams.size()));

			for (Team team : teamsHighestPoints) {
				team.setCategory(getUpCategory(team.getCategory()));
				teamRepository.save(team);
			}
		}

		logger.debug("{} - End", methodName);
	}

	@Override
	public String exportReport(String reportFormat) throws JRException, IOException {
		Tournament tournament = tournamentRepository.findByActive(true);
		List<Category> categories = categoryRepository.findAll();

		List<AverageInformation> information = new ArrayList<>();

		Collection<AverageReportDTO> collection = new ArrayList<>();
		List<AverageReportDTO> reportAverage = new ArrayList<>();
		for (Category category : categories) {
			int position = 1;
			List<Player> players = playerRepository.findByCategory(category);
			if (!players.isEmpty()) {
				for (Player player : players) {
					AverageInformation dto = new AverageInformation();
					dto.setLinesAverage(player.getLineAverage());
					dto.setLinesQuantity(player.getLinesQuantity());
					dto.setName(player.getName() + " " + player.getLastName());
					dto.setPines((int) player.getLastSummation());
					dto.setAverage(player.getAverage());
					dto.setPosition(position++);
					information.add(dto);
				}
				AverageReportDTO average = new AverageReportDTO();
				average.setAverages(information);
				average.setCategory(category.getLevel().toString());
				average.setGender(category.getType().toString());
				reportAverage.add(average);
			}
		}

		collection.addAll(reportAverage);

		InputStream jrxmlInputStream = new ClassPathResource("Promedios.jrxml").getInputStream();
		JasperDesign design = JRXmlLoader.load(jrxmlInputStream);
		JasperReport jasperReport = JasperCompileManager.compileReport(design);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("dedicateTo", tournament.getDedicateTo());
		parameters.put("tournamentName", tournament.getTournamentName());

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
				new JRBeanCollectionDataSource(collection));

		if (reportFormat.equalsIgnoreCase("html")) {
			JasperExportManager.exportReportToHtmlFile(jasperPrint, "average.html");
		}

		if (reportFormat.equalsIgnoreCase("pdf")) {
			JasperExportManager.exportReportToPdfFile(jasperPrint, "average.pdf");
		}
		return "Reports generated successfully";
	}

}
