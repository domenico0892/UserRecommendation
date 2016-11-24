package org.rm3umf.framework.eval;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import twitter4j.internal.logging.Logger;

/**
 * Questa classe rappresenta il risultato dell'applicazione della funzione di similarità 
 * da testare. Sarà l'insieme di unteti con il rispettivo ranking
 * 
 *  (u1, RankingU1)
 *  (u2, RankingU2) 
 *       ..
 *       
 * Dove il ranking non è altro che l'insieme di utenti che ottengono un score più alto 
 * applicando la funzione di similarità 
 * 
 * @author giulz
 *
 */
public class Result {

	private static Logger logger = Logger.getLogger(Result.class);

	private  Map<Long,List<Long>> result; //rappresenta il risultato da valutare 
	private String functionSimilarity;   //nome della funzione che ha prodotto tale risultato
	private int n; //numero di user considerati
	
	private String date; //data di applicazione dell'algoritmo
	
	//per misurare le performance in termini di tempo dell'algoritmo
	private  long duration;  //quanto ci ha messo l'algoritmo a produrre il risultato
	
	public Result(String functionSimilarity){
		this.result=new HashMap<Long,List<Long>>();
		this.duration=-1;
		this.functionSimilarity=functionSimilarity;
	}

	/**
	 * Serve quando i risultato viene letto in un file xml
	 */
	public Result(){
		this.result=new TreeMap<Long,List<Long>>();
	}
	
	/**
	 * Aggiunge un singolo utente alla lista degli uteti più simili all'utente con id userid
	 * @param userid 
	 * @param userBest
	 */
	public void addBestUser(long userid,long userBest){
		List<Long> listBest = this.result.get(userid);
		if(listBest==null){
			listBest= new LinkedList<Long>();
			this.result.put(userid,listBest);
		}
		//aggiungo lo userBest in ultima posizione della lista
		listBest.add(userBest);
	}
	
	/**
	 * Aggiunge l'intera lista degli utenti più simili ad userid
	 * @param userid
	 * @param bestUsers
	 */
	public void addListBestUser(Long userid,List<Long> bestUsers){
		result.put(userid, bestUsers);
	}
	
	
	/**
	 * Restituisce la lista degli utenti più simili ad userid
	 * @param userid
	 * @return listBestUser
	 */
	public List<Long> getBestUsers(Long userid){
		return result.get(userid);
	}

	/**
	 * Restituisce tutti gli utenti presenti nel risultato
	 * @return setUsers - utenti presenti nel sistema
	 */
	public Set<Long> getUser(){
		return result.keySet();
	}
	
	/*
	 ****************
	 * Performance  *
	 ****************
	 */

	/**
	 * Setta la durata dell'algoritmo in millisec
	 */

	public void setDuration(long duration) {
		this.duration=duration;	
	}

	/**
	 * Restituisce la durata dell'algoritmo
	 */
	public long getDuration() {
		return this.duration;	
	}

	
	/**
	 * Restituisce il nome della funzione che è stata applicata per creare il result
	 * @return nameFunction
	 */
	public String getFunctionSimilarity(){
		return this.functionSimilarity;
	}
	
	/**
	 * Setta il nome della funzione che si sta utilizzando per creare il result
	 * @param functionSimilarity
	 */
	public void setFunctionSimilarity(String functionSimilarity){
		this.functionSimilarity=functionSimilarity;
	}
	
	/**
	 * Setta la data di creazione del result
	 * @param date
	 */
	public void setDate(String date){
		this.date=date;
	}
	
	/**
	 * Restituisce la data di creazione del result
	 * @return date 
	 */
	public String getDate(){
		return this.date;
	}
	
	
	
	public static void main(String[] args){
		//write();
	}
	
	/**
	 * Restituisce il numero di utentei che si salvano per ogni utente
	 */
	public int getN(){
		return this.n;
	}
	
	/**
	 * Setta n cioe il numero di utenti più simili che si salvano in result per ogni utente
	 */
	public void setN(int n){
		this.n=n;
	}

}
