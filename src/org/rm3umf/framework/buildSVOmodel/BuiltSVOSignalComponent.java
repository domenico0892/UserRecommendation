package org.rm3umf.framework.buildSVOmodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.*;
import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.PseudoFragment;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.framework.buildmodel.extractor.ExtractorException;
import org.rm3umf.framework.buildmodel.extractor.StrategyExtraction;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.sentiment.SentimentResolver;

public class BuiltSVOSignalComponent {

	private static Logger logger = Logger.getLogger(BuiltSVOSignalComponent.class);

	private StrategyExtraction strategyExtractor;
	private SentimentResolver sentimentResolver;
	private double alfa;
	private double beta;
	private double gamma;


	public BuiltSVOSignalComponent(StrategyExtraction strategyExtractor, double a, double b, double c) throws Exception {
		this.strategyExtractor=strategyExtractor;
		this.sentimentResolver = new SentimentResolver("en");
		this.alfa = a;
		this.beta = b;
		this.gamma = c;
	}

	/**
	 * A partire dai lista dei periodi crea tutti quante le Signal Component 
	 * 
	 * @param listaPeriodi
	 * @throws PersistenceException
	 * @throws ExtractorException
	 */

	public void createSignalComponent(List<Period> listaPeriodi, ExecutorService e) throws PersistenceException, ExtractorException{/*
	 *Creo i signal componente relativi relative ai periodi 
	 */
		
		for(Period period :listaPeriodi.subList(0, listaPeriodi.size())){
//			System.out.println("Costruisco i SignalComponent per il periodo "+period);
			//salvo il periodo
			AAFacadePersistence.getInstance().periodSave(period);
			//il periodo crea tutte le signal component prensenti in esso
			ExtractSignalComponentByPeriod esc;
			try {
				e.submit(new ExtractSignalComponentByPeriod(strategyExtractor, alfa, beta, gamma, period, e));
//				esc = new ExtractSignalComponentByPeriod(strategyExtractor, alfa, beta, gamma, period, e);
//				esc.run();
//				System.err.println("1");
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
		}
//		try {
//			e.awaitTermination(10, TimeUnit.DAYS);
//		} catch (InterruptedException ex) {
//			// TODO Auto-generated catch block
//			ex.printStackTrace();
//		}
	}



	/**
	 * Questo metodo 
	 * @param period
	 * @throws ExtractorException
	 * @throws PersistenceException
	 */
}