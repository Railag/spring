package com.firrael.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.firrael.spring.data.storage.UserStorage;
import com.mongodb.Mongo;

@Configuration
@ComponentScan
public class RootConfiguration {

	public @Bean Mongo mongo() throws Exception {
        return new Mongo("localhost", 27017);
    }

    public @Bean MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), "test");
    }

    @Bean(name = "filterMultipartResolver")
    public CommonsMultipartResolver filterMultipartResolver() {
       CommonsMultipartResolver filterMultipartResolver = new CommonsMultipartResolver();
       filterMultipartResolver.setDefaultEncoding("utf-8");
       // resolver.setMaxUploadSize(512000);
       return filterMultipartResolver;
 }

	@Bean
	public UserStorage userDetailsService() {
		return new UserStorage();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
}
