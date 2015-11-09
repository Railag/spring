package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.List;

import com.firrael.spring.data.base.SelectableData;

public class Category extends SelectableData {

	public Category(String name) {
		super(name);
	}
	
	public static List<Category> stringsToCategories(List<String> names) {
		ArrayList<Category> result = new ArrayList<>();
		for (String category : names) {
			result.add(new Category(category));
		}
		
		return result;
	}
	
	public static List<String> categoriesToStrings(List<Category> categories) {
		ArrayList<String> result = new ArrayList<>();
		for (Category category : categories) {
			result.add(category.getName());
		}
		
		return result;
	}

}