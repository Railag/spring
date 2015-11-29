package com.firrael.spring.pagination;

import java.util.List;

public interface Page<T> {
	public List<T> getItems();
	
	public int getNumber();
	
	public void setItems(List<T> items);
	
	public T getItem(int position);
}