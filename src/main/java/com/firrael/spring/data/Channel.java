package com.firrael.spring.data;

import java.util.ArrayList;
import java.util.List;

public class Channel {
	private String name;
	private boolean checked;

	public Channel(String name) {
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean getChecked() {
		return checked;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Channel))
			return false;

		Channel secondChannel = (Channel) obj;

		boolean equals = this.name.equals(secondChannel.getName()) && this.getChecked() == secondChannel.getChecked();
		
		return equals;
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
	
	@Override
	public String toString() {
		return getName();
	}
	
	
}