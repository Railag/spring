//package com.firrael.spring.controllers;
//
//import java.security.Principal;
//import java.util.List;
//import java.util.Locale;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.firrael.spring.data.models.Article;
//import com.firrael.spring.data.models.User;
//import com.firrael.spring.data.storage.ArticleStorage;
//import com.firrael.spring.data.storage.UserStorage;
//import com.firrael.spring.pagination.ArticlePage;
//import com.firrael.spring.pagination.PageCreator;
//import com.firrael.spring.pagination.UserPage;
//
//@Controller
//public class AdminController {
//	@RequestMapping(value = "/admin", method = RequestMethod.GET)
//	public String admin(Locale locale, Model model) {
//		return "admin";
//	}
//	
//	@RequestMapping(value = "/users", method = RequestMethod.GET)
//	public String users(Locale locale, Model model, Principal principal, @RequestParam(required = false) Integer page) {
//
//		// only for admin
//		String login = principal.getName();
//
//		UserStorage storage = new UserStorage();
//		List<User> users = storage.getItems(100);
//
//		List<UserPage> pages = (List<UserPage>) PageCreator.getPagingList(users, UserPage.class);
//
//		model.addAttribute("pages", pages);
//
//		if (page == null || page >= pages.size() || page < 0)
//			page = 0;
//
//		model.addAttribute("currentPage", pages.get(page));
//
//		return "adminUsers";
//	}
//
//	@RequestMapping(value = "/articles", method = RequestMethod.GET)
//	public String articles(Locale locale, Model model, Principal principal,
//			@RequestParam(required = false) Integer page) {
//
//		// only for admin
//		String login = principal.getName();
//
//		ArticleStorage storage = new ArticleStorage();
//		List<Article> articles = storage.getItems(100);
//
//		List<ArticlePage> pages = (List<ArticlePage>) PageCreator.getPagingList(articles, ArticlePage.class);
//
//		model.addAttribute("pages", pages);
//
//		if (page == null || page >= pages.size() || page < 0)
//			page = 0;
//
//		model.addAttribute("currentPage", pages.get(page));
//
//		return "adminArticles";
//	}
//
//}
