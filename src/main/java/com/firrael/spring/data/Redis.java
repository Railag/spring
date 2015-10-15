package com.firrael.spring.data;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

public class Redis {
	private static RedisTemplate<String, String> template;

	public static void initialize(RedisTemplate<String, String> template) {
		Redis.template = template;
	}

	public static RedisTemplate<String, String> getInstance() {
		if (template != null)
			return template;
		else {
			ApplicationContext context = new ClassPathXmlApplicationContext("/WEB-INF/spring/root-context.xml");
			return template = (RedisTemplate<String, String>) context.getBean("redisTemplate");
		}
	}
}
