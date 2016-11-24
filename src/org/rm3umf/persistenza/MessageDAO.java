package org.rm3umf.persistenza;

import java.util.List;

import org.rm3umf.domain.Message;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.User;


public interface MessageDAO {
	
	public void save(Message t) throws PersistenceException;
	
	public void delete() throws PersistenceException ;
	
	//public User doRetriveByUser(User);
	public List<Message> doRetrieveByUserIdAndDate(User user,Period period) throws PersistenceException;

	public List<Message> retriveByUser(User u) throws PersistenceException;
	
	
	
	

}
