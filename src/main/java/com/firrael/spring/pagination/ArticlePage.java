package com.firrael.spring.pagination;

import java.util.ArrayList;
import java.util.List;

import com.firrael.spring.data.Article;

public class ArticlePage implements Page<Article> {

	private final static int PAGE_SIZE = 5;

	private List<Article> articles = new ArrayList<>();
	private int number = 0;
	private boolean isFirst;
	private boolean isLast;

	public ArticlePage(List<Article> articles, int number) {
		this.articles = articles;
		this.number = number;
	}

	public static List<ArticlePage> getPagingList(List<Article> articles) {
		ArrayList<ArticlePage> pages = new ArrayList<>();
		
		if (articles.size() <= PAGE_SIZE) {
			ArticlePage page = new ArticlePage(articles, 0);
			page.setFirst(true);
			page.setLast(true);
			pages.add(page);
			return pages;
		}
		
		for (int i = 0; i < articles.size(); i += PAGE_SIZE) {
			if (articles.size() - PAGE_SIZE < i) {
				ArticlePage page = new ArticlePage(articles.subList(i, articles.size()), (i / 5) + 1);
				page.setLast(true);
				pages.add(page);
				return pages;
			}
				
			ArticlePage page = new ArticlePage(articles.subList(i, i + PAGE_SIZE), i / 5);
			if (i == 0)
				page.setFirst(true);
			
			pages.add(page);
		}

		return pages;
	}

	@Override
	public List<Article> getItems() {
		return articles;
	}

	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public void setItems(List<Article> items) {
		articles = items;
	}

	@Override
	public Article getItem(int position) {
		return articles.get(position);
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}
}
