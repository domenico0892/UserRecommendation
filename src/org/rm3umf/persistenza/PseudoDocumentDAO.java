package org.rm3umf.persistenza;

import java.util.List;

import org.rm3umf.domain.Period;
import org.rm3umf.domain.PseudoFragment;
import org.rm3umf.domain.User;



public interface PseudoDocumentDAO {
	
	public PseudoFragment doRetrieve(User user,Period period) throws PersistenceException;
	
	public List<PseudoFragment> doRetriveByPeriod(Period period) throws PersistenceException;
	
	
	
	
}
