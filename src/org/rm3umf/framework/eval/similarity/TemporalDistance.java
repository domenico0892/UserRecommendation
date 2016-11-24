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


public class TemporalDistance extends SimilarityFunction{
	
	private static Logger logger = Logger.getLogger(D1Distance.class);
	private String namefunction;
	private int resolution;

	
	public TemporalDistance(int resolution) {
		this.namefunction="VECTOR_SPACE_MODEL";
		this.resolution=resolution;
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

		
			//distance=Double.POSITIVE_INFINITY;
			//Scorro tutta la mappa
		
		int lengthSignal = (int)Math.pow(2, this.resolution);
		int conceptTot = concept2singnal.keySet().size();	
		
		//ci sarà il totale
		double tot = 0.0;

		for( String conceptid : concept2singnal.keySet() ){

			double[][] signals = concept2singnal.get(conceptid);
			if(signals[0]!=null && signals[1]!=null){
				double[] vect1 = WaveletUtil.getInstance().approssimazione(signals[0],this.resolution);
				double[] vect2 = WaveletUtil.getInstance().approssimazione(signals[1],this.resolution);
				//double[] phasePrecision = calcolaPhasePrecision(vect1, vect2);
				
//				//seleziona solo le comp della risoluzione che mi interessa
				//vect1=Arrays.copyOfRange(vect1,5,8);
				//vect2=Arrays.copyOfRange(vect2,5,8);
				
						
						
//				double[] vectPhase1 = getPhase(vect1);
//				double[] vectPhase2 = getPhase(vect2);
//				
//				double[] vectModule1 = getModule(vect1);
//				double[] vectModule2 = getModule(vect2);
//				
				//metto a confronto le fase 
				double[] phasePrecision = calcolaPhasePrecision(vect2,vect1);
				
				
				lengthSignal=vect1.length;
				//Effettuo la moltiplicazione tra i moduli delle occorrenze dei due vettori 
				
				double localTot=0.0;
				for(int i=0 ; i< lengthSignal ; i++ ){
					
						double mod1 = vect1[i]; 
						double mod2 = vect2[i];
						double mod = Math.abs( mod1-mod2);
						localTot+=mod;
					
					
				}
					
				tot+=localTot;
			 }
		}
		//jaccard
		//Jaccard non ha senso perchè tanti piu segnali in comune ho tanto piu sarà grande tot !!!!
		//quindi mi basta dividere per i segnali totali
		double jaccard =  1./conceptTot; //Cosi non è jaccard...........................
		
		return tot  ;
	}
	
	
	/**
	 * 
	 * PROBLEMA: 1) per i segnali costanti non funziona perche ci sta una divisione per 0. In realta � proprio 
	 * la formula che non funziona.
	 * 2)Perche con due serie diverse come nell'esempio del mai mi da uno cioe massimi similarit�!!
	 */
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
		return Double.compare(o1.getScore(), o2.getScore());
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
	private  static double[] getPhase( double[] signal1 ) {
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
	
	/**
	 * Calcola il vettore del moduelo. 
	 */
	private  static double[] getModule( double[] signal1 ) {
		double[] module=new double[signal1.length];

		for(int i=0;i<signal1.length;i++){
			
			module[i]=Math.abs(signal1[i]);
		}
		return module;
	}
	
	public static double coefficientCrossCorrelation(double[] s1,double[] s2){
		//System.out.println("crosscorrelation:"+VectorUtil.getInstance().getString(s1)+" - "+VectorUtil.getInstance().getString(s2));
		//lunghezza del segnale
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
		    int cont=1; //conta il numero di volte che almeno una delle due componeni non � zero
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
	
//	public static void main(String[] args){
//		
//		double[] array1={-1 , 0 , -1 , 1 , 0 ,-1,-1, 0};
//		double[] array2={-1, 0,   0, -1,  0,  0, 0,-1};
//		double[] res = calcolaPhasePrecision(array1,array2);
//		
//		System.out.println(VectorUtil.getInstance().getString(res));
//		
//	}

}
