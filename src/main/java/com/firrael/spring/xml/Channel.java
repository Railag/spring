package com.firrael.spring.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@XStreamAlias("channel")
@Data
public class Channel {
	private List<Article> item;
}
