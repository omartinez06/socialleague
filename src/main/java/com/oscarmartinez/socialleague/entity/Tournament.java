package com.oscarmartinez.socialleague.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
public class Tournament {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	private String tournamentName;
	private String dedicateTo;
	private boolean applyClubes;
	private int firstClubValue;
	private int secondClubValue;
	private int thirdClubValue;
	private double firstClubQuota;
	private double secondClubQuota;
	private double thirdClubQuota;
	private double linesAverage;
	private boolean active;

}
