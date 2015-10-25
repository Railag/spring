package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class Redis {
	
	private final static String GLOBAL_ARTICLE_ID = "global:aid"; // article counter
	private final static String GLOBAL_USER_ID = "global:uid"; // user counter
	
	private final static String ARTICLE = "aid:"; // aid : 5
	private final static String USER = "uid:"; // uid: 15
	private final static String CATEGORY = "cid:"; // cid : 25
	private final static String CHANNEL = "chid:"; // chid : 35
	
	private final static String AID_SET = "aids"; //zset for all aids
	private final static String CID_SET = "cids:"; //zset for all cids
	private final static String CHID_SET = "chids:"; //zset for all chids
	
	private final static String USER_PREFIX = "user:"; // to get user by input login via user:<name>:uid
	private final static String ARTICLE_PREFIX = "article:";
	private final static String CATEGORY_PREFIX = "category:"; // to get category name by cid via category:<cid>
	private final static String CHANNEL_PREFIX = "channel:"; // to get channel name by chid via channel:<chid>
	
	private final static String USER_POSTFIX = ":uid";
	private final static String ARTICLE_POSTFIX = ":aid";
	private final static String CATEGORY_POSTFIX = ":cid";
	private final static String CHANNEL_POSTFIX = ":chid";
	
	// uid:<uid>:favArticles list с избранными статьями
	
	// cid:<cid>
	
	// category:<cid> returns category name
	
	// в uid list с cid выбранных категорий
	
	/**
	 * User not logged in => all feeds, all categories are shown
	 * 
	 * User is logged in => 
	 * get user login => 
	 * get user uid via user:<login>:uid value =>
	 * take user info and cache it in some Profile class for current user in order not to take it from redis every time
	 * Info to take:
	 * 	  channel preferences (chid list in uid hash)
	 *    category preferences (cid list in uid hash)
	 *    favArticles (aid list in uid hash)
	 *    
	 * Loading feed:
	 * if user is not logged in => 
	 * just take latest aids using global:aid counter	OR MAYBE	take all possible channels chid and load (articlesCount/channelsCount) latest articles from every channel.
	 * place them in sorted set in redis, get them and remove the list
	 * 
	 * if user is logged in => 
	 * for all chid from uid hash: get all articles for channel via chid (chid:<chid> contains list of aid-s) and create sorted list (List#1)
	 * then for all cid from uid hash: get cid list via cid:<cid> which contains aids of included articles, create sorted list (List#2) 
	 * and intersect (List#2) with (List#1) in order to get all articles which are present in user's selected categories from user's selected channels.
	 * 
	 * Models:
	 * 
	 * User:
	 * { login : test,
	 *   password: test123!,
	 *   email : test@test.com, 
	 *   favArticles : [1, 2, 15, 165, ..], // aid
	 *   selectedCategories : [1, 2, 7, ..], // cid
	 *   selectedChannels : [1, 2, ..], // chid
	 * }
	 * 
	 * Article:
	 * { title : Title,
	 *   link : http://habrahabr.ru/135678,
	 *   description : LongLongText,
	 *   date : Thu, 01 Oct 2015 07:10:00 GMT, // or convert it to unix time 
	 *   author : firrael,
	 *   categories : [1, 2, 5, ..] // we can get categories names via category:<cid>
	 * }
	 * 
	 * Category:
	 * not a hash, just value
	 * cid : category name
	 * 
	 * Channel:
	 * not a hash, just value
	 * chid : channel name
	 * 
	 * Saving pulled data:
	 * 
	 * Save all articles names to article:<title>:aid  in order to check if article is already in db =>
	 * Save all articles by their aid-s =>
	 * Increase aid-s counter =>
	 * Add new categories to cid list if they are not already there =>
	 * Increase cid-s counter =>
	 * Add all new articles to cid's lists =>
	 * Add new channels to chid list if they are not already there =>
	 * Increase chid-s counter =>
	 * Add all new articles to chid's lists.
	 * 
	 * 
	 * Structures:
	 * 
	 * Article counter global:aid
	 * Article hashes from 1 to counter aid:<aid>
	 * Can be connected with article via article:<title>:aid
	 * 
	 * 
	 * User counter global:uid
	 * User hashes from 1 to counter uid:<uid>
	 * Can be connected with user login via user:<login>:uid (and then uid:<uid>)
	 * 
	 * Category lists from 1 to counter cid:<cid>
	 * Can be connected with category title via cid:<cid>:title
	 * 
	 * Channel lists from 1 to counter chid:<chid>
	 * Can be connected with channel name via chid:<chid>:name
	 * 
	 * User is logged in => get user by login => get articles for him sorting by cid and chid from user preferences.
	 * User is not logged in => show latest articles in range FROM (article counter - articlesNumber) TO (articlesNumber).
	 * 
	 * Favorite articles can be shown to user on different page, e.g. /favorite.
	 * 
	 * User hash holds list of selected cid-s and chid-s. On user profile page (e.g. /profile) user will see all possible categories and channels
	 * and will have an ability to uncheck useless categories or channels.
	 * For new user all categories and channels will be pre-selected. So, hash for new user will contain cid-s of all categories and chid-s of all channels.
	 */
	
	/*
	 * Save all articles names to article:<title>:aid  in order to check if article is already in db =>		+
	 * Save all articles by their aid-s =>  																+
	 * Increase aid-s counter =>																			+
	 * Add new categories to cid list if they are not already there =>										+
	 * Add all new articles to cid's lists =>                                                               +?
	 * Add new channels to chid list if they are not already there =>										+
	 * Add all new articles to chid's lists.																+?
	*/
	public static void saveArticles(ArrayList<Article> newArticles) {
		for (Article a : newArticles) {
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
				String existingCategory = redisTemplate.opsForValue().get(CATEGORY_PREFIX + cid + CATEGORY_POSTFIX);
 				if (existingCategory.equals(category)) {
					exists = true;
					currentCid = Integer.valueOf(cid);
					break;
				}
			}
			
			if (!exists) {
				currentCid = cids.size();
				redisTemplate.opsForZSet().add(CID_SET, String.valueOf(currentCid), currentCid);
				redisTemplate.opsForValue().set(CATEGORY_PREFIX + currentCid + CATEGORY_POSTFIX, category);
				cids.add(String.valueOf(currentCid));
			}
			
			redisTemplate.opsForZSet().add(CATEGORY + currentCid, String.valueOf(currentCid), Double.valueOf(aid)); // add aid to zset
		}
	}
	
	private static void addChannel(Host host, String aid) {
		Set<String> chids = redisTemplate.opsForZSet().range(CHID_SET, 0, -1);
		
		int currentChid = 0;
		
		String newChannel = host.getChannelName();
		
		boolean exists = false;
		
		for (String chid : chids) {
			String existingChannel = redisTemplate.opsForValue().get(CHANNEL_PREFIX + chid + CHANNEL_POSTFIX);
			if (existingChannel.equals(newChannel)) {
				exists = true;
				currentChid = Integer.valueOf(chid);
				break;
			}
		}
		
		if (!exists) {
			currentChid = chids.size();
			redisTemplate.opsForZSet().add(CHID_SET, String.valueOf(currentChid), currentChid);
			redisTemplate.opsForValue().set(CHANNEL_PREFIX + currentChid + CHANNEL_POSTFIX, newChannel);
		}
		
		redisTemplate.opsForZSet().add(CHANNEL + currentChid, String.valueOf(currentChid), Double.valueOf(aid));
	}


	public static RedisTemplate<String, String> redisTemplate;

	public static void initialize(RedisTemplate<String, String> template) {
		Redis.redisTemplate = template;
	}

	public static RedisTemplate<String, String> getInstance() {
		return redisTemplate;
	}
}