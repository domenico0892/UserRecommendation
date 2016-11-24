package org.rm3umf.eval.clusterAlgorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.rm3umf.domain.*;
import org.rm3umf.framework.eval.similarityFunction.Similarity;






public class ClusterAlgorithm2 {

	
	private static final Logger logger=Logger.getLogger("ClusterAlgoritm");
	private int numberOfCluster;
	private Similarity similarity;
	
	public ClusterAlgorithm2(int numberOfCluster,Similarity similarity){
		this.numberOfCluster=numberOfCluster;
		this.similarity=similarity;
	}
	
	
	public List<List<UserModel>> start(List<UserModel> users){
		//Lista mediods
		UserModel[] mediods=new UserModel[this.numberOfCluster];
		
		//creo la lista pi� esterna 
		List<List<UserModel>> insiemeClusters=new ArrayList<List<UserModel>>(); //contiene i cluster
		//inserisco n liste all'interno 
		
		
		/**
		 * Inserisco un utente selezionato in modo casuale per in ogni cluster
		 */
		//serve per la selezione di un utente casuale
		Random random=new Random();
		for(int i=0;i<numberOfCluster;i++){
			List<UserModel> cluster=new LinkedList<UserModel>();
			insiemeClusters.add(cluster);
			//scelgo un utente a caso tra quelli in users e lo metto nella lista 
			int userIndex=random.nextInt(users.size());
			UserModel ur=users.get(userIndex);
			logger.info("scelto utente "+ur.getUser().getIduser()+" centroide del cluster "+i);
			//aggiorna l'array dei mediods
			mediods[i]=ur;
			//lo inserisco nel cluster
			insiemeClusters.get(i).add(ur);
			//rimuovo l'utente dalla lista users
			users.remove(userIndex);
		}
		//ho un utente in ogni cluster adesso devo mettere gli altri nei cluster
		//utilizzo la convenzione che il primo elemento nella lista � l'elemento mediodts del cluster 
		for(UserModel ur: users ){
			//List<UserModel> clusterCorrent=null;
			double similarityMax=0;
			//verifico qual'� il cluster con cui ha la similarit� massima
			int bestCluster=-1;
			for(int i=0; i< insiemeClusters.size();i++ ){
				//il mediots
				UserModel mediots=mediods[i];
				double similarityCorr=similarity.getSimilarity(ur, mediots);
				if(bestCluster==-1 || similarityCorr>= similarityMax ){
					similarityMax=similarityCorr;
					//Salvo il bestCluster
					bestCluster=i;
					
				}//fine if
				
			}//fine for sui cluster
			//salvo l'utente nel cluster con cui ha similarit� pi� alta
			logger.info("inserisco utente u " +ur.getUser().getIduser()+" nel cluster "+bestCluster);
			insiemeClusters.get(bestCluster).add(ur);
		}//fine for sugli ur
		
		/*
		 * Non ci sta pi� nulla nella lista users e i cluster sono tutti pieni
		 *comincia il vero e proprio algoritmo
		 */
		boolean continua=true; //diventer� false quando non si avr� un meglioramento
		while(continua){
			/**
			 * ALGORITMO
			 * 1)devo verificare se il mediots corrente per il cluster � quello che minimizza il costo_totale
			 *->se si STOP
			 *2)Cambio i mediots che minimizzano i costi totali i costi_totali 
			 *3)Confronto tutte le altre istanze con i nuovi mediots e creo i cluster ->torna ad 1)
			 * 
			 */
			
			/*
			 *ASSEGNO LE ISTANZE AL CLUSTER DEL MEDIODI PIU' VICINO  
			 */
			
			//ogni utente in ogni cluster deve essere confrontato con tutti i mediots degli altri cluster
			for(int indClusterCorr=0 ; indClusterCorr< insiemeClusters.size();indClusterCorr++){
				//mem cluster corrente
				List<UserModel> cluster=insiemeClusters.get(indClusterCorr);
				//scandisco le istanze del cluster i-esimo
				List<UserModel> listRemove=new LinkedList<UserModel>();
 				for(UserModel ur:cluster){
 					//verifico qual'� il mediods a cui si avvicina meglio
 					int indiceBestCluster=indClusterCorr;
 					//memorizzo la similarit� max
 					double bestSimilarity=0.0;
 					
					for(int i=0;i<mediods.length;i++){
						double simCorrente=this.similarity.getSimilarity(ur, mediods[i]);
						//XXX ATTENZIONE qui dipende se devo minimizzare la funzione o la devo massimizzare
						if(bestSimilarity<simCorrente){
							indiceBestCluster=i; 
							bestSimilarity=simCorrente;
						}
					}
					//effettuo la swap se necessario
					if(indClusterCorr != indiceBestCluster){
						insiemeClusters.get(indiceBestCluster).add(ur);
						listRemove.add(ur);			
					}
				}
 				//rimuvi gli elementi dal cluster
 				for(UserModel userToRemove:listRemove){
 					cluster.remove(userToRemove);
			}	
		}
		/*
		 * RICALCOLARE I MEDIOIDI
		 */
			
		continua=false;
		
			
		}
		
		showCluster(insiemeClusters, mediods);
		
		
		
		
		return null;
		
	}
	
	
	/**
	 * Dato un cluster calcola il mediods che massimizza il tatal error 
	 * r
	 */
	
	
	
	/**
	 * Calcola l'errore totale dato un cluster e il suo mediod
	 * @param cluster
	 * @param mediod
	 * @return
	 */
	private double calculateTotalError(List<UserModel> cluster,UserModel mediod){
		double totalError=0.0;
		
		//calcolo l'errore totale
		for(UserModel userCorr:cluster){
			totalError+=similarity.getSimilarity(userCorr, mediod);			
		}
		return totalError;
		
	}
	
	public void showCluster(List<List<UserModel>> insiemeCluster,UserModel[] mediods){
		for(int i=0;i<mediods.length;i++){
			UserModel mediod=mediods[i];
			List<UserModel> cluster=insiemeCluster.get(i);
			System.out.println("CLUSTER "+i+" mediod="+mediod);
			for(UserModel instance:cluster){
				System.out.println("          "+instance);
			}
		}
	}

	
	
}
