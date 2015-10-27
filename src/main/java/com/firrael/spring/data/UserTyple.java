package com.firrael.spring.data;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public class UserTyple implements TypedTuple<String> {
	
		@Override
		public int compareTo(TypedTuple<String> arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Double getScore() {
			// TODO Auto-generated method stub
			return null;
		}
}
