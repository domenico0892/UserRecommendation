package org.rm3umf.persistenza;

import java.util.List;

import org.rm3umf.domain.Signal;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.domain.User;


public interface SignalComponentDAO {
	
	/*SAVE*/
	public void save(SignalComponent sigComp) throws PersistenceException ;
	
	/*DELETE*/
	public void deleteAll()throws PersistenceException;
	
	public void delete(SignalComponent signalComponent)throws PersistenceException;
	
	public void deleteByConceptid(String conceptid) throws PersistenceException;
	
	/*RETRIVE*/
	public List<SignalComponent> doRetrieveByPeriodid(int periodid) throws PersistenceException;
	
	public List<SignalComponent> doRetrieveByUserid(long userid) throws PersistenceException;
	
	public List<SignalComponent> doRetrieveByConceptid(String conceptid) throws PersistenceException;
	

}
