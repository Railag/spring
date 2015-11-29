package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.List;

import com.firrael.spring.data.base.Fields;

public class ArticleFields implements Fields {
	public final static String TITLE = "title";
	public final static String LINK = "link";
	public final static String DESCRIPTION = "description";
	public final static String DATE = "pubDate";
	public final static String AUTHOR = "author";
	public final static String CATEGORY = "category";

	@Override
	public List<Object> asArray() {
		List<Object> fields = new ArrayList<>();
		fields.add(TITLE);
		fields.add(LINK);
		fields.add(DESCRIPTION);
		fields.add(DATE);
		fields.add(AUTHOR);
		fields.add(CATEGORY);
		return fields;
	}
}
