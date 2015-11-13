package com.firrael.spring.data;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public class StringTyple implements TypedTuple<String> {
	
	private final String value;
	private final Double score;
	
	public StringTyple(String value, Double score) {
		this.value = value;
		this.score = score;
	}
	
		@Override
		public int compareTo(TypedTuple<String> second) {
			return this.getScore().compareTo(second.getScore());
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public Double getScore() {
			return score;
		}
}