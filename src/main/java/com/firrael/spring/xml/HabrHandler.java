package com.firrael.spring.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class HabrHandler extends DefaultHandler2 {

	private final static String ITEM = "item";
	private final static String TITLE = "title";
	private final static String LINK = "link";
	private final static String DESCRIPTION = "description";
	private final static String DATE = "pubDate";
	private final static String AUTHOR = "author";

	private ArrayList<Article> articles;

	private Article currentArticle;

	// basic
	private boolean isItem;

	// inside item
	private boolean isTitle;
	private boolean isLink;
	private boolean isDescription;
	private boolean isDate;
	private boolean isAuthor;
	
	public HabrHandler() {
		articles = new ArrayList<>();
		currentArticle = new Article();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		isItem = checkElement(ITEM, qName);

		isTitle = checkInsideElement(TITLE, qName);

		isLink = checkInsideElement(LINK, qName);

		isDescription = checkInsideElement(DESCRIPTION, qName);

		isDate = checkInsideElement(DATE, qName);

		isAuthor = checkInsideElement(AUTHOR, qName);
	}

	private boolean checkElement(String itemName, String currentName) {
		return currentName.equalsIgnoreCase(itemName);
	}

	private boolean checkInsideElement(String itemName, String currentName) {
		if (!isItem)
			return false;

		return checkElement(itemName, currentName);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		if (isItem)
			return; //parseArticle(ch, start, length); 
		
		if (isTitle) {
			currentArticle.setTitle(new String(ch, start, length));
			isTitle = false;
			return;
		}

		if (isLink) {
			currentArticle.setLink(new String(ch, start, length));
			isLink = false;
			return;
		}

		if (isDescription) {
			currentArticle.setDescription(new String(ch, start, length));
			isDescription = false;
			return;
		}

		if (isDate) {
			currentArticle.setPubDate(new String(ch, start, length));
			isDate = false;
			return;
		}

		if (isAuthor) {
			currentArticle.setAuthor(new String(ch, start, length));
			isAuthor = false;
			articles.add(currentArticle);
			currentArticle = new Article();
			isItem = false;
			return;
		}

	}

	public List<Article> getArticles() {
		return articles;
	}

}
