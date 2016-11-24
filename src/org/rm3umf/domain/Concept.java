package org.rm3umf.domain;

/**
 * Questa classe rappresenta un concept generico. I concept pensati sono named entity e hashtag. 
 * La variabile di instanza type serve solo per named entity 
 * 
 * @author giulz
 *
 */

public class Concept {
	
	private String id;
	private String nameConcept;
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Concept(){
		
	}
	
	public Concept(String nameConcept){
		this.nameConcept=nameConcept;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNameConcept() {
		return nameConcept;
	}

	public void setNameConcept(String nameConcept) {
		this.nameConcept = nameConcept;
	}
	
	public boolean equals(Object concept){
		return (this.nameConcept).equals(((Concept)concept).getNameConcept());
	}
	
	public int hashCode(){
		return nameConcept.hashCode();
	}

	
	public String toString(){
		return "[CONCPET : name="+this.id+"]";
	}
	

}
