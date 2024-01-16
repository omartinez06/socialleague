package com.oscarmartinez.socialleague.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "pines")
	private int pines;

	@Column(name = "points")
	private int points;

	@Column(name = "added_date")
	private LocalDateTime addedDate;

	@Column(name = "added_by")
	private String addedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@ManyToOne
	private Category category;

	@ManyToOne
	private Player captain;

	@OneToOne(mappedBy = "team")
	private BackupLines lastLines;

	@Override
	public String toString() {
		return "Team: " + name + "\n" + "Pines: " + pines + "\n" + "Points: " + points + "\n" + "Category: "
				+ category.getLevel().toString() + "\n";
	}
}
