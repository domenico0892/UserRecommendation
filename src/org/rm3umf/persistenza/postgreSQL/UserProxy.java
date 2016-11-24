package org.rm3umf.persistenza.postgreSQL;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.rm3umf.domain.User;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.persistenza.UserDAO;




public class UserProxy extends User{
	
	private Logger logger = Logger.getLogger("persistenza.postgreSQL.UserProxy");
	private boolean caricatoUsernames = false;
	private boolean caricatoFollowed=false;
	private boolean caricatoFollower=false;
	
	public List<String> getUsernames() { 
		if (!this.caricatoUsernames) {
			logger.info("ProxyUser called "+this.getIduser()+": getUsernames");
			UserDAO dao = new UserDAOpostgreSQL();
			try {
				this.setUsernames(dao.doRetriveUsernamesById(this.getIduser()));
				this.caricatoUsernames = true;
			}
			catch (PersistenceException e) {
				throw new RuntimeException(e.getMessage() + "");
			}
		}
		return super.getUsernames(); 
	}
	
	
//	public Set<Long> getFollowed() { 
//		if (!this.caricatoFollowed) {
//			logger.info("ProxyUser called "+this.getIduser()+": getFollowed()");
//			UserDAO dao = new UserDAOpostgreSQL();
//			try {
//				this.setFollowed(dao.retriveFollowedById(this.getIduser()));
//				this.caricatoFollowed = true;
//			}
//			catch (PersistenceException e) {
//				throw new RuntimeException(e.getMessage() + "");
//			}
//		}
//		return super.getFollowed(); 
//	}
//	
//	public Set<Long> getFollower() { 
//		if (!this.caricatoFollower) {
//			logger.info("ProxyUser called "+this.getIduser()+": getFollower()");
//			UserDAO dao = new UserDAOpostgreSQL();
//			try {
//				this.setFollower(dao.retriveFollowerById(this.getIduser()));
//				this.caricatoFollower = true;
//			}
//			catch (PersistenceException e) {
//				throw new RuntimeException(e.getMessage() + "");
//			}
//		}
//		return super.getFollower(); 
//	}

}
