package com.firrael.spring.data;

import com.firrael.spring.parsing.Channel;

public class Rss {
	private Channel channel;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	@Override
	public String toString() {
		return channel.toString();
	}
	
}
