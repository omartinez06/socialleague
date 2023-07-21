package com.oscarmartinez.socialleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.Category;

@Repository("categoryRepository")
public interface ICategoryRepository extends JpaRepository<Category, Long> {

}
