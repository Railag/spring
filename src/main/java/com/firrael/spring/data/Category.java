package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.List;

public class Category {

	private String name;
	private boolean checked;

	public Category(String name) {
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean getChecked() {
		return checked;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Category))
			return false;

		Category secondCategory = (Category) obj;

		return this.name.equals(secondCategory.getName()) && this.getChecked() == secondCategory.getChecked();
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
	
	@Override
	public String toString() {
		return getName();
	}
}