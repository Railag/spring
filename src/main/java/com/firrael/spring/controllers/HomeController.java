package com.firrael.spring.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.firrael.spring.data.Article;
import com.firrael.spring.data.ArticleStorage;
import com.firrael.spring.data.Category;
import com.firrael.spring.data.Channel;
import com.firrael.spring.data.Host;
import com.firrael.spring.data.Redis;
import com.firrael.spring.data.SelectionModel;
import com.firrael.spring.data.User;
import com.firrael.spring.data.User.AUTH;
import com.firrael.spring.data.UserStorage;
import com.firrael.spring.pagination.ArticlePage;
import com.firrael.spring.pagination.PageCreator;
import com.firrael.spring.pagination.UserPage;
import com.firrael.spring.parsing.HabrHandler;
import com.firrael.spring.utils.Utf8Serializer;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private final static int PULL_DELAY = 1000 * 60 * 5; // 5 mins

	private List<Article> articles = new ArrayList<>();

	private static Logger logger = Logger.getLogger(HomeController.class.getName());

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Resource(name = "redisTemplate")
	private ListOperations<String, String> listOps;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Locale locale, Model model) {
		return "login";
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String admin(Locale locale, Model model) {
		return "admin";
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register(Locale locale, Model model) {
		User user = new User();
		user.setEmail("test");
		user.setLoggedIn(true);
		user.setAuthToken("testToken");
		List<String> favArticleHashes = new ArrayList<>();
		user.setFavoriteArticleHashes(favArticleHashes);
		user.setLogin("user");
		user.setPassword("test password");
		List<String> selectedCategories = Redis.getAllCids();
		user.setSelectedCategories(selectedCategories);

		List<String> selectedChannels = Redis.getAllChids();
		user.setSelectedChannels(selectedChannels);

		Redis.saveUser(user);

		return "register";
	}

	@RequestMapping(value = "/selection", method = RequestMethod.GET)
	public String selection(Locale locale, Model model, Principal principal) {

		String login = principal.getName();

		UserStorage storage = new UserStorage();
		User user = storage.findUserByLogin(login);

		List<Channel> userChannels = Channel.stringsToChannels(Redis.getChannelsForUser(user));
		List<Category> userCategories = Category.stringsToCategories(Redis.getCategoriesForUser(user));

		List<Channel> allChannels = Channel.stringsToChannels(Redis.getAllChannels());
		List<Category> allCategories = Category.stringsToCategories(Redis.getAllCategories());

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

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String users(Locale locale, Model model, Principal principal, @RequestParam(required = false) Integer page) {

		// only for admin
		String login = principal.getName();

		UserStorage storage = new UserStorage();
		List<User> users = storage.getItems(100);

		model.addAttribute("users", users);

		List<UserPage> pages = (List<UserPage>) PageCreator.getPagingList(users, UserPage.class);

		model.addAttribute("pages", pages);

		if (page == null || page >= pages.size() || page < 0)
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		return "adminUsers";
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String home(Locale locale, Model model, Principal principal, @RequestParam(required = false) Integer page) {

		String login = principal != null ? principal.getName() : null;

		Redis.initialize(redisTemplate);

		logger.info("/home controller");

		articles = getCachedArticles(login);

		if (articles.isEmpty()) {
			articles = loadFeed();
			sortFeed();
		}

		List<ArticlePage> pages = (List<ArticlePage>) PageCreator.getPagingList(articles, ArticlePage.class);

		model.addAttribute("pages", pages);

		if (page == null || page >= pages.size() || page < 0)
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		return "home";
	}

	private List<Article> loadFeed() {
		Redis.initialize(redisTemplate);

		ArrayList<Article> articles = new ArrayList<>();
		articles.addAll(getFeed(Host.HABR_HOST));
		articles.addAll(getFeed(Host.GEEKTIMES_HOST));
		articles.addAll(getFeed(Host.MEGAMOZG_HOST));
		return articles;
	}

	// request feed every 5 minutes
	@Scheduled(fixedDelay = PULL_DELAY)
	@Async
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

	private void sortFeed() {
		Collections.sort(articles);
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
