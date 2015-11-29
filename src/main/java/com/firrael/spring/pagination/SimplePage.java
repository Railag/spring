package com.firrael.spring.pagination;

import java.util.ArrayList;
import java.util.List;

public abstract class SimplePage<T> implements Page<T> {

	protected List<T> items = new ArrayList<>();
	protected int number = 0;
	protected boolean isFirst;
	protected boolean isLast;
	
	public SimplePage(List<T> items, int number) {
		this.items = items;
		this.number = number;
	}

	@Override
	public List<T> getItems() {
		return items;
	}

	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public void setItems(List<T> items) {
		this.items = items;
	}

	@Override
	public T getItem(int position) {
		return items.get(position);
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}
}