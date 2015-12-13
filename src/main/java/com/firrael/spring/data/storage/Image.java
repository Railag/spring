package com.firrael.spring.data.storage;

import java.io.InputStream;

public class Image {

	private String name;
	private InputStream content;

	public Image(String filename, InputStream content) {
		setName(filename);
		setContent(content);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}
}