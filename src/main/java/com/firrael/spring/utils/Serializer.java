package com.firrael.spring.utils;

public interface Serializer<T, E> {
	E serialize(T object);
	
	T deserialize(E object);
}
