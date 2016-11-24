package org.rm3umf.framework.buildmodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.domain.User;
import org.rm3umf.math.VectorUtil;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;
/**
 * FASE 2: a partire dagli utenti ottengo tutte le signal compoent  e crea i segnali  
 * 
 * In pratica questa classe data un utente crea la rappresetazione di esso costruendo un insieme di segnali
 * utilizzando le signal componente precedentemente memorizzate.
 * 
 * @author giulz
 *
 */
public class BuiltSignal {
	
	private int lenghtSignal;
	
	private int SOGLIASEGNALI;

	private FactorySmoothing factorySmoothing;
	private int ordineSmooth;
	private boolean doSmoothing;
	
	
	public BuiltSignal(int lenghtSignal,int sogliasegnali,int ordineSmooth){
				
		this.lenghtSignal=lenghtSignal;
		this.SOGLIASEGNALI=sogliasegnali;
		this.ordineSmooth=ordineSmooth;
		this.factorySmoothing=new FactorySmoothing(ordineSmooth);
		if(ordineSmooth==0)
			doSmoothing=false;
		else
			doSmoothing=true;
	}
	
	public void buildSignal(List<User> listaUser) throws PersistenceException{
		
		//AAFacadePersistence.getInstance().signalDelete();
	
		
		int i=0;
		for(User user :listaUser){
			i++;
			System.out.println("("+i+")salvo i segnal dell'utente : "+user);
			List<Signal> signalToUser=builtSignalByUser(user);
			int numSignals=signalToUser.size();
			
			
			//se user ha un num di segnali > THRESHOLD salvo i segnali altrimenti no
			if(numSignals>SOGLIASEGNALI){
				
				for(Signal signal : signalToUser){
					//effettuo lo smoothing
					if(doSmoothing){
						double[] smoothsingnal=this.factorySmoothing.mediaMobile(signal.getSignal());
						signal.setSignal(smoothsingnal);
						}
					AAFacadePersistence.getInstance().signalSave(signal);
					}
			}else{
				System.err.println("Non salvo i segnali di "+user+ " perche' inferiori alla soglia ("+numSignals+")");
			}
				
		}
		
	}
	
	/**
	 * Questo metodo crea tutti quanti i segnali per uno specifico utente.
	 * Recupera tutte le signal component dell'utente dal DB  e le aggrega in base al periodo 
	 * e al concept.
	 * 
	 * @param user 
	 * @return listSignal - la lista di segnali che forma il profilo
	 * @throws PersistenceException
	 */
	public List<Signal> builtSignalByUser(User user) throws PersistenceException{
		
		double maxValue=0.;
		double tot = 0.;
		double coefNorm =0.;
		
		
		List<SignalComponent> signalCompByUser = 
				AAFacadePersistence.getInstance().signaComponentRetriveByUser(user);
		Map<String,Signal> conceptid2signal=new HashMap<String,Signal>();
		for(SignalComponent signalComponent:signalCompByUser){
			Concept concept =signalComponent.getConcept();
			String idConcept=concept.getId();
			Signal signal=conceptid2signal.get(idConcept);
			if(signal==null){
				signal=new Signal();
				signal.setUser(user);
				signal.setConcept(concept);
				signal.setSignal(new double[this.lenghtSignal]);
				conceptid2signal.put(idConcept, signal);
			}
			//Adesso sto provando un idf sul periodo e un tf sull'intero UM
			
			//Calcolo il valore della componente i-esima del segnale 
			double value = signalComponent.getIdf(); //*signalComponent.getTf();  
			
			//System.out.println("value:"+value)
					
			tot+=value;
			
			
			//TF GLOBALE
			//Serve per fare un TF globale e non relativo allo pseudo-document di un periodo
			double occurence=signalComponent.getOccorence();
			//aggiorno il valore totale

			//sommo tutte le componenti per normalizzare alla fine
			coefNorm+=occurence;
			
			
			if(occurence>maxValue){
				maxValue=occurence;
			}
			
			//Setto la componente i-esima
    		signal.getSignal()[signalComponent.getPeriod().getIdPeriodo()]=value;
		}
		
		
		
//		La somma dei moduli di tutti i segnali da 1
//		double totModule = 0.;
//		for(String conceptid:conceptid2signal.keySet()){
//			Signal signal = conceptid2signal.get(conceptid);
//			totModule+=VectorUtil.getInstance().vectorModule(signal.getSignal());
//		
//		}
//		

		//prendo tutti i segnali e li divido per il numero massimo di occorrenze trovate in 
		//un periodo
		for(String conceptid:conceptid2signal.keySet()){
			Signal signal = conceptid2signal.get(conceptid);
			double[] arraySignal=signal.getSignal();
			for(int i=0; i<arraySignal.length;i++){
				arraySignal[i]=arraySignal[i]/maxValue;
			}
		}
		
		
		return new LinkedList<Signal>(conceptid2signal.values());
		
		
	}

}
