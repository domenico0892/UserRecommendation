package org.rm3umf.framework.eval.similarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.eval.SimilarityFunction;
import org.rm3umf.framework.eval.UserModelWithScore;
import org.rm3umf.math.WaveletUtil;
import org.apache.log4j.*;
/**
 * 
 * ATTENZIONE: funziona bene solo se la somma delle componente dei segnali di un UM è uguale ad 1 .
 *
 */
public class D1Distance extends SimilarityFunction{
	 
	private static Logger logger = Logger.getLogger(D1Distance.class);
	
	private String namefunction;
	private int resolution;
	private WaveletUtil waveletTrasform;
	

	public D1Distance(int resolution) {
		this.resolution=resolution;
		this.namefunction="D1_DISTANCE("+resolution+")";
		this.waveletTrasform=new WaveletUtil();
		
	}
	
	
	public  double getSimilarity(UserModel u1 , UserModel u2) {
		logger.debug("Calcolo similarità tra:"+u1+"-"+u2);

		//logger.info("calcola similarit� tra utenti "+u1.getUser().getIduser()+"-"+u2.getUser().getIduser());

		//lista segnali 1
		List<Signal> list1=u1.getSignals();

		//lista segnali 2
		List<Signal> list2=u2.getSignals();

		int conceptTot=0;

		int conceptCommon=0;

		Map<String,double[][]> concept2singnal =  new HashMap<String,double[][]>();

		//Scorro la prima lista
		for(Signal signal:list1){
			String conceptid = signal.getConcept().getId();
			double[] arraySignal=signal.getSignal();
			double[] arrayNull=new double[arraySignal.length];
			double[][] signals ={arraySignal,arrayNull};
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
				signals[0]=array2;
				concept2singnal.put(conceptid, signals);
			}else{
				conceptCommon++;
			}
			signals[1]=arraySignal;
		}
		
		conceptTot=concept2singnal.keySet().size();
		
		
		double norm=0.;
		double distance=0.0;
		//Scorro tutta la mappa
		for(String conceptid : concept2singnal.keySet() ){
			double[][] signals = concept2singnal.get(conceptid);

			//recupero i due segnali	
			double[] array1 =waveletTrasform.approssimazione(signals[0], this.resolution);
			double[] array2 =waveletTrasform.approssimazione(signals[1], this.resolution);
			
			
			double localDiff=0.0;
			
			//effettuo la differenza tra i due vettori
			
			for(int i=0;i<array1.length;i++){
				localDiff += Math.abs(array1[i] - array2[i]);
				if(array1[i]>array2[i]){
					norm+=array1[i];
				}else{ 
					norm+=array2[i];
				}
			}
			distance += localDiff; //sommo la media della distanza tra i 2 segnali
		}
		//normalizzo 
		//distance=distance/norm;
		
		return distance;	
	}
	
	public String getNameFunction(){
		return this.namefunction;
	}


	public int compare(UserModelWithScore o1, UserModelWithScore o2) {
		return Double.compare(o1.getScore(), o2.getScore());
	}




}
