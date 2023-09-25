package com.oscarmartinez.socialleague.resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
	
	private String type;
	private String level;
	private double minAverage;
	private double maxAverage;

}
