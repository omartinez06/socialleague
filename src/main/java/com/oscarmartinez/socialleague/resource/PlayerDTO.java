package com.oscarmartinez.socialleague.resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDTO {
	private long dpi;
	private String name;
	private String lastName;
	private int phone;
	private String address;
	private int handicap;
	private long lastSummation;
	private double average;
	private long team;
	private long category;
	private int linesQuantity;

}
