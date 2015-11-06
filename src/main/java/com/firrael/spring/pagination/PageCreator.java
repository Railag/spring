package com.firrael.spring.pagination;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class PageCreator {
	
	public final static int PAGE_SIZE = 5;
	
	public static List<?> getPagingList(List<?> items, Class<?> clazz) {
		ArrayList<SimplePage<?>> pages = new ArrayList<>();
		
		Constructor<?> constructor = clazz.getConstructors()[0];
		
		try {
		
		if (items.size() <= PAGE_SIZE) {
			SimplePage<?> page = (SimplePage<?>) constructor.newInstance(items, 0);
			page.setFirst(true);
			page.setLast(true);
			pages.add(page);
			return pages;
		}
		
		for (int i = 0; i < items.size(); i += PAGE_SIZE) {
			if (items.size() - PAGE_SIZE < i) {
				SimplePage<?> page = (SimplePage<?>) constructor.newInstance(items.subList(i, items.size()), i / 5);
				page.setLast(true);
				pages.add(page);
				return pages;
			}
				
			SimplePage<?> page = (SimplePage<?>) constructor.newInstance(items.subList(i, i + PAGE_SIZE), i / 5);
			if (i == 0)
				page.setFirst(true);
			
			if (i + PAGE_SIZE == items.size())
				page.setLast(true);

			pages.add(page);
		}
		
		} catch(Exception e) {
			e.printStackTrace();
		}

		return pages;
	}
}
