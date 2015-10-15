package com.firrael.spring.data.base;

import java.util.List;
import java.util.Map;

public interface Entity<T> {

	public T initialize(List<Object> values);

	public Map<String, Object> toHashMap();

	@Override
	public int hashCode();
}