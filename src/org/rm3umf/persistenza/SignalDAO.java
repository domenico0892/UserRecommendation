package org.rm3umf.persistenza;

import java.util.List;

import org.rm3umf.domain.Signal;
import org.rm3umf.domain.User;


public interface SignalDAO {
	
	public void save(Signal signal) throws PersistenceException ;
	
	public void deleteAll() throws PersistenceException ;
	
	public void delete(Signal signal) throws PersistenceException ;
	
	public List<Long> retriveUserid() throws PersistenceException;
	
	public List<Signal> doRetrieveByUser(User user) throws PersistenceException ;

}
