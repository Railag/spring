package com.firrael.spring.data.models;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;

public class SimpleResponse {
	private String responseJson;

	@JsonValue
    @JsonRawValue
	public String getResponseJson() {
		return responseJson;
	}

	public void setResponseJson(String responseJson) {
		this.responseJson = responseJson;
	}
}
