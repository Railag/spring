package com.firrael.spring;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
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
import org.xml.sax.SAXException;

import com.firrael.spring.xml.Article;
import com.firrael.spring.xml.HabrHandler;
import com.firrael.spring.xml.Rss;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private final static String HABR_HOST = "http://habrahabr.ru/rss";

	private static final String XML_FILE_NAME = "rss.xml";

	@Autowired
	private ApplicationContext context;

	private static Logger logger = Logger.getLogger(HomeController.class.getName());

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info(String.format("Welcome home! The client locale is {}.", locale));

//		Date date = new Date();
//		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
//
//		String formattedDate = dateFormat.format(date);
//
//		RestTemplate template = new RestTemplate();
//		//template.setMessageConverters(getMessageConverters());
//		Rss rss = null;
//		ResponseEntity<?> response = null;
//		try {
//			response = template.getForEntity(HABR_HOST, String.class);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		//logger.info(response);
		List<Article> articles = null;
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			HabrHandler handler = new HabrHandler();
			saxParser.parse(HABR_HOST, handler);
			articles = handler.getArticles();
			for (Article a : articles)
				logger.info(a);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		model.addAttribute("serverTime", articles.get(0).getDescription());

		return "home";
	}

	
}
