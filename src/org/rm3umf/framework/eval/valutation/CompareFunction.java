package org.rm3umf.framework.eval.valutation;

import java.util.List;

/**
 * Interfaccia che astrae tutte le funzioni
 * @author giulz
 *
 */
public interface CompareFunction {
	
	/**
	 * Restituisce gli n best users dell'utente identificato da userid, utilizzando una certa euristica 
	 */
	public List<Long> bestUsers(long userid , int n); 
	
	/**
	 * Restituisce il nome della funzione
	 */
	public String getName();

}
