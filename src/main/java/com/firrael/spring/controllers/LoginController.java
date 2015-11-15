//package com.firrael.spring.controllers;
//
//import java.security.Principal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import com.firrael.spring.data.models.User;
//import com.firrael.spring.data.storage.Redis;
//import com.firrael.spring.security.Role;
//import com.firrael.spring.utils.Utf8Serializer;
//
//@Controller
//public class LoginController {
//
//	@Autowired
//	@Qualifier("passwordEncoder")
//	private PasswordEncoder encoder;
//
//	@RequestMapping(value = "/login", method = RequestMethod.GET)
//	public String login(Locale locale, Model model) {
//		return "login";
//	}
//
//	@RequestMapping(value = "/register", method = RequestMethod.GET)
//	public String register(Locale locale, Model model) {
//		User user = new User();
//		/*
//		 * user.setEmail("test"); user.setAuthToken("testToken");
//		 * user.setLogin("user"); user.setPassword("test password");
//		 */
//
//		model.addAttribute("user", user);
//
//		return "register";
//	}
//
//	@RequestMapping(value = "/saveUser", method = RequestMethod.POST)
//	public String saveUser(Locale locale, Model model, Principal principal, @ModelAttribute("user") User user) {
//
//		user.setLoggedIn(true);
//
//		Utf8Serializer serializer = new Utf8Serializer();
//
//		user.setLogin(serializer.deserialize(user.getLogin()));
//
//		user.setPassword(encoder.encode(user.getPassword()));
//
//		user.setRole(Role.USER);
//
//		List<String> favArticleHashes = new ArrayList<>();
//		user.setFavoriteArticleHashes(favArticleHashes);
//
//		List<String> selectedCategories = Redis.getAllCids();
//		user.setSelectedCategories(selectedCategories);
//
//		List<String> selectedChannels = Redis.getAllChids();
//		user.setSelectedChannels(selectedChannels);
//
//		Redis.saveUser(user);
//
//		return "redirect:/";
//	}
//}
