package com.firrael.spring.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.firrael.spring.data.storage.MongoDB;
import com.firrael.spring.pagination.PageCreator;
import com.firrael.spring.pagination.Review;
import com.firrael.spring.pagination.ReviewPage;
import com.firrael.spring.upload.ImageModel;
import com.mongodb.gridfs.GridFSDBFile;

@Controller
public class HomeController {

	private static Logger logger = Logger.getLogger(HomeController.class.getName());

	@Autowired
	MongoTemplate mongoTemplate;

	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String home(Locale locale, Model model, Principal principal) {

		MongoDB.initialize(mongoTemplate);

		return "home";
	}

	@RequestMapping(value = { "/uploadImage" }, method = RequestMethod.GET)
	public String uploadImage(Locale locale, Model model, Principal principal) {

		MongoDB.initialize(mongoTemplate);

		ImageModel imageModel = new ImageModel();

		model.addAttribute("imageModel", imageModel);

		return "uploadImage";
	}

	@RequestMapping(value = "/getImage", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getPhoto(@RequestParam("fileName") String fileName) throws IOException {
		GridFSDBFile image = MongoDB.getImageByName(fileName);

		InputStream imageStream = image.getInputStream();
		return IOUtils.toByteArray(imageStream);
	}

	@RequestMapping(value = { "/saveImage" }, method = RequestMethod.POST)
	public String saveImage(Locale locale, Model model, Principal principal, @ModelAttribute ImageModel imageModel) {

		logger.info(imageModel.getImageID());
		logger.info("Image size: " + imageModel.getFileImage().getSize());
		logger.info("title: " + imageModel.getTitle());
		logger.info("text: " + imageModel.getDescription());

		MongoDB.saveFile(imageModel);

		List<GridFSDBFile> images = MongoDB.getAllImages();

		List<String> imageNames = new ArrayList<String>();

		for (GridFSDBFile image : images) {
			imageNames.add(image.getFilename());
		}

		// save to mongo

		model.addAttribute("imageNames", imageNames);

		return "showImages";
	}

	@RequestMapping(value = { "/images" }, method = RequestMethod.GET)
	public String images(Locale locale, Model model, Principal principal) {

		MongoDB.initialize(mongoTemplate);

		List<GridFSDBFile> images = MongoDB.getAllImages();

		List<String> imageNames = new ArrayList<String>();

		for (GridFSDBFile image : images) {
			imageNames.add(image.getFilename());
		}

		// save to mongo

		model.addAttribute("imageNames", imageNames);

		return "showImages";
	}

	@RequestMapping(value = { "/review" }, method = RequestMethod.GET)
	public String review(Locale locale, Model model, Principal principal,
			@RequestParam(required = false) Integer page) {
		Review review = new Review();

		model.addAttribute("review", review);

		MongoDB.initialize(mongoTemplate);

		List<Review> reviews = MongoDB.getAllReviews();

		Collections.sort(reviews);

		List<ReviewPage> pages = (List<ReviewPage>) PageCreator.getPagingList(reviews, ReviewPage.class);

		model.addAttribute("pages", pages);

		if (page == null || page >= pages.size() || page < 0)
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		return "review";
	}

	@RequestMapping(value = "/addReviewAsync", method = RequestMethod.POST)
	public String addReviewAsync(@ModelAttribute("review") Review review, BindingResult result, Model model) {
		MongoDB.initialize(mongoTemplate);

		review.setFormattedDate(new Date());

		MongoDB.saveReview(review);

		model.addAttribute("review", review);

		return "review :: review";
	}

	@RequestMapping(value = { "/addReview" }, method = RequestMethod.POST)
	public String addReview(Locale locale, Model model, Principal principal, @ModelAttribute("review") Review review,
			BindingResult result) {

		MongoDB.initialize(mongoTemplate);

		review.setFormattedDate(new Date());

		MongoDB.saveReview(review);

		return "redirect:/reviews";
	}

	@RequestMapping(value = { "/reviews" }, method = RequestMethod.GET)
	public String reviews(Locale locale, Model model, Principal principal,
			@RequestParam(required = false) Integer page) {

		MongoDB.initialize(mongoTemplate);

		List<Review> reviews = MongoDB.getAllReviews();

		Collections.sort(reviews);

		List<ReviewPage> pages = (List<ReviewPage>) PageCreator.getPagingList(reviews, ReviewPage.class);

		model.addAttribute("pages", pages);

		if (page == null || page >= pages.size() || page < 0)
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		return "reviews";
	}
}