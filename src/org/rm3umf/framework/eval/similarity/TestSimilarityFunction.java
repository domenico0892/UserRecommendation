package org.rm3umf.framework.eval.similarity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rm3umf.domain.User;
import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.eval.SimilarityFunction;
import org.rm3umf.framework.eval.UserModelWithScore;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.persistenza.UserDAO;
import org.rm3umf.persistenza.postgreSQL.UserDAOpostgreSQL;
import org.rm3umf.persistenza.postgreSQL.UserProxy;



public class TestSimilarityFunction {
	
	public static void main(String[] args) throws  InterruptedException, PersistenceException{
		
		Logger root = Logger.getRootLogger();
		BasicConfigurator.configure();
		root.setLevel(Level.DEBUG);
		
		SimilarityFunction function=new VectorScalarProd(1);
		
		
		UserDAO userDAO = new UserDAOpostgreSQL();
		
		List<User> listaUser=userDAO.retrieveOnlyUserWithFrieds();
		List<UserModel> users= new LinkedList<UserModel>();
		
		for(User user:listaUser){
				UserModel userModel=AAFacadePersistence.getInstance().userModelRetriveByUser(user);
				//Se un utente ha zero segnali lo tolgo perche non ha senso considerarlo
				if(userModel.getSignals().size()>0){
					users.add(userModel);
		
				}
			}
		User user = new UserProxy();
		user.setIduser(788098); //788098
		UserModel umCorr=AAFacadePersistence.getInstance().userModelRetriveByUser(user);
		
		List<UserModelWithScore> userScored = new LinkedList<UserModelWithScore>();
		//users.add(umCorr);
		
		for(UserModel um:users){
			double score=function.getSimilarity(umCorr, um);
			UserModelWithScore ums=new UserModelWithScore(um);
			ums.setScore(score);
			userScored.add(ums);	
		}
		Collections.sort(userScored,function);
		
		for(UserModelWithScore ums : userScored){
			System.out.println(ums);
		}

	}

}
