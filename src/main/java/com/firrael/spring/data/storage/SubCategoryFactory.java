package com.firrael.spring.data.storage;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

public class SubCategoryFactory {
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String IMAGES = "images";
	
	public static SubCategory buildSubCategory(DBObject source) {
		
		ObjectId objId = (ObjectId) source.get(ID);
		String id = objId.toString();
		String name = (String) source.get(NAME);
		List<Image> images = new ArrayList<>();
		BasicDBList imageList = (BasicDBList) source.get(IMAGES);
		for (Object dbref : imageList) {
			DBRef ref = (DBRef) dbref; 
			Image image = MongoDB.getImageById(ref.getId().toString());
			images.add(image);
		}
		
		
		
		SubCategory subCategory = new SubCategory(id, name, images);
		
		return subCategory;
	}
}
