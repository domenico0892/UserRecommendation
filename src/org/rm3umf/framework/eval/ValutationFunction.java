package org.rm3umf.framework.eval;

/**
 * Rappresenta una funzione di valutazione, cioè un oggetto che prende un oggetto Result in input e lo 
 * valuta restituiendo uno score che indica la qualità della funzione di valutazione
 * 
 * @author giulz
 */

public interface ValutationFunction {
	
	/**
	 * Dato il risulta result dell'applicazione della funzione di valutazione valuta la 
	 * correttezza del risulto utilizzando qualche genere di euristica 
	 * */
	public double valutate(Result result);
	
	/**
	 * Restituisce il tipo di funzione di valutazione
	 **/
	public String getNameFunction();

}
