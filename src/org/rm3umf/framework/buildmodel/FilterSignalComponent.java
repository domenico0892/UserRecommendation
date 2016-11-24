package org.rm3umf.framework.buildmodel;

import java.util.List;

import org.rm3umf.domain.Concept;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;

import twitter4j.internal.logging.Logger;

/**
 * Fase 2 :  Eliminiomo le compoenti che si riferiscono    
 *  
 * Questa filtro individua e cancella le signalComponent che si riferiscono ad un concept
 * referenziato una sola volta. Questo perchè essendo stato utilizzato una sola volta
 * potrebbe essere un concep errato o comunque non influente.
 * 
 * @author giulz
 *
 */

public class FilterSignalComponent {
	
	private static Logger logger = Logger.getLogger(FilterSignalComponent.class);
	
	
	public void filter(int THRESHOLD) throws PersistenceException{
		logger.info("avviato filtroSignalComponent");
		//recupero tutti i concept inrilevanti
		List<Concept> listaConcept = AAFacadePersistence.getInstance().conceptRetriveInrilevante(THRESHOLD);
		
		for(Concept concept:listaConcept){
			logger.info("cancello tutti i signalCompoent relativi al concept : "+concept);
			AAFacadePersistence.getInstance().signalComponentDeleteByConcept(concept);
			logger.info("cancello il concept : "+concept);
			AAFacadePersistence.getInstance().conceptDelete(concept);
		}
		logger.info("fine filtroSignalComponent");
		
		
		
	}

}
