package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.List;

import com.firrael.spring.data.base.SelectableData;

public class Channel extends SelectableData {

	public Channel(String name) {
		super(name);
	}
	
	public static List<Channel> stringsToChannels(List<String> names) {
		ArrayList<Channel> result = new ArrayList<>();
		for (String channel : names) {
			result.add(new Channel(channel));
		}
		
		return result;
	}
	
	public static List<String> channelsToStrings(List<Channel> channels) {
		ArrayList<String> result = new ArrayList<>();
		for (Channel channel : channels) {
			result.add(channel.getName());
		}
		
		return result;
	}	
}