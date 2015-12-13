package com.firrael.spring.data.storage;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = Category.COLLECTION_NAME)
public class Category {

	public static final String COLLECTION_NAME = "categories";
	
	@Id
    private String id;
	
	private String name;
	private List<SubCategory> subcategories;
	private String logoName;
	
	public Category(String name, List<SubCategory> subs, String logoName) {
		setName(name);
		setSubcategories(subs);
		setLogoName(logoName);
	}

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
	
	public String getLogoName() {
		return logoName;
	}

	public void setLogoName(String logoName) {
		this.logoName = logoName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof Category) )
			return false;
		
		Category cat = (Category) obj;
		
		return name.equals(cat.getName());
	}
}