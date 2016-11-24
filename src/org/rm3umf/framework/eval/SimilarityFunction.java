package org.rm3umf.framework.eval;

import java.util.Comparator;

import org.rm3umf.domain.UserModel;

/**
 * La classe astratta da estendere per realizzare una funzione di similarità basato sul modello 
 * Bag of Signal.
 * 
 * @author giulz
 */

public abstract  class SimilarityFunction implements Comparator<UserModelWithScore>{
	
	
    /**
     * Metodo che dati i profili utente secondo la rappresentazione Bag of Signal calcola uno score
     * che indica la similarità tra i due profili.
     * 
     * ATTENZIONE: in realtà potrebbe essere anche una distanza.
     * @param u1
     * @param u2
     * @return 
     */
	public abstract double getSimilarity(UserModel u1,UserModel u2);
	
	/**
	 * Funzione che restituisce il nome della funzione
	 * @return nameFunction 
	 */
	public abstract String getNameFunction();
	
	/** 
	 * Questo metodo mi permette di definire funzioni si similarità che funzioni di distanza
	 */
	public abstract int compare(UserModelWithScore o1, UserModelWithScore o2) ;
	

	
	
	
	
}
