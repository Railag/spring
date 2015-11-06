package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firrael.spring.data.base.Entity;
import com.firrael.spring.utils.ListSerializer;

public class User implements Entity<User> {
	private String uid;
	private String login;
	private String password;
	private String email;
	private List<String> favoriteArticleHashes;
	private List<String> selectedCategories;
	private List<String> selectedChannels;
	private AUTH authType;
	private boolean isLoggedIn;
	private Object authToken;

	public User() {
		favoriteArticleHashes = new ArrayList<>();
		selectedCategories = new ArrayList<>();
		authType = AUTH.NONE;
	}

	public enum AUTH {
		EMAIL, GOOGLE, FACEBOOK, TWITTER, NONE;
		
		@Override
		public String toString() {
			return this.name();
		};
	}
	
	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getFavoriteArticleHashes() {
		return favoriteArticleHashes;
	}

	public void setFavoriteArticleHashes(List<String> favoriteArticleHashes) {
		this.favoriteArticleHashes = favoriteArticleHashes;
	}

	public List<String> getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(List<String> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	public List<String> getSelectedChannels() {
		return selectedChannels;
	}

	public void setSelectedChannels(List<String> selectedChannels) {
		this.selectedChannels = selectedChannels;
	}

	@Override
	public int hashCode() {
		return login.hashCode();
	}

	public AUTH getAuthType() {
		return authType;
	}

	public void setAuthType(AUTH authType) {
		this.authType = authType;
	}

	public boolean getIsLoggedIn() {
		return isLoggedIn;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public Object getAuthToken() {
		return authToken;
	}

	public void setAuthToken(Object authToken) {
		this.authToken = authToken;
	}

	@Override
	public Map<String, Object> toHashMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(UserFields.LOGIN, getLogin());
		map.put(UserFields.PASSWORD, getPassword());
		map.put(UserFields.EMAIL, getEmail());
		map.put(UserFields.FAVORITE_ARTICLES, ListSerializer.getInstance().serialize(getFavoriteArticleHashes()));
		map.put(UserFields.SELECTED_CATEGORIES, ListSerializer.getInstance().serialize(getSelectedCategories()));
		map.put(UserFields.SELECTED_CHANNELS, ListSerializer.getInstance().serialize(getSelectedChannels()));
		map.put(UserFields.AUTH_TYPE, getAuthType().toString());
		map.put(UserFields.IS_LOGGED_IN, String.valueOf(getIsLoggedIn()));
		map.put(UserFields.AUTH_TOKEN, getAuthToken());
		return map;
	}

	@Override
	public User initialize(List<Object> values) {
		User user = new User();
		user.setLogin(values.get(0).toString());
		user.setPassword(values.get(1).toString());
		user.setEmail(values.get(2).toString());
		user.setFavoriteArticleHashes(ListSerializer.getInstance().deserialize(values.get(3).toString()));
		user.setSelectedCategories(ListSerializer.getInstance().deserialize(values.get(4).toString()));
		user.setSelectedChannels(ListSerializer.getInstance().deserialize(values.get(5).toString()));
		user.setAuthType(AUTH.valueOf(values.get(6).toString()));
		user.setLoggedIn(Boolean.parseBoolean(values.get(7).toString()));
		user.setAuthToken(values.get(8));
		return user;
	}
}

// user:hash-from-login {favorites, categories, site preferences,
// password}