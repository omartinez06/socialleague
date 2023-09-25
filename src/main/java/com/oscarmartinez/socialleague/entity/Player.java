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
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	private Date birth;
	private String name;
	private String lastName;
	private int phone;
	private int handicap;
	private long lastSummation;
	private int linesQuantity;
	private double average;
	private int maxLine;
	private Date addedDate;
	private String addedBy;
	private Date updatedDate;
	private String updatedBy;
	private int maxSerie;
	
	@ManyToOne
	private Team team;
	
	@ManyToOne
	private Category category;
	
	@OneToOne(mappedBy = "player")
	private BackupLines lastLines;
	
	@OneToOne(mappedBy = "player")
	private ClubGift clubGift;

}
