package com.firrael.spring.data.storage;

import java.util.List;

import com.mongodb.DBObject;

public class CategoryFactory {
	public static final String NAME = "name";
	public static final String SUBS = "subcategories";
	public static final String LOGO = "logoName";
	
	public static Category buildCategory(DBObject source) {
		
		String name = (String) source.get(NAME);
		List<SubCategory> subs = (List<SubCategory>) source.get(SUBS);
		String logoName = (String) source.get(LOGO);
		
		Category category = new Category(name, subs, logoName);
		
		return category;
	}
}