package com.firrael.spring.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.firrael.spring.data.storage.MongoDB;
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
}