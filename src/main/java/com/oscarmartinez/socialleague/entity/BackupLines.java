package com.oscarmartinez.socialleague.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class BackupLines {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	private int firstLine;
	private int secondLine;
	private int thirdLine;
	private LocalDateTime addedDate;

	@OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    @JsonIgnore
	private Player player;

	@OneToOne
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	@JsonIgnore
	private Team team;

}
