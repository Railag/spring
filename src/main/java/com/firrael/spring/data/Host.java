package com.firrael.spring.data;

public enum Host {
	Habrahabr, GeekTimes, MegaMozg;

	public final static String HABR_HOST = "http://habrahabr.ru/rss";
	public final static String GEEKTIMES_HOST = "http://geektimes.ru/rss";
	public final static String MEGAMOZG_HOST = "http://megamozg.ru/rss";

	private final static String HABR = "habrahabr";
	private final static String GEEKTIMES = "geektimes";
	private final static String MEGAMOZG = "megamozg";

	@Override
	public String toString() {
		switch (this) {
		
		case Habrahabr:
			return HABR_HOST;
		case GeekTimes:
			return GEEKTIMES_HOST;
		case MegaMozg:
			return MEGAMOZG_HOST;
		default:
			return HABR_HOST;
		}
	}

	public static Host parseHost(String host) {
		if (host.contains(HABR))
			return Habrahabr;
		else if (host.contains(GEEKTIMES))
			return GeekTimes;
		else if (host.contains(MEGAMOZG))
			return MegaMozg;
		else
			return Habrahabr;
	}
	
	public String getChannelName() {
		switch(this) {
		
		case Habrahabr:
			return HABR;
		case GeekTimes:
			return GEEKTIMES;
		case MegaMozg:
			return MEGAMOZG;
		default:
			return HABR;	
		}
	}
}