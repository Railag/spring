package com.firrael.spring.data.storage;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = Category.COLLECTION_NAME)
public class Category {

	public static final String COLLECTION_NAME = "category";
	
	@Id
    private String id;
	
	private String name;
	private List<SubCategory> subcategories;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<SubCategory> getSubcategories() {
		return subcategories;
	}
	
	public void setSubcategories(List<SubCategory> subcategories) {
		this.subcategories = subcategories;
	}
}