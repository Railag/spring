package com.firrael.spring.data.models;

import java.util.List;

import com.firrael.spring.data.Category;
import com.firrael.spring.data.Channel;

public class SelectionModel {
	private List<Channel> allChannels;
	private List<Category> allCategories;
	private List<Channel> selectedChannels;
	private List<Category> selectedCategories;
	
	public SelectionModel() {
	}
	
	public List<Channel> getAllChannels() {
		return allChannels;
	}
	public void setAllChannels(List<Channel> allChannels) {
		this.allChannels = allChannels;
	}
	public List<Category> getAllCategories() {
		return allCategories;
	}
	public void setAllCategories(List<Category> categories) {
		this.allCategories = categories;
	}
	
	public List<Channel> getSelectedChannels() {
		return selectedChannels;
	}
	public void setSelectedChannels(List<Channel> selectedChannels) {
		this.selectedChannels = selectedChannels;
	}
	public List<Category> getSelectedCategories() {
		return selectedCategories;
	}
	public void setSelectedCategories(List<Category> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}
}