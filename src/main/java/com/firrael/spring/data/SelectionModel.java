package com.firrael.spring.data;

import java.util.ArrayList;

public class SelectionModel {
	private ArrayList<Channel> allChannels;
	private ArrayList<Category> allCategories;
	private ArrayList<Channel> selectedChannels;
	private ArrayList<Category> selectedCategories;
	
	public SelectionModel() {
		selectedChannels = new ArrayList<>();
		selectedCategories = new ArrayList<>();
	}
	
	public ArrayList<Channel> getAllChannels() {
		return allChannels;
	}
	public void setAllChannels(ArrayList<Channel> allChannels) {
		this.allChannels = allChannels;
	}
	public ArrayList<Category> getAllCategories() {
		return allCategories;
	}
	public void setAllCategories(ArrayList<Category> allCategories) {
		this.allCategories = allCategories;
	}
	
	public ArrayList<Channel> getSelectedChannels() {
		return selectedChannels;
	}
	public void setSelectedChannels(ArrayList<Channel> selectedChannels) {
		this.selectedChannels = selectedChannels;
	}
	public ArrayList<Category> getSelectedCategories() {
		return selectedCategories;
	}
	public void setSelectedCategories(ArrayList<Category> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}
}