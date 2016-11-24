package org.rm3umf.framework.eval;

import org.rm3umf.domain.UserModel;

/**
 * Questa classe serve per poter associare aggiungere uno score a uno UserModel in modo da poterli
 * ordinare.
 *  
 * similarità
 * @author Giulz
 *
 */

public class UserModelWithScore extends UserModel {
	
	private double score;
	//private UserModel userModel;
	
	public UserModelWithScore(UserModel userModel){
		super(userModel.getUser(),userModel.getSignals());
		this.score=-1;
		
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String toString(){
		return "[USER MODEL :"+this.getUser().getIduser()+" numSignal="+this.getSignals().size()+" score="+this.score+"]";
	}
	
}
