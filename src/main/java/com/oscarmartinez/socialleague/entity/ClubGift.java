package com.oscarmartinez.socialleague.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
public class ClubGift {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "club1")
	private int club1;

	@Column(name = "club2")
	private int club2;

	@Column(name = "club3")
	private int club3;

	@Column(name = "total_gift")
	private double totalGift;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "player_id")
	private long playerId;

}
