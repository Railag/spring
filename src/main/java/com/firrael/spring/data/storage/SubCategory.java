package com.firrael.spring.data.storage;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = SubCategory.COLLECTION_NAME)
public class SubCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5084580435806737796L;

	public static final String COLLECTION_NAME = "subcategories";

	@Id
	private String id;

	private String name;

	@DBRef
	private List<Image> images;

	public SubCategory(String name, List<Image> images) {
		setName(name);
		setImages(images);
	}

	public SubCategory(String id, String name, List<Image> images) {
		setId(id);
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