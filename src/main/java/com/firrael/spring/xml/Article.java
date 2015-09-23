package com.firrael.spring.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@XStreamAlias("article")
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