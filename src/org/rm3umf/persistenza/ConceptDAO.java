package org.rm3umf.persistenza;

import java.util.List;

import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.User;


public interface ConceptDAO {
	
	public void save(Concept concept) throws PersistenceException; 
	
	public Concept doRetrieveById(String id) throws PersistenceException;
	
	public List<Concept> doRetrieveConceptInrilenvate(int soglia) throws PersistenceException ;

	public void deleteAll() throws PersistenceException ;
	
	public void delete(Concept concept) throws PersistenceException ;
	
	public String retrieveNameConcept(String id) throws PersistenceException ;
	
	public List<Concept> getConceptForUserAndPeriod(User user,Period period) throws PersistenceException ;
	
	public List<Concept> doRetrieveAll() throws PersistenceException ;

	
	
}
