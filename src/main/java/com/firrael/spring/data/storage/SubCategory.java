package com.firrael.spring.data.storage;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = SubCategory.COLLECTION_NAME)
public class SubCategory {

	public static final String COLLECTION_NAME = "subcategories";

	@Id
	private String id;

	private String name;
	private List<Image> images;

	public SubCategory(String name, List<Image> images) {
		setName(name);
		setImages(images);
	}

	public SubCategory() {
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

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

}