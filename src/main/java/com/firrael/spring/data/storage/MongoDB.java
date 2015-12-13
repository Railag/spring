package com.firrael.spring.data.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.firrael.spring.pagination.ImageFactory;
import com.firrael.spring.pagination.Review;
import com.firrael.spring.pagination.ReviewFactory;
import com.firrael.spring.upload.CategoryModel;
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

		File file = new File("c://images/" + imageModel.getName() + ".jpg");

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
			// TODO сюда категорию и сабкатегорию передавать
			gfsFile.setFilename(imageModel.getName());

			gfsFile.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Image> getAllImages() {
		GridFS gfsPhoto = new GridFS(mongo.getDb(), "photo");

		BasicDBObject inQuery = new BasicDBObject();

		List<Image> images = new ArrayList<>();

		List<GridFSDBFile> files = gfsPhoto.find(inQuery);

		for (GridFSDBFile file : files) {
			logger.info(file.getFilename());
			Image image = ImageFactory.buildImage(file);
			images.add(image);
		}

		return images;
	}

	public static Image getImageByName(String name) {
		GridFS gfsPhoto = new GridFS(mongo.getDb(), "photo");

		BasicDBObject inQuery = new BasicDBObject();
		inQuery.put("filename", name);

		// List<File> fileList = new ArrayList<File>();

		Image image = ImageFactory.buildImage(gfsPhoto.findOne(inQuery));

		return image;
	}

	public static void saveReview(Review review) {
		mongo.save(review);
	}

	public static void saveCategory(CategoryModel categoryModel) {
		Category category = new Category(categoryModel.getCategory(), new ArrayList<SubCategory>());
		mongo.save(category);
		saveFile(categoryModel);
	}

	private static void saveFile(CategoryModel categoryModel) {

		File file = new File("c://images/" + categoryModel.getCategory() + ".jpg");

		try {
			categoryModel.getFileImage().transferTo(file);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}

		DB db = mongo.getDb();
		GridFS gfsPhoto = new GridFS(db, "photo");
		GridFSInputFile gfsFile = null;
		try {
			gfsFile = gfsPhoto.createFile(file);
			gfsFile.setFilename(categoryModel.getCategory());

			gfsFile.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveSubCategory(SubCategory sub) {
		mongo.save(sub);
	}

	public static List<Review> getAllReviews() {
		List<Review> reviews = new ArrayList<>();
		DBCursor cursor = mongo.getCollection(Review.COLLECTION_NAME).find();
		try {
			while (cursor.hasNext()) {
				Review review = ReviewFactory.buildReview(cursor.next());
				reviews.add(review);
			}
		} finally {
			cursor.close();
		}

		return reviews;
	}

	public static List<Category> getAllCategories() {
		List<Category> categories = new ArrayList<>();
		DBCursor cursor = mongo.getCollection(Category.COLLECTION_NAME).find();
		try {
			while (cursor.hasNext()) {
				Category category = CategoryFactory.buildCategory(cursor.next());
				categories.add(category);
			}
		} finally {
			cursor.close();
		}

		return categories;
	}

	public static List<SubCategory> getAllSubCategories() {
		List<SubCategory> subCategories = new ArrayList<>();
		DBCursor cursor = mongo.getCollection(SubCategory.COLLECTION_NAME).find();
		try {
			while (cursor.hasNext()) {
				SubCategory subCategory = SubCategoryFactory.buildSubCategory(cursor.next());
				subCategories.add(subCategory);
			}
		} finally {
			cursor.close();
		}
		
		subCategories.add(new SubCategory("test", new ArrayList<Image>()));

		return subCategories;
	}
}