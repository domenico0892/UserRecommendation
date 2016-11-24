package org.rm3umf.framework.eval;

import java.io.IOException;
import java.util.List;
import org.rm3umf.domain.UserModel;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.xml.FactoryXml;
import util.FactoryFile;

/**
 * Permette di valutare una funzione di similarità in base alle funzioni di valutazione 
 * specificate
 * 
 * @author giulz
 *
 */



public class Valutator {
	
	private  int dimBuffer ; 
	private List<ValutationFunction> valutationFunction;
	private String pathReport;
	
	public Valutator(int dimBuffer,List<ValutationFunction> valutationFunction, String pathReport){
		this.valutationFunction=valutationFunction;
		this.dimBuffer=dimBuffer;
		this.pathReport=pathReport;
		
	}

	/**
	 * Vorrei 
	 * @param function
	 * @throws PersistenceException
	 * @throws IOException
	 */
	public  void valutate(SimilarityFunction function) throws PersistenceException, IOException{
		
		FactoryXml xmlFactory=new FactoryXml(); //
		
		//Recupero tutte le rappresentazioni utente presenti nel sistema
		List<UserModel> userModels= AAFacadePersistence.getInstance().userModelRetriveAll();
		//visualizza i modelli utente recuperati 
		for(UserModel usermodel: userModels){
			System.out.println("Recuperato rappresentazione utente : "+usermodel);
		}
		
		
		/*
		 * Creo e appplico la funzione di similarità ottenendo il result relativo alla fun di sim applicata
		 */
		ApplicatorFunction applicator = new ApplicatorFunction(dimBuffer);

		//Funzione di similarità 
		//SimilarityFunction function = new HaarSimilarity(0,4);	
		
		//Applica la funzione di similarità ad un insieme di UM e costruisco il risultato
		applicator.setSimilarityFunction(function);
		Result result=applicator.apply(userModels); // limito ai primi 100 gli utenti da inserire nel result
		//xmlFactory.saveResult(result);
		
		
//		TwittomenderResultCreator twittomenderResultCreator = new TwittomenderResultCreator(10);
//		Result result = twittomenderResultCreator.applyS1Function(userModels,100);
//		xmlFactory.saveResult(result);
		

		String resultValutation=function.getNameFunction()+" : ";
		//Valuta risultato
		for(ValutationFunction vf:this.valutationFunction){
			double scoreResult=vf.valutate(result);
			resultValutation+=vf.getNameFunction()+"="+scoreResult+", ";
			System.err.println(resultValutation);
		}
			
		
		FactoryFile.getInstance().writeLineOnFile(this.pathReport,resultValutation );
		
	}

}
