package com.oscarmartinez.socialleague.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oscarmartinez.socialleague.entity.Category;
import com.oscarmartinez.socialleague.resource.CategoryDTO;
import com.oscarmartinez.socialleague.service.ICategoryService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

	@Autowired
	private ICategoryService categoryService;

	@GetMapping
	public List<Category> getCategories() {
		return categoryService.listCategories();
	}

	@PostMapping
	public void addCategory(@RequestBody CategoryDTO category) throws Exception {
		categoryService.addCategory(category);
	}

	@PutMapping("{id}")
	public ResponseEntity<Category> editCategory(@PathVariable long id, @RequestBody CategoryDTO categoryDetail)
			throws Exception {
		return categoryService.editCategory(id, categoryDetail);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<HttpStatus> deleteCategory(@PathVariable long id) throws Exception {
		return categoryService.deleteCategory(id);
	}

	@GetMapping("{id}")
	public ResponseEntity<Category> getCategoryById(@PathVariable long id) throws Exception {
		return categoryService.getCategoryById(id);
	}

}
