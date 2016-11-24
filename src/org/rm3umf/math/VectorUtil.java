package org.rm3umf.math;

public class VectorUtil {

	private static VectorUtil instance;

	public static synchronized VectorUtil getInstance(){
		if(instance==null){
			instance=new VectorUtil();
		}
		return instance;
	}

	public String getString(double[] signal){
		String out="[";
		for(double d:signal)
			out=out+d+" ";
		out=out+"]";
		return out;
	}

	/**
	 * Restituisce il modulo del vettore passato come parametro
	 * @param vect
	 * @return res - valore del modulo
	 */
	public double vectorModule(double[] vect){
		double res=0;
		for(double comp:vect)
			res+=comp*comp;
		res=Math.sqrt(res);
		return res;
	}
	
	/**
	 * Restituisce il modulo del vettore passato come parametro
	 * @param vect
	 * @return res - valore del modulo
	 */
	public double vectorSumComponent(double[] vect){
		double res=0;
		for(double comp:vect)
			res+=comp;
		return res;
	}
	
	/**
	 * Restituisce l'energia del segnale
	 * @param vect
	 * @return res - valore del modulo
	 */
	public double energy(double[] vect){
		double res=0;
		for(double comp:vect)
			res+=comp*comp;
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

	
	public static double crossCorrelazione(double[] s1,double[] s2){
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
	
	
	
	public static double jaccard(double[] vect1,double[] vect2){
		double num=0.;
		double den=0.;
		double res=0.;
		
		/*Calcolo Jaccard per vettori*/
		num+=vectorScalarProd(vect1,vect2);
		den+=vectorScalarProd(vect1,vect1)+vectorScalarProd(vect2, vect2)-vectorScalarProd(vect1,vect2);
		//
		res= num / den;

		return res;
	}
	
	/**
	 * Precisione di fase media tra 2 segnali
	 * @param signal1
	 * @param signal2
	 * @return
	 */
	public  static double meanPhasePrecision(double[] signal1, double[] signal2) {
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
	
	/**
	 * Calcola il vettore della fase del segnale passato come parametro. 
	 * @param signal1
	 * @param signal2
	 * @return phasePrecision - precisione di fase media
	 */
	public  static double[] sign( double[] signal1 ) {
		double[] fase=new double[signal1.length];

		for(int i=0;i<signal1.length;i++){
			double x1=0.0;
			if(signal1[i]>=0){
				x1=1.;
			}else if(signal1[i]<0){
				x1=-1.;
			}
			fase[i]=x1;
		}
	//	System.out.println(VectorUtil.getInstance().getString(fase));
		return fase;
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



}
