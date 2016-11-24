package org.rm3umf.framework.importing;


import java.util.List;

import org.rm3umf.domain.Message;
import org.rm3umf.domain.User;

/**
 * Interfaccia che astrae il dataset in modo tale che per fare l'importing da un nuovo dataset 
 * basta implementare tale interfaccia
 * 
 * @author Giulz
 *
 */

public interface DatasetAdapter {
	
	public List<User> getUser() throws DatasetException;


	public List<Message> getMessagesByUser(User user) throws DatasetException;
	
	
	
	

}
