package com.firrael.spring.data.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.firrael.spring.data.ArticleFields;
import com.firrael.spring.data.Host;
import com.firrael.spring.data.base.Storage;
import com.firrael.spring.data.models.Article;

public class ArticleStorage implements Storage<Article, ArticleFields> {

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
		article.setAid(key);
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

		for (Article a : articles) {
			String aid = template.opsForValue()
					.get(RedisFields.ARTICLE_PREFIX + a.getTitle().hashCode() + RedisFields.ARTICLE_POSTFIX);
			if (aid == null) // new article
				saveArticle(a);
		}
	}

	private static void saveArticle(Article article) {
		RedisTemplate<String, String> template = Redis.getInstance();

		String aid = template.opsForValue().get(RedisFields.GLOBAL_ARTICLE_ID);
		if (aid == null) {
			aid = "1";
			template.opsForValue().increment(RedisFields.GLOBAL_ARTICLE_ID, 1);
		}
		template.opsForValue()
				.set(RedisFields.ARTICLE_PREFIX + article.getTitle().hashCode() + RedisFields.ARTICLE_POSTFIX, aid);
		template.opsForValue().increment(RedisFields.GLOBAL_ARTICLE_ID, 1);
		ArticleStorage storage = new ArticleStorage();
		storage.add(article, aid);

		template.opsForZSet().add(RedisFields.AID_SET, aid, Double.parseDouble(aid));

		addCategories(article.getCategories(), aid);

		addChannel(article.getHost(), aid);
	}

	private static void addCategories(List<String> categories, String aid) {
		RedisTemplate<String, String> template = Redis.getInstance();

		Set<String> cids = template.opsForZSet().range(RedisFields.CID_SET, 0, -1);

		int currentCid = 0;

		for (String category : categories) {
			boolean exists = false;

			for (String cid : cids) {
				String existingCategory = template.opsForValue()
						.get(RedisFields.CATEGORY_PREFIX + cid + RedisFields.CATEGORY_NAME_POSTFIX);
				if (existingCategory.equalsIgnoreCase(category)) {
					exists = true;
					currentCid = Integer.valueOf(cid);
					break;
				}
			}

			if (!exists) {
				currentCid = cids.size();
				template.opsForZSet().add(RedisFields.CID_SET, String.valueOf(currentCid), currentCid);
				template.opsForValue().set(RedisFields.CATEGORY_PREFIX + category + RedisFields.CATEGORY_POSTFIX,
						String.valueOf(currentCid));
				template.opsForValue().set(RedisFields.CATEGORY_PREFIX + currentCid + RedisFields.CATEGORY_NAME_POSTFIX,
						category);
				cids.add(String.valueOf(currentCid));
			}

			template.opsForZSet().add(RedisFields.CATEGORY + currentCid, String.valueOf(aid), Double.valueOf(aid)); // add
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
			currentChid = chids.size();
			template.opsForZSet().add(RedisFields.CHID_SET, String.valueOf(currentChid), currentChid);
			template.opsForValue().set(RedisFields.CHANNEL_PREFIX + newChannel + RedisFields.CHANNEL_POSTFIX,
					String.valueOf(currentChid));
			template.opsForValue().set(RedisFields.CHANNEL_PREFIX + currentChid + RedisFields.CHANNEL_NAME_POSTFIX,
					newChannel);
		}

		template.opsForZSet().add(RedisFields.CHANNEL + currentChid, String.valueOf(aid), Double.valueOf(aid));
	}

}
