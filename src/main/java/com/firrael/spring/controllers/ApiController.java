package com.firrael.spring.controllers;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.firrael.spring.api.ArticlesResponse;
import com.firrael.spring.data.Category;
import com.firrael.spring.data.Channel;
import com.firrael.spring.data.UserFields;
import com.firrael.spring.data.models.Article;
import com.firrael.spring.data.models.FavoriteJsonModel;
import com.firrael.spring.data.models.SelectionModel;
import com.firrael.spring.data.models.SimpleResponse;
import com.firrael.spring.data.models.User;
import com.firrael.spring.data.storage.Redis;
import com.firrael.spring.data.storage.UserStorage;
import com.firrael.spring.utils.Utf8Serializer;

@Controller
@RequestMapping("/api")
public class ApiController {

	@RequestMapping(value = { "/getArticles" }, method = RequestMethod.GET)
	public @ResponseBody ArticlesResponse getArticles(Locale locale, Model model, Principal principal,
			@RequestParam(required = false) Integer page, @RequestParam(required = false) String login,
			@RequestParam(required = false) String password) {

		// TODO ADD PAGING
		
		List<Article> articles = Redis.getCachedArticles(login);

		if (articles.isEmpty()) {
			articles = Redis.loadFeed();
		}

		Collections.sort(articles);

		ArticlesResponse response = new ArticlesResponse(articles);

		return response;
	}

	@RequestMapping(value = { "/getUserFavorites" }, method = RequestMethod.GET)
	public @ResponseBody ArticlesResponse getFavArticles(Locale locale, Model model, Principal principal,
			@RequestParam(required = false) Integer page, @RequestParam(required = false) String login,
			@RequestParam(required = false) String password) {

		// TODO ADD PAGING
		
		UserStorage storage = new UserStorage();
		User user = storage.findUserByLogin(login);

		List<Article> favArticles = Redis.getFavArticlesForUser(user);

		Collections.sort(favArticles);

		ArticlesResponse response = new ArticlesResponse(favArticles);

		return response;
	}
	
	@RequestMapping(value = { "/toggleFavoriteArticle" }, method = RequestMethod.GET)
	public @ResponseBody SimpleResponse favoriteArticle(Locale locale, Model model, Principal principal,
			@RequestBody FavoriteJsonModel jsonModel, @RequestParam(required = false) String login,
			@RequestParam(required = false) String password) {

		UserStorage storage = new UserStorage();
		User user = storage.get(jsonModel.getUid(), new UserFields());

		boolean nowFavorite = Redis.makeFavorite(user, jsonModel.getAid());

		SimpleResponse response = new SimpleResponse();
		response.setResponseJson("{\"nowFavorite\":\"" + nowFavorite + "\"}");

		return response;
	}
	
	@RequestMapping(value = "/selection", method = RequestMethod.GET)
	public @ResponseBody SelectionModel selection(Locale locale, Model model, Principal principal,
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password) {

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

		SelectionModel selectionModel = new SelectionModel(); // TODO replace SelectionModel with more lightweight model (no checked, only ids or titles)
		selectionModel.setAllCategories(allCategories);
		selectionModel.setAllChannels(allChannels);
		selectionModel.setSelectedCategories(userCategories);
		selectionModel.setSelectedChannels(userChannels);

		return selectionModel;
	}

	@RequestMapping(value = { "/updateSelectedChannels" }, method = RequestMethod.POST, params = "selectedChannels")
	public String selectionChannels(Locale locale, Model model, Principal principal,
			@RequestParam List<Channel> selectedChannels) {
		String login = principal.getName();

		UserStorage storage = new UserStorage();
		User user = storage.findUserByLogin(login);

		Redis.updateUserChannels(user, selectedChannels);

		return ""; // TODO some valid response
	}

	@RequestMapping(value = { "/updateSelectedCategories" }, method = RequestMethod.POST, params = "selectedCategories")
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

		return ""; // TODO some valid response
	}
	
	// TODO /login ? gain cookie and use session
}
