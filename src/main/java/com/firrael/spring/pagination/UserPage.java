package com.firrael.spring.pagination;

import java.util.List;

import com.firrael.spring.data.models.User;

public class UserPage extends SimplePage<User> {

	public UserPage(List<User> items, int number) {
		super(items, number);
	}
}