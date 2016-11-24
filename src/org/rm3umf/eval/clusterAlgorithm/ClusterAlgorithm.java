package org.rm3umf.eval.clusterAlgorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.rm3umf.domain.*;
import org.rm3umf.framework.eval.similarityFunction.Similarity;






public class ClusterAlgorithm {

	
	private static final Logger logger=Logger.getLogger("ClusterAlgoritm");
	private int numberOfCluster;
	private Similarity similarity;
	
	public ClusterAlgorithm(int numberOfCluster,Similarity similarity){
		this.numberOfCluster=numberOfCluster;
		this.similarity=similarity;
	}
	/**
	 * A questo metodo devo passare a lista di rapprentazioni utente users con almeno un segnale . Infatti se 
	 * una rappresentazione utente ha zero segnali non ha senzo che sia considerato perch� avr� similarit�
	 * uguale a zero con tutte le altre rappresentazioni utente.
	 * @param users una lista di User Model
	 * @return clusters gli UserModel di input divisi in cluster secondo la funzione di similarit� 
	 */
	
	public List<List<UserModel>> start(List<UserModel> users){
		//creo la lista pi� esterna 
		List<Cluster> insiemeClusters=new ArrayList<Cluster>(); //contiene i cluster
		//inserisco n liste all'interno 
		/**
		 * Inserisco un utente selezionato in modo casuale per in ogni cluster
		 */
		//serve per la selezione di un utente casuale
		Random random=new Random();
		for(int i=0;i<numberOfCluster;i++){
			//scelgo un utente a caso tra quelli in users e lo metto nella lista 
			int userIndex=random.nextInt(users.size());
			UserModel mediod=users.get(userIndex);
			Cluster cluster=new Cluster(i,mediod,similarity);
			cluster.addElement(mediod);
			insiemeClusters.add(cluster);
			//aggiorna l'array dei mediods
			users.remove(userIndex);
		}
		
		for(UserModel ur: users ){
			double similarityMax=0;
			//verifico qual'� il cluster con cui ha la similarit� massima
			Cluster bestCluster=null;
			for(Cluster cluster :insiemeClusters){
				double similarityCorr=cluster.getSimilarity(ur);
				if(bestCluster==null || similarityCorr>= similarityMax ){
					similarityMax=similarityCorr;
					//Salvo il bestCluster
					bestCluster=cluster;
				}
			}//fine for sui cluster
			//salvo l'utente nel cluster con cui ha similarit� pi� alta
			logger.info("inserisco utente u " +ur.getUser().getIduser()+" nel cluster "+bestCluster);
			bestCluster.addElement(ur);
		}//fine for sugli ur

		/*
		 * Non ci sta pi� nulla nella lista users e i cluster sono tutti pieni
		 *comincia il vero e proprio algoritmo
		 */
		boolean continua=true; //diventer� false quando non si avr� un meglioramento
		int numIterazione=0;
		while(continua){
			numIterazione++;
			System.out.println("ITERAZIONE:"+numIterazione);
			 /*
			 *ASSEGNO LE ISTANZE AL CLUSTER DEL MEDIODI PIU' VICINO  
			 */
			
			//inserisco tutti gli utenti dentro una lista
			//ogni utente in ogni cluster deve essere confrontato con tutti i mediots degli altri cluster
			List<UserModel> allUser=new LinkedList<UserModel>();
			for(Cluster cluster : insiemeClusters){
				allUser.addAll(cluster.getElements());
				cluster.getElements().clear();
			}
			//qui potre togliere da allUser i mediods e assegnare un nuovo mediods se il vecchio � un mediods nullo 
			
			for(UserModel userCorr : allUser ){
				double bestSimilarity=0.0;
				Cluster bestCluster=null;
				for(Cluster cluster:insiemeClusters){
					double simCorrente=cluster.getSimilarity(userCorr);
					//XXX ATTENZIONE qui dipende se devo minimizzare la funzione o la devo massimizzare
					if(bestSimilarity<=simCorrente){
						bestCluster=cluster; 
						bestSimilarity=simCorrente;
						}
					
					}
					//aggiungo l'elemento al cluster migliore
				bestCluster.addElement(userCorr);
				}	
			/*
			 * RICALCOLARE I MEDIOIDI
			 */
			continua=false;
			for(Cluster cluster:insiemeClusters){
				UserModel oldMedoid=cluster.getMedoid();
				UserModel newMedoid=cluster.calculateNewMedoids();
				if(!newMedoid.equals(oldMedoid))
					continua=true;
				}
			
		}
		
		/*
		 * Questa parte serve solo per mostrare i risultato dell'algoritmo
		 */
		System.out.println("NUM iterazione:"+numIterazione);
		for(Cluster c:insiemeClusters){
			UserModel mediod=c.getMedoid();
			System.out.println(c);
			List<UserModel> utenti=c.getElements();
			for(UserModel um: utenti){
				System.out.println("         "+um+" similarity with mediod:"+similarity.getSimilarity(um, mediod)+"|"+similarity.getSimilarity(um, mediod));
			}
		}
//		for(Cluster c1:insiemeClusters){
//			List<UserModel> userModel=c1.getElements();
//			for(Cluster c2 :insiemeClusters){
//				List<UserModel> userModel2=c2.getElements();
//				System.out.println(c1+"--"+c2);
//				for(UserModel um: userModel){
//					for(UserModel um2:userModel2){
//						if(um.getUser().getIduser()!=um2.getUser().getIduser())
//							System.out.println(um.getUser().getIduser()+"-"+um2.getUser().getIduser()+"-> sim:"+similarity.getSimilarity(um, um2));
//					}
//				}
//			}
//		}
		
		return null;

	}


	
	private void showCluster(List<List<UserModel>> insiemeCluster,int[] mediods){
		for(int i=0;i<mediods.length;i++){
			
			List<UserModel> cluster=insiemeCluster.get(i);
			
			System.out.println("CLUSTER "+i+" indiceMediods:"+mediods[i]);
			
			for(UserModel instance:cluster){
				
				System.out.println("          "+instance);
			}
		}
	}

	
	
}
