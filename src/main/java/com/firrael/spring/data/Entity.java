package com.firrael.spring.data;

import java.util.List;
import java.util.Map;

public interface Entity<T> {
	public Map<String, Object> toHashMap();

	public T initialize(List<Object> values);

	@Override
	public int hashCode();
}
