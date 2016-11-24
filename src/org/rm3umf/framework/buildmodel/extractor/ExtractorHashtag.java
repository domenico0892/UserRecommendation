package org.rm3umf.framework.buildmodel.extractor;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.log4j.Logger;
import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.PseudoFragment;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.domain.User;
import org.rm3umf.framework.buildmodel.BuildModelException;
import org.rm3umf.persistenza.PersistenceException;


import util.MD5;


public  class ExtractorHashtag implements StrategyExtraction{

	private static final Logger logger = Logger.getLogger("filtri.filter2ExtractConcept.estractor.ExtractorHashtag");
	
	private String regex = "#[\\w]+";
	
	
	public void exploreResource(boolean exploreResource){
		
	}
	
	public List<SignalComponent> extract(PseudoFragment pseudoDocument) throws ExtractorException {
		List<Concept> concept2user = null;
		Map<String,SignalComponent> map2tf=new HashMap<String, SignalComponent>();
		Period period = pseudoDocument.getPeriod();
		User user = pseudoDocument.getUser();
		
		//interrogo il db per ottenere lo pseudoDocument
		concept2user = getHashTag(pseudoDocument);
		
		 //il massimo relativo per calcolare tf
		int max=0;
		SignalComponent signalComp=null;

		String idConcept="";

		

		for(Concept concept:concept2user){
			idConcept=concept.getId();
			signalComp=map2tf.get(idConcept);
			int occurence;
			if(signalComp==null){
				signalComp=new SignalComponent();
				signalComp.setConcept(concept); //setto il concept
				signalComp.setPeriod(period);   //setto il periodo
				signalComp.setOccorence(0);//
				//setto l'utente
				signalComp.setUser(user);
				map2tf.put(concept.getId(),signalComp);
			}
			//incrementa occurence
			occurence=signalComp.getOccorence()+1;
			signalComp.setOccorence(occurence);
			//memorizzo il massimo
			if(max<occurence)
				max=occurence;
		}
		List<SignalComponent> listSigComp = new LinkedList<SignalComponent>(); 
		//calcola df
		for(String key:map2tf.keySet()){
			
			signalComp=map2tf.get(key);
			double tf=signalComp.getOccorence()/(double)max; 
			//ho fatto un cast
			signalComp.setTf(tf);
			//System.out.println(signalComp);
			//aggiunge sig comp alla lista 
			listSigComp.add(signalComp);
			
			
			
		}
		

		return listSigComp;
		
}
	
	
	private List<Concept> getHashTag(PseudoFragment pseudo) throws ExtractorException{
		List<Concept> listaHashtag=new LinkedList<Concept>();
		
		List<Message> listMessage=pseudo.getMessages();
		
		for(Message message:listMessage){
		//String regex = "\\s+https?://(www)?\\.?[\\w]+\\.[a-z]{2,3}\\s";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(message.getText());
		
		while (matcher.find()){
			Concept c=new Concept();
			//setto name trasformandolo in minuscolo
			String name=matcher.group().toLowerCase();
			//setto l'id
			String id="";
			try{
			id =MD5.getInstance().hashData(name);
			}catch(NoSuchAlgorithmException e){
				logger.info("errore durante il calcolo dell'id del concept");
				throw new ExtractorException(e.getMessage());
			}
			c.setId(id);
			c.setNameConcept(name);
			listaHashtag.add(c); 
		}
	}
		//pero ogni hashtag treo
		return listaHashtag;
		
	}
	
	
	
	
	

}
