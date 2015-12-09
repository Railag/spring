package com.firrael.spring.api;

import org.apache.log4j.Logger;

public abstract class ApiResponse<T> {
	Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	
	protected T data;
	
	public ApiResponse(T data) {
		this.data = data;
	}

	protected void log(String message) {
		logger.info(message);
	}
}