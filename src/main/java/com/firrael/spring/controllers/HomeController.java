package com.firrael.spring.controllers;

import java.io.IOException;
import java.io.StringReader;
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
		ArrayList<String> favArticleHashes = new ArrayList<>();
		favArticleHashes.add("412412");
		favArticleHashes.add("4125353");
		user.setFavoriteArticleHashes(favArticleHashes);
		user.setLogin("user");
		user.setPassword("test password");
		ArrayList<String> selectedCategories = new ArrayList<>();
		selectedCategories.add("2");
		selectedCategories.add("3");
		user.setSelectedCategories(selectedCategories);
		
		ArrayList<String> selectedChannels = new ArrayList<>();
		selectedChannels.add("1");
		selectedChannels.add("2");
		user.setSelectedChannels(selectedChannels);
		
		Redis.saveUser(user);
		
		return "register";
	}
	
	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return home(locale, model, 0);
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET, params = "page")
	public String home(Locale locale, Model model, @RequestParam int page) {

		Redis.initialize(redisTemplate);
		
		logger.info("/home controller");

		articles = getCachedArticles();

		if (articles.isEmpty())
			loadFeed();

		List<ArticlePage> pages = ArticlePage.getPagingList(articles);

		model.addAttribute("pages", pages);

		if (page >= pages.size())
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		return "home";
	}

	@Async
	private void loadFeed() {
		Redis.initialize(redisTemplate);
		
		getFeed(Host.HABR_HOST);
		getFeed(Host.GEEKTIMES_HOST);
		getFeed(Host.MEGAMOZG_HOST);

		sortFeed();
	}

	// request feed every 5 minutes
	@Scheduled(fixedDelay = PULL_DELAY)
	@Async
	private void pullNewArticles() {
		logger.info("feed updated");
		loadFeed();
	}

	private void cacheArticles(ArrayList<Article> articles) {
		Redis.saveArticles(articles);
	}

	private List<Article> getCachedArticles() {
//		ArticleStorage storage = new ArticleStorage();
//		return new ArrayList<>(storage.getItems(30));
		return Redis.getArticlesForUser("user");
	}

	private void sortFeed() {
	//	Collections.sort(articles);
	}

	private void getFeed(String host) {
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
			articles.addAll(newArticles);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
