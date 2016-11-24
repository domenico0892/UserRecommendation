package org.rm3umf.framework.buildmodel.extractor;

/**
 * Questa classe singleton serve per creare la StrategyExtraction che non verrà sabilita in base ad un file
 * di configurazione.
 * 
 * @author giulz
 *
 */

public class FactoryStrategyExtraction {
	
	private static FactoryStrategyExtraction instance;
	//strategia di estrazione
	private StrategyExtraction strategy;
	
	
	
	
	private FactoryStrategyExtraction(){
		//questa dovra leggere che tipo di estractor utilizzare da un file xml
		this.strategy=new ExtractorHashtag();
		//dovrà leggere se esplorare le risorse da un file xml
		this.strategy.exploreResource(false);
		
		
	}
	
	public static FactoryStrategyExtraction getInstance(){
		if(instance==null)
			instance=new FactoryStrategyExtraction();
		return instance;
	}
	
	public StrategyExtraction getStrategyExtraction(){
		return this.strategy;
	}
	

}
