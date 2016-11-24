package org.rm3umf.framework.eval.similarity;

import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.eval.SimilarityFunction;
import org.rm3umf.framework.eval.UserModelWithScore;

public class JaccardNoWeight extends SimilarityFunction{

	private static final Logger logger=Logger.getLogger(JaccardNoWeight.class);


	private String namefunction;

	public JaccardNoWeight() {
		this.namefunction="JACCARD_NOWEIGHT";
	}

	public  double getSimilarity(UserModel u1 , UserModel u2) {
		logger.debug("Calcolo similarit� tra:"+u1+"-"+u2);

		//lista segnali 1
		List<Signal> list1=u1.getSignals();

		//lista segnali 2
		List<Signal> list2=u2.getSignals();


		Collections.sort(list1);
		Collections.sort(list2);

		boolean continua=true;
		int i=0;//indice list1
		int j=0;//indice list2
		//lunghezza delle due liste
		int size1=list1.size();
		int size2=list2.size();

		
		//mi serve sapere il numero di segnali per decidere 
		int numeroSegnaliInComune=0;

		while(continua && size1!=0 && size2!=0 ){
			Signal signal1=list1.get(i);
			Signal signal2=list2.get(j);
			//se sono uguali calcolo fase e modulo
			if(signal1.getConcept().getId().equals(signal2.getConcept().getId())){
				numeroSegnaliInComune++;
				//esegui le trasformate	
				//aggiorno gli indici
				i++;
				j++;

			}
			//mando avanti quello che ha il concept che viene prima nell'ordine alfanumerico
			else{
				//compareTo � come un meno 
				if(signal1.compareTo(signal2)<0 ){

					//cioe se signal2 � pi� grande di signal1
					i++; //mando avanti lista1
				}else{
					//signal2 � >= di signal1 mando avanti signal2
					j++;
				}
			}
			//se ho superato la lunghezza di una delle due liste
			if(i==size1 || j==size2){
				continua=false;
			}
		}		
		//calcolo indice di jaccard
		int numeroConceptTot=size1+size2-(numeroSegnaliInComune);		
		double jaccard=numeroSegnaliInComune/(double)numeroConceptTot;
		//calcolo lo score tra u1 e u2
		logger.debug("similarity:"+jaccard+" concept in comune:"+numeroSegnaliInComune+"/"+numeroConceptTot);
		return jaccard;
	}

	public String getNameFunction(){
		return this.namefunction;
	}

	public int compare(UserModelWithScore o1, UserModelWithScore o2) {
		return Double.compare(o2.getScore(), o1.getScore());
	}



}