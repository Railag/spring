package com.firrael.spring.controllers;

import java.io.IOException;
import java.io.StringReader;
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
import com.firrael.spring.data.Host;
import com.firrael.spring.data.Redis;
import com.firrael.spring.data.User;
import com.firrael.spring.data.User.AUTH;
import com.firrael.spring.data.UserStorage;
import com.firrael.spring.pagination.ArticlePage;
import com.firrael.spring.parsing.HabrHandler;

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
		List<String> selectedCategories = Redis.getAllCategories();
		user.setSelectedCategories(selectedCategories);

		List<String> selectedChannels = Redis.getAllChannels();
		user.setSelectedChannels(selectedChannels);

		Redis.saveUser(user);

		return "register";
	}

	@RequestMapping(value = "/selection", method = RequestMethod.GET)
	public String selection(Locale locale, Model model, Principal principal) {

		String login = principal.getName();

		UserStorage storage = new UserStorage();
		User user = storage.findUserByLogin(login);

		List<String> userChannels = Redis.getChannelsForUser(user);
		List<String> userCategories = Redis.getCategoriesForUser(user);

		model.addAttribute("userChannels", userChannels);
		model.addAttribute("userCategories", userCategories);

		List<String> allChannels = Redis.getAllChannels();
		List<String> allCategories = Redis.getAllCategories();

		model.addAttribute("allChannels", allChannels);
		model.addAttribute("allCategories", allCategories);

		return "selection";
	}

	@RequestMapping(value = { "/selection" }, method = RequestMethod.GET, params = "selectedCategories")
	public String selection(Locale locale, Model model, Principal principal,
			@RequestParam List<String> selectedCategories) {
		return "selection";
	}

	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String home(Locale locale, Model model, Principal principal) {
		return home(locale, model, principal, 0);
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET, params = "page")
	public String home(Locale locale, Model model, Principal principal, @RequestParam int page) {

		String login = principal != null ? principal.getName() : null;

		Redis.initialize(redisTemplate);

		logger.info("/home controller");

		articles = getCachedArticles(login);

		if (articles.isEmpty()) {
			articles = loadFeed();
			sortFeed();
		}

		List<ArticlePage> pages = ArticlePage.getPagingList(articles);

		model.addAttribute("pages", pages);

		if (page >= pages.size())
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
