package com.firrael.spring.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

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
		if (items.equals(""))
			return Collections.emptyList();
		
		String[] split = items.split(Pattern.quote("|"));
		List<String> list = new ArrayList<String>(Arrays.asList(split));
		return list;
	}
}
