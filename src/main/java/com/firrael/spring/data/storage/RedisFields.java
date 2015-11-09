package com.firrael.spring.data.storage;

public class RedisFields {
	public final static String GLOBAL_ARTICLE_ID = "global:aid"; // article counter
	public final static String GLOBAL_USER_ID = "global:uid"; // user counter

	
	public final static String ARTICLE = "aid:"; // aid : 5
	public final static String USER = "uid:"; // uid: 15
	public final static String CATEGORY = "cid:"; // cid : 25
	public final static String CHANNEL = "chid:"; // chid : 35

	public final static String ARTICLE_PREFIX = "article:";
	public final static String USER_PREFIX = "user:"; // to get user by input login via user:<name>:uid
	public final static String CATEGORY_PREFIX = "category:"; // to get category name by cid via category:<cid>
	public final static String CHANNEL_PREFIX = "channel:"; // to get channel name by chid via channel:<chid>
	
	public final static String ARTICLE_POSTFIX = ":aid";
	public final static String USER_POSTFIX = ":uid";
	public final static String CATEGORY_POSTFIX = ":cid";
	public final static String CHANNEL_POSTFIX = ":chid";
	
	public final static String CATEGORY_NAME_POSTFIX = ":name";
	public final static String CHANNEL_NAME_POSTFIX = ":name";
	
	public final static String AID_SET = "aids"; // zset for all aids
	public final static String UID_SET = "uids"; // zset for all uids
	public final static String CID_SET = "cids:"; // zset for all cids
	public final static String CHID_SET = "chids:"; // zset for all chids

	public final static String CHANNEL_TEMP = "chtmp:";
	public final static String CATEGORY_TEMP = "ctmp:";
	public final static String ARTICLES_TEMP = "atmp:";
}


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
