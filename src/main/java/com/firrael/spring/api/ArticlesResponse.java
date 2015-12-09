package com.firrael.spring.api;

import java.util.List;

import com.firrael.spring.data.models.Article;

public class ArticlesResponse extends ApiResponse<List<Article>> {

	private List<Article> articles = data;
	
	public ArticlesResponse(List<Article> data) {
		super(data);
	}

//	@Override
//	public String sendJson(List<Article> data) {
//		ObjectMapper mapper = new ObjectMapper();
//
//		//For testing
//		Article article = data.get(0);
//		
//		String jsonInString;
//			try {
//				jsonInString = mapper.writeValueAsString(article);
//				
//				log(jsonInString);
//				
//				return jsonInString;
//			
//			} catch (JsonProcessingException e) {
//				e.printStackTrace();
//			}
//			
//		return null;
//	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}
}