package com.firrael.spring.data;

import com.firrael.spring.data.base.SelectableData;

public class Category extends SelectableData {

	public Category(String name) {
		super(name);
	}

	public Category(String name, Long count) {
		super(name, count);
	}
}