package com.oscarmartinez.socialleague.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.entity.Player;
import com.oscarmartinez.socialleague.entity.Team;
import com.oscarmartinez.socialleague.entity.enums.CategoryLevel;
import com.oscarmartinez.socialleague.entity.enums.CategoryType;
import com.oscarmartinez.socialleague.repository.ICategoryRepository;
import com.oscarmartinez.socialleague.repository.IPlayerRepository;
import com.oscarmartinez.socialleague.repository.ITeamRepository;
import com.oscarmartinez.socialleague.resource.AverageReportDTO;
import com.oscarmartinez.socialleague.resource.MaxLineReportDTO;
import com.oscarmartinez.socialleague.resource.MaxSerieReportDTO;
import com.oscarmartinez.socialleague.resource.PlayerDTO;
import com.oscarmartinez.socialleague.resource.TeamPointsReportDTO;
import com.oscarmartinez.socialleague.security.JwtProvider;

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
	public ResponseEntity<Player> addLine(long id, int lineValue) throws Exception {
		final String methodName = "addLine()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));
		int currentQuantity = player.getLinesQuantity();
		long lastSummation = player.getLastSummation();
		player.setLinesQuantity(currentQuantity + 1);
		player.setLastSummation(lastSummation + lineValue);

		double tempAverage = (double) player.getLastSummation() / (double) player.getLinesQuantity();
		double average = Math.round(tempAverage * 100) / 100;

		player.setAverage(average);

		//Este 200 del calculo de HDCP ingresarlo al comenzar torneo al igual que el 0.8.
		double handicapDouble = (200 - player.getAverage()) * 0.8;
		int handicap = handicapDouble < 0 ? 0 : (int) handicapDouble;
		player.setHandicap(handicap);

		if (lineValue > player.getMaxLine()) {
			player.setMaxLine(lineValue);
		}

		playerRepository.save(player);

		Team team = player.getTeam();
		int currentPines = team.getPines();
		team.setPines(currentPines + lineValue);
		teamRepository.save(team);

		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(player);
	}

	@Override
	public ResponseEntity<Player> addSerie(long id, int serieValue) throws Exception {
		final String methodName = "addSerie()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));
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
	public String exportReport(String reportFormat) throws JRException, IOException {
		List<AverageReportDTO> averageReport = getAverageReport();
		List<MaxLineReportDTO> maxLineReport = getMaxLineReport();
		List<MaxSerieReportDTO> maxSerieReport = getMaxSerieReport();
		List<TeamPointsReportDTO> teamPointsReport = getTeamPointsReport();
		
		
		

		// load file and compile it
		InputStream fileAverage = new ClassPathResource("AverageByCategory.jrxml").getInputStream();
		JasperDesign design = JRXmlLoader.load(fileAverage);
		JasperReport jasperReport = JasperCompileManager.compileReport(design);
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(averageReport);
		Map<String, Object> map = new HashMap<>();
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);

		/*File fileMaxLine = ResourceUtils.getFile("classpath:PinesByCategory.jrxml");
		JasperReport jasperReportMaxLine = JasperCompileManager.compileReport(fileMaxLine.getAbsolutePath());
		JRBeanCollectionDataSource maxLineDataSource = new JRBeanCollectionDataSource(maxLineReport);
		JasperPrint jasperPrintMaxLine = JasperFillManager.fillReport(jasperReportMaxLine, map, maxLineDataSource);

		File fileMaxSerie = ResourceUtils.getFile("classpath:MaxSerieByCategory.jrxml");
		JasperReport jasperReportMaxSerie = JasperCompileManager.compileReport(fileMaxSerie.getAbsolutePath());
		JRBeanCollectionDataSource maxSerieDataSource = new JRBeanCollectionDataSource(maxSerieReport);
		JasperPrint jasperPrintMaxSerie = JasperFillManager.fillReport(jasperReportMaxSerie, map, maxSerieDataSource);

		File fileTeamPoints = ResourceUtils.getFile("classpath:PointTeamByCategory.jrxml");
		JasperReport jasperReportTeamPoints = JasperCompileManager.compileReport(fileTeamPoints.getAbsolutePath());
		JRBeanCollectionDataSource teamPointsDataSource = new JRBeanCollectionDataSource(teamPointsReport);
		JasperPrint jasperPrintTeamPoints = JasperFillManager.fillReport(jasperReportTeamPoints, map,
				teamPointsDataSource);*/

		if (reportFormat.equalsIgnoreCase("html")) {
			JasperExportManager.exportReportToHtmlFile(jasperPrint, "average.html");
			/*JasperExportManager.exportReportToHtmlFile(jasperPrintMaxLine, "maxLine.html");
			JasperExportManager.exportReportToHtmlFile(jasperPrintMaxSerie, "maxSerie.html");
			JasperExportManager.exportReportToHtmlFile(jasperPrintTeamPoints, "teamPoints.html");*/
		}

		if (reportFormat.equalsIgnoreCase("pdf")) {
			JasperExportManager.exportReportToPdfFile(jasperPrint, "average.pdf");
			/*JasperExportManager.exportReportToPdfFile(jasperPrintMaxLine, "maxLine.pdf");
			JasperExportManager.exportReportToPdfFile(jasperPrintMaxSerie, "maxSerie.pdf");
			JasperExportManager.exportReportToPdfFile(jasperPrintTeamPoints, "teamPoints.pdf");*/
		}

		return "Reports generated successfully";
	}

	public List<AverageReportDTO> getAverageReport() {
		List<AverageReportDTO> finalReport = new ArrayList<>();
		List<AverageReportDTO> averageReportFA = new ArrayList<>();
		List<AverageReportDTO> averageReportFB = new ArrayList<>();
		List<AverageReportDTO> averageReportFC = new ArrayList<>();
		List<AverageReportDTO> averageReportFD = new ArrayList<>();
		List<AverageReportDTO> averageReportMA = new ArrayList<>();
		List<AverageReportDTO> averageReportMB = new ArrayList<>();
		List<AverageReportDTO> averageReportMC = new ArrayList<>();
		List<AverageReportDTO> averageReportMD = new ArrayList<>();
		List<Player> players = playerRepository.findAll();

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.A) {
				AverageReportDTO averageReportData = new AverageReportDTO();
				averageReportData.setAverage(p.getAverage());
				averageReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				averageReportData.setLastName(p.getLastName());
				averageReportData.setName(p.getName());
				averageReportData.setPines(p.getLastSummation());
				averageReportData.setLines(p.getLinesQuantity());
				averageReportFA.add(averageReportData);
			}
		});

		Collections.sort(averageReportFA, new Comparator<AverageReportDTO>() {

			@Override
			public int compare(AverageReportDTO o1, AverageReportDTO o2) {
				return Double.compare(o2.getAverage(), o1.getAverage());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.B) {
				AverageReportDTO averageReportData = new AverageReportDTO();
				averageReportData.setAverage(p.getAverage());
				averageReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				averageReportData.setLastName(p.getLastName());
				averageReportData.setName(p.getName());
				averageReportData.setPines(p.getLastSummation());
				averageReportData.setLines(p.getLinesQuantity());
				averageReportFB.add(averageReportData);
			}
		});

		Collections.sort(averageReportFB, new Comparator<AverageReportDTO>() {

			@Override
			public int compare(AverageReportDTO o1, AverageReportDTO o2) {
				return Double.compare(o2.getAverage(), o1.getAverage());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.C) {
				AverageReportDTO averageReportData = new AverageReportDTO();
				averageReportData.setAverage(p.getAverage());
				averageReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				averageReportData.setLastName(p.getLastName());
				averageReportData.setName(p.getName());
				averageReportData.setPines(p.getLastSummation());
				averageReportData.setLines(p.getLinesQuantity());
				averageReportFC.add(averageReportData);
			}
		});

		Collections.sort(averageReportFC, new Comparator<AverageReportDTO>() {

			@Override
			public int compare(AverageReportDTO o1, AverageReportDTO o2) {
				return Double.compare(o2.getAverage(), o1.getAverage());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.D) {
				AverageReportDTO averageReportData = new AverageReportDTO();
				averageReportData.setAverage(p.getAverage());
				averageReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				averageReportData.setLastName(p.getLastName());
				averageReportData.setName(p.getName());
				averageReportData.setPines(p.getLastSummation());
				averageReportData.setLines(p.getLinesQuantity());
				averageReportFD.add(averageReportData);
			}
		});

		Collections.sort(averageReportFD, new Comparator<AverageReportDTO>() {

			@Override
			public int compare(AverageReportDTO o1, AverageReportDTO o2) {
				return Double.compare(o2.getAverage(), o1.getAverage());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.A) {
				AverageReportDTO averageReportData = new AverageReportDTO();
				averageReportData.setAverage(p.getAverage());
				averageReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				averageReportData.setLastName(p.getLastName());
				averageReportData.setName(p.getName());
				averageReportData.setPines(p.getLastSummation());
				averageReportData.setLines(p.getLinesQuantity());
				averageReportMA.add(averageReportData);
			}
		});

		Collections.sort(averageReportMA, new Comparator<AverageReportDTO>() {

			@Override
			public int compare(AverageReportDTO o1, AverageReportDTO o2) {
				return Double.compare(o2.getAverage(), o1.getAverage());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.B) {
				AverageReportDTO averageReportData = new AverageReportDTO();
				averageReportData.setAverage(p.getAverage());
				averageReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				averageReportData.setLastName(p.getLastName());
				averageReportData.setName(p.getName());
				averageReportData.setPines(p.getLastSummation());
				averageReportData.setLines(p.getLinesQuantity());
				averageReportMB.add(averageReportData);
			}
		});

		Collections.sort(averageReportMB, new Comparator<AverageReportDTO>() {

			@Override
			public int compare(AverageReportDTO o1, AverageReportDTO o2) {
				return Double.compare(o2.getAverage(), o1.getAverage());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.C) {
				AverageReportDTO averageReportData = new AverageReportDTO();
				averageReportData.setAverage(p.getAverage());
				averageReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				averageReportData.setLastName(p.getLastName());
				averageReportData.setName(p.getName());
				averageReportData.setPines(p.getLastSummation());
				averageReportData.setLines(p.getLinesQuantity());
				averageReportMC.add(averageReportData);
			}
		});

		Collections.sort(averageReportMC, new Comparator<AverageReportDTO>() {

			@Override
			public int compare(AverageReportDTO o1, AverageReportDTO o2) {
				return Double.compare(o2.getAverage(), o1.getAverage());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.D) {
				AverageReportDTO averageReportData = new AverageReportDTO();
				averageReportData.setAverage(p.getAverage());
				averageReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				averageReportData.setLastName(p.getLastName());
				averageReportData.setName(p.getName());
				averageReportData.setPines(p.getLastSummation());
				averageReportData.setLines(p.getLinesQuantity());
				averageReportMD.add(averageReportData);
			}
		});

		Collections.sort(averageReportMD, new Comparator<AverageReportDTO>() {

			@Override
			public int compare(AverageReportDTO o1, AverageReportDTO o2) {
				return Double.compare(o2.getAverage(), o1.getAverage());
			}

		});

		finalReport.addAll(averageReportFA);
		finalReport.addAll(averageReportFB);
		finalReport.addAll(averageReportFC);
		finalReport.addAll(averageReportFD);
		finalReport.addAll(averageReportMA);
		finalReport.addAll(averageReportMB);
		finalReport.addAll(averageReportMC);
		finalReport.addAll(averageReportMD);

		return finalReport;
	}

	public List<MaxLineReportDTO> getMaxLineReport() {
		List<MaxLineReportDTO> finalReport = new ArrayList<>();
		List<MaxLineReportDTO> maxLineReportFA = new ArrayList<>();
		List<MaxLineReportDTO> maxLineReportFB = new ArrayList<>();
		List<MaxLineReportDTO> maxLineReportFC = new ArrayList<>();
		List<MaxLineReportDTO> maxLineReportFD = new ArrayList<>();
		List<MaxLineReportDTO> maxLineReportMA = new ArrayList<>();
		List<MaxLineReportDTO> maxLineReportMB = new ArrayList<>();
		List<MaxLineReportDTO> maxLineReportMC = new ArrayList<>();
		List<MaxLineReportDTO> maxLineReportMD = new ArrayList<>();
		List<Player> players = playerRepository.findAll();

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.A) {
				MaxLineReportDTO maxLineReportData = new MaxLineReportDTO();
				maxLineReportData.setMaxLine(p.getMaxLine());
				maxLineReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxLineReportData.setLastName(p.getLastName());
				maxLineReportData.setName(p.getName());
				maxLineReportFA.add(maxLineReportData);
			}
		});

		Collections.sort(maxLineReportFA, new Comparator<MaxLineReportDTO>() {

			@Override
			public int compare(MaxLineReportDTO o1, MaxLineReportDTO o2) {
				return Double.compare(o2.getMaxLine(), o1.getMaxLine());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.B) {
				MaxLineReportDTO maxLineReportData = new MaxLineReportDTO();
				maxLineReportData.setMaxLine(p.getMaxLine());
				maxLineReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxLineReportData.setLastName(p.getLastName());
				maxLineReportData.setName(p.getName());
				maxLineReportFB.add(maxLineReportData);
			}
		});

		Collections.sort(maxLineReportFB, new Comparator<MaxLineReportDTO>() {

			@Override
			public int compare(MaxLineReportDTO o1, MaxLineReportDTO o2) {
				return Double.compare(o2.getMaxLine(), o1.getMaxLine());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.C) {
				MaxLineReportDTO maxLineReportData = new MaxLineReportDTO();
				maxLineReportData.setMaxLine(p.getMaxLine());
				maxLineReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxLineReportData.setLastName(p.getLastName());
				maxLineReportData.setName(p.getName());
				maxLineReportFC.add(maxLineReportData);
			}
		});

		Collections.sort(maxLineReportFC, new Comparator<MaxLineReportDTO>() {

			@Override
			public int compare(MaxLineReportDTO o1, MaxLineReportDTO o2) {
				return Double.compare(o2.getMaxLine(), o1.getMaxLine());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.D) {
				MaxLineReportDTO maxLineReportData = new MaxLineReportDTO();
				maxLineReportData.setMaxLine(p.getMaxLine());
				maxLineReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxLineReportData.setLastName(p.getLastName());
				maxLineReportData.setName(p.getName());
				maxLineReportFD.add(maxLineReportData);
			}
		});

		Collections.sort(maxLineReportFD, new Comparator<MaxLineReportDTO>() {

			@Override
			public int compare(MaxLineReportDTO o1, MaxLineReportDTO o2) {
				return Double.compare(o2.getMaxLine(), o1.getMaxLine());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.A) {
				MaxLineReportDTO maxLineReportData = new MaxLineReportDTO();
				maxLineReportData.setMaxLine(p.getMaxLine());
				maxLineReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxLineReportData.setLastName(p.getLastName());
				maxLineReportData.setName(p.getName());
				maxLineReportMA.add(maxLineReportData);
			}
		});

		Collections.sort(maxLineReportMA, new Comparator<MaxLineReportDTO>() {

			@Override
			public int compare(MaxLineReportDTO o1, MaxLineReportDTO o2) {
				return Double.compare(o2.getMaxLine(), o1.getMaxLine());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.B) {
				MaxLineReportDTO maxLineReportData = new MaxLineReportDTO();
				maxLineReportData.setMaxLine(p.getMaxLine());
				maxLineReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxLineReportData.setLastName(p.getLastName());
				maxLineReportData.setName(p.getName());
				maxLineReportMB.add(maxLineReportData);
			}
		});

		Collections.sort(maxLineReportMB, new Comparator<MaxLineReportDTO>() {

			@Override
			public int compare(MaxLineReportDTO o1, MaxLineReportDTO o2) {
				return Double.compare(o2.getMaxLine(), o1.getMaxLine());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.C) {
				MaxLineReportDTO maxLineReportData = new MaxLineReportDTO();
				maxLineReportData.setMaxLine(p.getMaxLine());
				maxLineReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxLineReportData.setLastName(p.getLastName());
				maxLineReportData.setName(p.getName());
				maxLineReportMC.add(maxLineReportData);
			}
		});

		Collections.sort(maxLineReportMC, new Comparator<MaxLineReportDTO>() {

			@Override
			public int compare(MaxLineReportDTO o1, MaxLineReportDTO o2) {
				return Double.compare(o2.getMaxLine(), o1.getMaxLine());
			}

		});

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.D) {
				MaxLineReportDTO maxLineReportData = new MaxLineReportDTO();
				maxLineReportData.setMaxLine(p.getMaxLine());
				maxLineReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxLineReportData.setLastName(p.getLastName());
				maxLineReportData.setName(p.getName());
				maxLineReportMD.add(maxLineReportData);
			}
		});

		Collections.sort(maxLineReportMD, new Comparator<MaxLineReportDTO>() {

			@Override
			public int compare(MaxLineReportDTO o1, MaxLineReportDTO o2) {
				return Double.compare(o2.getMaxLine(), o1.getMaxLine());
			}

		});

		finalReport.addAll(maxLineReportFA);
		finalReport.addAll(maxLineReportFB);
		finalReport.addAll(maxLineReportFC);
		finalReport.addAll(maxLineReportFD);
		finalReport.addAll(maxLineReportMA);
		finalReport.addAll(maxLineReportMB);
		finalReport.addAll(maxLineReportMC);
		finalReport.addAll(maxLineReportMD);

		return finalReport;
	}

	public List<MaxSerieReportDTO> getMaxSerieReport() {
		List<MaxSerieReportDTO> finalReport = new ArrayList<>();
		List<MaxSerieReportDTO> maxSerieReportFA = new ArrayList<>();
		List<MaxSerieReportDTO> maxSerieReportFB = new ArrayList<>();
		List<MaxSerieReportDTO> maxSerieReportFC = new ArrayList<>();
		List<MaxSerieReportDTO> maxSerieReportFD = new ArrayList<>();
		List<MaxSerieReportDTO> maxSerieReportMA = new ArrayList<>();
		List<MaxSerieReportDTO> maxSerieReportMB = new ArrayList<>();
		List<MaxSerieReportDTO> maxSerieReportMC = new ArrayList<>();
		List<MaxSerieReportDTO> maxSerieReportMD = new ArrayList<>();
		List<Player> players = playerRepository.findAll();

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.A) {
				MaxSerieReportDTO maxSerieReportData = new MaxSerieReportDTO();
				maxSerieReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxSerieReportData.setLastName(p.getLastName());
				maxSerieReportData.setName(p.getName());
				maxSerieReportData.setMaxSerie(p.getMaxSerie());
				maxSerieReportFA.add(maxSerieReportData);
			}
		});

		Collections.sort(maxSerieReportFA, new Comparator<MaxSerieReportDTO>() {

			@Override
			public int compare(MaxSerieReportDTO o1, MaxSerieReportDTO o2) {
				return Double.compare(o2.getMaxSerie(), o1.getMaxSerie());
			}

		});

		for (int x = 0; x < maxSerieReportFA.size(); x++) {
			maxSerieReportFA.get(x).setPosition(x + 1);
		}

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.B) {
				MaxSerieReportDTO maxSerieReportData = new MaxSerieReportDTO();
				maxSerieReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxSerieReportData.setLastName(p.getLastName());
				maxSerieReportData.setName(p.getName());
				maxSerieReportData.setMaxSerie(p.getMaxSerie());
				maxSerieReportFB.add(maxSerieReportData);
			}
		});

		Collections.sort(maxSerieReportFB, new Comparator<MaxSerieReportDTO>() {

			@Override
			public int compare(MaxSerieReportDTO o1, MaxSerieReportDTO o2) {
				return Double.compare(o2.getMaxSerie(), o1.getMaxSerie());
			}

		});

		for (int x = 0; x < maxSerieReportFB.size(); x++) {
			maxSerieReportFB.get(x).setPosition(x + 1);
		}

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.C) {
				MaxSerieReportDTO maxSerieReportData = new MaxSerieReportDTO();
				maxSerieReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxSerieReportData.setLastName(p.getLastName());
				maxSerieReportData.setName(p.getName());
				maxSerieReportData.setMaxSerie(p.getMaxSerie());
				maxSerieReportFC.add(maxSerieReportData);
			}
		});

		Collections.sort(maxSerieReportFC, new Comparator<MaxSerieReportDTO>() {

			@Override
			public int compare(MaxSerieReportDTO o1, MaxSerieReportDTO o2) {
				return Double.compare(o2.getMaxSerie(), o1.getMaxSerie());
			}

		});

		for (int x = 0; x < maxSerieReportFC.size(); x++) {
			maxSerieReportFC.get(x).setPosition(x + 1);
		}

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.FEMALE && p.getCategory().getLevel() == CategoryLevel.D) {
				MaxSerieReportDTO maxSerieReportData = new MaxSerieReportDTO();
				maxSerieReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxSerieReportData.setLastName(p.getLastName());
				maxSerieReportData.setName(p.getName());
				maxSerieReportData.setMaxSerie(p.getMaxSerie());
				maxSerieReportFD.add(maxSerieReportData);
			}
		});

		Collections.sort(maxSerieReportFD, new Comparator<MaxSerieReportDTO>() {

			@Override
			public int compare(MaxSerieReportDTO o1, MaxSerieReportDTO o2) {
				return Double.compare(o2.getMaxSerie(), o1.getMaxSerie());
			}

		});

		for (int x = 0; x < maxSerieReportFD.size(); x++) {
			maxSerieReportFD.get(x).setPosition(x + 1);
		}

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.A) {
				MaxSerieReportDTO maxSerieReportData = new MaxSerieReportDTO();
				maxSerieReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxSerieReportData.setLastName(p.getLastName());
				maxSerieReportData.setName(p.getName());
				maxSerieReportData.setMaxSerie(p.getMaxSerie());
				maxSerieReportMA.add(maxSerieReportData);
			}
		});

		Collections.sort(maxSerieReportMA, new Comparator<MaxSerieReportDTO>() {

			@Override
			public int compare(MaxSerieReportDTO o1, MaxSerieReportDTO o2) {
				return Double.compare(o2.getMaxSerie(), o1.getMaxSerie());
			}

		});

		for (int x = 0; x < maxSerieReportMA.size(); x++) {
			maxSerieReportMA.get(x).setPosition(x + 1);
		}

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.B) {
				MaxSerieReportDTO maxSerieReportData = new MaxSerieReportDTO();
				maxSerieReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxSerieReportData.setLastName(p.getLastName());
				maxSerieReportData.setName(p.getName());
				maxSerieReportData.setMaxSerie(p.getMaxSerie());
				maxSerieReportMB.add(maxSerieReportData);
			}
		});

		Collections.sort(maxSerieReportMB, new Comparator<MaxSerieReportDTO>() {

			@Override
			public int compare(MaxSerieReportDTO o1, MaxSerieReportDTO o2) {
				return Double.compare(o2.getMaxSerie(), o1.getMaxSerie());
			}

		});

		for (int x = 0; x < maxSerieReportMB.size(); x++) {
			maxSerieReportMB.get(x).setPosition(x + 1);
		}

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.C) {
				MaxSerieReportDTO maxSerieReportData = new MaxSerieReportDTO();
				maxSerieReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxSerieReportData.setLastName(p.getLastName());
				maxSerieReportData.setName(p.getName());
				maxSerieReportData.setMaxSerie(p.getMaxSerie());
				maxSerieReportMC.add(maxSerieReportData);
			}
		});

		Collections.sort(maxSerieReportMC, new Comparator<MaxSerieReportDTO>() {

			@Override
			public int compare(MaxSerieReportDTO o1, MaxSerieReportDTO o2) {
				return Double.compare(o2.getMaxSerie(), o1.getMaxSerie());
			}

		});

		for (int x = 0; x < maxSerieReportMC.size(); x++) {
			maxSerieReportMC.get(x).setPosition(x + 1);
		}

		players.forEach(p -> {
			if (p.getCategory().getType() == CategoryType.MALE && p.getCategory().getLevel() == CategoryLevel.D) {
				MaxSerieReportDTO maxSerieReportData = new MaxSerieReportDTO();
				maxSerieReportData.setCategory(
						p.getCategory().getType().toString() + " - " + p.getCategory().getLevel().toString());
				maxSerieReportData.setLastName(p.getLastName());
				maxSerieReportData.setName(p.getName());
				maxSerieReportData.setMaxSerie(p.getMaxSerie());
				maxSerieReportMD.add(maxSerieReportData);
			}
		});

		Collections.sort(maxSerieReportMD, new Comparator<MaxSerieReportDTO>() {

			@Override
			public int compare(MaxSerieReportDTO o1, MaxSerieReportDTO o2) {
				return Double.compare(o2.getMaxSerie(), o1.getMaxSerie());
			}

		});

		for (int x = 0; x < maxSerieReportMD.size(); x++) {
			maxSerieReportMD.get(x).setPosition(x + 1);
		}

		finalReport.addAll(maxSerieReportFA);
		finalReport.addAll(maxSerieReportFB);
		finalReport.addAll(maxSerieReportFC);
		finalReport.addAll(maxSerieReportFD);
		finalReport.addAll(maxSerieReportMA);
		finalReport.addAll(maxSerieReportMB);
		finalReport.addAll(maxSerieReportMC);
		finalReport.addAll(maxSerieReportMD);

		return finalReport;
	}

	public List<TeamPointsReportDTO> getTeamPointsReport() {
		List<TeamPointsReportDTO> finalReport = new ArrayList<>();
		List<TeamPointsReportDTO> teamReportA = new ArrayList<>();
		List<TeamPointsReportDTO> teamReportB = new ArrayList<>();
		List<TeamPointsReportDTO> teamReportC = new ArrayList<>();
		List<Team> teams = teamRepository.findAll();

		teams.forEach(t -> {
			if (t.getCategory().getLevel() == CategoryLevel.A) {
				TeamPointsReportDTO teamPointsData = new TeamPointsReportDTO();
				teamPointsData.setCategory(t.getCategory().getLevel().toString());
				teamPointsData.setName(t.getName());
				teamPointsData.setPoints(t.getPoints());
				teamReportA.add(teamPointsData);
			}
			if (t.getCategory().getLevel() == CategoryLevel.B) {
				TeamPointsReportDTO teamPointsData = new TeamPointsReportDTO();
				teamPointsData.setCategory(t.getCategory().getLevel().toString());
				teamPointsData.setName(t.getName());
				teamPointsData.setPoints(t.getPoints());
				teamReportB.add(teamPointsData);
			}
			if (t.getCategory().getLevel() == CategoryLevel.C) {
				TeamPointsReportDTO teamPointsData = new TeamPointsReportDTO();
				teamPointsData.setCategory(t.getCategory().getLevel().toString());
				teamPointsData.setName(t.getName());
				teamPointsData.setPoints(t.getPoints());
				teamReportC.add(teamPointsData);
			}
		});

		Collections.sort(teamReportA, new Comparator<TeamPointsReportDTO>() {

			@Override
			public int compare(TeamPointsReportDTO o1, TeamPointsReportDTO o2) {
				return Double.compare(o2.getPoints(), o1.getPoints());
			}

		});

		for (int x = 0; x < teamReportA.size(); x++) {
			teamReportA.get(x).setPosition(x + 1);
		}

		Collections.sort(teamReportB, new Comparator<TeamPointsReportDTO>() {

			@Override
			public int compare(TeamPointsReportDTO o1, TeamPointsReportDTO o2) {
				return Double.compare(o2.getPoints(), o1.getPoints());
			}

		});

		for (int x = 0; x < teamReportB.size(); x++) {
			teamReportB.get(x).setPosition(x + 1);
		}

		Collections.sort(teamReportC, new Comparator<TeamPointsReportDTO>() {

			@Override
			public int compare(TeamPointsReportDTO o1, TeamPointsReportDTO o2) {
				return Double.compare(o2.getPoints(), o1.getPoints());
			}

		});

		for (int x = 0; x < teamReportC.size(); x++) {
			teamReportC.get(x).setPosition(x + 1);
		}

		finalReport.addAll(teamReportA);
		finalReport.addAll(teamReportB);
		finalReport.addAll(teamReportC);

		return finalReport;
	}

	@Override
	public ResponseEntity<HttpStatus> updateHandicap() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
