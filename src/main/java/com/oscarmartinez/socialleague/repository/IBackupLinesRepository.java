package com.oscarmartinez.socialleague.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.BackupLines;
import com.oscarmartinez.socialleague.entity.Player;

@Repository("backupRepository")
public interface IBackupLinesRepository extends JpaRepository<BackupLines, Long> {
	
	List<BackupLines> findByPlayer(Player player);

}