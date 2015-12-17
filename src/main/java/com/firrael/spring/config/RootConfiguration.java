package com.firrael.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.mongodb.Mongo;

@Configuration
@ComponentScan
public class RootConfiguration {

	public @Bean Mongo mongo() throws Exception {
		return new Mongo("localhost", 27017);
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {
		// MongoTemplate template = new MongoTemplate(mongo(), "test");

		/*
		 * List<Converter> converters = new ArrayList<Converter>();
		 * converters.add(new Converter<DBObject, SubCategory>() { public
		 * SubCategory convert(DBObject s) { return
		 * SubCategoryFactory.buildSubCategory(s); } });
		 * 
		 * converters.add(new Converter<SubCategory, DBObject>() { public
		 * DBObject convert(SubCategory s) { DBObject dbObject = new
		 * BasicDBObject(); dbObject.put("name", s.getName()); // image
		 * 
		 * if (user.getEmailAddress() != null) { DBObject emailDbObject = new
		 * BasicDBObject(); emailDbObject.put("value",
		 * user.getEmailAddress().getValue()); dbObject.put("email",
		 * emailDbObject); }
		 * 
		 * return dbObject; }
		 * 
		 * });
		 * 
		 * CustomConversions cc = new CustomConversions(converters);
		 * 
		 * //
		 * ((MappingMongoConverter)template.getConverter()).setCustomConversions
		 * (cc);
		 * 
		 * SimpleMongoDbFactory factory = new SimpleMongoDbFactory(new Mongo(),
		 * "test");
		 * 
		 * MongoMappingContext mappingContext = new MongoMappingContext();
		 * 
		 * DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
		 * 
		 * MappingMongoConverter converter = new
		 * MappingMongoConverter(dbRefResolver, mappingContext);
		 * 
		 * converter.setCustomConversions(cc);
		 */
		// MongoTemplate mongoTemplate = new MongoTemplate(factory, converter);

		MongoTemplate mongoTemplate = new MongoTemplate(mongo(), "test");

		return mongoTemplate;
	}

	@Bean(name = "filterMultipartResolver")
	public CommonsMultipartResolver filterMultipartResolver() {
		CommonsMultipartResolver filterMultipartResolver = new CommonsMultipartResolver();
		filterMultipartResolver.setDefaultEncoding("utf-8");
		// resolver.setMaxUploadSize(512000);
		return filterMultipartResolver;
	}
}
