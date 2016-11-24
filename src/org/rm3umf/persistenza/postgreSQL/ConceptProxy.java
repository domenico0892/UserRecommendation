package org.rm3umf.persistenza.postgreSQL;

import java.util.List;

import org.apache.log4j.Logger;
import org.rm3umf.domain.Concept;
import org.rm3umf.persistenza.ConceptDAO;
import org.rm3umf.persistenza.PersistenceException;



public class ConceptProxy extends Concept{
	
	private Logger logger = Logger.getLogger("persistenza.postgreSQL.ConceptProxy");
	private boolean caricato = false;
	
	public String getNameConcept() { 
        
        logger.info("ConceptProxy called");
		if (!this.caricato) {
			ConceptDAO dao = new ConceptDAOpostgreSQL();
			try {
				this.setNameConcept(dao.retrieveNameConcept(this.getId()));
				this.caricato = true;
			}
			catch (PersistenceException e) {
				throw new RuntimeException(e.getMessage() + "");
			}
		}
		return super.getNameConcept(); 
	}

}
