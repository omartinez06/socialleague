package com.oscarmartinez.socialleague.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "phone")
	private int phone;

	@Column(name = "handicap")
	private int handicap;

	@Column(name = "last_summation")
	private long lastSummation;

	@Column(name = "lines_quantity")
	private int linesQuantity;

	@Column(name = "average")
	private double average;

	@Column(name = "max_line")
	private int maxLine;

	@Column(name = "added_date")
	private LocalDateTime addedDate;

	@Column(name = "added_by")
	private String addedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "max_serie")
	private int maxSerie;

	@Column(name = "mail")
	private String mail;

	@Column(name = "line_average")
	private double lineAverage;

	@Column(name = "send_report_mail")
	private boolean sendReportMail;

	@Column(name = "birth")
	private LocalDate birth;

	@JsonIgnore
	@ManyToOne
	private Team team;

	@ManyToOne
	private Category category;

	@OneToOne(mappedBy = "player")
	private BackupLines lastLines;

}
