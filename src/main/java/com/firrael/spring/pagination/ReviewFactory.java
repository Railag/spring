package com.firrael.spring.pagination;

import com.mongodb.DBObject;

public class ReviewFactory {
	public static final String AUTHOR = "author";
	public static final String MESSAGE = "message";
	public static final String CONTACT = "contact";
	public static final String DATE = "date";
	
	public static Review buildReview(DBObject source) {
		String author = (String) source.get(AUTHOR);
		String message = (String) source.get(MESSAGE);
		String contact = (String) source.get(CONTACT);
		String date = (String) source.get(DATE);
		
		Review review = new Review(author, message, contact, date);
		
		return review;
	}
}