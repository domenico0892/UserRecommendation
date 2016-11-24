package org.rm3umf.framework.eval;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.rm3umf.domain.UserModel;

import twitter4j.internal.logging.Logger;
/**
 * Applica la funzione di similarità ai modelli utente  e produce il result,  che rappresenta il risultato 
 * dell'applicazione della funzione di similarità scelta
 * 
 * @author giulz
 */
public class ApplicatorFunction {

	private static Logger logger = Logger.getLogger(ApplicatorFunction.class);
	
	//Parameter
	private  int n ; //num di utenti da salvare nel result
	
	private SimilarityFunction similarityFunction;  
//	private List<UserModelWithScore> userModels;
	
	//Per valutare le performance
	private double duration;
	
	
	
	/**
	 * Costruttore che crea un applicator che salva solo i primi n in result
	 */
	public ApplicatorFunction(int n){
		this.n=n;
		
	}
	
	/**
	 * Costruttore che crea un applicator che salva per ogni user il ranking completo in result 
	 */
	public ApplicatorFunction(){
		this.n=-1;
		
	}
	
	/**
	 * Setta la funzione di similarità 
	 */
	public void setSimilarityFunction(SimilarityFunction similarityFunction) {
		this.similarityFunction=similarityFunction;
	}
	
	/**
	 * Applica la funzione di similarità ai modelli utenti e restituisce un oggetto che rappresenta
	 * il risultato dell'applicazione di tale funzione
	 */
	public Result apply(List<UserModel> userModels){
		
		//Creo il risultato
		Result result = new Result();
	    
		//setto il nome della funzione da utilizzare 
		result.setFunctionSimilarity(this.similarityFunction.getNameFunction()); 
		result.setN(n);
		
		//Prendo il tempo di inizio in millisec 
		GregorianCalendar gcStart = new GregorianCalendar();
		long timeStart=gcStart.getTimeInMillis();
		
		//Data corrente
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MM-HH_mm");
		String date=sdf.format(gc.getTime());
		result.setDate(date);
		
		//Ordino la lista
		Collections.sort(userModels,new UserModelComparator());
		
		/*Per ogni utente calcolo lo score con tutti gli altri utenti e li ordino in base a questo score*/
		int iteration=0;
		for(UserModel umCorr:userModels){
			//aggiorno l'iterazione e la visualizzo
			iteration++;
			System.out.println("ITERATION "+iteration);
			
			List<Long> bestUsers=getBestUser(umCorr,userModels);
			long useridCorr=umCorr.getUser().getIduser();
			//salvo il risultato 
			result.addListBestUser(useridCorr, bestUsers);
		}
	    
		//setto il tempo impiegato in millisecondi per l'applicazione dell'algoritmo
		GregorianCalendar gcEnd = new GregorianCalendar();
		long timeEnd=gcEnd.getTimeInMillis();
		long duration = timeEnd-timeStart;
		result.setDuration(duration);
		
		return result;
		
	}
	
	/**
	 * Applica la funzione di similarità ai primi n modelli utenti della lista e restituisce un oggetto 
	 * restituendo un result parziale relativo ai primi n utenti.
	 * 
	 * @param userModels
	 * @param limit - i primi n utenti da considerare  
	 */
	public Result apply(List<UserModel> userModels,int limit){
		
		//Creo il risultato
		Result result = new Result();
	    
		//setto il nome della funzione da utilizzare 
		result.setFunctionSimilarity(this.similarityFunction.getNameFunction()); 
		result.setN(n);
		
		//Prendo il tempo di inizio in millisec 
		GregorianCalendar gcStart = new GregorianCalendar();
		long timeStart=gcStart.getTimeInMillis();
		
		//Data corrente
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MM-HH_mm");
		String date=sdf.format(gc.getTime());
		result.setDate(date);
		
		
		//Ordino la lista 
		Collections.sort(userModels,new UserModelComparator());
		
		/*Per ogni utente calcolo lo score con tutti gli altri utenti e li ordino in base a questo score*/
		int iteration=0;
		for(int i=0 ; i<limit && i<userModels.size(); i++){
			UserModel umCorr = userModels.get(i);
			//aggiorno l'iterazione e la visualizzo
			iteration++;
			System.out.println("ITERATION "+iteration);
			
			List<Long> bestUsers=getBestUser(umCorr,userModels);
			long useridCorr=umCorr.getUser().getIduser();
			//salvo il risultato 
			result.addListBestUser(useridCorr, bestUsers);
		}
	    
		//setto il tempo impiegato in millisecondi per l'applicazione dell'algoritmo
		GregorianCalendar gcEnd = new GregorianCalendar();
		long timeEnd=gcEnd.getTimeInMillis();
		long duration = timeEnd-timeStart;
		result.setDuration(duration);
		
		return result;
		
	}

	
	/**
	 * Questo metodo applica la funzione di similarità e restituisce i ranking dei primi N utenti
	 * più simili all'utente corrente
	 * @param umCorr
	 * @param listUserModel
	 * @return listLong - la lista dei userid degli utenti piu simili a umCorr 
	 */
	private List<Long> getBestUser(UserModel umCorr,List<UserModel> listUserModel){
		System.out.println("Recupero i "+this.n+" user per "+umCorr);
		double scoreSimilarity=-1;
		List<UserModelWithScore> listUserModelWithScore=new LinkedList<UserModelWithScore>();
		
		for(UserModel um:listUserModel){
			//calcolo  la similarità
			scoreSimilarity=this.similarityFunction.getSimilarity(umCorr, um);
			if(!umCorr.equals(um)){
				UserModelWithScore usScored=new UserModelWithScore(um);
				usScored.setScore(scoreSimilarity);
				listUserModelWithScore.add(usScored); 
			}
		}
		//ordino la lista in base allo score
		Collections.sort(listUserModelWithScore,this.similarityFunction);
		
		//Se n=-1 allora restituisco tutta la lista 
		int indexLastElement=this.n;
		if(this.n==-1){
			indexLastElement=listUserModel.size();
		}
		
		//Metto gli N con ranking più alto in una lista di Long
		List<Long> bestUserModel=new LinkedList<Long>();
		for(UserModelWithScore um : listUserModelWithScore.subList(0,indexLastElement)){
			System.out.println(um +" - "+um.getScore());
			long userid=um.getUser().getIduser();
			bestUserModel.add(userid); 
		}
		return bestUserModel;

	}



	 
	
}