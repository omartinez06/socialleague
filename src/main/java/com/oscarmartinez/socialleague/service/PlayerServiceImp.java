package com.oscarmartinez.socialleague.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.entity.Player;
import com.oscarmartinez.socialleague.repository.ICategoryRepository;
import com.oscarmartinez.socialleague.repository.IPlayerRepository;
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
	private JwtProvider jwtProvider;

	@Override
	public List<Player> listPlayers() {
		return playerRepository.findAll();
	}

	@Override
	public void addPlayer(PlayerDTO player) throws Exception {
		final String methodName = "addPlayer()";
		logger.debug("{} - Begin", methodName);
		Player newPlayer = new Player();
		newPlayer.setAddedBy(jwtProvider.getUserName());
		newPlayer.setAddedDate(new Date());
		newPlayer.setAddress(player.getAddress());
		newPlayer.setAverage(player.getAverage());
		Category category = categoryRepository.findById(player.getCategory())
				.orElseThrow(() -> new Exception("Category does not exist with id: " + player.getCategory()));
		newPlayer.setCategory(category);
		newPlayer.setDpi(player.getDpi());
		newPlayer.setHandicap(player.getHandicap());
		newPlayer.setLastName(player.getLastName());
		newPlayer.setName(player.getName());
		newPlayer.setPhone(player.getPhone());
		newPlayer.setLastSummation(player.getLastSummation());
		newPlayer.setLinesQuantity(player.getLinesQuantity());

		playerRepository.save(newPlayer);
		logger.debug("{} - End", methodName);
	}

	@Override
	public ResponseEntity<Player> editPlayer(long id, PlayerDTO playerDetail) throws Exception {
		final String methodName = "editPlayer()";
		logger.debug("{} - Begin", methodName);
		Player player = playerRepository.findById(id)
				.orElseThrow(() -> new Exception("Player does not exist with id: " + id));
		player.setUpdatedBy(jwtProvider.getUserName());
		player.setUpdatedDate(new Date());
		player.setAddress(playerDetail.getAddress());
		player.setAverage(playerDetail.getAverage());
		Category category = categoryRepository.findById(playerDetail.getCategory())
				.orElseThrow(() -> new Exception("Category does not exist with id: " + playerDetail.getCategory()));
		player.setCategory(category);
		player.setDpi(playerDetail.getDpi());
		player.setLastName(playerDetail.getLastName());
		player.setName(playerDetail.getName());
		player.setPhone(playerDetail.getPhone());
		player.setLinesQuantity(playerDetail.getLinesQuantity());
		player.setLastSummation(playerDetail.getLastSummation());
		player.setHandicap(playerDetail.getHandicap());

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

		double handicapDouble = (200 - player.getAverage()) * 0.8;
		int handicap = handicapDouble < 0 ? 0 : (int) handicapDouble;
		player.setHandicap(handicap);
		playerRepository.save(player);
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(player);
	}

}
