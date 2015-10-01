package com.firrael.spring.xml;

import java.util.Date;
import java.util.List;

public class Article implements Cloneable, Comparable<Article> {
	private String title;
	private String guid;
	private String link;
	private String description;
	private Date date;
	private String author;
	private List<String> category;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}
	
	@Override
	public String toString() {
		return author + category + date + description + link;
	}
	
	@Override
	public Article clone() {
		try {
			return (Article) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int compareTo(Article secondArticle) {
		return 0;
	}
}