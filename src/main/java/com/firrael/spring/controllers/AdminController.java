package com.firrael.spring.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firrael.spring.data.ArticleFields;
import com.firrael.spring.data.Category;
import com.firrael.spring.data.UserFields;
import com.firrael.spring.data.models.Article;
import com.firrael.spring.data.models.User;
import com.firrael.spring.data.storage.ArticleStorage;
import com.firrael.spring.data.storage.Redis;
import com.firrael.spring.data.storage.UserStorage;
import com.firrael.spring.pagination.ArticlePage;
import com.firrael.spring.pagination.PageCreator;
import com.firrael.spring.pagination.UserPage;
import com.firrael.spring.utils.Utf8Serializer;

@Controller
public class AdminController {
	
	private final static int MAX_BINDED_VALUES = 1000000;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.setAutoGrowCollectionLimit(MAX_BINDED_VALUES);
	}
	
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String admin(Locale locale, Model model) {
		return "admin";
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String users(Locale locale, Model model, Principal principal, @RequestParam(required = false) Integer page) {

		// only for admin
		String login = principal.getName();

		UserStorage storage = new UserStorage();
		List<User> users = storage.getItems(100);

		List<UserPage> pages = (List<UserPage>) PageCreator.getPagingList(users, UserPage.class);

		model.addAttribute("pages", pages);

		if (page == null || page >= pages.size() || page < 0)
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		return "adminUsers";
	}

	@RequestMapping(value = "/articles", method = RequestMethod.GET)
	public String articles(Locale locale, Model model, Principal principal,
			@RequestParam(required = false) Integer page) {

		// only for admin
		String login = principal.getName();

		ArticleStorage storage = new ArticleStorage();
		List<Article> articles = storage.getItems(100);

		List<ArticlePage> pages = (List<ArticlePage>) PageCreator.getPagingList(articles, ArticlePage.class);

		model.addAttribute("pages", pages);

		if (page == null || page >= pages.size() || page < 0)
			page = 0;

		model.addAttribute("currentPage", pages.get(page));

		return "adminArticles";
	}

	@RequestMapping(value = "/detailArticle", method = RequestMethod.GET)
	public String detailArticle(Locale locale, Model model, Principal principal,
			@RequestParam("articleAid") String aid) {

		// only for admin
		String login = principal.getName();
		
		ArticleStorage storage = new ArticleStorage();
		Article article = storage.get(aid, new ArticleFields());

		model.addAttribute("article", article);

		return "detailArticle";
	}

	@RequestMapping(value = "/removeArticle", method = RequestMethod.GET)
	public String removeArticle(Locale locale, Model model, Principal principal,
			@RequestParam("articleAid") String aid) {

		// only for admin
		String login = principal.getName();
		
		Redis.removeArticle(aid);
		
		return "redirect:/articles";
	}

	@RequestMapping(value = "/editArticle", method = RequestMethod.GET)
	public String editArticle(Locale locale, Model model, Principal principal,
			@RequestParam("articleAid") String aid) {

		// only for admin
		String login = principal.getName();
		
		ArticleStorage storage = new ArticleStorage();
		Article article = storage.get(aid, new ArticleFields());
		
		model.addAttribute("article", article);

		return "editArticle";
	}

	@RequestMapping(value = "/updateArticle", method = RequestMethod.POST)
	public String updateArticle(Locale locale, Model model, Principal principal,
			@ModelAttribute("article") Article article, BindingResult result) {

		// only for admin
		String login = principal.getName();
		
		Utf8Serializer serializer = new Utf8Serializer();

		article.setAuthor(serializer.deserialize(article.getAuthor()));
		
		article.setDescription(serializer.deserialize(article.getDescription()));
		
		article.setLink(serializer.deserialize(article.getLink()));
		
		article.setTitle(serializer.deserialize(article.getTitle()));
		
		for (int i = 0; i < article.getCategories().size(); i++) {
			String s = article.getCategories().get(i);
			article.getCategories().set(i, serializer.deserialize(s));
		}

		Redis.updateArticle(article);

		return "redirect:/articles";
	}
	
	@RequestMapping(value = "/detailUser", method = RequestMethod.GET)
	public String detailUser(Locale locale, Model model, Principal principal,
			@RequestParam("userUid") String uid) {

		// only for admin
		String login = principal.getName();
		
		UserStorage storage = new UserStorage();
		User user = storage.get(uid, new UserFields());

		model.addAttribute("user", user);

		return "detailUser";
	}
	
	@RequestMapping(value = "/removeUser", method = RequestMethod.GET)
	public String removeUser(Locale locale, Model model, Principal principal,
			@RequestParam("userUid") String uid) {

		// only for admin
		String login = principal.getName();
		
		Redis.removeUser(uid);
		
		return "redirect:/users";
	}

	@RequestMapping(value = "/editUser", method = RequestMethod.GET)
	public String editUser(Locale locale, Model model, Principal principal,
			@RequestParam("userUid") String uid) {

		// only for admin
		String login = principal.getName();
		
		UserStorage storage = new UserStorage();
		User user = storage.get(uid, new UserFields());
		
		model.addAttribute("user", user);

		return "editUser";
	}

	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public String updateArticle(Locale locale, Model model, Principal principal,
			@ModelAttribute("user") User user, BindingResult result) {

		// only for admin
		String login = principal.getName();
		
		Utf8Serializer serializer = new Utf8Serializer();
		
		user.setEmail(serializer.deserialize(user.getEmail()));
		
		user.setLogin(serializer.deserialize(user.getLogin()));

		Redis.updateUser(user);
		
		return "redirect:/users";
	}


}