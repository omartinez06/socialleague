package com.oscarmartinez.socialleague.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.oscarmartinez.socialleague.entity.enums.CategoryLevel;
import com.oscarmartinez.socialleague.entity.enums.CategoryType;

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
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Enumerated(EnumType.STRING)
	private CategoryType type;

	@Enumerated(EnumType.STRING)
	private CategoryLevel level;

	@Column(name = "added_date")
	private LocalDateTime addedDate;

	@Column(name = "added_by")
	private String addedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "min_average")
	private double minAverage;

	@Column(name = "max_average")
	private double maxAverage;

}
