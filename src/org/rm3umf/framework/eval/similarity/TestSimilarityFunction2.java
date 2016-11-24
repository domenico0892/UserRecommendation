package org.rm3umf.framework.eval.similarity;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.User;
import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.eval.SimilarityFunction;
import org.rm3umf.persistenza.PersistenceException;


public class TestSimilarityFunction2 {
	
public static void main(String[] args) throws  InterruptedException, PersistenceException{
		
		Logger root = Logger.getRootLogger();
		BasicConfigurator.configure();
		root.setLevel(Level.DEBUG);
		
		//USER 1
		User user1 = new User();
		user1.setIduser(1);
		
		
		Concept c1 = new Concept();
		c1.setId("casa");
		Signal s1 = new Signal();
		s1.setConcept(c1);
		double[] array1={1,0,0,0,0,0,0,0};
		s1.setSignal(array1);


		Concept c2 = new Concept();
		c2.setId("calcio");
		Signal s2 = new Signal();
		s2.setConcept(c2);
		double[] array2={1,0,0,0,0,0,0,0};
		s2.setSignal(array2);


		UserModel userModel1 = new UserModel();
		userModel1.setUser(user1);
		List<Signal> listSignal1=new LinkedList<Signal>();
		listSignal1.add(s1);
		listSignal1.add(s2);
		userModel1.setSignals(listSignal1);



		//USER 2
		User user2 = new User();
		user2.setIduser(2);

		//casa
		Concept c3 = new Concept();
		c3.setId("casa");
		Signal s3 = new Signal();
		s3.setConcept(c3);
		double[] array3={1,0,0,0,0,0,0,0};
		s3.setSignal(array3);
		
		//nuoto
		Concept c4 = new Concept();
		c4.setId("calcio");
		Signal s4 = new Signal();
		s4.setConcept(c4);
		double[] array4={1,0,0,0,0,0,0,0};
		s4.setSignal(array4);


		UserModel userModel2 = new UserModel();
		userModel2.setUser(user2);
		List<Signal> listSignal2=new LinkedList<Signal>();
		listSignal2.add(s3);
		listSignal2.add(s4);
		userModel2.setSignals(listSignal2);
		
		
		
		
		List<Integer> list = new LinkedList<Integer>();
		list.add(1);
		SimilarityFunction function=new HaarSimilarity(1,1);
		System.out.println("similarity="+function.getSimilarity(userModel1,userModel2));
		
		
		

	}


}
