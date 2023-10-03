package com.oscarmartinez.socialleague.resource;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDTO {
	
	private String gender;
	private String category;
	private List<ReportInformation> averages;

}
