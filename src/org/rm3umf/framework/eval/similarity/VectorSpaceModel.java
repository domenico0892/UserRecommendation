package org.rm3umf.framework.eval.similarity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.User;
import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.eval.SimilarityFunction;
import org.rm3umf.framework.eval.UserModelWithScore;
import org.rm3umf.math.WaveletUtil;


public class VectorSpaceModel extends SimilarityFunction{
	
		private static Logger logger = Logger.getLogger(D1Distance.class);
		
		private String namefunction;
	
		
		

		public VectorSpaceModel() {
			this.namefunction="VECTOR_SPACE_MODEL";
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

			Map<String,double[]> concept2singnal =  new HashMap<String,double[]>();

			//Scorro la prima lista
			for(Signal signal:list1){

				double frequency = WaveletUtil.getInstance().trasforma(signal.getSignal())[0];
				String conceptid = signal.getConcept().getId(); 

				double[] signals ={frequency , 0};
				concept2singnal.put(conceptid, signals );
			}

			//Segnali della seconda lista
			for(Signal signal:list2){
				double frequency = WaveletUtil.getInstance().trasforma(signal.getSignal())[0];

				String conceptid=signal.getConcept().getId(); 

				double[] signals = concept2singnal.get(conceptid);
				if(signals==null){		
					signals=new double[2];


				}else{
					conceptCommon++;
				}
				signals[1]=frequency;
				concept2singnal.put(conceptid, signals);
			}

			int lengthVector = concept2singnal.keySet().size();
			double[] vector1 = new double[lengthVector] ;
			double[] vector2 = new double[lengthVector] ;

			//Creo i due vettori delle frequenze
			int indice = 0;
			for(String conceptid : concept2singnal.keySet() ){
				double comp1 = concept2singnal.get(conceptid)[0];
				double comp2 = concept2singnal.get(conceptid)[1];
				vector1[indice] = comp1;
				vector2[indice] = comp2;
				indice++;	
			}
			//eseguo coseno similarità
			double res = cosineSimilarity(vector1,vector2);

			return res;	
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
		
		
		public int compare(UserModelWithScore o1, UserModelWithScore o2) {
			return Double.compare(o2.getScore(), o1.getScore());
		}
		
		
//		public static void main(String[] args){
//			User user1=new User();
//			
//			//User user2=new
//			UserModel um1=new UserModel();
//			List<Signal> list1=new LinkedList<Signal>();
//			Concept concept1 = new Concept();
//			concept1.setId("concept1");
//		    //signal 1
//			Signal signal1=new Signal();
//		    signal1.setConcept(concept1);
//		    double[] array1={1.,1.0,0.,0.};
//		    signal1.setSignal(array1);
//			
//			
//			
//			UserModel um2=new UserModel();
//					
//		}




}
