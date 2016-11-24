 package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.rm3umf.domain.User;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.persistenza.UserDAO;



public class UserDAOpostgreSQL  implements UserDAO{
	
	private final static Logger logger=Logger.getLogger("persistenza.postgreSQL.UserDAOpostgreSQL");
	
	/**
	 * Metodo recupera utente  
	 * @param id
	 * @user 
	 */
	public User doRetriveUserById(long id) throws PersistenceException {
		User user=new UserProxy();
		user.setIduser(id);
		return user;
	}
	
	/**
	 * Recupera tutti gli utenti nel database
	 * @return users
	 */
	public List<User> doRetrieveAll() throws PersistenceException {
		User user = null;
		List<User> listaUser = new ArrayList<User>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select id from users";
			statement = connection.prepareStatement(retrieve);
			result = statement.executeQuery();
			while (result.next()) {
				user = new UserProxy();
				user.setIduser(result.getInt(1));
				listaUser.add(user);
			}
		}
		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return listaUser;
	}
	
	/**
	 * Questo metodo recupera gli usernames dell'utente identificato dall'id 
	 * @param id
	 * @return usernames
	 * @throws PersistenceException
	 */

	public List<String> doRetriveUsernamesById(long id) throws PersistenceException {
		List<String> usernames=new LinkedList<String>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select username from username where userid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, id);
			result = statement.executeQuery();
			while(result.next()){
				usernames.add(result.getString(1));
			}
		}
		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
			
		}
		finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return usernames;
	}
	
	
	
/**
 * Salva l'utete corrente nel DB
 * @param user
 * @throws PersistenceException
 */
	
public void save(User user) throws PersistenceException {
	Connection connection=null;
	logger.info("salvo utente :"+user.getIduser());
	long id=user.getIduser();
	List<String> listName=user.getUsernames();
	PreparedStatement statement = null;
	try {
		connection=DataSourcePostgreSQL.getInstance().getConnection();
		String insert = "insert into users(id) values (?)";
		statement = connection.prepareStatement(insert);
		statement.setLong(1, id);
		statement.executeUpdate();

		//effettuo una seconda query per inserire gli username dell'utente
		for(String name:listName){
			saveUsername(id,name);
		}
//		//memorizzo le relazioni dell'utente
//		for(Long followed:user.getFollowed())
//			saveFollowed(user.getIduser(), followed);
//		
//		for(Long follower:user.getFollower())
//			saveFollower(user.getIduser(), follower);
		
		
	}
	catch (SQLException e) {
		logger.error("errore durante il salvataggio degli utenti");
	}
	finally {
		try {
			if (statement != null) 
				statement.close();
			if (connection!= null)
				connection.close();
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
	}
}


/**
 * Metodo privato per salvare gli username dell'utente con id 	
 * @param iduser
 * @param username
 * @throws PersistenceException
 */
	
private void saveUsername(long iduser,String username) throws PersistenceException {
	logger.info("salvo gli username dell'utente :"+iduser);
	Connection connection=DataSourcePostgreSQL.getInstance().getConnection();
	PreparedStatement statement = null;
	try {			
		String insertUsername = "insert into username(userid,username) values (?,?)";
		statement = connection.prepareStatement(insertUsername);
		statement.setLong(1, iduser);
		statement.setString(2, username);
		statement.executeUpdate();	
	}
	catch (SQLException e) {
		logger.error("errore durante il salvataggio degli utenti");
		throw new PersistenceException(e.getMessage());
	}
	finally {
		try {
			if (statement != null) 
				statement.close();
			if (connection!= null)
				connection.close();
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
	}
}
	

	
	public void delete() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from users";
			statement = connection.prepareStatement(update);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		finally {
			try {
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}
	
	/**
	 * FOLLOWED
	 */
	
	public void saveFollowed(long userid,long followed) throws PersistenceException {
		Connection connection=null;
	
		PreparedStatement statement = null;
		try {
			connection=DataSourcePostgreSQL.getInstance().getConnection();
			String insert = "insert into followed(userid,followed) values (?,?)";
			statement = connection.prepareStatement(insert);
			statement.setLong(1, userid);
			statement.setLong(2, followed);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			logger.error("errore durante il salvataggio dei followed");
			e.printStackTrace();
			throw new PersistenceException(e.getMessage());
		}
//		finally {
//			try {
//				if (statement != null) 
//					statement.close();
//				if (connection!= null)
//					connection.close();
//			} catch (SQLException e) {
//				throw new PersistenceException(e.getMessage());
//			}
//		}
	}
	
	
	public Set<Long> retriveFollowedById(long id) throws PersistenceException {
		Set<Long> userFollowed=new HashSet<Long>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select followed from followed where userid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, id);
			result = statement.executeQuery();
			while(result.next()){
				userFollowed.add(result.getLong(1));
			}
		}
		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());	
		}
		finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return userFollowed;
	}
	
	public void deleteFollowed() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from followed";
			statement = connection.prepareStatement(update);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		finally {
			try {
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	/**
	 * E' un metodo momentaneo successivamente gli utenti di cui non posso recuperare i followed non voglio
	 * proprio che siano analizzati nel dataset.
	 */
	public List<User> retrieveOnlyUserWithFrieds() throws PersistenceException {
		User user = null;
		List<User> listaUser = new ArrayList<User>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select distinct userid from followed ";
			statement = connection.prepareStatement(retrieve);
			result = statement.executeQuery();
			while (result.next()) {
				user = new UserProxy();
				user.setIduser(result.getInt(1));
				listaUser.add(user);
			}
		}
		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return listaUser;
	}

	
	

	/**
	 * FOLLOWER
	 */
	public Set<Long> retriveFollowerById(long iduser) throws PersistenceException{
	Set<Long> userFollowed=new HashSet<Long>();
	DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet result = null;
	try {
		connection = ds.getConnection();
		String retrieve = "select follower from follower where userid=?";
		statement = connection.prepareStatement(retrieve);
		statement.setLong(1, iduser);
		result = statement.executeQuery();
		while(result.next()){
			userFollowed.add(result.getLong(1));
		}
	}
	catch (SQLException e) {
		throw new PersistenceException(e.getMessage());	
	}
	finally {
		try {
			if (result != null)
				result.close();
			if (statement != null) 
				statement.close();
			if (connection!= null)
				connection.close();
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
	}
	return userFollowed;
	}
	
	public void saveFollower(long userid, long follower) throws PersistenceException {
		Connection connection=null;
		PreparedStatement statement = null;
		try {
			connection=DataSourcePostgreSQL.getInstance().getConnection();
			String insert = "insert into follower(userid,follower) values (?,?)";
			statement = connection.prepareStatement(insert);
			statement.setLong(1, userid);
			statement.setLong(2, follower);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			logger.error("errore durante il salvataggio dei follower");
			e.printStackTrace();
			throw new PersistenceException(e.getMessage());
		}
//		finally {
//			try {
//				if (statement != null) 
//					statement.close();
//				if (connection!= null)
//					connection.close();
//			} catch (SQLException e) {
//				throw new PersistenceException(e.getMessage());
//			}
//		}
	}
	
	public void deleteFollower() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from follower";
			statement = connection.prepareStatement(update);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		finally {
			try {
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}
	

	
}