package com.oscarmartinez.socialleague.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.entity.enums.CategoryLevel;
import com.oscarmartinez.socialleague.entity.enums.CategoryType;
import com.oscarmartinez.socialleague.repository.ICategoryRepository;
import com.oscarmartinez.socialleague.resource.CategoryDTO;
import com.oscarmartinez.socialleague.security.JwtProvider;

@Service
public class CategoryServiceImp implements ICategoryService {

	private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImp.class);

	@Autowired
	private ICategoryRepository categoryRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Override
	public List<Category> listCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public ResponseEntity<HttpStatus> addCategory(CategoryDTO category) throws Exception {
		final String methodName = "addCategory()";
		logger.debug("{} - Begin", methodName);
		Category newCategory = new Category();
		newCategory.setLevel(CategoryLevel.valueOf(category.getLevel()));
		newCategory.setType(CategoryType.valueOf(category.getType()));
		newCategory.setAddedDate(new Date());
		newCategory.setAddedBy(jwtProvider.getUserName());
		
		if(isExist(newCategory.getLevel(), newCategory.getType()))
			throw new Exception("ALREADY_EXIST");
		
		categoryRepository.save(newCategory);
		logger.debug("{} - End", methodName);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Category> editCategory(long id, CategoryDTO categoryDetail) throws Exception {
		final String methodName = "editCategory()";
		logger.debug("{} - Begin", methodName);
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new Exception("Category does not exist with id: " + id));
		category.setUpdatedBy(jwtProvider.getUserName());
		category.setUpdatedDate(new Date());
		category.setLevel(CategoryLevel.valueOf(categoryDetail.getLevel()));
		category.setType(CategoryType.valueOf(categoryDetail.getType()));

		categoryRepository.save(category);
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(category);
	}

	@Override
	public ResponseEntity<HttpStatus> deleteCategory(long id) throws Exception {
		final String methodName = "deleteCategory()";
		logger.debug("{} - Begin", methodName);
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new Exception("Category does not exist with id: " + id));
		categoryRepository.delete(category);
		logger.debug("{} - End", methodName);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Category> getCategoryById(long id) throws Exception {
		final String methodName = "getCategoryById()";
		logger.debug("{} - Begin", methodName);
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new Exception("Category does not exist with id: " + id));
		logger.debug("{} - End", methodName);
		return ResponseEntity.ok(category);
	}

	public boolean isExist(CategoryLevel level, CategoryType type) {
		List<Category> categories = categoryRepository.findAll();
		for (Category category : categories) {
			if (category.getLevel() == level && category.getType() == type)
				return true;
		}
		return false;
	}

}
