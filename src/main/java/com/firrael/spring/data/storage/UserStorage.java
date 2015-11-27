package com.firrael.spring.data.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.firrael.spring.data.ArticleFields;
import com.firrael.spring.data.Category;
import com.firrael.spring.data.Channel;
import com.firrael.spring.data.UserFields;
import com.firrael.spring.data.base.Storage;
import com.firrael.spring.data.models.Article;
import com.firrael.spring.data.models.User;
import com.firrael.spring.utils.ListSerializer;

@Service("userDetailsService")
public class UserStorage implements Storage<User, UserFields>, UserDetailsService {

	private static Logger logger = Logger.getLogger(ArticleStorage.class.getName());

	@Override
	public void add(User user, String uid) {
		String key = String.format("%s%s", RedisFields.USER, uid);
		RedisTemplate<String, String> template = Redis.getInstance();
		template.opsForHash().putAll(key, user.toHashMap());
	}

	@Override
	public User get(String uid, UserFields userFields) {
		String key = String.format("%s%s", RedisFields.USER, uid);
		List<Object> fields = userFields.asArray();
		RedisTemplate<String, String> template = Redis.getInstance();
		List<Object> values = template.opsForHash().multiGet(key, fields);
		User user = new User().initialize(values);
		user.setUid(uid);
		return user;
	}

	public User findUserByLogin(String login) {
		String uid = getUidForLogin(login);
		return get(uid, new UserFields());
	}

	@Override
	public List<User> getItems(final int count) {
		ArrayList<User> cachedUsers = new ArrayList<>();

		RedisTemplate<String, String> template = Redis.getInstance();
		String userCounter = template.opsForValue().get("global:uid");
		Double counter = Double.parseDouble(userCounter);
		Set<TypedTuple<String>> set = template.opsForZSet().rangeByScoreWithScores("uids", counter - count, counter);
		for (TypedTuple<String> tt : set) {
			String uid = tt.getValue();
			User user = get(uid, new UserFields());
			cachedUsers.add(user);
		}
		return cachedUsers;
	}

	public static void saveUser(User user) {
		RedisTemplate<String, String> template = Redis.getInstance();

		String uid = template.opsForValue().get(RedisFields.USER_PREFIX + user.getLogin() + RedisFields.USER_POSTFIX);
		if (uid == null) { // new user
			uid = template.opsForValue().get(RedisFields.GLOBAL_USER_ID);
			if (uid == null) {
				uid = "1";
				template.opsForValue().increment(RedisFields.GLOBAL_USER_ID, 1);
			}
			template.opsForValue().set(RedisFields.USER_PREFIX + user.getLogin() + RedisFields.USER_POSTFIX, uid);
			template.opsForValue().increment(RedisFields.GLOBAL_USER_ID, 1);

			UserStorage storage = new UserStorage();
			storage.add(user, uid);

			template.opsForZSet().add(RedisFields.UID_SET, uid, Double.parseDouble(uid));
		}
	}

	public static void updateUserChannels(User user, List<Channel> selectedChannels) {
		RedisTemplate<String, String> template = Redis.getInstance();

		List<String> chids = new ArrayList<>();
		for (Channel c : selectedChannels)
			chids.add(Redis.getChidForChannel(c.getName()));
		user.setSelectedChannels(chids);

		String key = String.format("%s:%s", "uid", user.getUid());
		template.opsForHash().put(key, UserFields.SELECTED_CHANNELS, ListSerializer.getInstance().serialize(chids));
	}

	public static void updateUserCategories(User user, List<Category> selectedCategories) {
		RedisTemplate<String, String> template = Redis.getInstance();

		List<String> cids = new ArrayList<>();
		for (Category c : selectedCategories)
			cids.add(Redis.getCidForCategory(c.getName()));
		user.setSelectedCategories(cids);

		String key = String.format("%s:%s", "uid", user.getUid());
		template.opsForHash().put(key, UserFields.SELECTED_CATEGORIES, ListSerializer.getInstance().serialize(cids));
	}

	public static List<Channel> getChannelsForUser(User user) {
		List<String> chids = user.getSelectedChannels();
		return Redis.getChannelsForChids(chids);
	}

	public static List<Category> getCategoriesForUser(User user) {
		List<String> cids = user.getSelectedCategories();
		return Redis.getCategoriesForCids(cids);
	}

	public static String getUidForLogin(String login) {
		RedisTemplate<String, String> template = Redis.getInstance();

		return template.opsForValue().get(RedisFields.USER_PREFIX + login + RedisFields.USER_POSTFIX);
	}

	public static List<Article> getArticlesForUser(String login) {
		String uid = getUidForLogin(login);

		UserStorage storage = new UserStorage();
		User user = storage.get(uid, new UserFields());

		return getFilteredArticlesForUser(user);
	}

	private static List<Article> getFilteredArticlesForUser(User user) {
		RedisTemplate<String, String> template = Redis.getInstance();

		// based on categories and channels

		// getting union of all selected categories aids and intersecting them
		// with all selecting channels aids.

		List<String> categories = user.getSelectedCategories(); // cids
		List<String> categoryKeys = new ArrayList<>();
		for (String category : categories)
			categoryKeys.add(RedisFields.CATEGORY + category);

		boolean isInTempCategories = false;
		if (categoryKeys.size() > 1) {
			Long count = template.opsForZSet().unionAndStore(categoryKeys.get(0),
					categoryKeys.subList(1, categoryKeys.size()), RedisFields.CATEGORY_TEMP + user.getUid());
			logger.info("Total selected user articles for categories: " + count);
			isInTempCategories = true;
		}

		List<String> channels = user.getSelectedChannels(); // chids
		List<String> channelKeys = new ArrayList<>();
		for (String channel : channels)
			channelKeys.add(RedisFields.CHANNEL + channel);

		boolean isInTempChannels = false;
		if (channelKeys.size() > 1) {
			Long count = template.opsForZSet().unionAndStore(channelKeys.get(0),
					channelKeys.subList(1, channelKeys.size()), RedisFields.CHANNEL_TEMP + user.getUid());
			logger.info("Total selected user articles for channels: " + count);
			isInTempChannels = true;
		}

		String articlesForSelectedCategoriesKey = isInTempCategories ? RedisFields.CATEGORY_TEMP + user.getUid()
				: categoryKeys.get(0);
		String articlesForSelectedChannelsKey = isInTempChannels ? RedisFields.CHANNEL_TEMP + user.getUid()
				: channelKeys.get(0);

		String filteredArticlesKey = RedisFields.ARTICLES_TEMP + user.getUid();

		Long count = template.opsForZSet().intersectAndStore(articlesForSelectedCategoriesKey,
				articlesForSelectedChannelsKey, filteredArticlesKey);
		logger.info("Total filtered user articles: " + count);

		Set<String> filteredAids = template.opsForZSet().range(filteredArticlesKey, 0, -1);
		ArticleStorage storage = new ArticleStorage();
		ArticleFields fields = new ArticleFields();

		ArrayList<Article> filteredArticles = new ArrayList<>();
		for (String aid : filteredAids)
			filteredArticles.add(storage.get(aid, fields));

		return filteredArticles;
	}

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		String uid = getUidForLogin(login);
		User user = get(uid, new UserFields());

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(
				user.getEmail(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, user.getRole().getAuthorities());

		logger.info("logged: " + user.getLogin());

		return securityUser;
	}

	public static boolean toggleFavorite(User user, String aid) {
		RedisTemplate<String, String> template = Redis.getInstance();

		List<String> favoriteHashes = user.getFavoriteArticleHashes();
		
		boolean nowFavorite;
		
		if (favoriteHashes.contains(aid)) {
			favoriteHashes.remove(aid);
			nowFavorite = false;
		} else {
			favoriteHashes.add(aid);
			nowFavorite = true;
		}
		
		String key = String.format("%s:%s", "uid", user.getUid());
		template.opsForHash().put(key, UserFields.FAVORITE_ARTICLES, ListSerializer.getInstance().serialize(user.getFavoriteArticleHashes()));
	
		return nowFavorite;
	}

	public void removeFavorite(User user, String aid) {
		RedisTemplate<String, String> template = Redis.getInstance();
		
		List<String> favoriteHashes = user.getFavoriteArticleHashes();
		
		if (favoriteHashes.contains(aid)) {
			favoriteHashes.remove(aid);
			String key = String.format("%s:%s", "uid", user.getUid());
			template.opsForHash().put(key, UserFields.FAVORITE_ARTICLES, ListSerializer.getInstance().serialize(user.getFavoriteArticleHashes()));
		} 
	}
}
