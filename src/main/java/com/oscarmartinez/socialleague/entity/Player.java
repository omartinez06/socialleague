package com.oscarmartinez.socialleague.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
	private long dpi;
	private String name;
	private String lastName;
	private int phone;
	private String address;
	private int handicap;
	private long lastSummation;
	private int linesQuantity;
	private double average;
	private Date addedDate;
	private String addedBy;
	private Date updatedDate;
	private String updatedBy;
	
	@ManyToOne
	private Team team;
	
	@ManyToOne
	private Category category;

}
