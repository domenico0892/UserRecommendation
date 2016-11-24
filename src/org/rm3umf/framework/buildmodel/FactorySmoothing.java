package org.rm3umf.framework.buildmodel;

public class FactorySmoothing {
	
	private int ordine;
	
	public FactorySmoothing(int ordine){
		this.ordine=ordine;
	}
	
	
	
	
	
	
	
	public double[] mediaMobile(double[] signal){
		double[] newSignal=new double[signal.length-(this.ordine-1)];
		
		for(int i=0;i<signal.length-(this.ordine-1);i++){
			for(int j=i;j<i+this.ordine;j++)
				newSignal[i]+=signal[j];
			newSignal[i]=newSignal[i]/(double)ordine;
		}
		return newSignal;
		
	}
	
	public  static double[] mediaMobile(double[] signal,int ordine){
		double[] newSignal=new double[signal.length-(ordine-1)];
		
		for(int i=0;i<signal.length-(ordine-1);i++){
			for(int j=i;j<i+ordine;j++)
				newSignal[i]+=signal[j];
			newSignal[i]=newSignal[i]/(double)ordine;
		}
		return newSignal;
		
	}
	
	public static void main(String[] args){
		FactorySmoothing fs=new FactorySmoothing(0);
		double[] array={1,2,3,4,5,6,0};
		
		double[] newArray=fs.mediaMobile(array);
		
		for(double e:newArray)
			System.out.println(e);
		
		
	}

}
