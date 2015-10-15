package com.firrael.spring.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;

import com.firrael.spring.data.base.Storage;

public class ArticleStorage implements Storage<Article, ArticleFields> {

	private final static String ARTICLE_KEY = "article";

	@Override
	public void add(Article article) {
		String key = String.format("%s:%s", ARTICLE_KEY, article.hashCode());
		RedisTemplate<String, String> template = Redis.getInstance();
		template.opsForHash().putAll(key, article.toHashMap());
		template.opsForValue().increment("countArticles", 1);
	}

	@Override
	public Article get(String hash, ArticleFields articleFields) {
		List<Object> fields = articleFields.asArray();
		RedisTemplate<String, String> template = Redis.getInstance();
		List<Object> values = template.opsForHash().multiGet(hash, fields);
		Article article = new Article().initialize(values);

		return article;
	}

	@Override
	public List<Article> getItems(final int count) {
		ArrayList<Article> cachedArticles = new ArrayList<>();
		RedisTemplate<String, String> template = Redis.getInstance();
		Iterable<byte[]> results = template.execute(new RedisCallback<Iterable<byte[]>>() {

			@Override
			public Iterable<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {

				List<byte[]> binaryKeys = new ArrayList<byte[]>();

				ScanOptionsBuilder builder = new ScanOptionsBuilder();
				builder.count(count);
				builder.match(ARTICLE_KEY + ":*");
				ScanOptions options = builder.build();

				Cursor<byte[]> cursor = connection.scan(options);
				while (cursor.hasNext()) {
					binaryKeys.add(cursor.next());
				}

				try {
					cursor.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return binaryKeys;
			}
		});

		List<String> hashes = new ArrayList<String>();

		for (byte[] b : results)
			hashes.add(new String(b));

		for (String hash : hashes)
			cachedArticles.add(get(hash, new ArticleFields()));

		return cachedArticles;
	}
}
