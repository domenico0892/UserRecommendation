package org.rm3umf.math;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.*;
import math.transform.jwave.Transform;
import math.transform.jwave.handlers.DiscreteWaveletTransform;
import math.transform.jwave.handlers.WaveletTransform;
import math.transform.jwave.handlers.wavelets.Daub02;
import math.transform.jwave.handlers.wavelets.Haar02;
import math.transform.jwave.handlers.wavelets.Haar02Orthogonal;
/**
 * Classe che implementa la trasformata wavelet utilizzando la libreria Jwave. La trasformata
 * wavelet si può fare con segnali che siano potenza di 2 di lunghezza. Se un segnale è di una 
 * lungehzza   diversa di un potenza di 2 inserisco 0 finio ad arrivare alla potenza di 2 successiva.
 * ES:
 *    [1,2,3,5,6] -diventa- [1,2,3,4,5,6,0,0] 
 *    [1,2,3]     -diventa- [1,2,3,0] 
 * 
 * @author Giulz
 *
 */


public class WaveletUtil {
	
	private static Logger logger =  Logger.getLogger(WaveletUtil.class);
	
	private WaveletTransform waveletTrasform; 
	private static WaveletUtil instance;
	
	
	public static WaveletUtil getInstance(){
		if(instance==null)
			instance=new WaveletUtil();
		return instance;
	}
	
	
	public WaveletUtil(){
		this.waveletTrasform=new DiscreteWaveletTransform(new  Haar02() );	
	}
	
	/**
	 * Il sengnale passato come parametro viene trasformato utilizzando la trasformata wavelet 
	 * e successivamente vengono restituite le componente in base alla risoluzione passato
	 * come secondo parametro
	 * 
	 * @param signal
	 * @param level
	 * @return trasfSignal - componente del segnale trasformato secondo la risoluzione
	 */
	public   double[] approssimazione(double[] signal,int level){
		/*
		 * Devo rendere la lunghezza del signale una potenza di 2  
		 */

		// Verifico che la lunghezza sia una potenza di 2 

		//poichè il segnale deve essere una potenza di 2
		double esp=log(signal.length, 2);
		//arrotondo per eccesso
		double parteInt=Math.ceil(esp); //ATTENZIONE :sto arrotondano per diffetto quindi perdo delle inf
		//calcolo la lunghezza del segnale
		int lengthSignal = (int) Math.pow(2, parteInt);
		//Creo il segnale aggiungendo zeri alla fine finchè non raggiungo la potenza di 2 successiva
		double[] signalTrasform=Arrays.copyOfRange(signal,0,lengthSignal);
		
		int lenghtArray = lengthSignal; 
		if(level>0){
			//calcola la lunghezza in base alla risoluzione
			lenghtArray=(int)Math.pow(2.0,(int)parteInt - level);
			//trasforma il segnale
			signalTrasform = this.waveletTrasform.forwardWavelet( signalTrasform,level);

		}
		//Devo restituire solo i Trend
		double[] trendSignal =Arrays.copyOfRange(signalTrasform,0,lenghtArray);
		return trendSignal;
	}
	
	/**
	 * Il sengnale passato come parametro viene trasformato utilizzando la trasformata wavelet 
	 * e successivamente vengono restituite le componente in base alla risoluzione passato
	 * come secondo parametro
	 * 
	 * @param signal
	 * @param level
	 * @return trasfSignal - componente del segnale trasformato secondo la risoluzione
	 */
	public   double[] dettagli(double[] signal,int level){
		/*
		 * Devo rendere la lunghezza del signale una potenza di 2  
		 */

		// Verifico che la lunghezza sia una potenza di 2 

		//poichè il segnale deve essere una potenza di 2
		double esp=log(signal.length, 2);
		//arrotondo per eccesso
		double parteInt=Math.ceil(esp); //ATTENZIONE :sto arrotondano per diffetto quindi perdo delle inf
		//calcolo la lunghezza del segnale
		int lengthSignal = (int) Math.pow(2, parteInt);
		//Creo il segnale aggiungendo zeri alla fine finchè non raggiungo la potenza di 2 successiva
		double[] signalTrasform=Arrays.copyOfRange(signal,0,lengthSignal);
		
		int lenghtArray = lengthSignal; 
		if(level>0){
			//calcola la lunghezza in base alla risoluzione
			lenghtArray=(int)Math.pow(2.0,(int)parteInt - level);
			//trasforma il segnale
			signalTrasform = this.waveletTrasform.forwardWavelet( signalTrasform,level);

		}
		//Devo restituire solo i Trend
		double[] trendSignal =Arrays.copyOfRange(signalTrasform,lenghtArray,lenghtArray*2);
		return trendSignal;
	}
	
	
	public   double[] haarCoefficient(double[] signal,int minResolution,int maxResolution){
		/*
		 * Devo rendere la lunghezza del signale una potenza di 2  
		 */

		// Verifico che la lunghezza sia una potenza di 2 
		
		
		/*
		 * Trasfomo il segnale in una lunghezza uguale alla potenza di 2 
		 */
		double esp=log(signal.length, 2);
		//arrotondo per eccesso
		double parteInt=Math.ceil(esp);
		//calcolo la lunghezza del segnale
		int lengthSignal = (int) Math.pow(2, parteInt);
		//Creo il segnale aggiungendo zeri alla fine finchè non raggiungo la potenza di 2 successiva
		double[] signalTrasform=Arrays.copyOfRange(signal,0,lengthSignal);
		
		
		double[] haarCoeficient = {};
		
		for(int currentResolution=minResolution ; currentResolution<=maxResolution ; currentResolution++){
			//calcola la lunghezza in base alla risoluzione
			//trasforma il segnale
			int level = ((int)parteInt)-currentResolution ;
			haarCoeficient = ArrayUtils.addAll(haarCoeficient,approssimazione( signalTrasform,level));
						//.waveletTrasform.forwardWavelet( signalTrasform,resolution);
		}
		
		
		return haarCoeficient;
	}
	
	
	public   double[] haarCoefficient(double[] signal){
		/*
		 * Devo rendere la lunghezza del signale una potenza di 2  
		 */

		
		// Verifico che la lunghezza sia una potenza di 2 
		
		
		/*
		 * Trasfomo il segnale in una lunghezza uguale alla potenza di 2 
		 */
		double esp=log(signal.length, 2);
		//arrotondo per eccesso
		double parteInt=Math.ceil(esp);
		//calcolo la lunghezza del segnale
		int lengthSignal = (int) Math.pow(2, parteInt);
		//Creo il segnale aggiungendo zeri alla fine finchè non raggiungo la potenza di 2 successiva
		double[] signalTrasform=Arrays.copyOfRange(signal,0,lengthSignal);
		

		int maxResolution=(int)parteInt;
		int minResolution=0;
		
		
		double[] haarCoeficient = {};
		
		for(int currentResolution=minResolution ; currentResolution<=maxResolution ; currentResolution++){
			//calcola la lunghezza in base alla risoluzione
			//trasforma il segnale
			int level = ((int)parteInt)-currentResolution ;
			haarCoeficient = ArrayUtils.addAll(haarCoeficient,approssimazione( signalTrasform,level));
						//.waveletTrasform.forwardWavelet( signalTrasform,resolution);
		}
		
		
		return haarCoeficient;
	}
	
	/**
	 * Dato un segnale s e una risoluzione min e una max mi restituisce tutte le approssimazioni 
	 * dei vettore dalla risoluzione min al max. Es  s,min,max -> [T(sig,min),T(s,min+1),...,T(s,max)]
	 * @param signal
	 * @param minResolution
	 * @param maxResolution
	 * @return [T(sig,min),T(s,min+1),...,T(s,max)]
	 */
	public   double[][] multiResolution(double[] signal,int minResolution,int maxResolution){
		/*
		 * Trasfomo il segnale in una lunghezza uguale alla potenza di 2 
		 */
		double esp=log(signal.length, 2);
		//arrotondo per eccesso
		double parteInt=Math.ceil(esp);
		//calcolo la lunghezza del segnale
		int lengthSignal = (int) Math.pow(2, parteInt);
		//Creo il segnale aggiungendo zeri alla fine finchè non raggiungo la potenza di 2 successiva
		double[] signalTrasform=Arrays.copyOfRange(signal,0,lengthSignal);
		
		double[][] multiResArray = new double[maxResolution-minResolution+1][];
		
		int indice=0;
		for(int currentResolution=minResolution ; currentResolution<=maxResolution ; currentResolution++){
			//calcola la lunghezza in base alla risoluzione
			//trasforma il segnale
			int level = ((int)parteInt)-currentResolution ;
			multiResArray[indice] = approssimazione( signalTrasform,level); 
			indice++;
						//.waveletTrasform.forwardWavelet( signalTrasform,resolution);
		}
		
		
		return multiResArray;
	}
	
	
	
	/**
	 * Il sengnale passato come parametro viene trasformato utilizzando la trasformata wavelet 
	 * 
	 * @param signal
	 * @return trasfSignal 
	 */
	public   double[] trasforma(double[] signal){
		/*
		 * Devo rendere la lunghezza del signale una potenza di 2  
		 */

		// Verifico che la lunghezza sia una potenza di 2 

		//poichè il segnale deve essere una potenza di 2
		double esp=log(signal.length, 2);
		//arrotondo per eccesso
		double parteInt=Math.ceil(esp);
	
		//calcolo la lunghezza del segnale
		int lengthSignal = (int) Math.pow(2, parteInt);
	
		//Creo il segnale aggiungendo zeri alla fine finchè non raggiungo la potenza di 2 successiva
		double[] signalToReturn=Arrays.copyOfRange(signal,0,lengthSignal);
		
		//trasforma il segnale
	    signalToReturn = this.waveletTrasform.forwardWavelet(signalToReturn);
	    
		return signalToReturn;
	}
	
	public double[] antitrasforma(double[] haar){

		//poichè il segnale deve essere una potenza di 2
		double esp=log(haar.length, 2);
		//arrotondo per eccesso
		double parteInt=Math.ceil(esp);
		//calcolo la lunghezza del segnale
		int lengthSignal = (int) Math.pow(2, parteInt);
		//Creo il segnale aggiungendo zeri alla fine finchè non raggiungo la potenza di 2 successiva
		double[] signalToReturn=Arrays.copyOfRange(haar,0,lengthSignal);

		//trasforma il segnale
	    signalToReturn = this.waveletTrasform.reverseWavelet(signalToReturn);
		
		return signalToReturn;
		
	}
	
	
	
	
	/**
	 * Restituisce la logaritmo in base due di x
	 * @param x
	 * @param base
	 * @return potenza di 2
	 */
	private  double log(int x, int base){
	    return (Math.log(x) / Math.log(base));
	}
	
	/**
	 * Elimna il rumore: traforma , mette 0 e antitrasforma 
	 * @return
	 */
	public  double[] denoising(double[] signal,int livello){
		
		double[] segnaleTrasformato = trasforma(signal);
		
		int signalLength = segnaleTrasformato.length;
		
		int start = (int)Math.pow(2,livello-1);
		//
		for(int i=start; i<signalLength ;i++){
			segnaleTrasformato[i] = 0. ; 
		}
		
		//System.out.println("denoised:"+VectorUtil.getInstance().getString(segnaleTrasformato));
		
		return antitrasforma(segnaleTrasformato);
		
		
	}
	
	
	
//	/**
//	 * Elimna il rumore: traforma , mette 0 e antitrasforma 
//	 * @return
//	 */
//	public  double[] (double[] signal,int livello){
//		
//		double[] segnaleTrasformato = trasforma(signal);
//		
//		int signalLength = segnaleTrasformato.length;
//		
//		int start = (int)Math.pow(2,livello-1);
//		//
//		for(int i=start; i<signalLength ;i++){
//			segnaleTrasformato[i] = 0. ; 
//		}
//		
//		//System.out.println("denoised:"+VectorUtil.getInstance().getString(segnaleTrasformato));
//		
//		return antitrasforma(segnaleTrasformato);
//		
//		
//	}

	public static void main (String[] args){
		//segnale originale
		double[] signal = {0,1,0,0,0,0,0,0};
		
		
		double[] antitrasf = WaveletUtil.getInstance().approssimazione(signal, 0);
		System.out.println(VectorUtil.getInstance().getString(antitrasf));
		
		//traformo
		double[] multiresolution =WaveletUtil.getInstance().haarCoefficient(signal);
		for(double elem: multiresolution)
			System.out.println(elem);
		
	 
//		//denoising
//		double soglia = 1.9;
//		
//		for(int i =0;i<signalT.length;i++){
//			if(Math.abs(signalT[i])<soglia)
//				signalT[i]=0;
//	}
//		System.out.println("annullo la tendeza "+VectorUtil.getInstance().getString(signalT));y
//		//antitrasformo
//		double[] antitrasf = WaveletUtil.getInstance().antitrasforma( signalT);
//		System.out.println(VectorUtil.getInstance().getString(antitrasf));
//		
	}
	
	
	
	

}
