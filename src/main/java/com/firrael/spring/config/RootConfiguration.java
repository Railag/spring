package com.firrael.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ComponentScan
public class RootConfiguration {
	
	@Bean
	public JedisConnectionFactory jedisConnFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setUsePool(true);
		factory.setHostName("localhost");
		factory.setPort(6379);
		return factory;
	}

	@Bean()
	public RedisTemplate<String, String> redisTemplate() {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(stringRedisSerializer());
		redisTemplate.setValueSerializer(stringRedisSerializer());
		redisTemplate.setHashKeySerializer(stringRedisSerializer());
		redisTemplate.setDefaultSerializer(stringRedisSerializer());
		redisTemplate.setConnectionFactory(jedisConnFactory());
		return redisTemplate;
	}

	@Bean
	public StringRedisSerializer stringRedisSerializer() {
		return new StringRedisSerializer();
	}
}
