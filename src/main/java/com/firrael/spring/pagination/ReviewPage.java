package com.firrael.spring.pagination;

import java.util.List;

public class ReviewPage extends SimplePage<Review> {

	public ReviewPage(List<Review> items, int number) {
		super(items, number);
	}
}