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


public class TimeWeightSimilarity extends SimilarityFunction{

	private static Logger logger = Logger.getLogger(D1Distance.class);
	private String namefunction;

	private double T;
	
	public TimeWeightSimilarity(int T) {
		this.T = T;
		this.namefunction = "TimeWeightSimilarity";
	}
	
	
	
	public  double getSimilarity(UserModel u1 , UserModel u2) {
		logger.debug("Calcolo similarità tra:"+u1+"-"+u2);

		//logger.info("calcola similarit� tra utenti "+u1.getUser().getIduser()+"-"+u2.getUser().getIduser());

		//lista segnali 1
		List<Signal> list1=u1.getSignals();

		//lista segnali 2
		List<Signal> list2=u2.getSignals();

		Map<String,double[][]> concept2singnal =  new HashMap<String,double[][]>();

		//Scorro la prima lista
		for(Signal signal:list1){
			String conceptid = signal.getConcept().getId();
			double[] array=attenua(signal.getSignal());
			double[][] signals ={array,null};
			concept2singnal.put(conceptid, signals );
		}

		//Segnali della seconda lista
		for(Signal signal:list2){
			String conceptid=signal.getConcept().getId();
			double[] arraySignal=attenua(signal.getSignal());
			double[][] signals = concept2singnal.get(conceptid);
			if(signals==null){		
				signals = new double[2][];
				signals[0]=null;
				concept2singnal.put(conceptid, signals);
			}
			signals[1]=arraySignal;
		}
		
		//ci sarà il totale
		double tot = 0.0;

		double norm1=0;
		double norm2=0;
		
		for( String conceptid : concept2singnal.keySet() ){

			double[][] signals = concept2singnal.get(conceptid);
			
			//se il sengale in comune
			if(signals[0]!=null && signals[1]!=null){
//				double[] vect1 = WaveletUtil.getInstance().haarCoefficient(signals[0]); //mem il sig1
//				double[] vect2 = WaveletUtil.getInstance().haarCoefficient(signals[1]); //mem il sig2
				
//				
//				double[] fase1=VectorUtil.getInstance().sign(vect1) ;
//				double[] fase2=VectorUtil.getInstance().sign(vect2) ;
				
			//	double simVector = VectorUtil.getInstance().cosineSimilarity(vect1, vect2) ;
				
				/*
				 * Calcolo l'importanza del concept all'interno dei profili utente
				 */
				double module1 = VectorUtil.getInstance().vectorSumComponent(signals[0]);
				double module2 =  VectorUtil.getInstance().vectorSumComponent(signals[1]);
				double sumModule = module1*module2;
				
				//sommo il contributo del vettore al totale
				tot+=sumModule;
				
				//aggingo la somma dei moduli a norm per normalizzare alla fine
				norm1+=module1*module1;
				norm2+=module2*module2;
				
			}else{
			//se una delle due è null 
			if(signals[0]!=null){
				double module= VectorUtil.getInstance().vectorSumComponent(signals[0]);
				norm1+=module*module;
			}
			
			if(signals[1]!=null){
				double module= VectorUtil.getInstance().vectorSumComponent(signals[1]);
				norm2+=module*module;
				
			}
			}
		}

	
		
		tot=tot/(Math.sqrt(norm1)*Math.sqrt(norm2));
		return  tot;
	}
	
	public double[] attenua (double[] signal) {
		double[] result = signal;
		for (int i=0;i<result.length;i++) {
			result[i] = result[i] * Math.pow(Math.E, (1/T)*(result.length-i));
			//System.err.println((1/T)*(result.length-i));
		}
		return result;
	}
	
	public String getNameFunction(){
		return this.namefunction;
	}
	
	
	public int compare(UserModelWithScore o1, UserModelWithScore o2) {
		return Double.compare(o2.getScore(), o1.getScore());
	}
	
	public static void main (String[] args) {
		double a[] = new double[]{1,1,1,1,1,1,1,1,1,1,1};
		double b[] = new TimeWeightSimilarity(-10).attenua(a);
		for (double n:b)
			System.out.println(n);
	}
}
