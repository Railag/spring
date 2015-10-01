package com.firrael.spring.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class ArticleHandler extends DefaultHandler2 {
	private final static String TITLE = "title";
	private final static String LINK = "link";
	private final static String DESCRIPTION = "description";
	private final static String DATE = "pubDate";
	private final static String AUTHOR = "author";
	
	private final static  SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
															//Thu, 01 Oct 2015 07:10:00 GMT
	private Article currentArticle;

	// inside item
	private boolean isTitle;
	private boolean isLink;
	private boolean isDescription;
	private boolean isDate;
	private boolean isAuthor;

	public ArticleHandler() {
		currentArticle = new Article();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		isTitle = checkInsideElement(TITLE, qName);

		isLink = checkInsideElement(LINK, qName);

		isDescription = checkInsideElement(DESCRIPTION, qName);

		isDate = checkInsideElement(DATE, qName);

		isAuthor = checkInsideElement(AUTHOR, qName);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
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
			String dateString = new String(ch, start, length);
			Date date = parseDate(dateString);
			currentArticle.setDate(date);
			isDate = false;
			return;
		}

		if (isAuthor) {
			currentArticle.setAuthor(new String(ch, start, length));
			isAuthor = false;
			return;
		}
	}

	private Date parseDate(String dateString) {
		try {
			return DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean checkInsideElement(String itemName, String currentName) {
		return currentName.equalsIgnoreCase(itemName);
	}
	
	public Article getArticle() {
		Article article = currentArticle.clone();
		currentArticle = new Article();
		return article;
	}

}
