package org.rm3umf.domain;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.rm3umf.framework.buildmodel.WeighingScheme;
import org.rm3umf.framework.buildmodel.extractor.ExtractorException;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;

public class Period {
	
	private static Logger logger = Logger.getLogger(Period.class);
	
	private int idPeriodo;
	private String dataInizioPeriodo;
	private String dataFinePeriodo;
	
	//lista di pseudoDocument per period
//	private List<PseudoFragment> listaPseudo;
//	private WeighingScheme weighingScheme;
//	private Map<String,SignalComponent> 
	
	public Period(int idPeriodo,String dataInizioPerio,String dataFinePeriodo){
//		this.weighingScheme=new WeighingScheme();
		
		this.dataInizioPeriodo=dataInizioPerio;
		this.dataFinePeriodo=dataFinePeriodo;
		this.idPeriodo=idPeriodo;	
	}
	
	
	
	public Period(){
//		this.weighingScheme=new WeighingScheme();
	
	}
	
	
//	/**
//	 * Questo metodo analizza un period costruendo tutti gli PseudoDocument relativi al periodo estraendo
//	 * i SignalComponent. Inoltre utilizza lo Weighting Scheme per  la pesatura
//	 * 
//	 * @throws ExtractorException
//	 * @throws PersistenceException 
//	 */
//	public void extractSignalComponent() throws ExtractorException, PersistenceException{
//		logger.info("creo i signal component");
//		this.listaPseudo=AAFacadePersistence.getInstance().pseudoDocumentGetByPeriod(this);
//		//memorizzerà i signalComp da passare alla classe WeighingScheme 
//		
//		List<SignalComponent> periodSigComp = new LinkedList<SignalComponent>(); 
//		
//		List<SignalComponent> sigCompForPseudo=null;
//		for(PseudoFragment pseudo:this.listaPseudo){
//			logger.info("analizzo pseudoDocument : "+pseudo);
//			//estraggo dallo pseudo-document i signalComponent presenti
//			sigCompForPseudo=pseudo.extractSignalComponent();
//		//	System.out.println(this.weighingScheme);
//			if(sigCompForPseudo.size()>0){
//				this.weighingScheme.setTF(sigCompForPseudo); //analizzo gli pseudoDocument   
//				periodSigComp.addAll(sigCompForPseudo);
//			}
//			//salvo tutti i signalCom
//		}
//		//setto idf
//		this.weighingScheme.setIDF(periodSigComp); 
//		
//		//Salva tutti....potrei farli ritornare
//		for(SignalComponent sigComp:periodSigComp){
//			System.out.println("salvo :"+sigComp);
//			AAFacadePersistence.getInstance().signalComponentSave(sigComp);
//		}
//	}
	
	
	
	
	public void setIdPeriodo(int idPeriodo){
		this.idPeriodo=idPeriodo;
	}
	
	public int getIdPeriodo(){
		return this.idPeriodo;
	}
	
	public String getDataInizioPeriodo() {
		return dataInizioPeriodo;
	}
	
	public void setDataInizioPeriodo(String dataInizioPeriodo) {
		this.dataInizioPeriodo = dataInizioPeriodo;
	}
	
	public String getDataFinePeriodo() {
		return dataFinePeriodo;
	}
	
	public void setDataFinePeriodo(String dataFinePeriodo) {
		this.dataFinePeriodo = dataFinePeriodo;
	}
	
	/*PERSISTENZA*/
//	public void retrievePseudoDocument() throws PersistenceException{
//		this.listaPseudo=AAFacadePersistence.getInstance().pseudoDocumentGetByPeriod(this);
//	}
	
//	public void save() throws PersistenceException{
//		AAFacadePersistence.getInstance().periodSave(this);
//	}
	
	
	
	public String toString(){
		return "[PERIODO ("+this.idPeriodo+"): "+this.dataInizioPeriodo+" : "+this.dataFinePeriodo+"]";
	}
  
}