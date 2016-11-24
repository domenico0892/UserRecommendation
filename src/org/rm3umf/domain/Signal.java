package org.rm3umf.domain;

public class Signal implements Comparable<Signal>{
	
	private User user;
	private Concept concept;
	private double[] signal;
	
	
	
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Concept getConcept() {
		return concept;
	}
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	public double[] getSignal() {
		return signal;
	}
	public void setSignal(double[] signal) {
		this.signal = signal;
	}
	
	public String toString(){
//		String stringSignal="["+valueSignal(signal)+"]";
		String stringSignal="["+"-signal-"+"]";
		return "[SIGNAL:"+user.getIduser()+"-"+concept.getId()+"-"+stringSignal+"]";
	}
	
	private String valueSignal(double[] array){
		String stringa="";
		if(array!=null){
			for(double f:array){
				stringa=stringa+" "+f;
			}
		}
		return stringa;
	}
	
	public int compareTo(Signal arg0) {
		return concept.getId().compareTo(arg0.getConcept().getId());
	}
	
	
	

}
