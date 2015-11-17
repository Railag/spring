package com.firrael.spring.security;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.multipart.support.MultipartFilter;

public class MessageSecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
	
	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
	   super.beforeSpringSecurityFilterChain(servletContext);

	   FilterRegistration.Dynamic springMultipartFilter;
	   springMultipartFilter = servletContext.addFilter(
	    "springMultipartFilter", new MultipartFilter());
	   springMultipartFilter.addMappingForUrlPatterns(null, false, "/*");
	}
}