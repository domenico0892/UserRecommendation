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


public class VectorScalarProd extends SimilarityFunction{
	private static Logger logger = Logger.getLogger(D1Distance.class);
	private String namefunction;
	private int level;

	/**
	 * @param level
	 */
	public VectorScalarProd(int level) {
		this.namefunction="VectorScalarProd_"+level;
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

		double coeffNorm1=0.;
		double coeffNorm2=0.;

		for( String conceptid : concept2singnal.keySet() ){

			double[][] signals = concept2singnal.get(conceptid);
			//se il sengale in comune
			if(signals[0]!=null && signals[1]!=null){
				double[] vect1 =WaveletUtil.getInstance().approssimazione(signals[0],this.level); //mem il sig1
				double[] vect2 =WaveletUtil.getInstance().approssimazione(signals[1],this.level); //mem il sig2

				tot+=VectorUtil.getInstance().vectorScalarProd(vect1,vect2);  

				double energy1=VectorUtil.getInstance().energy(vect1);
				double energy2=VectorUtil.getInstance().energy(vect2);
				coeffNorm1+=energy1;//VectorUtil.getInstance().energy(sig1ByLevel);//vectorScalarProd(sig1ByLevel, sig1ByLevel); //energia s1
				coeffNorm2+=energy2;//VectorUtil.getInstance().energy(sig2ByLevel);//vectorScalarProd(sig2ByLevel, sig2ByLevel); //energia s2

			}else{
				//se una delle due è null 
				if(signals[0]!=null){
					//non ci sta bisogno di fare la trasformata perche la trasformata wavelet conserva l'energia
					double[] sig=WaveletUtil.getInstance().approssimazione(signals[0],this.level); 
					//double[] sig = signals[0];
					double energy=VectorUtil.getInstance().energy(sig);
					coeffNorm1+=energy; //enegia s1
				}
				if(signals[1]!=null){
					double[] sig=WaveletUtil.getInstance().approssimazione(signals[1],this.level); 
					double energy=VectorUtil.getInstance().energy(sig);
					coeffNorm2+=energy; //enegia s1

				}
			}

		}

		return    tot/(Math.sqrt(coeffNorm1*coeffNorm2));
	}
	
	
	public static double vectorScalarProd(double[] vect1,double[] vect2){
		double res =0.;
		//lunghezza vettori
		int length = vect1.length;
		//
		for(int i=0;i<length;i++){
			res+=Math.abs(vect1[i])*Math.abs(vect2[i]);
		}
		return res;
	}
	
	/**
	 * Effettua la somma tra due vettori n-dim
	 * @param vect1
	 * @param vect2
	 * @return res - vettore risultato
	 */
	public static double[] vectorSum(double[] vect1,double[] vect2){
		int length = vect1.length;
		double[] res = new double[length];
		for(int i = 0; i<length;i++){
			res[i]=vect1[i]+vect2[i];
		}
		return res;
	}
	
	/**
	 * Restituisce il modulo del vettore passato come parametro
	 * @param vect
	 * @return res - valore del modulo
	 */
	public static double vectorModule(double[] vect){
		double res=0;
		for(double comp:vect){
			res+=comp*comp;
		}
		res=Math.sqrt(res);
		return res;
	}
	
	public static double cosineSimilarity(double[] s1,double[] s2){
		
		//lunghezza del segnale
		int n=s1.length;	
		//variabile necessarie
		double denS1,denS2,denom,res;
		
		/* Calculate the denominator */
		denS1 = 0.0;
		denS2 = 0.0;
		for (int i=0;i<n;i++) {
			denS1 += (s1[i] ) * (s1[i] );
			denS2 += (s2[i] ) * (s2[i] );
		}
		
		denom = Math.sqrt(denS1)* Math.sqrt(denS2);


		//inizializzo il risultato
		
		/* Calculate the correlation series */

		//for (int delay=0;delay<maxdelay;delay++) {		
		double nom=0;
		for (int i=0;i<n;i++) {
			nom+=(s1[i])*(s2[i]);
		}
		
		res=0.0;
		
		res= nom / denom;
		return res;
	}

	
	public String getNameFunction(){
		return this.namefunction;
	}
	
	
	public int compare(UserModelWithScore o1, UserModelWithScore o2) {
		return Double.compare(o2.getScore(), o1.getScore());
	}
	
	public static double signalSimilarity(double[] signal1,double[] signal2){
		double res =0.;
		//trasformo i segnali
		double[] sig1Trasf=WaveletUtil.getInstance().trasforma(signal1);
		double[] sig2Trasf=WaveletUtil.getInstance().trasforma(signal2);
		
		double num = VectorUtil.getInstance().vectorScalarProd(sig1Trasf, sig2Trasf);
		
		double cff=VectorUtil.getInstance().vectorScalarProd(sig1Trasf,sig1Trasf);
		double cgg=VectorUtil.getInstance().vectorScalarProd(sig2Trasf,sig2Trasf);
		double den=Math.sqrt(cff*cgg);
		
		res=num/den;
		return res;
		
	}
	
	
	public static void main(String[] args){
		
		double[] array1={0.5,0.5};
		double[] array2={1,0};
		
		double[] v =vectorSum(array1, array2);
		
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
