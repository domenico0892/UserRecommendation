package org.rm3umf.framework.eval.similarity;


import java.util.Arrays;
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
 * ( min=0 max=5 )->  S@10 = (600)105 ,  
 * ( min=0 max=4 ) ->  S@10 = (600)108.0 ,  (tot) 198.0
 * ( min=0 max=3) ->  S@10 = (600)106.0 ,
 * ( min=1 max=3) ->  S@10 = (600) 104,  
 * ( min=1 max=4) ->  S@10 = (600) 104,  
 * @author giulz
 *
 */
public class HaarSimilarity extends SimilarityFunction{
	

	private static Logger logger = Logger.getLogger(D1Distance.class);
	private String namefunction;
	private int minResolution;
	private int maxResolution;
	

	
	public HaarSimilarity(int minResolution,int maxResolution) {
		this.namefunction="haaSimilarity("+minResolution+","+maxResolution+")";
		this.minResolution=minResolution;
		this.maxResolution=maxResolution;
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
		}

		int conceptTot = concept2singnal.keySet().size();	
		
		//ci sarà il totale
		double tot = 0.0;

		double norm1=0;
		double norm2=0;
		double num=0;	
		double den=0;
		
		for( String conceptid : concept2singnal.keySet() ){

			double[][] signals = concept2singnal.get(conceptid);
			
			//se il sengale in comune
			if(signals[0]!=null && signals[1]!=null){
				double[] vect1 = signals[0]; //mem il sig1
				double[] vect2 = signals[1]; //mem il sig2
				
				/*
				 * Calcolo la similarità tra le due segnali utilizzando un metodo multi risoluzione 
				 */
				double[] haarCoefficient1 =WaveletUtil.getInstance().haarCoefficient(vect1,this.minResolution , this.maxResolution);//, this.minResolution,this.maxResolution);
				double[] haarCoefficient2 =WaveletUtil.getInstance().haarCoefficient(vect2,this.minResolution , this.maxResolution);;//, this.minResolution,this.maxResolution);
				
//				double[] haarCoefficient1 =WaveletUtil.getInstance().trasforma(VectorUtil.sign(vect1));//, this.minResolution,this.maxResolution);
//				double[] haarCoefficient2 =WaveletUtil.getInstance().trasforma(VectorUtil.sign(vect2));//, this.minResolution,this.maxResolution);
	
				
//				num = VectorUtil.vectorScalarProd(haarCoefficient1, haarCoefficient2); 		
//				den = Math.sqrt(VectorUtil.vectorScalarProd(haarCoefficient1, haarCoefficient1)*VectorUtil.vectorScalarProd(haarCoefficient2, haarCoefficient2));
				
				double simVector = VectorUtil.cosineSimilarity(haarCoefficient1, haarCoefficient2);
				
				/*
				 * Calcolo l'importanza del concept all'interno dei profili utente
				 */
				double module1 = VectorUtil.getInstance().energy(vect1);
				double module2 = VectorUtil.getInstance().energy(vect2);
				double sumModule = module1*module2;
				
				//sommo il contributo del vettore al totale
				tot+=sumModule*simVector;
				
				//aggingo la somma dei moduli a norm per normalizzare alla fine
				norm1+=module1*module1;
				norm2+=module2*module2;
				
			}else{
			//se una delle due è null 
			if(signals[0]!=null){
				double module=VectorUtil.getInstance().energy(signals[0]);
				norm1+=module*module;
			}
			
			if(signals[1]!=null){
				double module=VectorUtil.getInstance().energy(signals[1]);
				norm2+=module*module;
				
			}
			}
		}

		//jaccard
		//Jaccard non ha senso perchè tanti piu segnali in comune ho tanto piu sarà grande tot !!!!
		//quindi mi basta dividere per i segnali totali
		//double jaccard = conceptCommon/(double)conceptTot ; //Cosi non è jaccard...........................
//		if(conceptCommon>0){
//			tot=tot/coeffNormalizzazione;
//		}
		
		tot= tot/(Math.sqrt(norm1) * Math.sqrt(norm2));
		return    tot;
	}
	
	
	public static double haarCorrelation(double[] haar1,double[] haar2){
		double res = 0.;
		double lengthSignal=haar1.length;
		//Effettuo la moltiplicazione tra i moduli delle occorrenze dei due vettori 

		double num=0.;
		double den1=0.;
		double den2=0.;

		for(int i=0 ; i< lengthSignal ; i++ ){
				num+=haar1[i]*haar2[i]; 
				den1+= haar1[i]*haar1[i];
				den2+= haar2[i]*haar2[i];
				
		}
		res=2*num/(den1+den2);
		return res;
	}

	
	
	public String getNameFunction(){
		return this.namefunction;
	}
	
	
	public int compare(UserModelWithScore o1, UserModelWithScore o2) {
		return Double.compare(o2.getScore(), o1.getScore());
	}
	
	
	
	private  static double[] calcolaPhasePrecision(double[] signal1, double[] signal2) {
		//System.out.println("FASE");
		    double[] fase=new double[signal1.length];
			for(int i=0;i<signal1.length;i++){;
				double x1=signal1[i];
				if(x1>0){
					x1=1;
				}else if(x1<0){
					x1=-1;
				}
				double x2=signal2[i];
				if(x2>0){
					x2=1;
				}else if(x2<0){
					x2=-1;
				}
				fase[i]= Math.abs(x1+x2)/2.;
			
			//	System.out.print("("+signal1[i]+"|"+signal2[i]+"->"+fase[i]+")");
			}
			
			//System.out.println();
			return fase;
		}
	
	/**
	 * Calcola il vettore della fase del segnale passato come parametro. 
	 * @param signal1
	 * @param signal2
	 * @return phasePrecision - precisione di fase media
	 */
	private  static double[] sign( double[] signal1 ) {
		double[] fase=new double[signal1.length];

		for(int i=0;i<signal1.length;i++){
			double x1=0.0;
			if(signal1[i]>0){
				x1=1;
			}else if(signal1[i]<0){
				x1=-1;
			}
			fase[i]=x1;
		}
	//	System.out.println(VectorUtil.getInstance().getString(fase));
		return fase;
	}
	

	
	public static double coefficientCrossCorrelation(double[] s1,double[] s2){
		//System.out.println("crosscorrelation:"+VectorUtil.getInstance().getString(s1)+" - "+VectorUtil.getInstance().getString(s2));
		//lunghezza del segnaleconceptTot
		int n=s1.length;	
		//variabile necessarie
		double ms1,ms2,denS1,denS2,denom,res;
		//sono le medie dei valori
		ms1=0.0;
		ms2=0.0;
		for(int i=0;i<s1.length;i++){
			ms1+=s1[i];
			ms2+=s2[i];
		}
		//divido per la lunghezza del segnale
		ms1=ms1/(double)n;
		ms2=ms2/(double)n;

		//System.out.println("ms1:"+ms1);
		//System.out.println("ms2:"+ms2);


		/* Calculate the denominator */
		denS1 = 0.0;
		denS2 = 0.0;
		for (int i=0;i<n;i++) {
			denS1 += (s1[i] - ms1) * (s1[i] - ms1);
			denS2 += (s2[i] - ms2) * (s2[i] - ms2);
		}
		//System.out.println("denS1:"+denS1+" denS2:"+denS2);

		denom = Math.sqrt(denS1*denS2);
		//System.out.println("denom:"+denom);

		//inizializzo il risultato
		res=0.0;

		/* Calculate the correlation series */

		//for (int delay=0;delay<maxdelay;delay++) {		
		double nom=0;
		for (int i=0;i<n;i++) {
			nom+=(s1[i]-ms1)*(s2[i]-ms2);
		}

		res= nom / denom;

		return res;
	}
	
	
	/**
	 * Precisione di fase media tra 2 segnali
	 * @param signal1
	 * @param signal2
	 * @return
	 */
	private  static double meanPhasePrecision(double[] signal1, double[] signal2) {
		//System.out.println("FASE");
		    double[] fase=new double[signal1.length];
		    double tot=0.0;
		    int cont=0; //conta il numero di volte che almeno una delle due componeni non � zero
			for(int i=0;i<signal1.length;i++){
				double x1=signal1[i];
				if(x1>0){
					x1=1;
				}else if(x1<0){
					x1=-1;
				}
				double x2=signal2[i];
				if(x2>0){
					x2=1;
				}else if(x2<0){
					x2=-1;
				}
				fase[i]= Math.abs(x1+x2)/2.;
				//System.out.println(fase[i]);
				if(x1!=0 || x2!=0){
					tot=tot+fase[i];
					cont++;
				}
			//	System.out.print("("+signal1[i]+"|"+signal2[i]+"->"+fase[i]+")");
			}
			
			//System.out.println();
			return tot/(double)cont;
		}
	
	public static void main(String[] args){
		
		double[] array1={1,0,0,0};
		double[] array2={1,0,0,0};
		
		double res =  meanPhasePrecision(array1,array2);
		
		
		
		System.out.println("haar="+res);
//		
//		array1=WaveletUtil.getInstance().trasforma(array1);
//		array2=WaveletUtil.getInstance().trasforma(array2);
//		
//		System.out.println(VectorUtil.getInstance().getString(array1));
//		System.out.println(VectorUtil.getInstance().getString(array2));
//	
		
	}

}
