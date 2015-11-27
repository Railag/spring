package com.firrael.spring.data.storage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;

import com.firrael.spring.data.Category;
import com.firrael.spring.data.Channel;
import com.firrael.spring.data.models.Article;
import com.firrael.spring.data.models.User;

public class Redis {

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
		for (String cid : cids)
			categories.add(getCategoryForCid(cid));

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