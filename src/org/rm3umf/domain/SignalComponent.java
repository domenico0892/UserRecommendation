package org.rm3umf.domain;

import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;



public class SignalComponent {
	
	private Concept concept;
	private User user;
	private Period period;
	
	
	private int occorence;
	private double tf;
	private double idf;
	private double tfidf;
	
	
public SignalComponent(){
		
	}
	
	public SignalComponent(Concept concept,User user,Period period){
		this.concept=concept;
		this.user=user;
		this.period=period;
		this.occorence=0;
//		this.tf=0;
//		this.idf=0;
//		this.tfidf=0;
	}
	
	public double getTf() {
		return tf;
	}
	public void setTf(double tf) {
		this.tf = tf;
	}
	public double getIdf() {
		return idf;
	}
	public void setIdf(double idf) {
		this.idf = idf;
	}
	public double getTfidf() {
		return tfidf;
	}
	public void setTfidf(double tfidf) {
		this.tfidf = tfidf;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public int getOccorence() {
		return occorence;
	}
	public void setOccorence(int occorence) {
		this.occorence = occorence;
	}
	public Concept getConcept() {
		return concept;
	}
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	
	
	
	
	/**
	 * Incrementa e restituisce il numero di volte che il concept � stato trovato nello pseudoDocument
	 * @return occurence
	 */
	public int incrementOccurence(){
		this.occorence++;
		return occorence;
	}
	
	/**
	 * PERSISTENCE
	 */
	
//	public void save() throws PersistenceException{
//		//save concept 
//		AAFacadePersistence.getInstance().signalComponentSave(this);
//	}
	
	public String toString(){
		return "[SIGNAL_COMP:CONCEP="+this.concept.getNameConcept()+"("+concept.getType()+")"+" USER="+this.user.getIduser()+" occurence:"+this.occorence+" tf:"
		+this.tf+" idf="+this.idf+" ]";
	}

}