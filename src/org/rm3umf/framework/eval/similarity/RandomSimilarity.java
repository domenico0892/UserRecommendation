package org.rm3umf.framework.eval.similarity;
import java.util.Random;

import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.eval.SimilarityFunction;
import org.rm3umf.framework.eval.UserModelWithScore;


/**
 * E' un implementazione della funzione si similarit� che assegna una similarit� random come termine
 * di paragone con le altre similarit� 
 */

public class RandomSimilarity extends SimilarityFunction{
	
	
	private String namefunction = "RandomSimilarity"  ;
	private Random random;
	
	
	
	public RandomSimilarity() {
		random=new Random();
	}

	 
	
	public double getSimilarity(UserModel u1, UserModel u2) {
		double similarity=random.nextDouble();
		return similarity ;
	}

	
	public String getNameFunction() {
		return this.namefunction;
	}



	public int compare(UserModelWithScore o1, UserModelWithScore o2) {
		return Double.compare(o2.getScore(), o1.getScore());
	}
}
