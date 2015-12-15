package com.firrael.spring.pagination;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class ReviewFactory {
	public static final String ID = "_id";
	public static final String AUTHOR = "author";
	public static final String MESSAGE = "message";
	public static final String CONTACT = "contact";
	public static final String DATE = "date";
	public static final String HIDDEN = "hidden";
	
	public static Review buildReview(DBObject source) {
		
		ObjectId objId = (ObjectId) source.get(ID);
		String id = objId.toString();
		String author = (String) source.get(AUTHOR);
		String message = (String) source.get(MESSAGE);
		String contact = (String) source.get(CONTACT);
		String date = (String) source.get(DATE);
		int hidden = (int) source.get(HIDDEN);
		
		Review review = new Review(id, author, message, contact, date, hidden);
		
		return review;
	}
}