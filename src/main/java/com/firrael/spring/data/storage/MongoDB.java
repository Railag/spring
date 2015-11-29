package com.firrael.spring.data.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.firrael.spring.pagination.Review;
import com.firrael.spring.pagination.ReviewFactory;
import com.firrael.spring.upload.ImageModel;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoDB {

	static Logger logger = Logger.getLogger(MongoDB.class);

	static MongoTemplate mongo;

	public static void initialize(MongoTemplate template) {
		mongo = template;
	}

	public static MongoTemplate getInstance() {
		return mongo;
	}

	public static void saveFile(ImageModel imageModel) {

		File file = new File("c://images/" + imageModel.getTitle() + ".jpg");

		try {
			imageModel.getFileImage().transferTo(file);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}

		DB db = mongo.getDb();
		GridFS gfsPhoto = new GridFS(db, "photo");
		GridFSInputFile gfsFile = null;
		try {
			gfsFile = gfsPhoto.createFile(file);
			gfsFile.setFilename(imageModel.getTitle());
			gfsFile.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<GridFSDBFile> getAllImages() {
		GridFS gfsPhoto = new GridFS(mongo.getDb(), "photo");

		BasicDBObject inQuery = new BasicDBObject();

		List<GridFSDBFile> files = gfsPhoto.find(inQuery);
		for (GridFSDBFile file : files) {
			logger.info(file.getFilename());
		}

		return files;
	}

	public static GridFSDBFile getImageByName(String name) {
		GridFS gfsPhoto = new GridFS(mongo.getDb(), "photo");

		BasicDBObject inQuery = new BasicDBObject();
		inQuery.put("filename", name);

		// List<File> fileList = new ArrayList<File>();

		return gfsPhoto.findOne(inQuery);
	}

	public static void saveReview(Review review) {
		mongo.save(review);
	}
	
	public static List<Review> getAllReviews() {
		List<Review> reviews = new ArrayList<>();
		DBCursor cursor = mongo.getCollection(Review.COLLECTION_NAME).find();
		try {
		   while(cursor.hasNext()) {
			   Review review = ReviewFactory.buildReview(cursor.next());
		       logger.info(review);
		       reviews.add(review);
		   }
		} finally {
		   cursor.close();
		}
		
		return reviews;
	}
}