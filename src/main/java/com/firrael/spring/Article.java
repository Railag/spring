package com.firrael.spring;

import java.util.List;

import lombok.Data;

@Data
public class Article {
	private String title;
	private String guid;
	private String link;
	private String description;
	private String pubDate;
	private String author;
	private List<String> category;
}