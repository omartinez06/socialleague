package com.oscarmartinez.socialleague.resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AverageReportDTO {
	
	private int position;
	private String lastName;
	private String name;
	private String category;
	private int lines;
	private long pines;
	private double average;

}
