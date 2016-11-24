package org.rm3umf.domain;

import java.util.List;

import org.rm3umf.framework.buildmodel.extractor.ExtractorException;
import org.rm3umf.framework.buildmodel.extractor.FactoryStrategyExtraction;
import org.rm3umf.framework.buildmodel.extractor.StrategyExtraction;

public class PseudoFragment {
	
	
	
	private User user;
	private Period period;
//	//voglio che ogni pseudo-doc conosca le componenti 
//	private List<SignalComponent> signalComponets; 
	
//	//pattern strategy
//	private StrategyExtraction estractrionStrategy;
	
	//lista di tweets per periodo
	private List<Message> messages ;
	
	
    public PseudoFragment(){
    	//prendo la strategia di estrazioe da factory
  //  	this.estractrionStrategy=FactoryStrategyExtraction.getInstance().getStrategyExtraction();
    }
	
	public PseudoFragment(User user,Period period,List<Message> tweets){
		this.user=user;
		this.period=period;
		this.messages=tweets;
		
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

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public List<Message> getMessages() {
		return this.messages;
	}
	
	
//	public List<SignalComponent> extractSignalComponent() throws ExtractorException{
//		return this.estractrionStrategy.extract(this);
//	}
//	
//	public StringBuffer getPseudoDocument(){
//		
//		
//	}
	
	public String toString(){
		return "[PSEUDO-DOCUMENT: period="+this.period.getIdPeriodo()+" user="+this.user.getIduser()+" lista=]";
	}
	

}
