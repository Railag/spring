package com.firrael.spring.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

public class Redis {
	
	@Autowired
	private static RedisTemplate<String, String> redisTemplate;

	public static void initialize(RedisTemplate<String, String> template) {
		Redis.redisTemplate = template;
	}

	public static RedisTemplate<String, String> getInstance() {
		if (redisTemplate != null)
			return redisTemplate;
		else {
			ApplicationContext context = new ClassPathXmlApplicationContext("/WEB-INF/spring/root-context.xml");
			return redisTemplate = (RedisTemplate<String, String>) context.getBean("redisTemplate");
		}
	}
}
