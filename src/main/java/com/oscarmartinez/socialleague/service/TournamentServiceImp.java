package com.oscarmartinez.socialleague.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
import com.oscarmartinez.socialleague.resource.Constants;
import com.oscarmartinez.socialleague.resource.ReportDTO;
import com.oscarmartinez.socialleague.resource.ReportHDCPDTO;
import com.oscarmartinez.socialleague.resource.ReportInformation;
import com.oscarmartinez.socialleague.resource.ReportInformationHDCP;
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
public class TournamentServiceImp implements ITournamentService {

	private static final Logger logger = LoggerFactory.getLogger(TournamentServiceImp.class);

	@Value("${mail.sender.username}")
	private String senderUsername;

	@Value("${mail.sender.password}")
	private String senderPassword;

	@Value("${mail.sender.body}")
	private String mailBody;

	@Autowired
	private ITournamentRepository tournamentRepository;

	@Autowired
	private IPlayerRepository playerRepository;

	@Autowired
	private ICategoryRepository categoryRepository;

	@Autowired
	private ITeamRepository teamRepository;

	@Autowired
	private IClubGiftRepository giftRepository;

	@Autowired
	private IBackupLinesRepository backUpRepository;

	private List<Player> modifiedPlayers = new ArrayList<>();

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
		tournament.setDay(0);
		tournament.setMinHDCP(newTournament.getMinHDCP());
		tournament.setMaxHDCP(newTournament.getMaxHDCP());

		tournamentRepository.save(tournament);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Tournament> getActiveTournament() throws Exception {
		final String methodName = "getActiveTournament()";
		logger.debug("{} - Begin", methodName);

		Tournament tournament = tournamentRepository.findByActive(true);

		if (tournament == null)
			return ResponseEntity.ok(new Tournament());

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
			case Constants.TOURNAMENT_MIN_HDCP:
				currentTournament.setMinHDCP((int) value);
				break;
			case Constants.TOURNAMENT_MAX_HDCP:
				currentTournament.setMaxHDCP((int) value);
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

		giftRepository.deleteAll();

		for (Player player : playerRepository.findAll()) {
			player.setAverage(0);
			player.setLastSummation(0);
			player.setLineAverage(0D);
			player.setLinesQuantity(0);
			player.setMaxLine(0);
			player.setMaxSerie(0);
			playerRepository.save(player);
		}

		backUpRepository.deleteAll();

		for (Team team : teamRepository.findAll()) {
			team.setPines(0);
			team.setPoints(0);
			teamRepository.save(team);
		}

		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(tournament);
	}

	public void endTournament() {
		final String methodName = "endTournament()";
		logger.debug("{} - Begin", methodName);
		evaluateCategoryToChangePlayer();
		evaluateCategoryToDownTeam();
		evaluateCategoryToUpTeam();
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

	public void evaluateCategoryToChangePlayer() {
		final String methodName = "evaluateCategoryToUp()";
		logger.debug("{} - Begin", methodName);
		List<Category> categories = categoryRepository.findAll();
		List<Player> players = playerRepository.findAll();

		for (Player player : players) {
			if (player.getCategory().getLevel().equals(CategoryLevel.A))
				continue;

			if (player.getAverage() >= player.getCategory().getMinAverage()
					&& player.getAverage() <= player.getCategory().getMaxAverage())
				continue;

			for (Category category : categories) {
				if (!category.getType().equals(player.getCategory().getType()))
					continue;

				if (player.getAverage() >= category.getMinAverage()
						&& player.getAverage() <= category.getMaxAverage()) {
					logger.debug("{} - player {} goes to category {}", methodName,
							player.getName() + " " + player.getLastName(), category.getLevel());
					player.setCategory(category);
					player.setUpdatedBy("SYSTEM");
					player.setUpdatedDate(LocalDateTime.now());
					playerRepository.save(player);
					modifiedPlayers.add(player);
				}
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

	public void resetAllPlayers() {
		final String methodName = "resetAllPlayers()";
		logger.debug("{} - Begin", methodName);

		List<Player> players = playerRepository.findAll();

		for (Player player : players) {
			player.setLastLines(null);
			player.setLastSummation(0);
			player.setLineAverage(0D);
			player.setLinesQuantity(0);
			player.setMaxLine(0);
			player.setMaxSerie(0);
		}
	}

	@Override
	public String exportReport(String reportFormat) throws JRException, IOException {
		Tournament tournament = tournamentRepository.findByActive(true);

		Collection<ReportDTO> collectionAverage = getAverageReport();
		Collection<ReportDTO> collectionPines = getBiggestLinesReport();
		Collection<ReportDTO> collectionSeries = getSerieReport();
		Collection<ReportDTO> collectionTeamPoints = getTeamPointsReport();
		Collection<ReportDTO> collectionTeamPines = getTeamPinesReport();
		Collection<ReportHDCPDTO> collectionHdcp = getHDCPReport();

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("dedicateTo", tournament != null ? tournament.getDedicateTo() : "");
		parameters.put("tournamentName", tournament != null ? tournament.getTournamentName() : "");

		InputStream jrxmlInputStreamAverage = new ClassPathResource("Promedios.jrxml").getInputStream();
		JasperDesign designAverage = JRXmlLoader.load(jrxmlInputStreamAverage);
		JasperReport jasperReportAverage = JasperCompileManager.compileReport(designAverage);
		JasperPrint jasperPrintAverage = JasperFillManager.fillReport(jasperReportAverage, parameters,
				new JRBeanCollectionDataSource(collectionAverage));

		InputStream jrxmlInputStreamPines = new ClassPathResource("LineasAltas.jrxml").getInputStream();
		JasperDesign designPines = JRXmlLoader.load(jrxmlInputStreamPines);
		JasperReport jasperReportPines = JasperCompileManager.compileReport(designPines);
		JasperPrint jasperPrintPines = JasperFillManager.fillReport(jasperReportPines, parameters,
				new JRBeanCollectionDataSource(collectionPines));

		InputStream jrxmlInputStreamSeries = new ClassPathResource("Series.jrxml").getInputStream();
		JasperDesign designSeries = JRXmlLoader.load(jrxmlInputStreamSeries);
		JasperReport jasperReportSeries = JasperCompileManager.compileReport(designSeries);
		JasperPrint jasperPrintSeries = JasperFillManager.fillReport(jasperReportSeries, parameters,
				new JRBeanCollectionDataSource(collectionSeries));

		InputStream jrxmlInputStreamTeamPoints = new ClassPathResource("TeamPoints.jrxml").getInputStream();
		JasperDesign designTeamPoints = JRXmlLoader.load(jrxmlInputStreamTeamPoints);
		JasperReport jasperReportTeamPoints = JasperCompileManager.compileReport(designTeamPoints);
		JasperPrint jasperPrintTeamPoints = JasperFillManager.fillReport(jasperReportTeamPoints, parameters,
				new JRBeanCollectionDataSource(collectionTeamPoints));

		InputStream jrxmlInputStreamTeamPines = new ClassPathResource("TeamPines.jrxml").getInputStream();
		JasperDesign designTeamPines = JRXmlLoader.load(jrxmlInputStreamTeamPines);
		JasperReport jasperReportTeamPines = JasperCompileManager.compileReport(designTeamPines);
		JasperPrint jasperPrintTeamPines = JasperFillManager.fillReport(jasperReportTeamPines, parameters,
				new JRBeanCollectionDataSource(collectionTeamPines));

		InputStream jrxmlInputStreamHdcp = new ClassPathResource("Hdcp.jrxml").getInputStream();
		JasperDesign designHdcp = JRXmlLoader.load(jrxmlInputStreamHdcp);
		JasperReport jasperReportHdcp = JasperCompileManager.compileReport(designHdcp);
		JasperPrint jasperPrintHdcp = JasperFillManager.fillReport(jasperReportHdcp, parameters,
				new JRBeanCollectionDataSource(collectionHdcp));

		if (reportFormat.equalsIgnoreCase("html")) {
			JasperExportManager.exportReportToHtmlFile(jasperPrintAverage, "promedios.html");
			JasperExportManager.exportReportToHtmlFile(jasperPrintPines, "linea_alta.html");
			JasperExportManager.exportReportToHtmlFile(jasperPrintSeries, "serie_alta.html");
			JasperExportManager.exportReportToHtmlFile(jasperPrintTeamPoints, "puntos_equipos.html");
			JasperExportManager.exportReportToHtmlFile(jasperPrintTeamPines, "pines_equipos.html");
			JasperExportManager.exportReportToHtmlFile(jasperPrintHdcp, "hdcp.html");
		}

		if (reportFormat.equalsIgnoreCase("pdf")) {
			JasperExportManager.exportReportToPdfFile(jasperPrintAverage, "promedios.pdf");
			JasperExportManager.exportReportToPdfFile(jasperPrintPines, "linea_alta.pdf");
			JasperExportManager.exportReportToPdfFile(jasperPrintSeries, "serie_alta.pdf");
			JasperExportManager.exportReportToPdfFile(jasperPrintTeamPoints, "puntos_equipos.pdf");
			JasperExportManager.exportReportToPdfFile(jasperPrintTeamPines, "pines_equipos.pdf");
			JasperExportManager.exportReportToPdfFile(jasperPrintHdcp, "hdcp.pdf");
		}
		return "Reports generated successfully";
	}

	public Collection<ReportDTO> getSerieReport() {
		List<Category> categories = categoryRepository.findAll();
		Tournament tournament = tournamentRepository.findByActive(true);
		List<ReportInformation> information = new ArrayList<>();
		Collection<ReportDTO> collection = new ArrayList<>();
		List<ReportDTO> reportAverage = new ArrayList<>();
		for (Category category : categories) {
			information = new ArrayList<>();
			List<Player> players = playerRepository.findByCategory(category);
			if (!players.isEmpty()) {
				for (Player player : players) {
					if(BLIND.equals(player.getName()))
						continue;
					/*if (tournament != null && player.getLineAverage() < tournament.getLinesAverage())
						continue;*/

					ReportInformation dto = new ReportInformation();
					dto.setName(player.getName() + " " + player.getLastName());
					dto.setPines(player.getMaxSerie());
					dto.setPosition(0);
					dto.setTittleCategory("CATEGORIA " + category.getLevel() + ", RAMA "
							+ (category.getType().equals(CategoryType.MALE) ? "MASCULINA" : "FEMENINA"));
					information.add(dto);
				}
				ReportDTO average = new ReportDTO();
				average.setAverages(sortArrayByPines(information));
				average.setCategory(category.getLevel().toString());
				average.setGender(category.getType().toString());
				reportAverage.add(average);
			}
		}

		collection.addAll(reportAverage);
		return collection;
	}

	public Collection<ReportDTO> getBiggestLinesReport() {
		List<Category> categories = categoryRepository.findAll();
		Tournament tournament = tournamentRepository.findByActive(true);
		List<ReportInformation> information = new ArrayList<>();
		Collection<ReportDTO> collection = new ArrayList<>();
		List<ReportDTO> reportAverage = new ArrayList<>();
		for (Category category : categories) {
			information = new ArrayList<>();
			List<Player> players = playerRepository.findByCategory(category);
			if (!players.isEmpty()) {
				for (Player player : players) {
					if(BLIND.equals(player.getName()))
						continue;
					/*if (tournament != null && player.getLineAverage() < tournament.getLinesAverage())
						continue;*/

					ReportInformation dto = new ReportInformation();
					dto.setName(player.getName() + " " + player.getLastName());
					dto.setPines(player.getMaxLine());
					dto.setPosition(0);
					dto.setTittleCategory("CATEGORIA " + category.getLevel() + ", RAMA "
							+ (category.getType().equals(CategoryType.MALE) ? "MASCULINA" : "FEMENINA"));
					information.add(dto);
				}
				ReportDTO average = new ReportDTO();
				average.setAverages(sortArrayByPines(information));
				average.setCategory(category.getLevel().toString());
				average.setGender(category.getType().toString());
				reportAverage.add(average);
			}
		}

		collection.addAll(reportAverage);
		return collection;
	}
	
	protected static final String BLIND = "BLIND";

	public Collection<ReportDTO> getAverageReport() {
		List<Category> categories = categoryRepository.findAll();
		Tournament tournament = tournamentRepository.findByActive(true);
		List<ReportInformation> information = new ArrayList<>();
		Collection<ReportDTO> collection = new ArrayList<>();
		List<ReportDTO> reportAverage = new ArrayList<>();
		for (Category category : categories) {
			if (CategoryLevel.N.equals(category.getLevel()))
				continue;

			information = new ArrayList<>();
			List<Player> players = playerRepository.findByCategory(category);
			if (!players.isEmpty()) {
				for (Player player : players) {
					if(BLIND.equals(player.getName()))
						continue;
					/*if (tournament != null && player.getLineAverage() < tournament.getLinesAverage())
						continue;*/

					ReportInformation dto = new ReportInformation();
					dto.setLinesAverage(player.getLineAverage());
					dto.setLinesQuantity(player.getLinesQuantity());
					dto.setName(player.getName() + " " + player.getLastName());
					dto.setPines((int) player.getLastSummation());
					dto.setAverage(player.getAverage());
					dto.setPosition(0);
					dto.setTittleCategory("CATEGORIA " + category.getLevel() + ", RAMA "
							+ (category.getType().equals(CategoryType.MALE) ? "MASCULINA" : "FEMENINA"));
					information.add(dto);
				}
				ReportDTO average = new ReportDTO();
				average.setAverages(sortArrayByAverage(information));
				average.setCategory(category.getLevel().toString());
				average.setGender(category.getType().toString());
				reportAverage.add(average);
			}
		}

		collection.addAll(reportAverage);
		return collection;
	}

	public Collection<ReportDTO> getTeamPointsReport() {
		List<Category> categories = categoryRepository.findByType(CategoryType.TEAM);
		List<ReportInformation> information = new ArrayList<>();
		Collection<ReportDTO> collection = new ArrayList<>();
		List<ReportDTO> reportTeamPoints = new ArrayList<>();
		for (Category category : categories) {
			information = new ArrayList<>();
			List<Team> teams = teamRepository.findByCategory(category);
			if (!teams.isEmpty()) {
				for (Team team : teams) {
					System.out.println(team);
					ReportInformation dto = new ReportInformation();
					dto.setName(team.getName());
					dto.setPoints(team.getPoints());
					dto.setPosition(0);
					dto.setTittleCategory("CATEGORIA " + category.getLevel());
					information.add(dto);
				}
				ReportDTO teamPoints = new ReportDTO();
				teamPoints.setAverages(sortArrayByPoints(information));
				teamPoints.setCategory(category.getLevel().toString());
				teamPoints.setGender(category.getType().toString());
				reportTeamPoints.add(teamPoints);
			}
		}

		collection.addAll(reportTeamPoints);
		return collection;
	}

	public Collection<ReportDTO> getTeamPinesReport() {
		List<Category> categories = categoryRepository.findByType(CategoryType.TEAM);
		List<ReportInformation> information = new ArrayList<>();
		Collection<ReportDTO> collection = new ArrayList<>();
		List<ReportDTO> reportTeamPoines = new ArrayList<>();
		for (Category category : categories) {
			information = new ArrayList<>();
			List<Team> teams = teamRepository.findByCategory(category);
			if (!teams.isEmpty()) {
				for (Team team : teams) {
					System.out.println(team);
					ReportInformation dto = new ReportInformation();
					dto.setName(team.getName());
					dto.setPines(team.getPines());
					dto.setPosition(0);
					dto.setTittleCategory("CATEGORIA " + category.getLevel());
					information.add(dto);
				}
				ReportDTO teamPoints = new ReportDTO();
				teamPoints.setAverages(sortArrayByPoints(information));
				teamPoints.setCategory(category.getLevel().toString());
				teamPoints.setGender(category.getType().toString());
				reportTeamPoines.add(teamPoints);
			}
		}

		collection.addAll(reportTeamPoines);
		return collection;
	}

	public List<ReportInformation> sortArrayByPines(List<ReportInformation> currentArray) {
		Collections.sort(currentArray, new Comparator<ReportInformation>() {
			@Override
			public int compare(ReportInformation info1, ReportInformation info2) {
				return Integer.compare(info2.getPines(), info1.getPines());
			}
		});

		int position = 1;
		for (ReportInformation information : currentArray) {
			information.setPosition(position++);
		}
		return currentArray;
	}

	public List<ReportInformation> sortArrayByAverage(List<ReportInformation> currentArray) {
		Collections.sort(currentArray, new Comparator<ReportInformation>() {
			@Override
			public int compare(ReportInformation info1, ReportInformation info2) {
				return Double.compare(info2.getAverage(), info1.getAverage());
			}
		});

		int position = 1;
		for (ReportInformation information : currentArray) {
			information.setPosition(position++);
		}
		return currentArray;
	}

	public List<ReportInformation> sortArrayByPoints(List<ReportInformation> currentArray) {
		Collections.sort(currentArray, new Comparator<ReportInformation>() {
			@Override
			public int compare(ReportInformation info1, ReportInformation info2) {
				// Primero, compara por puntos
				int pointsComparison = Integer.compare(info2.getPoints(), info1.getPoints());

				// Si los puntos son iguales, compara por el campo pines
				if (pointsComparison == 0) {
					return Integer.compare(info2.getPines(), info1.getPines());
				}

				return pointsComparison;
			}
		});

		int position = 1;
		for (ReportInformation information : currentArray) {
			information.setPosition(position++);
		}
		return currentArray;
	}

	// REVISAR ESTE REPORTE AQUI VOY
	public Collection<ReportHDCPDTO> getHDCPReport() {
		List<ReportInformationHDCP> information = new ArrayList<>();
		Collection<ReportHDCPDTO> collection = new ArrayList<>();
		List<ReportHDCPDTO> reportPlayers = new ArrayList<>();

		List<Team> teams = teamRepository.findAll();

		if (!teams.isEmpty()) {
			for (Team team : teams) {
				information = new ArrayList<>();
				List<Player> players = playerRepository.findAllByTeam(team);
				if (!players.isEmpty()) {
					for (Player player : players) {
						if(BLIND.equals(player.getName()))
							continue;
						
						ReportInformationHDCP dto = new ReportInformationHDCP();
						dto.setName(player.getName() + " " + player.getLastName());
						dto.setLinesQuantity(player.getLinesQuantity());
						dto.setAverage(player.getAverage());
						dto.setHdcp(player.getHandicap());
						dto.setTittleCategory(team.getName());
						information.add(dto);
					}
				}
				ReportHDCPDTO hdcp = new ReportHDCPDTO();
				hdcp.setPlayers(information);
				hdcp.setCategory(team.getName());
				reportPlayers.add(hdcp);
			}
		}

		collection.addAll(reportPlayers);
		return collection;
	}

	public Collection<ReportHDCPDTO> getGiftReport() {
		List<ReportInformationHDCP> information = new ArrayList<>();
		Collection<ReportHDCPDTO> collection = new ArrayList<>();
		List<ReportHDCPDTO> reportPlayers = new ArrayList<>();

		List<ClubGift> gifts = giftRepository.findAll();

		if (!gifts.isEmpty()) {
			for (ClubGift gift : gifts) {
				information = new ArrayList<>();
				ReportInformationHDCP dto = new ReportInformationHDCP();
				Optional<Player> playerOptional = playerRepository.findById(gift.getPlayerId());
				dto.setName(playerOptional.get().getName() + " " + playerOptional.get().getLastName());
				dto.setLinesQuantity((int) gift.getTotalGift());
				dto.setTittleCategory("Premios Individuales Clubes");
				information.add(dto);
			}
			ReportHDCPDTO hdcp = new ReportHDCPDTO();
			hdcp.setPlayers(information);
			hdcp.setCategory("Premios Totales");
			reportPlayers.add(hdcp);
		}

		collection.addAll(reportPlayers);
		return collection;
	}

	@Override
	public ResponseEntity<?> sendEmailWithAttachment() throws MessagingException, IOException {
		List<Player> playersToSendMail = playerRepository.findBySendReportMail(true);

		String emails = playersToSendMail.stream().map(Player::getMail).collect(Collectors.joining(";"));

		// SMTP Configuration
		Properties propiedades = new Properties();
		/*
		 * propiedades.put("mail.smtp.host", "smtp.gmail.com");
		 * propiedades.put("mail.smtp.port", "587"); propiedades.put("mail.smtp.auth",
		 * "true"); propiedades.put("mail.smtp.starttls.enable", "true");
		 */

		propiedades.put("mail.smtp.host", "smtp-mail.outlook.com");
		propiedades.put("mail.smtp.port", "587");
		propiedades.put("mail.smtp.auth", "true");
		propiedades.put("mail.smtp.starttls.enable", "true");

		// User and password configuration
		Session session = Session.getInstance(propiedades, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderUsername, senderPassword);
			}
		});

		try {
			// Create message
			Message mensaje = new MimeMessage(session);
			mensaje.setFrom(new InternetAddress(senderUsername)); // Cambia esto a tu dirección de correo electrónico
			mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emails));
			mensaje.setSubject("Reportes Liga Social " + LocalDateTime.now());
			// mensaje.setText("Hola te compartimos los reportes de la liga social.");

			// Create body message
			Multipart multipart = new MimeMultipart();

			// Add body message
			BodyPart cuerpoMensaje = new MimeBodyPart();
			cuerpoMensaje.setText(mailBody);
			multipart.addBodyPart(cuerpoMensaje);

			List<String> archivosAdjuntos = new ArrayList<>();
			archivosAdjuntos.add("../socialleague/linea_alta.pdf");
			archivosAdjuntos.add("../socialleague/pines_equipos.pdf");
			archivosAdjuntos.add("../socialleague/promedios.pdf");
			archivosAdjuntos.add("../socialleague/puntos_equipos.pdf");
			archivosAdjuntos.add("../socialleague/pines_equipos.pdf");

			// Attach files
			for (String archivoAdjunto : archivosAdjuntos) {
				BodyPart adjunto = new MimeBodyPart();
				DataSource fuente = new FileDataSource(archivoAdjunto);
				adjunto.setDataHandler(new DataHandler(fuente));
				adjunto.setFileName(fuente.getName());
				multipart.addBodyPart(adjunto);
			}

			// Set content message
			mensaje.setContent(multipart);

			// Send mail
			Transport.send(mensaje);

			return new ResponseEntity<>(HttpStatus.OK);

		} catch (MessagingException e) {
			throw e;
		}
	}

	@Override
	public String exportReportClubGift(String reportFormat) throws JRException, IOException {
		Tournament tournament = tournamentRepository.findByActive(true);

		Collection<ReportHDCPDTO> collectionGiftClub = getGiftReport();

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("dedicateTo", tournament != null ? tournament.getDedicateTo() : "");
		parameters.put("tournamentName", tournament != null ? tournament.getTournamentName() : "");

		InputStream jrxmlInputStreamGiftClub = new ClassPathResource("GiftClub.jrxml").getInputStream();
		JasperDesign designGiftClub = JRXmlLoader.load(jrxmlInputStreamGiftClub);
		JasperReport jasperReportGiftClub = JasperCompileManager.compileReport(designGiftClub);
		JasperPrint jasperPrintGiftClub = JasperFillManager.fillReport(jasperReportGiftClub, parameters,
				new JRBeanCollectionDataSource(collectionGiftClub));

		if (reportFormat.equalsIgnoreCase("html")) {
			JasperExportManager.exportReportToHtmlFile(jasperPrintGiftClub, "clubes.html");
		}

		if (reportFormat.equalsIgnoreCase("pdf")) {
			JasperExportManager.exportReportToPdfFile(jasperPrintGiftClub, "clubes.pdf");
		}

		return "Report generated successfully";
	}

}
