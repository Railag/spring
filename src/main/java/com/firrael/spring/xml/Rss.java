package com.firrael.spring.xml;

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
