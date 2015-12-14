package com.firrael.spring.upload;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.firrael.spring.data.storage.Category;
import com.firrael.spring.data.storage.SubCategory;

public class ImageModel {

	private int imageID;
	private MultipartFile fileImage;
	private String name;
	private String category;
	private String subcategory;
	
	private List<Category> allCategories;
	private List<SubCategory> allSubs;

	public int getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID = imageID;
	}

	public MultipartFile getFileImage() {
		return fileImage;
	}

	public void setFileImage(MultipartFile fileImage) {
		this.fileImage = fileImage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Category> getAllCategories() {
		return allCategories;
	}

	public void setAllCategories(List<Category> allCategories) {
		this.allCategories = allCategories;
	}

	public List<SubCategory> getAllSubs() {
		return allSubs;
	}

	public void setAllSubs(List<SubCategory> allSubs) {
		this.allSubs = allSubs;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}
}