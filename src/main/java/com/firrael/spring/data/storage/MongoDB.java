package com.firrael.spring.data.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.firrael.spring.upload.ImageModel;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
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

		// List<File> fileList = new ArrayList<File>();

		List<GridFSDBFile> files = gfsPhoto.find(inQuery);
		for (GridFSDBFile file : files) {
			// File f = new File("c://images/placeholder" + file.getFilename() +
			// ".jpg");
			logger.info(file.getFilename());
			// try {
			// file.writeTo(f);
			// file.getInputStream()
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

			// fileList.add(f);
		}

		return files;
		// return fileList;

		// DBCursor cursor = gfsPhoto.getFileList();
		// while (cursor.hasNext()) {
		// logger.info(cursor.next());
		// }
	}

	public static GridFSDBFile getImageByName(String name) {
		GridFS gfsPhoto = new GridFS(mongo.getDb(), "photo");

		BasicDBObject inQuery = new BasicDBObject();
		inQuery.put("filename", name);

		// List<File> fileList = new ArrayList<File>();

		return gfsPhoto.findOne(inQuery);
	}
}