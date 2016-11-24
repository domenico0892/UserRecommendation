package org.rm3umf.framework.buildmodel;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rm3umf.domain.Period;
import org.rm3umf.domain.PseudoFragment;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.domain.User;
import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.buildmodel.extractor.ExtractorDBpediaEntity;
import org.rm3umf.framework.buildmodel.extractor.ExtractorDBpediaType;
import org.rm3umf.framework.buildmodel.extractor.ExtractorException;
import org.rm3umf.framework.buildmodel.extractor.ExtractorHashtag;
import org.rm3umf.lucene.FacadeLucene;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.MessageDAO;
import org.rm3umf.persistenza.PersistenceException;


/**
 * Questa classe del framework costruisce i segnali in base al paramentro DAYPERIOD.
 * Inoltre � sempre in questo componente in cui vengono estratti i concept a partire dagli pseudoDocument 
 * Inoltre questo modulo potr� essere rilanciato pi� volte nel ciclo di vita dell'applicazione per valutare 
 * come cambiano le performace scegliendo un intervallo di tempo differente per la costruzione dei SignalComponent
 * 
 * ATTENZIONE:prima di avviare questo modulo � necessario che sia stato ultimato l'importing
 * 
 * @author Giulz
 *
 */


public class BuildModel {
	
	
	/*PARAMETRI*/
	
	//giorni con cui si costruiscono i segnali
	private final int DAYPERIOD=7;  //giorni del periodo 
	
	//rappresenta il numero minimo di segnali che rendono un profilo utente valido	
	private int SOGLIASEGNALI = 10;  //al di sotto di questa soglia di segnali il profilo utente viene scartato
	
	private int SOGLIACONCEPT = 2;  //al di sotto di questa soglia di concept il concept e tutti i sig comp 
									//vengono scartati
	
	//VARIABILI ISTANZA
	private List<User> listaUser;
	private BuiltSignalComponent signalComponetCreator;
	
	
	public BuildModel() throws PersistenceException{
		//
		this.listaUser=AAFacadePersistence.getInstance().userRetriveAll();
		//
		this.signalComponetCreator=new BuiltSignalComponent(new ExtractorHashtag());
	
	}
	
	public void start () throws  PersistenceException, ExtractorException, BuildModelException{
		
		/*
		 *=============================================
		 * FASE -1 : Preparo il DB 
		 *=============================================
		 * */
	
		/*prepara il database*/
		//AAFacadePersistence.getInstance().prepareDBBuilderSignal();
		
		/*
		 *=============================================
		 * FASE 1.0 : Creazione dei periodi 
		 *=============================================
		 * */
		
		//FILTRO PERIODI
		FactoryPeriod factrotyPeriod=new FactoryPeriod();
		
		//recupero la massima e minima data di pubblicazione dei tweets
		String startDate=AAFacadePersistence.getInstance().periodGetMinDate();
		String endDate=AAFacadePersistence.getInstance().periodGetMaxDate();
//		
		
		//creo la lista dei periodi in base alla data massima e minima dei tweet
		List<Period> listaPeriodi=factrotyPeriod.createPeriods(startDate,endDate,this.DAYPERIOD);
		
//		/*
//		 *=============================================
//		 * FASE 1.5 : creazione delle signal component 
//		 *=============================================
//		 * */
//		
		//Crea le signal component relativi a tutti i periodi
		signalComponetCreator.createSignalComponent(listaPeriodi);
//		
		/*
		 *=============================================
		 * FASE 1.9 :  Eliminiano dal sistema le signal component inrrilevanti
		 *=============================================
		 * */
		
		/* Elimino i signal componente relativi ad un concept referenziato solo una volta
		 * perchè probabilmente sono relativi ad errori o cmq non sono rilevanti 
		 */
//		FilterSignalComponent filterSigComp = new FilterSignalComponent();
//		filterSigComp.filter(SOGLIACONCEPT);
				
		/*
		 *===================================
		 * FASE 2 : creazione dei segnali 
		 *===================================
		 * */
		
//		List<Period> listaPeriodi = AAFacadePersistence.getInstance().periodRetriveAll();
		//AAFacadePersistence.getInstance().signalDelete();
		BuiltSignal signalCreator = new BuiltSignal(listaPeriodi.size(),SOGLIASEGNALI,1); //smoothing
		signalCreator.buildSignal(listaUser);
	
		/*
		 *===================================
		 * FASE 3 : creazione dell'indice
		 *===================================
		 * */
//		
//		List<UserModel> modelliUtente = AAFacadePersistence.getInstance().userModelRetriveAll(); 
//		IndexPreparator indexPreparator = new IndexPreparator("./index");
//		indexPreparator.prepareIndex(modelliUtente);
//		
		/*
		 *===================================
		 * FASE 3 : creazione dell'indice
		 *===================================
		 * */
		
		
			
	}
	
	

}
