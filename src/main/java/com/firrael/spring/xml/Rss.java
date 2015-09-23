package com.firrael.spring.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@XStreamAlias("rss")
@Data
public class Rss {
	private Channel channel;
}
