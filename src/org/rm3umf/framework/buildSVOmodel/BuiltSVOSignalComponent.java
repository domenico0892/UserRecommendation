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

	public void createSignalComponent(List<Period> listaPeriodi) throws PersistenceException, ExtractorException{/*
	 *Creo i signal componente relativi relative ai periodi 
	 */
		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for(Period period :listaPeriodi.subList(0, listaPeriodi.size())){
			System.out.println("Costruisco i SignalComponent per il periodo "+period);
			//salvo il periodo
			AAFacadePersistence.getInstance().periodSave(period);
			//il periodo crea tutte le signal component prensenti in esso
			try {
				es.submit(new ExtractSignalComponentByPeriod(strategyExtractor, alfa, beta, gamma, period));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		try {
			es.awaitTermination(10, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	/**
	 * Questo metodo 
	 * @param period
	 * @throws ExtractorException
	 * @throws PersistenceException
	 */
}