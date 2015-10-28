package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

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
		user.setUid(hash);
		return user;
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
			User user = get("uid:" + uid, new UserFields());
			cachedUsers.add(user);
		}
		return cachedUsers;
	}

}
