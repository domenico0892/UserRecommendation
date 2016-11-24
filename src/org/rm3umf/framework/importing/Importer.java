package org.rm3umf.framework.importing;



import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rm3umf.domain.*;
import org.rm3umf.lucene.FacadeLucene;
//import org.rm3umf.net.downloader.QueueException;
//import org.rm3umf.net.twitter.FacadeTwitter4j;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;


import twitter4j.TwitterException;

/**
 * Questa classe gestisce l'importing dei dati all'interno del DB. Questa parte del framework
 * va fatta una sola volta infatti sono i passi successivi che possono cambiare per la 
 * valutazione.
 * 
 * @author Giulz
 *
 */


public class Importer {
	
	private static final Logger logger = Logger.getLogger(Importer.class);
	
	//private ResourcExtractor resourcer;
	private SocialEnricherFromDB modelEnricher;
	
	
	
	public Importer(){
		//creo il resourcer
		//this.resourcer = new ResourcExtractor();
		//lo faccio partire
		//resourcer.start();
		
//		this.modelEnricher=new SocialEnricher();
		this.modelEnricher = new SocialEnricherFromDB();
		
	
		
	}

	
	
	
	public void start() throws DatasetException, PersistenceException, IOException{

		
		//inizializzo il DB
		//potrei mettere tutte queste operazioni in una classe prepareDB che cancella tutto
		//se � necessario ed eventualmente crea le tabelle
		//AAFacadePersistence.getInstance().prepareDBImporting();
		
		
		//FILTRO IMPORTING
//		DatasetAdapter dataset =new DatasetRM3();
//		DatasetAdapter dataset =new DatasetUmap();
		DatasetAdapter dataset = new NuovoDataset();
		List<User> utentiDataset=dataset.getUser();

		
		
		int sizeUsers=utentiDataset.size();
		int iterazione=0;
		//Scorro tutti gli utenti
		for(User user:utentiDataset){
			iterazione++;
			System.err.println("Siamo all'utente :"+user+"["+iterazione +"su" +sizeUsers+"]");
			//serve per evitare di fare richieste inutili a twitter	
			
			boolean isPublicProfine=false;
			Set<Long> listaFollower=null;
			Set<Long> listaFollowed=null;
			//Set<Long> rilevantFollowers=null;
			
			
			//Recupero i follower e followed dell'utente corrente	
			listaFollower=modelEnricher.getFollower(user.getIduser());
			listaFollowed=modelEnricher.getFollowed(user.getIduser());
				
			
			//se l'utente non ha il profilo pubblico si va avanti listaFollowed e listaFollower saranno null
			if(listaFollowed!=null && listaFollower!=null){
				List<Message> listaMessaggi=null;

				//salvo l'utente con tutte le sue relazioni
			 	AAFacadePersistence.getInstance().userSave(user);
			 	
				//salvo followed e followers dell'utente user
			 	AAFacadePersistence.getInstance().userSaveFollowed(user, listaFollowed);
				AAFacadePersistence.getInstance().userSaveFollower(user, listaFollower);
		
				
				//recupero tutti i messaggi per lo user corrente dal dataset
				listaMessaggi=dataset.getMessagesByUser(user);

				/*Scandisco tutti i messaggi recuperati per l'utente*/
				for(Message message:listaMessaggi){
						//salvo il messaggio con le risorse che sono state trovate
						AAFacadePersistence.getInstance().messageSave(message);
						logger.info("salvato messaggio:"+message);				
				}
				
				//estraggo e salvo le risorse e le salvo
				//resourcer.addResource(listaMessaggi);

			}else{
				logger.info("Non posso recuperare following/followes per l'utente "+user+" ");
			}



		}
		
	}
	
	
	
}