package com.firrael.spring.data.storage;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

public class CategoryFactory {
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String SUBS = "subcategories";
	
	public static Category buildCategory(DBObject source) {
		
		ObjectId objId = (ObjectId) source.get(ID);
		String id = objId.toString();
		String name = (String) source.get(NAME);
		List<SubCategory> subs = new ArrayList<>();
		BasicDBList subsList = (BasicDBList) source.get(SUBS);
		for (Object dbref : subsList) {
			DBRef ref = (DBRef) dbref; 
			SubCategory sub = MongoDB.getSubById(ref.getId().toString());
			subs.add(sub);
		}
		
		Category category = new Category(id, name, subs);
		
		return category;
	}
}