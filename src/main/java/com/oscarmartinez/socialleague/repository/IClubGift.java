package com.oscarmartinez.socialleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.ClubGift;

@Repository("clubGiftRepository")
public interface IClubGift extends JpaRepository<ClubGift, Long> {

}
