package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.data.redis.core.RedisTemplate;

import com.firrael.spring.utils.ListSerializer;

public class Redis {

	private final static String GLOBAL_ARTICLE_ID = "global:aid"; // article
																	// counter
	private final static String GLOBAL_USER_ID = "global:uid"; // user counter

	private final static String ARTICLE = "aid:"; // aid : 5
	private final static String USER = "uid:"; // uid: 15
	private final static String CATEGORY = "cid:"; // cid : 25
	private final static String CHANNEL = "chid:"; // chid : 35

	private final static String AID_SET = "aids"; // zset for all aids
	private final static String UID_SET = "uids"; // zset for all uids
	private final static String CID_SET = "cids:"; // zset for all cids
	private final static String CHID_SET = "chids:"; // zset for all chids

	private final static String USER_PREFIX = "user:"; // to get user by input
														// login via
														// user:<name>:uid
	private final static String ARTICLE_PREFIX = "article:";
	private final static String CATEGORY_PREFIX = "category:"; // to get
																// category name
																// by cid via
																// category:<cid>
	private final static String CHANNEL_PREFIX = "channel:"; // to get channel
																// name by chid
																// via
																// channel:<chid>

	private final static String USER_POSTFIX = ":uid";
	private final static String ARTICLE_POSTFIX = ":aid";
	private final static String CATEGORY_POSTFIX = ":cid";
	private final static String CHANNEL_POSTFIX = ":chid";

	private final static String CATEGORY_NAME_POSTFIX = ":name";
	private final static String CHANNEL_NAME_POSTFIX = ":name";

	private final static String CHANNEL_TEMP = "chtmp:";
	private final static String CATEGORY_TEMP = "ctmp:";
	private final static String ARTICLES_TEMP = "atmp:";

	private static Logger logger = Logger.getLogger(Redis.class.getName());

	// uid:<uid>:favArticles list с избранными статьями

	// cid:<cid>

	// category:<cid> returns category name

	// в uid list с cid выбранных категорий

	/**
	 * User not logged in => all feeds, all categories are shown
	 * 
	 * User is logged in => get user login => get user uid via user:<login>:uid
	 * value => take user info and cache it in some Profile class for current
	 * user in order not to take it from redis every time Info to take: channel
	 * preferences (chid list in uid hash) category preferences (cid list in uid
	 * hash) favArticles (aid list in uid hash)
	 * 
	 * Loading feed: if user is not logged in => just take latest aids using
	 * global:aid counter OR MAYBE take all possible channels chid and load
	 * (articlesCount/channelsCount) latest articles from every channel. place
	 * them in sorted set in redis, get them and remove the list
	 * 
	 * if user is logged in => for all chid from uid hash: get all articles for
	 * channel via chid (chid:<chid> contains list of aid-s) and create sorted
	 * list (List#1) then for all cid from uid hash: get cid list via cid:
	 * <cid> which contains aids of included articles, create sorted list
	 * (List#2) and intersect (List#2) with (List#1) in order to get all
	 * articles which are present in user's selected categories from user's
	 * selected channels.
	 * 
	 * Models:
	 * 
	 * User: { login : test, password: test123!, email : test@test.com,
	 * favArticles : [1, 2, 15, 165, ..], // aid selectedCategories : [1, 2, 7,
	 * ..], // cid selectedChannels : [1, 2, ..], // chid }
	 * 
	 * Article: { title : Title, link : http://habrahabr.ru/135678, description
	 * : LongLongText, date : Thu, 01 Oct 2015 07:10:00 GMT, // or convert it to
	 * unix time author : firrael, categories : [1, 2, 5, ..] // we can get
	 * categories names via category:<cid> }
	 * 
	 * Category: not a hash, just value cid : category name
	 * 
	 * Channel: not a hash, just value chid : channel name
	 * 
	 * Saving pulled data:
	 * 
	 * Save all articles names to article:<title>:aid in order to check if
	 * article is already in db => Save all articles by their aid-s => Increase
	 * aid-s counter => Add new categories to cid list if they are not already
	 * there => Increase cid-s counter => Add all new articles to cid's lists =>
	 * Add new channels to chid list if they are not already there => Increase
	 * chid-s counter => Add all new articles to chid's lists.
	 * 
	 * 
	 * Structures:
	 * 
	 * Article counter global:aid Article hashes from 1 to counter aid:<aid> Can
	 * be connected with article via article:<title>:aid
	 * 
	 * 
	 * User counter global:uid User hashes from 1 to counter uid:<uid> Can be
	 * connected with user login via user:<login>:uid (and then uid:<uid>)
	 * 
	 * Category lists from 1 to counter cid:<cid> Can be connected with category
	 * title via cid:<cid>:title
	 * 
	 * Channel lists from 1 to counter chid:<chid> Can be connected with channel
	 * name via chid:<chid>:name
	 * 
	 * User is logged in => get user by login => get articles for him sorting by
	 * cid and chid from user preferences. User is not logged in => show latest
	 * articles in range FROM (article counter - articlesNumber) TO
	 * (articlesNumber).
	 * 
	 * Favorite articles can be shown to user on different page, e.g. /favorite.
	 * 
	 * User hash holds list of selected cid-s and chid-s. On user profile page
	 * (e.g. /profile) user will see all possible categories and channels and
	 * will have an ability to uncheck useless categories or channels. For new
	 * user all categories and channels will be pre-selected. So, hash for new
	 * user will contain cid-s of all categories and chid-s of all channels.
	 */

	/*
	 * Save all articles names to article:<title>:aid in order to check if
	 * article is already in db => + Save all articles by their aid-s => +
	 * Increase aid-s counter => + Add new categories to cid list if they are
	 * not already there => + Add all new articles to cid's lists => +? Add new
	 * channels to chid list if they are not already there => + Add all new
	 * articles to chid's lists. +?
	 */
	public static void saveArticles(List<Article> articles) {
		for (Article a : articles) {
			String aid = getInstance().opsForValue().get(ARTICLE_PREFIX + a.getTitle().hashCode() + ARTICLE_POSTFIX);
			if (aid == null) // new article
				saveArticle(a);
		}
	}

	private static void saveArticle(Article article) {
		String aid = redisTemplate.opsForValue().get(GLOBAL_ARTICLE_ID);
		if (aid == null) {
			aid = "1";
			redisTemplate.opsForValue().increment(GLOBAL_ARTICLE_ID, 1);
		}
		redisTemplate.opsForValue().set(ARTICLE_PREFIX + article.getTitle().hashCode() + ARTICLE_POSTFIX, aid);
		redisTemplate.opsForValue().increment(GLOBAL_ARTICLE_ID, 1);
		ArticleStorage storage = new ArticleStorage();
		storage.add(article, aid);

		redisTemplate.opsForZSet().add(AID_SET, aid, Double.parseDouble(aid));

		addCategories(article.getCategories(), aid);

		addChannel(article.getHost(), aid);
	}

	private static void addCategories(List<String> categories, String aid) {
		Set<String> cids = redisTemplate.opsForZSet().range(CID_SET, 0, -1);

		int currentCid = 0;

		for (String category : categories) {
			boolean exists = false;

			for (String cid : cids) {
				String existingCategory = redisTemplate.opsForValue()
						.get(CATEGORY_PREFIX + cid + CATEGORY_NAME_POSTFIX);
				if (existingCategory.equals(category)) {
					exists = true;
					currentCid = Integer.valueOf(cid);
					break;
				}
			}

			if (!exists) {
				currentCid = cids.size();
				redisTemplate.opsForZSet().add(CID_SET, String.valueOf(currentCid), currentCid);
				redisTemplate.opsForValue().set(CATEGORY_PREFIX + category + CATEGORY_POSTFIX,
						String.valueOf(currentCid));
				redisTemplate.opsForValue().set(CATEGORY_PREFIX + currentCid + CATEGORY_NAME_POSTFIX, category);
				cids.add(String.valueOf(currentCid));
			}

			redisTemplate.opsForZSet().add(CATEGORY + currentCid, String.valueOf(aid), Double.valueOf(aid)); // add
																												// aid
																												// to
																												// zset
		}
	}

	private static void addChannel(Host host, String aid) {
		Set<String> chids = redisTemplate.opsForZSet().range(CHID_SET, 0, -1);

		int currentChid = 0;

		String newChannel = host.getChannelName();

		boolean exists = false;

		for (String chid : chids) {
			String existingChannel = redisTemplate.opsForValue().get(CHANNEL_PREFIX + chid + CHANNEL_NAME_POSTFIX);
			if (existingChannel.equals(newChannel)) {
				exists = true;
				currentChid = Integer.valueOf(chid);
				break;
			}
		}

		if (!exists) {
			currentChid = chids.size();
			redisTemplate.opsForZSet().add(CHID_SET, String.valueOf(currentChid), currentChid);
			redisTemplate.opsForValue().set(CHANNEL_PREFIX + newChannel + CHANNEL_POSTFIX, String.valueOf(currentChid));
			redisTemplate.opsForValue().set(CHANNEL_PREFIX + currentChid + CHANNEL_NAME_POSTFIX, newChannel);
		}

		redisTemplate.opsForZSet().add(CHANNEL + currentChid, String.valueOf(aid), Double.valueOf(aid));
	}

	public static void saveUser(User user) {
		String uid = getInstance().opsForValue().get(USER_PREFIX + user.getLogin() + USER_POSTFIX);
		if (uid == null) { // new user
			uid = redisTemplate.opsForValue().get(GLOBAL_USER_ID);
			if (uid == null) {
				uid = "1";
				redisTemplate.opsForValue().increment(GLOBAL_USER_ID, 1);
			}
			redisTemplate.opsForValue().set(USER_PREFIX + user.getLogin() + USER_POSTFIX, uid);
			redisTemplate.opsForValue().increment(GLOBAL_USER_ID, 1);

			UserStorage storage = new UserStorage();
			storage.add(user, uid);

			redisTemplate.opsForZSet().add(UID_SET, uid, Double.parseDouble(uid));

			// saveUserFavorites(user.getFavoriteArticleHashes(), uid);
			//
			// saveUserCategories(user.getSelectedCategories(), uid);
			//
			// saveUserChannels(user.getSelectedChannels(), uid);
		}
	}

	public static List<Article> getArticlesForUser(String login) {
		String uid = getUidForLogin(login);

		UserStorage storage = new UserStorage();
		User user = storage.get(uid, new UserFields());

		return getFilteredArticlesForUser(user);
	}

	private static List<Article> getFilteredArticlesForUser(User user) {
		// based on categories and channels

		// getting union of all selected categories aids and intersecting them
		// with all selecting channels aids.

		List<String> categoryKeys = user.getSelectedCategories(); // cids
		for (String category : categoryKeys) {
			category = CATEGORY + category;
		}
		
		boolean isInTempCategories = false;
		if (categoryKeys.size() > 1) {
			Long count = redisTemplate.opsForZSet().unionAndStore(categoryKeys.get(0),
					categoryKeys.subList(1, categoryKeys.size()), CATEGORY_TEMP + user.getUid());
			logger.info("Total selected user articles for categories: " + count);
			isInTempCategories = true;
		}

		List<String> channelKeys = user.getSelectedChannels(); // chids
		for (String channel : channelKeys)
			channel = CHANNEL + channel;
		
		
		boolean isInTempChannels = false;
		if (channelKeys.size() > 1) {
			Long count = redisTemplate.opsForZSet().unionAndStore(channelKeys.get(0),
					channelKeys.subList(1, channelKeys.size()), CHANNEL_TEMP + user.getUid());
			logger.info("Total selected user articles for channels: " + count);
			isInTempChannels = true;
		}

		String articlesForSelectedCategoriesKey = isInTempCategories ? CATEGORY_TEMP + user.getUid()
				: categoryKeys.get(0);
		String articlesForSelectedChannelsKey = isInTempChannels ? CHANNEL_TEMP + user.getUid()
				: channelKeys.get(0);

		String filteredArticlesKey = ARTICLES_TEMP + user.getUid();

		Long count = redisTemplate.opsForZSet().intersectAndStore(articlesForSelectedCategoriesKey,
				articlesForSelectedChannelsKey, filteredArticlesKey);
		logger.info("Total filtered user articles: " + count);

		Set<String> filteredAids = redisTemplate.opsForZSet().range(filteredArticlesKey, 0, -1);
		ArticleStorage storage = new ArticleStorage();
		ArticleFields fields = new ArticleFields();

		ArrayList<Article> filteredArticles = new ArrayList<>();
		for (String aid : filteredAids)
			filteredArticles.add(storage.get(aid, fields));

		return filteredArticles;
	}

	public static String getChidForChannel(String channel) {
		return redisTemplate.opsForValue().get(CHANNEL_PREFIX + channel + CHANNEL_POSTFIX);
	}

	public static String getCidForCategory(String category) {
		return redisTemplate.opsForValue().get(CATEGORY_PREFIX + category + CATEGORY_POSTFIX);
	}

	public static String getUidForLogin(String login) {
		return redisTemplate.opsForValue().get(USER_PREFIX + login + USER_POSTFIX);
	}

	public static String getChannelForChid(String chid) {
		return redisTemplate.opsForValue().get(CHANNEL_PREFIX + chid + CHANNEL_NAME_POSTFIX);
	}

	public static String getCategoryForCid(String cid) {
		return redisTemplate.opsForValue().get(CATEGORY_PREFIX + cid + CATEGORY_NAME_POSTFIX);
	}

	// private static void saveUserFavorites(List<String> favoriteArticleHashes,
	// String uid) {
	//
	// Set<TypedTuple<String>> set = new TreeSet<>();
	//
	// for (String hash : favoriteArticleHashes) {
	// UserTyple typle = new UserTyple(hash, Double.parseDouble(hash));
	// set.add(typle);
	// }
	//
	// redisTemplate.opsForZSet().add(USER_ARTICLE_SET, set);
	// }
	//
	// private static void saveUserCategories(List<String> selectedCategories,
	// String uid) {
	//
	// Set<TypedTuple<String>> set = new TreeSet<>();
	//
	// for (String hash : selectedCategories) {
	// UserTyple typle = new UserTyple(hash, Double.parseDouble(hash));
	// set.add(typle);
	// }
	//
	// redisTemplate.opsForZSet().add(USER_CATEGORY_SET, set);
	// }
	//
	// private static void saveUserChannels(List<String> selectedChannels,
	// String uid) {
	// redisTemplate.opsForZSet().add(USER_CHANNEL_SET, uid,
	// Double.parseDouble(uid));
	// }

	public static RedisTemplate<String, String> redisTemplate;

	public static void initialize(RedisTemplate<String, String> template) {
		Redis.redisTemplate = template;
	}

	public static RedisTemplate<String, String> getInstance() {
		return redisTemplate;
	}

	public static List<String> getChannelsForUser(User user) {
		List<String> chids = user.getSelectedChannels();
		return getChannelsForChids(chids);
	}

	public static List<String> getChannelsForChids(List<String> chids) {
		List<String> channels = new ArrayList<>();
		for (String chid : chids)
			channels.add(getChannelForChid(chid));

		return channels;
	}

	public static List<String> getCategoriesForUser(User user) {
		List<String> cids = user.getSelectedCategories();
		return getCategoriesForCids(cids);
	}

	private static List<String> getCategoriesForCids(List<String> cids) {
		List<String> categories = new ArrayList<>();
		for (String cid : cids)
			categories.add(getCategoryForCid(cid));

		return categories;
	}

	public static List<String> getAllChannels() {
		List<String> chidsList = getAllChids();
		return getChannelsForChids(chidsList);
	}

	public static List<String> getAllChids() {
		return new ArrayList<>(redisTemplate.opsForZSet().range(CHID_SET, 0, -1));
	}

	public static List<String> getAllCategories() {
		List<String> cidsList = getAllCids();
		return getCategoriesForCids(cidsList);
	}

	public static List<String> getAllCids() {
		return new ArrayList<>(redisTemplate.opsForZSet().range(CID_SET, 0, -1));
	}

	public static void updateUserChannels(User user, List<Channel> selectedChannels) {
		List<String> chids = new ArrayList<>();
		for (Channel c : selectedChannels)
			chids.add(getChidForChannel(c.getName()));
		user.setSelectedChannels(chids);

		String key = String.format("%s:%s", "uid", user.getUid());
		redisTemplate.opsForHash().put(key, UserFields.SELECTED_CHANNELS,
				ListSerializer.getInstance().serialize(chids));
	}

	public static void updateUserCategories(User user, List<Category> selectedCategories) {
		List<String> cids = new ArrayList<>();
		for (Category c : selectedCategories)
			cids.add(getCidForCategory(c.getName()));
		user.setSelectedCategories(cids);

		String key = String.format("%s:%s", "uid", user.getUid());
		redisTemplate.opsForHash().put(key, UserFields.SELECTED_CATEGORIES,
				ListSerializer.getInstance().serialize(cids));
	}
}