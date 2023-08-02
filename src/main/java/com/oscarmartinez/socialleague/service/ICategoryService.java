package com.oscarmartinez.socialleague.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.resource.CategoryDTO;

public interface ICategoryService {

	List<Category> listCategories();

	void addCategory(CategoryDTO team) throws Exception;

	ResponseEntity<Category> editCategory(long id, CategoryDTO categoryDetail) throws Exception;

	ResponseEntity<HttpStatus> deleteCategory(long id) throws Exception;

	ResponseEntity<Category> getCategoryById(long id) throws Exception;

}
