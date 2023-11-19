package com.oscarmartinez.socialleague.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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
public class Team {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	private String name;
	private int pines;
	private int points;
	private Date addedDate;
	private String addedBy;
	private Date updatedDate;
	private String updatedBy;

	@ManyToOne
	private Category category;
	
	@ManyToOne
	private Player captain;
	
	@OneToOne(mappedBy = "team")
	private BackupLines lastLines;
	
	@Override
	public String toString() {
		return "Team: " + name + "\n"
				+ "Pines: " + pines + "\n"
				+ "Points: " + points + "\n"
				+ "Category: " + category.getLevel().toString() + "\n";
	}

}
