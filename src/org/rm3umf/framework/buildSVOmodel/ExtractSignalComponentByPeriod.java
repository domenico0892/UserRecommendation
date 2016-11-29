package org.rm3umf.framework.buildSVOmodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

public class ExtractSignalComponentByPeriod implements Runnable {

	private StrategyExtraction strategyExtractor;
	private SentimentResolver sentimentResolver;
	private double alfa;
	private double beta;
	private double gamma;
	private Period period;
	private ExecutorService exs;

	public ExtractSignalComponentByPeriod (StrategyExtraction strategyExtractor, double a, double b, double c, Period p, ExecutorService e) throws Exception {
		this.strategyExtractor=strategyExtractor;
		this.sentimentResolver = new SentimentResolver("en");
		this.alfa = a;
		this.beta = b;
		this.gamma = c;
		this.period = p;
		this.exs = e;
	}

	public void run () {

		
		List<PseudoFragment> listaPseudo;
		try {
			listaPseudo = AAFacadePersistence.getInstance().pseudoDocumentGetByPeriod(period);
			
			for (PseudoFragment pf : listaPseudo) {
				try {
//					exs.submit(new ExtractSignalComponentByPF(strategyExtractor, alfa, beta, gamma, pf));
					System.out.println("Analyzing pf: " + pf.getPeriod().getIdPeriodo() + " " + pf.getUser().getIduser());
//					System.err.println("Submitted nuovo thread signal component" + Math.random());
//					System.err.println(this.exs.isShutdown());
					extractSignalComponentByPseudoFragment(pf);
//					AAFacadePersistence.getInstance().signalComponentSave(new SignalComponent(new Concept(), pf.getUser(), pf.getPeriod()));
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//			try {
//				exs.awaitTermination(10, TimeUnit.DAYS);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public void extractSignalComponentByPseudoFragment (PseudoFragment pf) {

		Map<Concept, List<Message>> concept2Messages = null;
		try {
			concept2Messages = concept2Messages(pf);
		} catch (ExtractorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<Message, Integer> message2sentiment = message2sentiment(pf);

		SignalComponent sigComp;

		double s, v, o, svo;
		int p, neg, neu;

		for (Concept c : concept2Messages.keySet()) {
			System.out.println("Analyzing concept: " + pf.getPeriod().getIdPeriodo() + " " + pf.getUser().getIduser() + c.getNameConcept());
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
			try {
				System.out.println("Saving concept: " + pf.getPeriod().getIdPeriodo() + " " + pf.getUser().getIduser() + c.getNameConcept());
				AAFacadePersistence.getInstance().signalComponentSave(sigComp);
				System.out.println("Saved concept: " + pf.getPeriod().getIdPeriodo() + " " + pf.getUser().getIduser() + c.getNameConcept());
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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




