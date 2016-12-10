package org.rm3umf.framework.buildSVOmodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

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
public class BuiltSVOSignal {

	private int lenghtSignal;

	private int SOGLIASEGNALI;

	//	private FactorySmoothing factorySmoothing;
	private int ordineSmooth;
	private boolean doSmoothing;


	public BuiltSVOSignal(int lenghtSignal,int sogliasegnali,int ordineSmooth){

		this.lenghtSignal=lenghtSignal;
		this.SOGLIASEGNALI=sogliasegnali;
		this.ordineSmooth=ordineSmooth;
		//		this.factorySmoothing=new FactorySmoothing(ordineSmooth);
		if(ordineSmooth==0)
			doSmoothing=false;
		else
			doSmoothing=true;
	}

	public void buildSignal(List<User> listaUser) throws PersistenceException, InterruptedException, ExecutionException{

		//AAFacadePersistence.getInstance().signalDelete();

		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Future<List<Signal>>> signals = new LinkedList<>();

		for(User user :listaUser) 
			signals.add(es.submit(new BuildSVOSignalByUser(user, this.lenghtSignal)));

		for (Future<List<Signal>> sig : signals) {
			if (sig.get().size() > SOGLIASEGNALI) {
				for (Signal s : sig.get()) {
					AAFacadePersistence.getInstance().signalSave(s);
				}
			}}
	}

}