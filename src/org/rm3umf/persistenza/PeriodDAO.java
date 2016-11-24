package org.rm3umf.persistenza;

import java.util.List;

import org.rm3umf.domain.Period;

public interface PeriodDAO {
	
	public String getMaxDate() throws PersistenceException;
	
	public String getMinDate() throws PersistenceException;
	
	public void save(Period period) throws PersistenceException;
	
	public void delete() throws PersistenceException;
	
	public List<Period> doRetriveAll() throws PersistenceException;
	
	public Period doRetriveById(int id) throws PersistenceException;

	public String retriveDataFine(int idPeriodo) throws PersistenceException;
	
	public String retriveDataInizio(int idPeriodo) throws PersistenceException;
	
		

}
