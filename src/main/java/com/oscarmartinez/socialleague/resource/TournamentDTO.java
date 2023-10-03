package com.oscarmartinez.socialleague.resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentDTO {
	
	private String tournamentName;
	private String dedicateTo;
	private boolean applyClubes;
	private int firstClubValue;
	private int secondClubValue;
	private int thirdClubValue;
	private int firstClubQuota;
	private int secondClubQuota;
	private int thirdClubQuota;
	private double linesAverage;
	private int pointsForHDCP;
	private double averageForHDCP;
	private int numberDays;


}
