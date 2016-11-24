package org.rm3umf.math;

public class Convolution {
	
	
	public static void main(String[] args){
		double[] input={0,0,0,0,10,0,0,0,1};
		
		double[] output=convolution(input);
		
		System.out.println(VectorUtil.getInstance().vectorSumComponent(output));
	}
	
	
	
	public static double[] convolution(double[] signal){
		
		//res
		double[] output = new double[signal.length*3];
		
		for(int i=0;i<signal.length;i++){
			double m=i;
			for(int j=0;j<signal.length*3;j++){
				output[j]+=signal[i]*gaussiana(j-signal.length, m);
			}
		}
		
		return output;
	}
	
	public static double gaussiana(double x,double m){
		double res=0;
		
		double sigma=1;
		
		
		//denominatore
		double den=sigma*Math.sqrt(2*Math.PI);
		
		//numeratore
		double esp=Math.pow((x-m)/sigma, 2)*(-1./2);
		double num= Math.pow(Math.E,esp);
		
		res=num/den;
		return res;
		
	}

}
