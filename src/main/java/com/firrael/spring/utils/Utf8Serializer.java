package com.firrael.spring.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class Utf8Serializer implements Serializer<String, String> {

	@Override
	public String serialize(String normalString) {
		try {
			return URLEncoder.encode(normalString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return normalString;
	}

	@Override
	public String deserialize(String utf8String) {
		/*
		 * First step: encode the incorrectly converted UTF-8 strings back to
		 * the original URL format
		 */
		String restoredString = null;
		try {
			restoredString = URLEncoder.encode(utf8String, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/*
		 * Second step: decode to UTF-8 again from the original one
		 */
		try {
			return URLDecoder.decode(restoredString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return utf8String;
	}

}
