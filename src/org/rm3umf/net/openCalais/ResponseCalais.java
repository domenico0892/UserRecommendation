package org.rm3umf.net.openCalais;
/**
 * Questa classe incapsula la response di OpenCalais 
 * @author Giulz
 *
 */

public class ResponseCalais {
	
	private String name;
	private String type;
	private int occurences;
	private double relevance;
	private String uri;
	
	
	public ResponseCalais(){
		
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public int getOccurences() {
		return occurences;
	}


	public void setOccurences(int instances) {
		this.occurences = instances;
	}
	
	/**/
	


	public double getRelevance() {
		return relevance;
	}


	public void setRelevance(double relevence) {
		this.relevance = relevence;
	}


	public String getUri() {
		return uri;
	}


	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
	
	
	

}
