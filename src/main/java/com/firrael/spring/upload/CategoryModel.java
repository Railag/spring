package com.firrael.spring.upload;

import org.springframework.web.multipart.MultipartFile;

public class CategoryModel {
	private int imageID;
	private MultipartFile fileImage;
	private String category;

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}