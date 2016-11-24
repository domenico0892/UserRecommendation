package org.rm3umf.framework.eval;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.User;
import org.rm3umf.domain.UserModel;
import org.rm3umf.lucene.FacadeLucene;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;

import util.UtilText;

/**
 * Crea il Resualt a partire da una implementazione di basata su Lucene della funzione s1
 * che si trova sull'articolo Twittomender 
 * 
 * S@10 = 0.16211878009630817 (202) 
 * MKT  = 0.12358201086533352 (156)
 * @author giulz
 *
 */
public class TwittomenderResultCreator {
	
	
	private int n;
	
	public TwittomenderResultCreator(int n){
		FacadeLucene.getInstance().prepareSearching();
		this.n=n;
	}
	
	
	/**
	 * Applica la funzione di similarità ai modelli utenti e restituisce un oggetto che rappresenta
	 * il risultato dell'applicazione di tale funzione
	 */
	public Result applyS1Function(List<UserModel> userModels,int limit){
		
		if(limit<0){
			limit=userModels.size();
		}
		
		//Creo il risultato
		Result result = new Result();
	    
		//setto il nome della funzione da utilizzare 
		result.setFunctionSimilarity("S1 Twittomender"); 
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
		//Collections.sort(userModels,new UserModelComparator());
		
		/*Per ogni utente calcolo lo score con tutti gli altri utenti e li ordino in base a questo score*/
		int iteration=0;
		for(int i=0 ; i<limit && i<userModels.size(); i++){
			UserModel umCorr = userModels.get(i);
			iteration++;
			System.out.println("ITERATION "+iteration+":"+umCorr);
			List<Long> bestUsers=FacadeLucene.getInstance().retriveByPseudodocument(getPseudoDocument(umCorr.getUser()));
			//mostro tutti gli utenti
//			for(Long id: bestUsers)
//				System.out.println("    "+id);
			long useridCorr=umCorr.getUser().getIduser();
			//salvo il risultato 
			if(bestUsers.size()>n){
				bestUsers=bestUsers.subList(0,this.n);
			}
			result.addListBestUser(useridCorr, bestUsers);
		}
	    
		//setto il tempo impiegato in millisecondi per l'applicazione dell'algoritmo
		GregorianCalendar gcEnd = new GregorianCalendar();
		long timeEnd=gcEnd.getTimeInMillis();
		long duration = timeEnd-timeStart;
		result.setDuration(duration);
		
		return result;
		
	}
	
	
//	private String getPseudoDocument(User user){
//		List<Message> listMessage=null;
//		try{
//			listMessage=AAFacadePersistence.getInstance().messageRetriveByUser(user);
//		}catch(PersistenceException pe){
//			throw new RuntimeException("errore durante la creazione dello pseudo-document");
//		}
//		String pseudoDocument = "";
//		for(Message m:listMessage){
//			pseudoDocument=pseudoDocument+" \n"+m.getText();
//		}
//		
//		//System.out.println(pseudoDocument);
//		return pseudoDocument;
//		
//	}
	
	
	private String getPseudoDocument(User user){
		String pseudodocument="";
			try {
				pseudodocument=FacadeLucene.getInstance().getPseudodocument(user.getIduser());
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		//trasformo tutto in lowercase
		//pseudodocument=pseudodocument.toLowerCase();
			
		//per diminuire il numero dei query term elimino le stopwords
		pseudodocument=UtilText.getInstance().removeStopWord(pseudodocument);	

		if(pseudodocument.length()>1000000)
			pseudodocument=pseudodocument.substring(0,1000000);
		
		//System.out.println(pseudodocument);
		return pseudodocument;
		
	}

}
