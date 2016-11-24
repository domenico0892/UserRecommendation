package org.rm3umf.domain;

import java.util.LinkedList;
import java.util.List;

import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;


/**
 * E' una classe che mi serve solo per fare l'importing
 * @author Giulz
 *
 */

public class Message {
	
	private User user;
	private Long idMessage;

	private String date;
	private String text;
	
	private List<Resource> resources;
	
	
	public Message(){
		this.resources= new LinkedList<Resource>();
	}
	
	public Long getIdMessage() {
		return idMessage;
	}
	public void setIdMessage(Long idMessage) {
		this.idMessage = idMessage;
	}
	public User getUser() {
		return this.user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
//	public List<Resource> getResources(){
//		return this.resources;
//	}
//	
//	public void setResources(List<Resource> resources){
//		this.resources=resources;
//	}
//	
//	public void save() throws PersistenceException{
//		AAFacadePersistence.getInstance().messageSave(this);
//	}
//	
//	public void addResource(Resource resource) {
//		this.resources.add(resource);
//	}
	
	
	public String toString(){
		return "[MESSAGE: id="+idMessage+"("+date+")"+text+" #resource="+resources.size()+"]";
	}
	
	
	
	
	
	
	
	
	
	
}
