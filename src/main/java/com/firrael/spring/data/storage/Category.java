package com.firrael.spring.data.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = Category.COLLECTION_NAME)
public class Category implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7442147875099477815L;

	public static final String COLLECTION_NAME = "categories";

	@Id
	private String id;

	private String name;

	@DBRef
	private List<SubCategory> subcategories;
	
	public Category(String id, String name, List<SubCategory> subs) {
		setId(id);
		setName(name);
		setSubcategories(subs);
	}

	public Category() {
	}

	public Category(String name, ArrayList<SubCategory> subs) {
		setName(name);
		setSubcategories(subs);
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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Category))
			return false;

		Category cat = (Category) obj;

		return name.equals(cat.getName());
	}
}