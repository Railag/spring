package com.firrael.spring.data.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.firrael.spring.data.ArticleFields;
import com.firrael.spring.data.Category;
import com.firrael.spring.data.Host;
import com.firrael.spring.data.StringTyple;
import com.firrael.spring.data.UserFields;
import com.firrael.spring.data.base.Storage;
import com.firrael.spring.data.models.Article;
import com.firrael.spring.data.models.User;
import com.firrael.spring.utils.ListSerializer;

public class ArticleStorage implements Storage<Article, ArticleFields> {

	private static Logger logger = Logger.getLogger(ArticleStorage.class.getName());

	@Override
	public void add(Article article, String aid) {
		String key = String.format("%s%s", RedisFields.ARTICLE, aid);
		RedisTemplate<String, String> template = Redis.getInstance();
		template.opsForHash().putAll(key, article.toHashMap());
	}

	@Override
	public Article get(String aid, ArticleFields articleFields) {
		String key = String.format("%s%s", RedisFields.ARTICLE, aid);
		List<Object> fields = articleFields.asArray();
		RedisTemplate<String, String> template = Redis.getInstance();
		List<Object> values = template.opsForHash().multiGet(key, fields);
		Article article = new Article().initialize(values);
		article.setAid(aid);
		return article;
	}

	@Override
	public List<Article> getItems(final int count) {
		ArrayList<Article> cachedArticles = new ArrayList<>();

		RedisTemplate<String, String> template = Redis.getInstance();
		String articleCounter = template.opsForValue().get("global:aid");
		if (articleCounter == null)
			return Collections.emptyList();

		Double counter = Double.parseDouble(articleCounter);
		Set<TypedTuple<String>> set = template.opsForZSet().rangeByScoreWithScores("aids", counter - count, counter);
		for (TypedTuple<String> tt : set) {
			String aid = tt.getValue();
			Article article = get(aid, new ArticleFields());
			cachedArticles.add(article);
		}
		return cachedArticles;
	}

	public static void saveArticles(List<Article> articles) {
		RedisTemplate<String, String> template = Redis.getInstance();

		Set<String> cids = template.opsForZSet().range(RedisFields.CID_SET, 0, -1);

		for (Article a : articles) {
			String aid = template.opsForValue()
					.get(RedisFields.ARTICLE_PREFIX + a.getTitle() + RedisFields.ARTICLE_POSTFIX);
			if (aid == null) // new article
				saveArticle(a, cids);
		}

		Set<TypedTuple<String>> cidsZSet = new TreeSet<>();
		for (String cid : cids)
			cidsZSet.add(new StringTyple(cid, Double.valueOf(cid)));

		template.opsForZSet().add(RedisFields.CID_SET, cidsZSet);
	}

	private static void saveArticle(Article article, Set<String> cids) {
		RedisTemplate<String, String> template = Redis.getInstance();

		String aid = template.opsForValue().get(RedisFields.GLOBAL_ARTICLE_ID);
		if (aid == null) {
			aid = "1";
			template.opsForValue().increment(RedisFields.GLOBAL_ARTICLE_ID, 1);
		}
		template.opsForValue().set(RedisFields.ARTICLE_PREFIX + article.getTitle() + RedisFields.ARTICLE_POSTFIX, aid);
		template.opsForValue().increment(RedisFields.GLOBAL_ARTICLE_ID, 1);
		ArticleStorage storage = new ArticleStorage();
		storage.add(article, aid);

		template.opsForZSet().add(RedisFields.AID_SET, aid, Double.parseDouble(aid));

		addCategories(article.getCategories(), aid, cids);

		addChannel(article.getHost(), aid);
	}

	private static void addCategories(List<String> categories, String aid, Set<String> cids) {
		RedisTemplate<String, String> template = Redis.getInstance();

		for (String category : categories) {
			int currentCid = 0;

			boolean exists = false;

			for (String cid : cids) {
				String existingCategory = template.opsForValue()
						.get(RedisFields.CATEGORY_PREFIX + cid + RedisFields.CATEGORY_NAME_POSTFIX);
				if (existingCategory.equals(category)) {
					exists = true;
					currentCid = Integer.valueOf(cid);
					break;
				}
			}

			if (!exists) {
				currentCid = cids.size() + 1;
				if (category.equals("Информационная безопасность")) {
					logger.info(category + " | AID: " + aid + " " + currentCid);
				}
				// if (category.equalsIgnoreCase("fallout 4") || currentCid ==
				// 346) {
				// logger.info(category + " | AID: " + aid + " " + currentCid);
				// }
				// template.opsForZSet().add(RedisFields.CID_SET,
				// String.valueOf(currentCid), currentCid);
				template.opsForValue().set(RedisFields.CATEGORY_PREFIX + category + RedisFields.CATEGORY_POSTFIX,
						String.valueOf(currentCid));

				template.opsForValue().set(RedisFields.CATEGORY_PREFIX + currentCid + RedisFields.CATEGORY_NAME_POSTFIX,
						category);
				cids.add(String.valueOf(currentCid));
			}

			template.opsForZSet().add(RedisFields.CATEGORY + currentCid, String.valueOf(aid), Double.valueOf(aid)); // add
			if (currentCid == 306) {
				logger.info(category + " | AID: " + aid + " " + currentCid);
			}
			// aid
			// to
			// zset
		}
	}

	private static void addChannel(Host host, String aid) {
		RedisTemplate<String, String> template = Redis.getInstance();

		Set<String> chids = template.opsForZSet().range(RedisFields.CHID_SET, 0, -1);

		int currentChid = 0;

		String newChannel = host.getChannelName();

		boolean exists = false;

		for (String chid : chids) {
			String existingChannel = template.opsForValue()
					.get(RedisFields.CHANNEL_PREFIX + chid + RedisFields.CHANNEL_NAME_POSTFIX);
			if (existingChannel.equalsIgnoreCase(newChannel)) {
				exists = true;
				currentChid = Integer.valueOf(chid);
				break;
			}
		}

		if (!exists) {
			currentChid = chids.size() + 1;
			template.opsForZSet().add(RedisFields.CHID_SET, String.valueOf(currentChid), currentChid);
			template.opsForValue().set(
					RedisFields.CHANNEL_PREFIX + newChannel.toLowerCase() + RedisFields.CHANNEL_POSTFIX,
					String.valueOf(currentChid));
			template.opsForValue().set(RedisFields.CHANNEL_PREFIX + currentChid + RedisFields.CHANNEL_NAME_POSTFIX,
					newChannel.toLowerCase());
		}

		template.opsForZSet().add(RedisFields.CHANNEL + currentChid, String.valueOf(aid), Double.valueOf(aid));
	}

	public static List<Article> getArticlesForCategory(String category) {

		String cid = Redis.getCidForCategory(category);

		List<String> aids = Redis.getAidsForCid(cid);

		List<Article> articles = new ArrayList<>();

		ArticleStorage storage = new ArticleStorage();
		ArticleFields articleFields = new ArticleFields();

		for (String aid : aids)
			articles.add(storage.get(aid, articleFields));

		return articles;
	}

	public static void updateArticle(Article article) {
		RedisTemplate<String, String> template = Redis.getInstance();

		String key = String.format("%s:%s", "aid", article.getAid());
		template.opsForHash().put(key, ArticleFields.AUTHOR, article.getAuthor());
		template.opsForHash().put(key, ArticleFields.CATEGORY,
				ListSerializer.getInstance().serialize(article.getCategories()));
		template.opsForHash().put(key, ArticleFields.DESCRIPTION, article.getDescription());
		template.opsForHash().put(key, ArticleFields.LINK, article.getLink());
		template.opsForHash().put(key, ArticleFields.TITLE, article.getTitle());

	}

	public static void removeArticle(String aid) {
		RedisTemplate<String, String> template = Redis.getInstance();

		ArticleStorage storage = new ArticleStorage();
		Article article = storage.get(aid, new ArticleFields());
		
		// remove aid from its channels sets
		String host = article.getHost().getChannelName();
		String chid = Redis.getChidForChannel(host);
		template.opsForZSet().remove(RedisFields.CHANNEL + chid, aid);

		
		// remove aid from its categories sets
		for (String category : article.getCategories()) {
			String cid = Redis.getCidForCategory(category);
			
			template.opsForZSet().remove(RedisFields.CATEGORY + cid, aid);
		}
		
		// remove article:<title>:aid
		template.delete(RedisFields.ARTICLE_PREFIX + article.getTitle() + RedisFields.ARTICLE_POSTFIX);
		
		// remove aid:<aid> hash
		String key = String.format("%s:%s", "aid", aid);
		
		template.opsForHash().delete(key, new ArticleFields().asArray());
		
		// remove aid from all aids set
		template.opsForZSet().remove(RedisFields.AID_SET, aid);
	}

}
