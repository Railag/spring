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

	public static void saveArticles(List<Article> articles) {
		ArticleStorage.saveArticles(articles);
	}

	public static List<String> getChannelsForUser(User user) {
		return UserStorage.getChannelsForUser(user);
	}

	public static List<String> getCategoriesForUser(User user) {
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

	public static List<Article> getArticlesForUser(String login) {
		return UserStorage.getArticlesForUser(login);
	}

	public static String getChidForChannel(String channel) {
		return redisTemplate.opsForValue().get(RedisFields.CHANNEL_PREFIX + channel + RedisFields.CHANNEL_POSTFIX);
	}

	public static String getCidForCategory(String category) {
		return redisTemplate.opsForValue().get(RedisFields.CATEGORY_PREFIX + category + RedisFields.CATEGORY_POSTFIX);
	}

	public static String getChannelForChid(String chid) {
		return redisTemplate.opsForValue().get(RedisFields.CHANNEL_PREFIX + chid + RedisFields.CHANNEL_NAME_POSTFIX);
	}

	public static String getCategoryForCid(String cid) {
		return redisTemplate.opsForValue().get(RedisFields.CATEGORY_PREFIX + cid + RedisFields.CATEGORY_NAME_POSTFIX);
	}

	public static List<String> getChannelsForChids(List<String> chids) {
		List<String> channels = new ArrayList<>();
		for (String chid : chids)
			channels.add(getChannelForChid(chid));

		return channels;
	}

	public static List<String> getCategoriesForCids(List<String> cids) {
		List<String> categories = new ArrayList<>();
		for (String cid : cids)
			categories.add(getCategoryForCid(cid));

		return categories;
	}

	public static List<String> getAllCategories() {
		List<String> cidsList = getAllCids();
		return getCategoriesForCids(cidsList);
	}

	public static List<String> getAllChannels() {
		List<String> chidsList = getAllChids();
		return getChannelsForChids(chidsList);
	}

	public static List<String> getAllChids() {
		return new ArrayList<>(redisTemplate.opsForZSet().range(RedisFields.CHID_SET, 0, -1));
	}

	public static List<String> getAllCids() {
		return new ArrayList<>(redisTemplate.opsForZSet().range(RedisFields.CID_SET, 0, -1));
	}
}