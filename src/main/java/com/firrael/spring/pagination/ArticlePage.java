package com.firrael.spring.pagination;

import java.util.List;

import com.firrael.spring.data.models.Article;

public class ArticlePage extends SimplePage<Article> {

	public ArticlePage(List<Article> articles, int number) {
		super(articles,  number);
	}
}
