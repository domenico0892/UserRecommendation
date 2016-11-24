package org.rm3umf.framework.buildmodel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.domain.SignalGlobal;
import org.rm3umf.domain.User;
import org.rm3umf.framework.eval.similarity.VectorScalarProd;
import org.rm3umf.math.VectorUtil;
import org.rm3umf.math.WaveletUtil;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;

public class BuiltSignalGlobal {private int lenghtSignal;

private int SOGLIASEGNALI;


public BuiltSignalGlobal(int lenghtSignal,int sogliasegnali){
			
	this.lenghtSignal=lenghtSignal;
	this.SOGLIASEGNALI=sogliasegnali;
}

public static void buildSignal() throws PersistenceException{

	double maxValue=0.;
	double tot = 0.;
	List<Concept> listaConcepts = AAFacadePersistence.getInstance().conceptRetrieveAll();

	List<Period> periods= AAFacadePersistence.getInstance().periodRetriveAll();
	List<SignalGlobal> listaSegnaliGlobali= new LinkedList<SignalGlobal>(); 


	for(Concept concept:listaConcepts){
		System.out.println(concept.getNameConcept());


		//recupero tutti i signal component relativi al concept concept
		List<SignalComponent> signalComponents = AAFacadePersistence.getInstance().signalComponentRetriveByConcept(concept);

		SignalGlobal sigGlob = new SignalGlobal();
		double[] globalSignal = new double[periods.size()];
		sigGlob.setConcept(concept);

		//Scandisco tutti i signal component 
		for(SignalComponent sigComp:signalComponents){

			int idPeriod=sigComp.getPeriod().getIdPeriodo();
			globalSignal[idPeriod]=globalSignal[idPeriod]+sigComp.getOccorence();

		}

		int compNotNull=0;
		for(double comp:globalSignal){
			if(comp>0){
				compNotNull++;
			}
		}

		if(compNotNull>3){	
			sigGlob.setGlobalSignal(globalSignal);
			listaSegnaliGlobali.add(sigGlob);
			System.out.println(VectorUtil.getInstance().getString(globalSignal));
		}

	}try{
		FileOutputStream file = new FileOutputStream("globalSignal.txt");
		PrintStream Output = new PrintStream(file);
		//voglio vedere i segnali globali più simili
		for(SignalGlobal signalGlobal:listaSegnaliGlobali){
			System.out.println(signalGlobal.getConcept().getNameConcept());
			Output.println(signalGlobal.getConcept().getNameConcept()+"-"+VectorUtil.getInstance().getString(signalGlobal.getGlobalSignal()));
			//		for(SignalGlobal signalGlobal1:listaSegnaliGlobali){
			//			double correlation = signalSimilarity(signalGlobal.getGlobalSignal(), signalGlobal1.getGlobalSignal());
			//			if((correlation>=0.9) && !(signalGlobal.getConcept().equals(signalGlobal1.getConcept()))  ){
			//					System.out.println("           ("+correlation+")"+signalGlobal1.getConcept().getNameConcept()+" ");
			//					Output.println("      ("+correlation+")"+signalGlobal1.getConcept().getNameConcept()+" ");
			//			}
			//		}
		}
	}catch(IOException e){
		System.out.println("erroreeee");
	}

}


public static double signalSimilarity(double[] signal1,double[] signal2){
	double res =0.;
	//trasformo i segnali
	double[] sig1Trasf=WaveletUtil.getInstance().trasforma(signal1);
	double[] sig2Trasf=WaveletUtil.getInstance().trasforma(signal2);
	
	double num = VectorUtil.getInstance().vectorScalarProd(sig1Trasf, sig2Trasf);
	
	double cff=VectorUtil.getInstance().vectorScalarProd(sig1Trasf,sig1Trasf);
	double cgg=VectorUtil.getInstance().vectorScalarProd(sig2Trasf,sig2Trasf);
	double den=Math.sqrt(cff*cgg);
	
	res=num/den;
	return res;
	
}
	
	
	




public static void  main(String[] args) throws PersistenceException{
	buildSignal();
	
}
	
	

}
