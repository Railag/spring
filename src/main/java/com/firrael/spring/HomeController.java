package com.firrael.spring;

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
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.firrael.spring.xml.Article;
import com.firrael.spring.xml.ArticleFields;
import com.firrael.spring.xml.HabrHandler;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private final static String HABR_HOST = "http://habrahabr.ru/rss";
	private final static String GEEKTIMES_HOST = "http://geektimes.ru/rss";
	private final static String MEGAMOZG_HOST = "http://megamozg.ru/rss";

	private final static String ARTICLE_KEY = "article";

	private static int ARTICLES_COUNT = 0;

	private ArrayList<Article> articles = new ArrayList<>();

	@Autowired
	private ApplicationContext context;

	private static Logger logger = Logger.getLogger(HomeController.class.getName());

	@Autowired
	private RedisTemplate<String, String> template;

	@Resource(name = "redisTemplate")
	private ListOperations<String, String> listOps;

	public void addArticle(Article article) {
		// listOps.leftPush(userId, url.toExternalForm());

		// template.boundListOps(userId).leftPush(url.toExternalForm());
		String key = String.format("%s:%s", ARTICLE_KEY, article.hashCode());
		template.opsForHash().putAll(key, article.toHashMap());
		template.opsForValue().increment("count", 1);
	}

	public Article getArticle(String hash) {
		List<Object> fields = ArticleFields.asArray();
		List<Object> values = template.opsForHash().multiGet(hash, fields);
		Article article = Article.create(values);

		return article;
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {

		articles = getCachedArticles();

		if (articles.isEmpty()) {

			getFeed(HABR_HOST);
			getFeed(GEEKTIMES_HOST);
			getFeed(MEGAMOZG_HOST);

			sortFeed();
		}

		cacheArticles(articles);

		model.addAttribute("articles", articles);

		return "home";
	}

	private void cacheArticles(ArrayList<Article> articles) {
		for (Article a : articles)
			addArticle(a);
	}

	private ArrayList<Article> getCachedArticles() {
		ArrayList<Article> cachedArticles = new ArrayList<>();
		Iterable<byte[]> results = template.execute(new RedisCallback<Iterable<byte[]>>() {

			@Override
			public Iterable<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {

				List<byte[]> binaryKeys = new ArrayList<byte[]>();

				ScanOptionsBuilder builder = new ScanOptionsBuilder();
				builder.count(30);
				builder.match(ARTICLE_KEY + ":*");
				ScanOptions options = builder.build();

				Cursor<byte[]> cursor = connection.scan(options);
				while (cursor.hasNext()) {
					binaryKeys.add(cursor.next());
				}

				try {
					cursor.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return binaryKeys;
			}
		});

		List<String> hashes = new ArrayList<String>();

		for (byte[] b : results)
			hashes.add(new String(b));

		for (String hash : hashes)
			cachedArticles.add(getArticle(hash));

		return cachedArticles;
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
