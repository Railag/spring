package com.firrael.spring.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListSerializer implements Serializer<List<String>, String> {
	
	public static ListSerializer getInstance() {
		return new ListSerializer();
	}
	
	@Override
	public String serialize(List<String> list) {
		if (list.isEmpty())
			return "";
		
		StringBuilder builder = new StringBuilder();
		for (String s : list) {
			builder.append(s).append("|");
		}
		return builder.substring(0, builder.length() - 1);
	}

	@Override
	public List<String> deserialize(String object) {
		String items = object.toString();
		List<String> list = new ArrayList<String>(Arrays.asList(items.split("|")));
		return list;
	}
}
