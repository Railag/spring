package com.firrael.spring.data.storage;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.firrael.spring.data.Category;
import com.firrael.spring.data.Channel;
import com.firrael.spring.data.Host;
import com.firrael.spring.data.models.Article;
import com.firrael.spring.data.models.User;
import com.firrael.spring.parsing.HabrHandler;

public class Redis {
	
	public static Logger logger = Logger.getLogger(Redis.class);

	public static RedisTemplate<String, String> redisTemplate;

	public static void initialize(RedisTemplate<String, String> template) {
		Redis.redisTemplate = template;
	}

	public static RedisTemplate<String, String> getInstance() {
		return redisTemplate;
	}

	
	public static synchronized void saveArticles(List<Article> articles) {
		ArticleStorage.saveArticles(articles);
	}
	
	public static List<Article> getArticlesForCategory(String category) {
		return ArticleStorage.getArticlesForCategory(category);
	}
	
	public static void updateArticle(Article article) {
		ArticleStorage.updateArticle(article);
	}
	
	public static void removeArticle(String aid) {
		ArticleStorage.removeArticle(aid);
	}

	public static List<Channel> getChannelsForUser(User user) {
		return UserStorage.getChannelsForUser(user);
	}

	public static List<Category> getCategoriesForUser(User user) {
		return UserStorage.getCategoriesForUser(user);
	}

	public static void saveUser(User user) {
		UserStorage.saveUser(user);
	}

	public static void updateUserChannels(User user, List<Channel> selectedChannels) {
		UserStorage.updateUserChannels(user, selectedChannels);
	}

	public static void updateUserCategories(User user, List<Category> selectedCategories) {
		UserStorage.updateUserCategories(user, selectedCategories);
	}
	

	public static boolean makeFavorite(User user, String aid) {
		return UserStorage.toggleFavorite(user, aid);
	}

	public static List<Article> getArticlesForUser(String login) {
		return UserStorage.getArticlesForUser(login);
	}
	
	public static void removeUser(String uid) {
		UserStorage.removeUser(uid);
	}
	
	public static void updateUser(User user) {
		UserStorage.updateUser(user);
	}
	
	public static List<Article> getFavArticlesForUser(User user) {
		return UserStorage.getFavArticlesForUser(user);
	}
	
	public static List<Article> loadFeed() {
		ArrayList<Article> articles = new ArrayList<>();
		articles.addAll(getFeed(Host.HABR_HOST));
		articles.addAll(getFeed(Host.GEEKTIMES_HOST));
		articles.addAll(getFeed(Host.MEGAMOZG_HOST));
		return articles;
	}
	
	private static List<Article> getFeed(String host) {
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
			saveArticles(new ArrayList<>(newArticles));
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
	
	public static List<Article> getCachedArticles(String login) {
		if (login != null)
			return Redis.getArticlesForUser(login);
		else {
			ArticleStorage storage = new ArticleStorage();
			return new ArrayList<>(storage.getItems(100));
		}
	}
	
	// returns list with related CIDs
	public static List<String> searchForCategoryCids(final String search) {
		Iterable<byte[]> result = redisTemplate.execute(new RedisCallback<Iterable<byte[]>>() {

			  @Override
			  public Iterable<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {

			    List<byte[]> binaryKeys = new ArrayList<byte[]>();

			    ScanOptionsBuilder builder = new ScanOptionsBuilder();
			    builder.count(1000000);
			    builder.match(RedisFields.CATEGORY_PREFIX + "*" + search + "*" + RedisFields.CATEGORY_POSTFIX);
			    
			    Cursor<byte[]> cursor = connection.scan(builder.build());
			    while (cursor.hasNext()) {
			      binaryKeys.add(cursor.next());
			    }

			    try {
			      cursor.close();
			    } catch (IOException e) {
			    }

			    return binaryKeys;
			  }
			});
		
		List<String> cids = new ArrayList<>();
		
		for (byte[] value : result) {
			String key = redisTemplate.getStringSerializer().deserialize(value);
			String cid = redisTemplate.opsForValue().get(key);
			cids.add(cid);
			logger.info(key);
		}
		
		return cids;
	}

	public static String getChidForChannel(String channel) {
		return redisTemplate.opsForValue().get(RedisFields.CHANNEL_PREFIX + channel + RedisFields.CHANNEL_POSTFIX);
	}

	public static String getCidForCategory(String category) {
		return redisTemplate.opsForValue().get(RedisFields.CATEGORY_PREFIX + category + RedisFields.CATEGORY_POSTFIX);
	}

	public static Channel getChannelForChid(String chid) {
		String name = redisTemplate.opsForValue().get(RedisFields.CHANNEL_PREFIX + chid + RedisFields.CHANNEL_NAME_POSTFIX);
		Long count = redisTemplate.opsForZSet().size(RedisFields.CHANNEL + chid);
		return new Channel(name, count);
	}

	public static Category getCategoryForCid(String cid) {
		String name = redisTemplate.opsForValue().get(RedisFields.CATEGORY_PREFIX + cid + RedisFields.CATEGORY_NAME_POSTFIX);
		Long count = redisTemplate.opsForZSet().size(RedisFields.CATEGORY + cid);
		if ( count < 5)
			return null;
		
		return new Category(name, count);
	}

	public static List<Channel> getChannelsForChids(List<String> chids) {
		List<Channel> channels = new ArrayList<>();
		for (String chid : chids)
			channels.add(getChannelForChid(chid));

		return channels;
	}

	public static List<Category> getCategoriesForCids(List<String> cids) {
		List<Category> categories = new ArrayList<>();
		for (String cid : cids) {
			Category category = getCategoryForCid(cid);
			if (category != null)
				categories.add(category);
		}

		return categories;
	}

	public static List<Category> getAllCategories() {
		List<String> cidsList = getAllCids();
		return getCategoriesForCids(cidsList);
	}

	public static List<Channel> getAllChannels() {
		List<String> chidsList = getAllChids();
		return getChannelsForChids(chidsList);
	}

	public static List<String> getAllChids() {
		return new ArrayList<>(redisTemplate.opsForZSet().range(RedisFields.CHID_SET, 0, -1));
	}

	public static List<String> getAllCids() {
		return new ArrayList<>(redisTemplate.opsForZSet().range(RedisFields.CID_SET, 0, -1));
	}
	
	public static List<String> getAllUids() {
		return new ArrayList<> (redisTemplate.opsForZSet().range(RedisFields.UID_SET, 0, -1));
	}

	public static List<String> getAidsForCid(String cid) {
		return new ArrayList<>(redisTemplate.opsForZSet().range(RedisFields.CATEGORY + cid, 0, -1));
	}
}