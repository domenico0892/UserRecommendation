package org.rm3umf.persistenza;

import org.rm3umf.domain.Resource;

public interface ResourceDAO {
	
	public void save(Resource resource) throws PersistenceException;
	
	public Resource doRetrieveById(String id) throws PersistenceException;
	
	public void delete() throws PersistenceException;
	
	public void deleteInstance() throws PersistenceException;

	public void saveInstance(Long idMessage, Resource res) throws PersistenceException;
}
