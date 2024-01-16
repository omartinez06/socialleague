package com.oscarmartinez.socialleague.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "first_line")
	private int firstLine;

	@Column(name = "second_line")
	private int secondLine;

	@Column(name = "third_line")
	private int thirdLine;

	@Column(name = "added_date")
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
