package org.rm3umf.domain;

public class SignalGlobal {
	
	private Concept concept;
	private double[] globalSignal;
	
	
	
	public double[] getGlobalSignal() {
		return globalSignal;
	}
	
	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public void setGlobalSignal(double[] globalSignal) {
		this.globalSignal = globalSignal;
	}
	

}
