package com.firrael.spring.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.firrael.spring.data.Host;
import com.firrael.spring.data.UserFields;
import com.firrael.spring.data.models.Article;
import com.firrael.spring.data.models.FavoriteJsonModel;
import com.firrael.spring.data.models.SimpleResponse;
import com.firrael.spring.data.models.User;
import com.firrael.spring.data.storage.ArticleStorage;
import com.firrael.spring.data.storage.Redis;
import com.firrael.spring.data.storage.UserStorage;
import com.firrael.spring.pagination.ArticlePage;
import com.firrael.spring.pagination.PageCreator;
import com.firrael.spring.parsing.HabrHandler;

@Controller
public class HomeController {

	private final static int PULL_DELAY = 1000 * 60 * 5; // 5 mins

	private static Logger logger = Logger.getLogger(HomeController.class.getName());

	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate<String, String> template;

	@RequestMapping(value = { "/category" }, method = RequestMethod.GET)
	public String category(Locale locale, Model model, @RequestParam(required = true) String category,
			@RequestParam(required = false) Integer page) {

		List<Article> articles = Redis.getArticlesForCategory(category);

		List<ArticlePage> pages = (List<ArticlePage>) PageCreator.getPagingList(articles, ArticlePage.class);

		model.addAttribute("pages", pages);

		if (page == null || page >= pages.size() || page < 0)
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		model.addAttribute("category", category);

		return "category";
	}

	@RequestMapping(value = { "/favoriteArticle" }, method = RequestMethod.POST)
	public @ResponseBody SimpleResponse favoriteArticle(Locale locale, Model model, Principal principal,
			@RequestBody FavoriteJsonModel jsonModel) {

		UserStorage storage = new UserStorage();
		User user = storage.get(jsonModel.getUid(), new UserFields());

		boolean nowFavorite = Redis.makeFavorite(user, jsonModel.getAid());

		SimpleResponse response = new SimpleResponse();
		response.setResponseJson("{\"nowFavorite\":\"" + nowFavorite + "\"}");

		return response;
	}

	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String home(Locale locale, Model model, Principal principal, @RequestParam(required = false) Integer page) {

		String login = principal != null ? principal.getName() : null;

		if (login != null) {
			UserStorage storage = new UserStorage();
			User user = storage.findUserByLogin(login);
			model.addAttribute("user", user);
		}

		Redis.initialize(template);

		logger.info("/home controller");

		List<Article> articles = getCachedArticles(login);

		if (articles.isEmpty()) {
			articles = loadFeed();
			Collections.sort(articles);
		}

		List<ArticlePage> pages = (List<ArticlePage>) PageCreator.getPagingList(articles, ArticlePage.class);

		model.addAttribute("pages", pages);

		if (page == null || page >= pages.size() || page < 0)
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		return "home";
	}

	private List<Article> loadFeed() {
		Redis.initialize(template);

		ArrayList<Article> articles = new ArrayList<>();
		articles.addAll(getFeed(Host.HABR_HOST));
		articles.addAll(getFeed(Host.GEEKTIMES_HOST));
		articles.addAll(getFeed(Host.MEGAMOZG_HOST));
		return articles;
	}

	// request feed every 5 minutes
	@Scheduled(fixedDelay = PULL_DELAY)

	private void pullNewArticles() {
		logger.info("feed updated");
		loadFeed();
	}

	private void cacheArticles(List<Article> articles) {
		Redis.saveArticles(articles);
	}

	private List<Article> getCachedArticles(String login) {
		if (login != null)
			return Redis.getArticlesForUser(login);
		else {
			ArticleStorage storage = new ArticleStorage();
			return new ArrayList<>(storage.getItems(100));
		}
	}

	private List<Article> getFeed(String host) {
		RestTemplate template = new RestTemplate();

		ResponseEntity<?> response = null;
		try {
			response = template.getForEntity(host, String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Article> newArticles = null;

		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			HabrHandler handler = new HabrHandler();
			saxParser.parse(new InputSource(new StringReader(response.getBody().toString())), handler);
			newArticles = handler.getArticles();
			cacheArticles(new ArrayList<>(newArticles));
			return newArticles;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();

	}

}
