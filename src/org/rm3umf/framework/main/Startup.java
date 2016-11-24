package org.rm3umf.framework.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.rm3umf.framework.eval.SimilarityFunction;
import org.rm3umf.framework.eval.Valutator;
import org.rm3umf.framework.eval.ValutationFunction;
import org.rm3umf.framework.eval.similarity.FunctionMultiresolution;
import org.rm3umf.framework.eval.similarity.HaarSimilarity;
import org.rm3umf.framework.eval.similarity.JaccardNoWeight;
import org.rm3umf.framework.eval.similarity.PatternSimilarity;
import org.rm3umf.framework.eval.similarity.PatternSimilarity2;
import org.rm3umf.framework.eval.similarity.RandomSimilarity;
import org.rm3umf.framework.eval.similarity.TemporalCosineSimilarity;
import org.rm3umf.framework.eval.similarity.TimeWeightSimilarity;
import org.rm3umf.framework.eval.similarity.VectorScalarProd;
import org.rm3umf.framework.eval.similarity.VectorSpaceModel;
import org.rm3umf.framework.eval.similarity.VectorSum;
import org.rm3umf.framework.eval.valutation.LuceneFriendFollower;
import org.rm3umf.framework.eval.valutation.MeanKendallTau;
import org.rm3umf.framework.eval.valutation.MeanReciprocalRank;
import org.rm3umf.framework.eval.valutation.SuccessAtRankK;

import org.rm3umf.persistenza.PersistenceException;

import util.FactoryFile;

public class Startup {
	
	
	
	
	public static void main(String[] args) throws PersistenceException, IOException{
		
		
		
		int sizeRecList=10;
		Date dt=new Date();
		dt.toString();
		
		String modelDesc="dayPerPeriod=1,schemaPesatura=tf*idf,typeSignal=dbpedia_entity,sogliaSegnali=10,sogliaConcept=2,dataset_14_05a07_08";
		
		
		
		Calendar gc = GregorianCalendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String date=sdf.format(gc.getTime());
		
		String pathReport="report"+System.currentTimeMillis()+date+"["+modelDesc+"].txt";
		
		
		
		
		
		//valutation
		
		List<SimilarityFunction> listaFunzioniSimilarità=new LinkedList<SimilarityFunction>();
	
		
		listaFunzioniSimilarità.add(new RandomSimilarity());
		listaFunzioniSimilarità.add(new TimeWeightSimilarity(-200));
//		listaFunzioniSimilarità.add( new VectorSpaceModel());

//		listaFunzioniSimilarità.add( new HaarSimilarity(0,0));
//		listaFunzioniSimilarità.add( new HaarSimilarity(0,1));
//		listaFunzioniSimilarità.add( new HaarSimilarity(0,2));
//		listaFunzioniSimilarità.add( new HaarSimilarity(0,3));
//		listaFunzioniSimilarità.add( new HaarSimilarity(0,4));
//		listaFunzioniSimilarità.add( new HaarSimilarity(0,5));
//		listaFunzioniSimilarità.add( new HaarSimilarity(0,6));
//		listaFunzioniSimilarità.add( new HaarSimilarity(0,6));

/*	    listaFunzioniSimilarità.add( new HaarSimilarity(0,0));
		listaFunzioniSimilarità.add( new HaarSimilarity(1,1));
		listaFunzioniSimilarità.add( new HaarSimilarity(2,2));
		listaFunzioniSimilarità.add( new HaarSimilarity(3,3));
		listaFunzioniSimilarità.add( new HaarSimilarity(4,4));
		listaFunzioniSimilarità.add( new HaarSimilarity(5,5));
		listaFunzioniSimilarità.add( new HaarSimilarity(6,6));
		
		
		
		
		listaFunzioniSimilarità.add(new VectorSum(6));
		listaFunzioniSimilarità.add(new VectorSum(5));
		listaFunzioniSimilarità.add(new VectorSum(4));
		listaFunzioniSimilarità.add(new VectorSum(3));
		listaFunzioniSimilarità.add(new VectorSum(2));
		listaFunzioniSimilarità.add(new VectorSum(1));
		listaFunzioniSimilarità.add(new VectorSum(0));
		listaFunzioniSimilarità.add(new FunctionMultiresolution(0,5));
		listaFunzioniSimilarità.add(new FunctionMultiresolution(1,5));
		listaFunzioniSimilarità.add(new FunctionMultiresolution(0,4));
		listaFunzioniSimilarità.add(new FunctionMultiresolution(1,4));
		
		
		
		listaFunzioniSimilarità.add(new TemporalCosineSimilarity());
		listaFunzioniSimilarità.add(new VectorScalarProd(0));
		listaFunzioniSimilarità.add(new VectorScalarProd(1));
		listaFunzioniSimilarità.add(new VectorScalarProd(2));
		listaFunzioniSimilarità.add(new VectorScalarProd(3));
		listaFunzioniSimilarità.add(new VectorScalarProd(4));
		listaFunzioniSimilarità.add(new VectorScalarProd(5));
		listaFunzioniSimilarità.add(new VectorScalarProd(6));
//		*/
		
		
		/*
		 *Valuto il risultato ottenuto applicado una certa funzione di similarità 
		 */
		
		List<ValutationFunction> listValFun=new LinkedList<ValutationFunction>();
		//ValutationEngine valutationEngine = new ValutationEngine();
		//AnalyzerResult resultAnalyzer  = new  MeanKendallTau(new LuceneFriendFollower());
		listValFun.add(new  SuccessAtRankK());
		//listValFun.add(new  MeanKendallTau(new LuceneFriendFollower()));
		//listValFun.add(new   MeanReciprocalRank());
		
		
		
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy MM dd - hh:mm");
		String date2=sdf2.format(gc.getTime());
	
		
		
		
		
		//for(int i=5; i<=30;i=i+5){
		
			String newReport="["+date2+", |recList|="+sizeRecList+"]";
			FactoryFile.getInstance().writeLineOnFile(pathReport, newReport);
			//Creo il modulo di valutazione
			Valutator eval = new Valutator(sizeRecList,listValFun , pathReport);
		
		//valuta tutte le funzioni di similarità 
			for(SimilarityFunction function : listaFunzioniSimilarità)
				eval.valutate(function);
		//}
	}

}
