package com.oscarmartinez.socialleague.resource;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AverageReportDTO {
	
	private String gender;
	private String category;
	private List<AverageInformation> averages;

}
