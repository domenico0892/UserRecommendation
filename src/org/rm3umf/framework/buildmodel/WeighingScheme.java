package org.rm3umf.framework.buildmodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.rm3umf.domain.Concept;
import org.rm3umf.domain.SignalComponent;

/**
 * Questa classe avrà la responsabilità di pesare i SignalComponent. In pratica conoscendo i signalCompoent
 * di un singolo pseudoDocument setta la compoenent df mentre conoscendo tutti i singnalComponent dell'itero
 * periodo setterà gli idf.
 * 
 * ATTENZIONE: vorrei applicare il pattern Template Method
 * @author giulz
 *
 */

public   class WeighingScheme {
	
	
	private int numberOfDocument;
	
	public WeighingScheme(){
		this.numberOfDocument=0;
	}
	
	/**
	 * Questo metodo va eseguito ogni volta che si estraggono i signalComponent dallo pseudodocument 
	 * @param listSignalComp - lista sig comp estratti da pseudo-doc
	 */
	public void setTF(List<SignalComponent> listSignalComp){
		System.out.println("#document:"+this.numberOfDocument);
		this.numberOfDocument++;
		//calcolo la massima occorrenza
		int maxOccurence=0;
		for(SignalComponent sigCompCorr:listSignalComp ){
			int sigOccur=sigCompCorr.getOccorence();
			if(sigOccur>maxOccurence){
				maxOccurence=sigOccur;
			}
		}
		
		//setto la componente di pesatura locale allo pseudoDoc
		for(SignalComponent sigCompCorr:listSignalComp){
			double tf=((double)sigCompCorr.getOccorence())/((double)maxOccurence);
			sigCompCorr.setTf(tf);
		}
		
	}
	
	/**
	 * Questo metodo va eseguito a fine periodo 
	 * @param periodSignalComp -tutti  i signalcompoent estratti dal periodo corrente
	 */
	public void setIDF(List<SignalComponent> periodSignalComp){
		System.out.println("IDF -> #document:"+this.numberOfDocument);
		
		Map<String,List<SignalComponent>> mapPeriod=new HashMap<String, List<SignalComponent>>(); //memorizza i signal comp trovati nel periodo dividendoli 
		//scorro tutti i signalcomponent del period
		for(SignalComponent sigCompCorr:periodSignalComp ){	
			Concept concept=sigCompCorr.getConcept();
			String idConcept=concept.getId();
			//PERIOD CONCEPT OCCURENCE
			List<SignalComponent> listaSignComp=null; //memorizza la lista dei sigComp per concept di un period
			listaSignComp=mapPeriod.get(idConcept);
			if(listaSignComp==null){
				listaSignComp=new LinkedList<SignalComponent>();
				mapPeriod.put(idConcept,listaSignComp);
			}
			listaSignComp.add(sigCompCorr);
		}
		
		//aggiorno i sig Comp con idf
		for(String key:mapPeriod.keySet()){
			List<SignalComponent> listaSigComp=mapPeriod.get(key);
			double idf=Math.log(((double)this.numberOfDocument)/((double)listaSigComp.size()) );
		
			System.out.println("idf:"+idf);
			for(SignalComponent s:listaSigComp){
				s.setIdf(idf);
			}
		}//fine settaggio idf
		
	}
	
	
}
