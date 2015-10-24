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

public class UserStorage implements Storage<User, UserFields> {

	private final static String USER_KEY = "uid";

	@Override
	public void add(User user, String uid) {
		String key = String.format("%s:%s", USER_KEY, uid);
		RedisTemplate<String, String> template = Redis.getInstance();
		template.opsForHash().putAll(key, user.toHashMap());
	}

	@Override
	public User get(String hash, UserFields userFields) {
		List<Object> fields = userFields.asArray();
		RedisTemplate<String, String> template = Redis.getInstance();
		List<Object> values = template.opsForHash().multiGet(hash, fields);
		User user = new User().initialize(values);

		return user;
	}

	@Override
	public List<User> getItems(final int count) {
		ArrayList<User> cachedUsers = new ArrayList<>();
		RedisTemplate<String, String> template = Redis.getInstance();
		Iterable<byte[]> results = template.execute(new RedisCallback<Iterable<byte[]>>() {

			@Override
			public Iterable<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {

				List<byte[]> binaryKeys = new ArrayList<byte[]>();

				ScanOptionsBuilder builder = new ScanOptionsBuilder();
				builder.count(count);
				builder.match(USER_KEY + ":*");
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
			cachedUsers.add(get(hash, new UserFields()));

		return cachedUsers;
	}

}
