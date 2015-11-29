package com.firrael.spring.controllers;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firrael.spring.data.Category;
import com.firrael.spring.data.Channel;
import com.firrael.spring.data.models.Article;
import com.firrael.spring.data.models.SelectionModel;
import com.firrael.spring.data.models.User;
import com.firrael.spring.data.storage.Redis;
import com.firrael.spring.data.storage.UserStorage;
import com.firrael.spring.pagination.ArticlePage;
import com.firrael.spring.pagination.PageCreator;
import com.firrael.spring.utils.Utf8Serializer;

@Controller
public class ProfileController {


	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String profile(Locale locale, Model model, Principal principal) {

		String login = principal.getName();

		UserStorage storage = new UserStorage();
		User user = storage.findUserByLogin(login);

		return "profile";
	}
	

	@RequestMapping(value = "/favorites", method = RequestMethod.GET)
	public String favorites(Locale locale, Model model, Principal principal, @RequestParam (required = false) Integer page) {

		String login = principal.getName();

		UserStorage storage = new UserStorage();
		User user = storage.findUserByLogin(login);
		
		model.addAttribute("user", user);

		List<Article> favArticles = Redis.getFavArticlesForUser(user);
		
		Collections.sort(favArticles);

		List<ArticlePage> pages = (List<ArticlePage>) PageCreator.getPagingList(favArticles, ArticlePage.class);

		model.addAttribute("pages", pages);

		if (page == null || page >= pages.size() || page < 0)
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		return "favorites";
	}
	
	@RequestMapping(value = "/selection", method = RequestMethod.GET)
	public String selection(Locale locale, Model model, Principal principal) {

		String login = principal.getName();

		UserStorage storage = new UserStorage();
		User user = storage.findUserByLogin(login);

		List<Channel> userChannels = Redis.getChannelsForUser(user);
		List<Category> userCategories = Redis.getCategoriesForUser(user);

		List<Channel> allChannels = Redis.getAllChannels();
		List<Category> allCategories = Redis.getAllCategories();

		Collections.sort(userChannels);
		Collections.sort(userCategories);
		Collections.sort(allChannels);
		Collections.sort(allCategories);

		SelectionModel selectionModel = new SelectionModel();
		selectionModel.setAllCategories(allCategories);
		selectionModel.setAllChannels(allChannels);
		selectionModel.setSelectedCategories(userCategories);
		selectionModel.setSelectedChannels(userChannels);

		model.addAttribute("selectionModel", selectionModel);

		return "selection";
	}

	@RequestMapping(value = { "/selection" }, method = RequestMethod.POST, params = "selectedChannels")
	public String selectionChannels(Locale locale, Model model, Principal principal,
			@RequestParam List<Channel> selectedChannels) {
		String login = principal.getName();

		UserStorage storage = new UserStorage();
		User user = storage.findUserByLogin(login);

		Redis.updateUserChannels(user, selectedChannels);

		return selection(locale, model, principal);
	}

	@RequestMapping(value = { "/selection" }, method = RequestMethod.POST, params = "selectedCategories")
	public String selectionCategories(Locale locale, Model model, Principal principal,
			@RequestParam List<Category> selectedCategories) {
		String login = principal.getName();

		Utf8Serializer serializer = new Utf8Serializer();

		for (Category c : selectedCategories) {
			c.setName(serializer.deserialize(c.getName()));
		}

		UserStorage storage = new UserStorage();
		User user = storage.findUserByLogin(login);

		Redis.updateUserCategories(user, selectedCategories);

		return selection(locale, model, principal);
	}
}
