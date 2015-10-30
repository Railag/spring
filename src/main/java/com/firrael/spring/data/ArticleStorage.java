package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.firrael.spring.data.base.Storage;

public class ArticleStorage implements Storage<Article, ArticleFields> {

	private final static String ARTICLE_KEY = "aid";

	@Override
	public void add(Article article, String aid) {
		String key = String.format("%s:%s", ARTICLE_KEY, aid);
		RedisTemplate<String, String> template = Redis.getInstance();
		template.opsForHash().putAll(key, article.toHashMap());
	}

	@Override
	public Article get(String hash, ArticleFields articleFields) {
		List<Object> fields = articleFields.asArray();
		RedisTemplate<String, String> template = Redis.getInstance();
		List<Object> values = template.opsForHash().multiGet(hash, fields);
		Article article = new Article().initialize(values);
		article.setAid(hash);
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
			Article article = get("aid:" + aid, new ArticleFields());
			cachedArticles.add(article);
		}
		return cachedArticles;
	}
}
