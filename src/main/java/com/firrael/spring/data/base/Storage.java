package com.firrael.spring.data.base;

import java.util.List;

public interface Storage<T extends Entity<T>, E extends Fields> {
	void add(T item, String id);
	
	T get(String hash, E fields);
	
	List<T> getItems(final int count);
}