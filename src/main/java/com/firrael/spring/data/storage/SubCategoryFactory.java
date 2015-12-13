package com.firrael.spring.data.storage;

import java.util.List;

import com.mongodb.DBObject;

public class SubCategoryFactory {
	public static final String NAME = "name";
	public static final String IMAGES = "images";
	
	public static SubCategory buildSubCategory(DBObject source) {
		
		String name = (String) source.get(NAME);
		List<Image> images = (List<Image>) source.get(IMAGES);
		
		SubCategory subCategory = new SubCategory(name, images);
		
		return subCategory;
	}
}
