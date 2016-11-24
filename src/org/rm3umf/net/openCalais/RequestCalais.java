package org.rm3umf.net.openCalais;

import java.util.List;

public class RequestCalais {
	
	private String text;
	
	private List<String> urls;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> links) {
		this.urls = links;
	}
	
	public int length(){
		return text.length();
	}

}
