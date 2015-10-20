package com.firrael.spring.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class Redis {
	
	@Autowired
	private static RedisTemplate<String, String> redisTemplate;

	public static void initialize(RedisTemplate<String, String> template) {
		Redis.redisTemplate = template;
	}

	public static RedisTemplate<String, String> getInstance() {
		return redisTemplate;
	}
}
