package com.oscarmartinez.socialleague.entity;

import javax.persistence.Column;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String tournamentName;

	@Column(name = "dedicate_to")
	private String dedicateTo;

	@Column(name = "apply_clubes")
	private boolean applyClubes;

	@Column(name = "first_club_value")
	private int firstClubValue;

	@Column(name = "second_club_value")
	private int secondClubValue;

	@Column(name = "third_club_value")
	private int thirdClubValue;

	@Column(name = "first_club_quota")
	private int firstClubQuota;

	@Column(name = "second_club_quota")
	private int secondClubQuota;

	@Column(name = "third_club_quota")
	private int thirdClubQuota;

	@Column(name = "lines_average")
	private double linesAverage;

	@Column(name = "number_days")
	private int numberDays;

	@Column(name = "active")
	private boolean active;

	@Column(name = "points_for_hdcp")
	private int pointsForHDCP;

	@Column(name = "average_for_hdcp")
	private double averageForHDCP;

	@Column(name = "min_hdcp")
	private int minHDCP;

	@Column(name = "max_hdcp")
	private int maxHDCP;

	@Column(name = "day")
	private int day;

}
