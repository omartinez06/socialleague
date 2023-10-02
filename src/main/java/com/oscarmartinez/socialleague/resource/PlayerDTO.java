package com.oscarmartinez.socialleague.resource;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDTO {
	private String birth;
	private String name;
	private String lastName;
	private int phone;
	private int handicap;
	private long lastSummation;
	private double average;
	private long team;
	private long category;
	private int linesQuantity;
	private int maxLine;
	private int maxSerie;
	private String mail;
	private double lineAverage;

}
