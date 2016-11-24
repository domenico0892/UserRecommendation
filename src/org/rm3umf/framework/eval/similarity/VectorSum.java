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

/**
 * 
 * @author giulz
 * 
 * level 5 = 202 
 *
 */
public class VectorSum extends SimilarityFunction{
	

	public final int ALPHA=2;
	
	private static Logger logger = Logger.getLogger(D1Distance.class);
	private String namefunction;
	private int level;

	
	public VectorSum(int level) {
		this.namefunction="VectorSum(lev="+level+")";
		this.level=level;
	}
	
	
	
	public  double getSimilarity(UserModel u1 , UserModel u2) {
		logger.debug("Calcolo similarità tra:"+u1+"-"+u2);

		//logger.info("calcola similarit� tra utenti "+u1.getUser().getIduser()+"-"+u2.getUser().getIduser());

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
			//concept2singnal.put(conceptid, signals);
		}

		
			//distance=Double.POSITIVE_INFINITY;
			//Scorro tutta la mappa
		
		//int lengthSignal = (int)Math.pow(2, this.resolution);
		int conceptTot = concept2singnal.keySet().size();	
		
		//ci sarà il totale
		double tot = 0.0;
		
		double coeffNormalizzazione=0.;
		double sommatoria1=0.;
		double sommatoria2=0.;
		
		for( String conceptid : concept2singnal.keySet() ){

			double[][] signals = concept2singnal.get(conceptid);
			double[] approx1=null;
			double[] approx2=null;
			
			//se il sengale in comune
			if(signals[0]!=null && signals[1]!=null){
				double[] vect1 = signals[0]; //mem il sig1
				double[] vect2 = signals[1]; //mem il sig2
				
				//trasformo i segnali a seconda del livello di risoluzione	
				approx1 = WaveletUtil.getInstance().approssimazione(vect1,this.level);
				approx2 = WaveletUtil.getInstance().approssimazione(vect2,this.level);
				
			
				//
				double module1=VectorUtil.getInstance().vectorModule(approx1);
				double module2=VectorUtil.getInstance().vectorModule(approx2);
				
				
				//effettuo la somma tra vettori*VectorUtil.getInstance().cosineSimilarity(sig2ByLevel, sig2ByLevel);
				double[] vectorSum = VectorUtil.getInstance().vectorSum(approx1,approx2);
				
				
				
			//	double[] vectorPhase1=VectorUtil.getInstance().sign(sig1ByLevel);
			//	double[] vectorPhase2=VectorUtil.getInstance().sign(sig2ByLevel);
				
				//calcolo la coseno similarità tra i vettori phase1 phase2
				//double phaseSim=VectorUtil.getInstance().cosineSimilarity(vectorPhase1, vectorPhase2);
				
				//Calcolo la il modulo del vettore somma 
				//mi da l'importanza dei segnali nei due profili
				double moduleSum=VectorUtil.getInstance().vectorModule(vectorSum);
				
				tot+=moduleSum;
				coeffNormalizzazione+=(module1+module2);				
			}else{
				
				//se una delle due è null 
				if(signals[0]!=null){
					double[] sig=WaveletUtil.getInstance().approssimazione(signals[0],this.level);
					//coeffNormalizzazione+=vectorScalarProd(sig, sig);
					double module = VectorUtil.getInstance().vectorModule(sig);
					coeffNormalizzazione+=module;
				}
				
				if(signals[1]!=null){
					double[] sig=WaveletUtil.getInstance().approssimazione(signals[1],this.level);
					double module = VectorUtil.getInstance().vectorModule(sig);
					coeffNormalizzazione+=module;
				}
			}
			
		}
		
		
		tot=tot/coeffNormalizzazione;
		
		return    tot;
	}
	
	
	
	public static double vectorScalarProd(double[] vect1,double[] vect2){
		
		double res =0.;
		//lunghezza vettori
		int length = vect1.length;
		//
		for(int i=0;i<length;i++){
			res+=vect1[i]*vect2[i];
		}
		
		return res;
	}
	

	

	
	
	public String getNameFunction(){
		return this.namefunction;
	}
	
	
	public int compare(UserModelWithScore o1, UserModelWithScore o2) {
		return Double.compare(o2.getScore(), o1.getScore());
	}
	
	
	public static void main(String[] args){
		
		double[] array1={0.5,0.5};
		double[] array2={1,0};
		
		double[] v =VectorUtil.getInstance().vectorSum(array1, array2);
		
		//System.out.println(VectorUtil.getInstance().getString(v));
		
		System.out.println(vectorScalarProd(array1, array2));
//		
//		array1=WaveletUtil.getInstance().trasforma(array1);
//		array2=WaveletUtil.getInstance().trasforma(array2);
//		
//		System.out.println(VectorUtil.getInstance().getString(array1));
//		System.out.println(VectorUtil.getInstance().getString(array2));
//	
		
	}

}
