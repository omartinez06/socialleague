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
	private double firstClubQuota;
	private double secondClubQuota;
	private double thirdClubQuota;
	private double linesAverage;


}
