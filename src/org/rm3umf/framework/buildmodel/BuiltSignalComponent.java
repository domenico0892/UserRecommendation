package org.rm3umf.framework.buildmodel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.*;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.PseudoFragment;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.framework.buildmodel.extractor.ExtractorException;
import org.rm3umf.framework.buildmodel.extractor.StrategyExtraction;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;

/**
 * FASE 1:  Analizza il periodo e crea le signal component.
 * 
 * Questa classe analizze un periodo. In pratica significa che recupera tutti gli pseudo-fragment relativi al
 * periodo li analizza e ci estrae le signal component che verranno memorizzate sul DB per la successiva 
 * fase.
 * @author giulz
 *
 */
public class BuiltSignalComponent {
	
	private static Logger logger = Logger.getLogger(BuiltSignalComponent.class);
	
	private StrategyExtraction strategyExtractor;
	private WeighingScheme ws;
	
	
	public BuiltSignalComponent(StrategyExtraction strategyExtractor){
		this.strategyExtractor=strategyExtractor;
		this.ws=new WeighingScheme();
	}
	
	
	
	/**
	 * A partire dai lista dei periodi crea tutti quante le Signal Component 
	 * 
	 * @param listaPeriodi
	 * @throws PersistenceException
	 * @throws ExtractorException
	 */
	
	public void createSignalComponent(List<Period> listaPeriodi) throws PersistenceException, ExtractorException{/*
		 *Creo i signal componente relativi relative ai periodi 
		 */
		for(Period period :listaPeriodi.subList(0, listaPeriodi.size())){
			System.out.println("Costruisco i SignalComponent per il periodo "+period);
			//salvo il periodo
			AAFacadePersistence.getInstance().periodSave(period);
			//il periodo crea tutte le signal component prensenti in esso
			extractSignalComponent(period);

		}
	}
	
	
	
	/**
	 * Questo metodo 
	 * @param period
	 * @throws ExtractorException
	 * @throws PersistenceException
	 */
	public void extractSignalComponent(Period period) throws ExtractorException, PersistenceException{
		logger.info("creo i signal component");
		List<PseudoFragment> listaPseudo=AAFacadePersistence.getInstance().pseudoDocumentGetByPeriod(period);
		//Memorizzerà tutti i signal component del periodo 
		//i signalComp da passare alla classe WeighingScheme 
		List<SignalComponent> periodSigComp = new LinkedList<SignalComponent>(); 
		List<SignalComponent> sigCompForPseudo=null;
		for(PseudoFragment pseudo: listaPseudo){
			logger.info("analizzo pseudoDocument : "+pseudo);
			//estraggo dallo pseudo-document i signalComponent presenti
			sigCompForPseudo=this.strategyExtractor.extract(pseudo);
		//	System.out.println(this.weighingScheme);
			if(sigCompForPseudo.size()>0){
				ws.setTF(sigCompForPseudo); //analizzo gli pseudoDocument   
				periodSigComp.addAll(sigCompForPseudo);
			}
			//salvo tutti i signalCom
		}
		//setto idf
		ws.setIDF(periodSigComp); 
		
		//Salva tutti....potrei farli ritornare
		for(SignalComponent sigComp:periodSigComp){
			System.out.println("salvo :"+sigComp);
			AAFacadePersistence.getInstance().signalComponentSave(sigComp);
		}
	}

}
