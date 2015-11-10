package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.List;

import com.firrael.spring.data.base.Fields;

public class UserFields implements Fields {
	public final static String LOGIN = "login";
	public final static String PASSWORD = "password";
	public final static String EMAIL = "email";
	public final static String FAVORITE_ARTICLES = "fav_articles";
	public final static String SELECTED_CATEGORIES = "selected_categories";
	public final static String SELECTED_CHANNELS = "selected_channels";
	public final static String AUTH_TYPE = "auth_type";
	public final static String IS_LOGGED_IN = "is_logged_in";
	public final static String ROLE = "role";

	public List<Object> asArray() {
		List<Object> fields = new ArrayList<>();
		fields.add(LOGIN);
		fields.add(PASSWORD);
		fields.add(EMAIL);
		fields.add(FAVORITE_ARTICLES);
		fields.add(SELECTED_CATEGORIES);
		fields.add(SELECTED_CHANNELS);
		fields.add(AUTH_TYPE);
		fields.add(IS_LOGGED_IN);
		fields.add(ROLE);
		return fields;
	}
}
