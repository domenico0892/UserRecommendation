package org.rm3umf.framework.eval.similarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.eval.SimilarityFunction;
import org.rm3umf.framework.eval.UserModelWithScore;
import org.rm3umf.math.WaveletUtil;


public class EuclideanDistance extends SimilarityFunction{
	
private static final Logger logger=Logger.getLogger(EuclideanDistance.class);
	
	
	private String namefunction;
	private int resolution;
	

	public EuclideanDistance(int resolution) {
		this.resolution=resolution;
		this.namefunction="EUCLIDEAN_DISCANCE("+resolution+")";
		
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
			double[] array=signal.getSignal();
			double[] array2=new double[array.length];
			double[][] signals ={array,array2};
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

		double distance=0; 




		//distance=Double.POSITIVE_INFINITY;
		//Scorro tutta la mappa
		for(String conceptid : concept2singnal.keySet() ){

			double[][] signals = concept2singnal.get(conceptid);

			if(signals[0]!=null && signals[1]!=null){

				double[] array1 =signals[0];
				double[] array2 =signals[1];
				//	System.out.print(array1!=null ? VectorUtil.getInstance().getString(array1):"null");
				//	System.out.println(array2!=null ? VectorUtil.getInstance().getString(array2):"null");

				//recupero i due segnali	
				array1 =WaveletUtil.getInstance().trasforma(signals[0]);
				array2 =WaveletUtil.getInstance().trasforma(signals[1]);
				distance+=distanzaQuadratica(array1, array2);
			}

		}
		
		//conceptTot=list1.size()+list2.size()-conceptCommon;
		return distance;

	}
	
	


	
	public static double euclideanDistance(double[] freqs1, double[] freqs2) {
	    double res = 0;
	    for (int i = 0; i < freqs1.length; i++) {
	        res += (freqs1[i] - freqs2[i]) * (freqs1[i] - freqs2[i]);
	    }
	    res = Math.sqrt(res);
	  //  System.out.println(res);
	    return res;
	}
	
	public static double distanzaQuadratica(double[] freqs1, double[] freqs2) {
	    double res = 0;
	    for (int i = 0; i < freqs1.length; i++) {
	        res += (freqs1[i] - freqs2[i]) * (freqs1[i] - freqs2[i]);
	    }
	    //res = Math.sqrt(res);
	  //  System.out.println(res);
	    return res;
	}
	
	
	
	
	
public String getNameFunction(){
	return this.namefunction;
}






public int compare(UserModelWithScore o1, UserModelWithScore o2) {
	return Double.compare(o1.getScore(), o2.getScore());
}



}
