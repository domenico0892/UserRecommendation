package org.rm3umf.framework.eval;

import java.util.LinkedList;
import java.util.List;
import twitter4j.internal.logging.Logger;

/**
 * CLASSE INUTILE..
 * 
 * Applica più funzioni di similarità
 * @author giulz
 *
 */
public class ValutationEngine {
	
	private static Logger logger = Logger.getLogger(ValutationEngine.class); 
	
	private List<ValutationFunction> funzioniDiValutazione; //la lista di funzioni di valutazione
	
	
	public ValutationEngine(){
		this.funzioniDiValutazione=new LinkedList<ValutationFunction>();
	}
	
	/**
	 * Aggiungi una funzione di valutazione 
	 * @param valFun - funzione di valutazione
	 */
	public void addResultAnalyzer(ValutationFunction valFun){
		logger.info("aggiunta funzione di val : "+valFun);
		this.funzioniDiValutazione.add(valFun);
	}
	
	/**
	 * Valuta il risultato result (risultato dell'applicazione di una funzione di similarità 
	 * tra utenti) utilizzando le diverse funzioni di valutazione implemente
	 * @param result - risultato dell'applicazione di una fun di sim tra utenti ad un UM
	 */
	public void valutate(Result result){
		logger.info("inizia a valutare il risultato ");
		for(ValutationFunction funVal:this.funzioniDiValutazione){
			//valuta il risultato
			double valutation = funVal.valutate(result);
			logger.info(funVal+" = "+valutation);
		}	
	}
	
}
