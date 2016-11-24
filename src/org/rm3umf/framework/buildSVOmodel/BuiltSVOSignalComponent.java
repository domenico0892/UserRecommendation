package org.rm3umf.framework.buildSVOmodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
		for (PseudoFragment pf : listaPseudo)
		extractSignalComponentByPseudoFragment(pf);
	}

	public void extractSignalComponentByPseudoFragment (PseudoFragment pf) throws ExtractorException, PersistenceException {

		Map<Concept, List<Message>> concept2Messages = concept2Messages(pf);
		Map<Message, Integer> message2sentiment = message2sentiment(pf);
		
		SignalComponent sigComp;
		
		double s, v, o, svo;
		int p, neg, neu;

		for (Concept c : concept2Messages.keySet()) {
			v = (double) concept2Messages.get(c).size() / pf.getMessages().size();;
			p = getPositiveMessages(message2sentiment);
			neg = getNegativeMessages(message2sentiment);
			neu = getNeutralMessages(message2sentiment);
			o = neu / (neu + neg + p);
			s = 1 / (1 + Math.pow(10, -((p - neg)/(p + neg))));
			svo = alfa * s + beta * v + gamma * o;
			sigComp = new SignalComponent();
			sigComp.setConcept(c);
			sigComp.setPeriod(pf.getPeriod());
			sigComp.setUser(pf.getUser());
			sigComp.setTfidf(svo);
			AAFacadePersistence.getInstance().signalComponentSave(sigComp);
		}


	}

	private Map<Concept, List<Message>> concept2Messages (PseudoFragment pf) throws ExtractorException {
		List<Concept> concepts;
		List<Message> l;
		Map<Concept, List<Message>> concept2Messages = new HashMap<>();
		for (Message m : pf.getMessages()) {
			concepts = this.strategyExtractor.extract(m);
			for (Concept c : concepts) {
				if (!concept2Messages.containsKey(c)) {
					l = new LinkedList<Message>();
					l.add(m);
					concept2Messages.put(c, l);
				}
				else {
					l = concept2Messages.get(c);
					l.add(m);
				}
			}
		}
		return concept2Messages;
	}

	private Map<Message, Integer> message2sentiment (PseudoFragment pf) {
		Map<Message, Integer> message2sentiment = new HashMap<>();
		for (Message m : pf.getMessages())
			message2sentiment.put(m, this.sentimentResolver.getSentiment(m.getText()));
		return message2sentiment;
	}
	
	private int getPositiveMessages (Map<Message, Integer> map) {
		int n = 0;
		for (Message m : map.keySet() ) {
			if (map.get(m) > 0)
				n++;
		}
		return n;
	}
	
	private int getNeutralMessages (Map<Message, Integer> map) {
		int n = 0;
		for (Message m : map.keySet() ) {
			if (map.get(m) == 0)
				n++;
		}
		return n;
	}
	
	private int getNegativeMessages (Map<Message, Integer> map) {
		int n = 0;
		for (Message m : map.keySet() ) {
			if (map.get(m) < 0)
				n++;
		}
		return n;
	}

}
