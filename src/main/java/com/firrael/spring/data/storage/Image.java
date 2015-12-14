package com.firrael.spring.data.storage;

import java.io.InputStream;

public class Image {

	private String id;
	private String name;
	private InputStream content;

	public Image(String id, String fileName, InputStream content) {
		setId(id);
		setName(fileName);
		setContent(content);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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