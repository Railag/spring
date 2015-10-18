package com.firrael.spring.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import com.firrael.spring.data.Redis;
import com.firrael.spring.pagination.ArticlePage;
import com.firrael.spring.parsing.HabrHandler;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private final static String HABR_HOST = "http://habrahabr.ru/rss";
	private final static String GEEKTIMES_HOST = "http://geektimes.ru/rss";
	private final static String MEGAMOZG_HOST = "http://megamozg.ru/rss";

	private final static int PULL_DELAY = 1000 * 60 * 5; // 5 mins

	private static int ARTICLES_COUNT = 0;

	private ArrayList<Article> articles = new ArrayList<>();

	@Autowired
	private ApplicationContext context;

	private static Logger logger = Logger.getLogger(HomeController.class.getName());

	@Autowired
	private RedisTemplate<String, String> template;

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
	
	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return home(locale, model, 0);
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET, params = "page")
	public String home(Locale locale, Model model, @RequestParam int page) {

		Redis.initialize(template);

		articles = getCachedArticles();

		if (articles.isEmpty())
			loadFeed();

		cacheArticles(articles);

		List<ArticlePage> pages = ArticlePage.getPagingList(articles);

		model.addAttribute("pages", pages);

		if (page >= pages.size())
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		return "home";
	}

	@Async
	private void loadFeed() {
		getFeed(HABR_HOST);
		getFeed(GEEKTIMES_HOST);
		getFeed(MEGAMOZG_HOST);

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
		ArticleStorage storage = new ArticleStorage();
		for (Article a : articles)
			storage.add(a);
	}

	private ArrayList<Article> getCachedArticles() {
		ArticleStorage storage = new ArticleStorage();
		return new ArrayList<>(storage.getItems(30));
	}

	private void sortFeed() {
		Collections.sort(articles);
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
