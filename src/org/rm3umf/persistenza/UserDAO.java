package org.rm3umf.persistenza;

import java.util.List;
import java.util.Set;

import org.rm3umf.domain.User;


public interface UserDAO {
	
	//metodo per recuperare l'utente per id
	public User doRetriveUserById(long id)throws PersistenceException;
	
	//
	public void save(User user) throws PersistenceException;
	
	//metodo per cancellare tutte le righe della tabella user
	public void delete() throws PersistenceException;
	
	//recupera tutti gli utenti del DB
	public List<User> doRetrieveAll() throws PersistenceException ;

	public List<String> doRetriveUsernamesById(long id) throws PersistenceException; 
	
	public void saveFollowed(long userid,long followed) throws PersistenceException ;
	
	public Set<Long> retriveFollowedById(long id) throws PersistenceException ;
	
	public void deleteFollowed() throws PersistenceException;
	
	public List<User> retrieveOnlyUserWithFrieds() throws PersistenceException;

	public void saveFollower(long userid, long l) throws PersistenceException;

	public Set<Long> retriveFollowerById(long iduser) throws PersistenceException;

	public void deleteFollower() throws PersistenceException;
	
	

}
