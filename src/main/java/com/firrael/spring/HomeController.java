package com.firrael.spring;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.firrael.spring.xml.Article;
import com.firrael.spring.xml.HabrHandler;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private final static String HABR_HOST = "http://habrahabr.ru/rss";
	private final static String GEEKTIMES_HOST = "http://geektimes.ru/rss";
	private final static String MEGAMOZG_HOST = "http://megamozg.ru/rss";

	private ArrayList<Article> articles = new ArrayList<>();

	@Autowired
	private ApplicationContext context;

	private static Logger logger = Logger.getLogger(HomeController.class.getName());

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {

		if (articles.isEmpty()) {

			getFeed(HABR_HOST);
			getFeed(GEEKTIMES_HOST);
			getFeed(MEGAMOZG_HOST);

			sortFeed();
		}

		model.addAttribute("articles", articles);

		return "home";
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
			for (Article a : newArticles)
				logger.info(a);
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
