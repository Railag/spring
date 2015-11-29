package com.firrael.spring.pagination;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.DBObject;

@Document(collection = Review.COLLECTION_NAME)
public class Review implements Comparable<Review> {
	public static final String COLLECTION_NAME = "reviews";
	
	public static final String FORMAT = "dd/MMMM/yyyy HH:mm:ss";
	
	@Id
    private String id;
	
	private String author;
	private String message;
	private String contact;
	private String date;

	public Review(String author, String message, String contact, String date) {
		this.author = author;
		this.message = message;
		this.contact = contact;
		this.date = date;
	}
	
	public Review() {
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDate() {
		return date;
	}

	public String getFormattedDate() {
		return new SimpleDateFormat(FORMAT, Locale.getDefault()).format(date);
	}
	
	public void setFormattedDate(Date date) {
		this.date = new SimpleDateFormat(FORMAT, Locale.getDefault()).format(date);
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public int compareTo(Review secondReview) {
		return secondReview.getDate().compareTo(date);
	}
}