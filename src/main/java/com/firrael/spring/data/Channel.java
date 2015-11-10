package com.firrael.spring.data;

import com.firrael.spring.data.base.SelectableData;

public class Channel extends SelectableData {

	public Channel(String name) {
		super(name);
	}
	
	public Channel(String name, Long count) {
		super(name, count);
	}
}