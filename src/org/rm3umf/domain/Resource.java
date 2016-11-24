package org.rm3umf.domain;

import java.security.NoSuchAlgorithmException;

import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;

import util.MD5;

public class Resource {
	
	private String id;
	private String url;
	private String page;
	
	public Resource(){
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	
	public String getMD5(String url){
		String md5=null;
		try {
			md5=MD5.getInstance().hashData(url);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return md5;
	}
	
	/*
	 * PERSISTENCE
	 */
	
	public void save() throws PersistenceException{
		AAFacadePersistence.getInstance().resourceSave(this);
	}
	
	
	public String toString(){
		return "[RESOURCE url="+this.url+"]";
	}
	
	
	

}
