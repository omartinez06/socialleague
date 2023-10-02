package com.oscarmartinez.socialleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.entity.enums.CategoryLevel;
import com.oscarmartinez.socialleague.entity.enums.CategoryType;

@Repository("categoryRepository")
public interface ICategoryRepository extends JpaRepository<Category, Long> {

	Category findByLevelAndType(CategoryLevel level, CategoryType type);
}
