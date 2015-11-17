package com.firrael.spring.controllers;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartFilter;

import com.firrael.spring.upload.ImageModel;

@Controller
public class HomeController {

	private static Logger logger = Logger.getLogger(HomeController.class.getName());

	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String home(Locale locale, Model model, Principal principal) {

		ImageModel imageModel = new ImageModel();

		model.addAttribute("imageModel", imageModel);

		return "uploadImage";
	}
	
	@RequestMapping(value = { "/saveImage" }, method = RequestMethod.POST)
	public String saveImage(Locale locale, Model model, Principal principal, @ModelAttribute ImageModel imageModel){

		logger.info(imageModel.getImageID());
		logger.info("Image size: " + imageModel.getFileImage().getSize());
		logger.info("title: " + imageModel.getTitle());
		logger.info("text: " + imageModel.getDescription());
		
		// save to mongo
		
//		model.addAttribute("imageModel", imageModel);

		return "uploadImage";
	}
}