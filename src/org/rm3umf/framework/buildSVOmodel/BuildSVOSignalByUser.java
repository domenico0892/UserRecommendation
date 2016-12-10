package org.rm3umf.framework.buildSVOmodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.domain.User;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;

public class BuildSVOSignalByUser implements Callable<List<Signal>> {

	private User user;
	private int length;

	public BuildSVOSignalByUser (User u, int l) {
		this.user = u;
		this.length = l;
	}

	@Override
	public List<Signal> call() {
//		SyncCount.getInstance().inc();
		double maxValue=0.;
		double tot = 0.;
		double coefNorm =0.;


		List<SignalComponent> signalCompByUser = null;
		try {
			
			signalCompByUser = AAFacadePersistence.getInstance().signaComponentRetriveByUser(user);
			System.out.println("Analizzo user: "+ this.user.getIduser());
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String,Signal> conceptid2signal=new HashMap<String,Signal>();
		for(SignalComponent signalComponent:signalCompByUser){
			
			Concept concept =signalComponent.getConcept();
			
			String idConcept=concept.getId();
			Signal signal=conceptid2signal.get(idConcept);
			if(signal==null){
				signal=new Signal();
				signal.setUser(user);
				signal.setConcept(concept);
				signal.setSignal(new double[length]);
				conceptid2signal.put(idConcept, signal);
			}
			//Setto la componente i-esima
			signal.getSignal()[signalComponent.getPeriod().getIdPeriodo()]=signalComponent.getTfidf();
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
		//		for(String conceptid:conceptid2signal.keySet()){
		//			Signal signal = conceptid2signal.get(conceptid);
		//			double[] arraySignal=signal.getSignal();
		//			for(int i=0; i<arraySignal.length;i++){
		//				arraySignal[i]=arraySignal[i]/maxValue;
		//			}
		//		}
		//		
		
		
		return new LinkedList<Signal>(conceptid2signal.values());


	}
}



