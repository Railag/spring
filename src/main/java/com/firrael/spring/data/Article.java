package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firrael.spring.data.base.Entity;

public class Article implements Cloneable, Comparable<Article>, Entity<Article> {
	private String title;
	private String link;
	private String description;
	private Date date;
	private String author;
	private List<String> categories;
	private Host host;

	public Article() {
		categories = new ArrayList<>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public List<String> getCategories() {
		return categories;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public String toString() {
		return author + categories + date + description + link;
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
		return date.compareTo(secondArticle.getDate());
	}

	public void addCategory(String category) {
		categories.add(category);
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	private Object getCategoriesAsString() {
		StringBuilder builder = new StringBuilder();
		for (String s : getCategories()) {
			builder.append(s).append("|");
		}
		return builder.substring(0, builder.length() - 1);
	}

	public Map<String, Object> toHashMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(ArticleFields.TITLE, getTitle());
		map.put(ArticleFields.LINK, getLink());
		map.put(ArticleFields.DESCRIPTION, getDescription());
		map.put(ArticleFields.DATE, String.valueOf(getDate().getTime()));
		map.put(ArticleFields.AUTHOR, getAuthor());
		map.put(ArticleFields.CATEGORY, getCategoriesAsString());
		return map;
	}

	@Override
	public Article initialize(List<Object> values) {
		Article article = new Article();
		article.setTitle(values.get(0).toString());
		article.setLink(values.get(1).toString());
		article.setHost(Host.parseHost(values.get(1).toString()));
		article.setDescription(values.get(2).toString());
		article.setDate(new Date(Long.valueOf(values.get(3).toString())));
		article.setAuthor(values.get(4).toString());
		article.setCategories(getCategoriesAsList(values.get(5)));
		return article;
	}

	private List<String> getCategoriesAsList(Object object) {
		String categories = object.toString();
		List<String> categoriesList = new ArrayList<String>(Arrays.asList(categories.split("|")));
		return categoriesList;
	}

	@Override
	public int hashCode() {
		return title.hashCode();
	}
}