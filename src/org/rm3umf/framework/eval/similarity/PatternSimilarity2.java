package org.rm3umf.framework.eval.similarity;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.eval.SimilarityFunction;
import org.rm3umf.framework.eval.UserModelWithScore;
import org.rm3umf.math.VectorUtil;
import org.rm3umf.math.WaveletUtil;

public class PatternSimilarity2 extends SimilarityFunction{
	
	private static Logger logger = Logger.getLogger(PatternSimilarity.class);
	private String namefunction;

		
	public PatternSimilarity2() {
		this.namefunction="PATTERN_SIMILARITY2";
	}
	
	
	public  double getSimilarity(UserModel u1 , UserModel u2) {
		logger.debug("Calcolo similarità tra:"+u1+"-"+u2);

		//lista segnali 1
		List<Signal> list1=u1.getSignals();

		//lista segnali 2
		List<Signal> list2=u2.getSignals();


		int conceptCommon=0;
		Map<String,double[][]> concept2singnal =  new HashMap<String,double[][]>();

		//Scorro la prima lista
		for(Signal signal:list1){
			String conceptid = signal.getConcept().getId();
			double[] array=signal.getSignal();
			double[] array2=new double[array.length];
			double[][] signals ={array,null};
			concept2singnal.put(conceptid, signals );
		}

		//Segnali della seconda lista
		for(Signal signal:list2){
			String conceptid=signal.getConcept().getId();
			double[] arraySignal=signal.getSignal();
			double[][] signals = concept2singnal.get(conceptid);
			if(signals==null){		
				double[] array2=new double[arraySignal.length];
				signals = new double[2][];
				signals[0]=null;
				concept2singnal.put(conceptid, signals);
			}else{
				conceptCommon++;
			}
			signals[1]=arraySignal;
		}

		int conceptTot = concept2singnal.keySet().size();	

		//ci sarà il totale
		double tot = 0.0;
		double globalEnergy=0.0;

		//Scorro la mappa con tutti i segnali di u1 e u2
		for( String conceptid : concept2singnal.keySet() ){

			double[][] signals = concept2singnal.get(conceptid);

			//Se entrabi i profili utente posseggono i segnale relativo a conceptid 
			if(signals[0]!=null && signals[1]!=null){

				double[] sign1 = WaveletUtil.getInstance().trasforma(signals[0]); //mem il sig1
				double[] sign2 = WaveletUtil.getInstance().trasforma(signals[1]); //mem il sig2

				/*Scorro i due segnal calcoladomi energia */

				for(int i=0;i<sign1.length;i++){
					//componenti dei due segnali trasformati
					double comp1=sign1[i];
					double comp2=sign2[i];
					double phaseSimilarity=samePhase(comp1,comp2);
				//	System.out.println(comp1+" "+comp2 +"->"+phaseSimilarity);
					double energy1=comp1*comp1;
					double energy2=comp2*comp2;
					
					//in pratica se stanno in opposizione di fase sottrarro le due componenti 
					//mentre se stanno in fase le sommo
					double compEnergy=Math.abs((energy1+phaseSimilarity*energy2)); 
					
					tot+=compEnergy;
					globalEnergy+=(energy1+energy2);
				}			
			}else{
				//se una delle due è null 
				if(signals[0]!=null){
					double energy=VectorUtil.getInstance().energy(signals[0]);
					globalEnergy+=energy;
				}
				if(signals[1]!=null){
					double energy=VectorUtil.getInstance().energy(signals[1]);
					globalEnergy+=energy;

				}
			}
		}

		double similarity=tot/globalEnergy;
		return    similarity;
	}
	
	
	/**
	 * Compara la fase delle due componenti comp1 e comp2 restituendo 1 hanno la stessa fase e 0 altrimenti.
	 * @param comp1
	 * @param comp2
	 * @return res 
	 */
	public double samePhase(double comp1,double comp2){
		double res=0.;
		if((comp1>0 && comp2>0)||(comp1<0 && comp2<0)){
			res=1;
		}
		return res;
	}
	
	/**
	 * Restituisce il nome della funzione
	 * @return nome della funzone
	 */
	public String getNameFunction(){
		return this.namefunction;
	}
	
	
	public int compare(UserModelWithScore o1, UserModelWithScore o2) {
		return Double.compare(o2.getScore(), o1.getScore());
	}
	
	
}

