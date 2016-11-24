package org.rm3umf.framework.eval.similarity;

import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.eval.SimilarityFunction;
import org.rm3umf.framework.eval.UserModelWithScore;
/**
 * Implementa una funzione di similarità (non una distanza) che utilizza altre 2 funzioni di similarità pesate con dei valori alpha e beta
 * Applicazione del pattern Composite. 
 *
 * @author giulz
 *
 */


public class SimilarityComposite extends SimilarityFunction{
	
	
	//Parameter
	private double alpha;
	private double  beta;
	
	//Similarity function
	private SimilarityFunction function1;
	private SimilarityFunction function2;
	
	//name
	private String nameFunction;
	
	/**
	 * Costruisce la funzione di similarità composta
	 * @param function1 - funzione di similarità 1 
	 * @param alpha -  peso funzione 1
	 * @param function2 - funzione si similarità 2
	 * @param beta - peso funzione 2
	 * ATTENZIONE = alpha+beta deve essere uguale ad 1
	 */
	public SimilarityComposite(SimilarityFunction function1,double alpha,SimilarityFunction function2, double beta){
		this.function1=function1;
		this.function2=function2;
		this.alpha=alpha;
		this.beta=beta;
		//setto il nome
		this.nameFunction="COMPOSITE("+alpha+"*"+function1.getNameFunction()+","+beta+"*"+function2.getNameFunction()+")";
	}
	
	/**
	 * Applica le due funzioni di similarità pensandole secondo i parametri alpha e beta e restituisce un 
	 * score di similarità tra i due profili
	 */
	public double getSimilarity(UserModel u1, UserModel u2) {
		double res = (alpha*function1.getSimilarity(u1, u2))+(beta*function2.getSimilarity(u1, u2)); 
		return res;
	}

	
	public String getNameFunction() {
		return this.nameFunction;
	}

	
	public int compare(UserModelWithScore o1, UserModelWithScore o2) {
		return Double.compare(o2.getScore(), o1.getScore());
	}

}
