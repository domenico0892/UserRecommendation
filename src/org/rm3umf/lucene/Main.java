package org.rm3umf.lucene;



import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.postgreSQL.UserProxy;


//import fceval.algorithms.Arru_Zeppi.JaccardSimilarity.indexer.*;


public class Main {
	
	public static void main (String args[]) throws IndexException , IOException{
		
		Logger root = Logger.getRootLogger();
		BasicConfigurator.configure();
		root.setLevel(Level.DEBUG);
		
		FacadeLucene facadeLucene=new FacadeLucene("./index");
		//mi preparo a cercare
		facadeLucene.prepareSearching();
		
		AAFacadePersistence facade=new AAFacadePersistence();
		
		UserProxy user=new UserProxy();
		user.setIduser(14545455);
		
	//	Set<Long> followed=user.getFollowed();
		
		
	//	System.out.println("size:"+followed.size());
		
		//BufferedReader reader = new BufferedReader(new InputStreamReader(is));

//		List<Long> listaUtenti = facadeLucene.getSimilarUser(user);
//		System.out.println("size:"+listaUtenti.size());
//		for(Long stringa:listaUtenti)
//			System.out.println(stringa);
	}

}
