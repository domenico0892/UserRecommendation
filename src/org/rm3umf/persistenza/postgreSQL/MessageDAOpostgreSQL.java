package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;


import org.apache.log4j.Logger;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.Resource;
import org.rm3umf.domain.User;
import org.rm3umf.persistenza.MessageDAO;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.persistenza.ResourceDAO;



public class MessageDAOpostgreSQL implements MessageDAO{
	
	private static final Logger logger=Logger.getLogger("persistenza.postgreSQL.MessageDAOpostgreSQL");
	
	/**
	 * Salvo il messaggio nella base di dati 
	 * 
	 * ATTENZIONE: devo fare la sostituzione con text.replaceAll(..) perche postgres da errore se riceve /u0000
	 *@param message  
	 */
	public void save (Message message) throws PersistenceException {
		
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection=null;
		PreparedStatement statement = null;
		try {
			connection = ds.getConnection();
			String insert = "insert into message(id,text,date,userid) values (?,?,?,?) ";
			statement = connection.prepareStatement(insert);
			statement.setLong(1, message.getIdMessage());
			statement.setString(2, message.getText().replaceAll("\u0000", "")); //POSTGRES NECESSITA DI QUESTA SOSTITUZIONE
			statement.setDate(3, Date.valueOf(message.getDate().substring(0,10)));
			statement.setLong(4, message.getUser().getIduser());
			statement.executeUpdate();
			//salvo anche le risorse associate al messaggio
//			List<Resource> resources = message.getResources();
//			for(Resource res : resources){
//				ResourceDAO resDAO= new ResourceDAOpostgreSQL();
//				resDAO.saveInstance(message.getIdMessage(),res);
//				
//			}
			
		}
		catch (SQLException e) {
			logger.error("errore durante il salvantaggio del messaggio "+message.getIdMessage());
			if (!e.getMessage().startsWith("Duplicate entry")) {
				throw new PersistenceException(e.getMessage());
			}
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
	
	public void delete() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from message ";
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
	
	public List<Message> doRetrieveByUserId(String userId) throws PersistenceException {
		Message tweet = null;
		List<Message> listaTweet = new ArrayList<Message>();
//		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
//		Connection connection = null;
//		PreparedStatement statement = null;
//		ResultSet result = null;
//		try {
//			connection = ds.getConnection();
//			String retrieve = "select id,date,user ?  from tweets";
//			statement = connection.prepareStatement(retrieve);
//			statement.setString(1, userId);
//			result = statement.executeQuery();
//			while (result.next()) {
//				tweet = new Tweet();
//				tweet.setId(result.getInt("id"));
//				tweet.setUser(result.getString("user"));
//				tweet.setMessaggio(result.getString("messaggio"));
//				//devo aggiungere il campo data
//				
//				listaTweet.add(tweet);
//			}
//		}
//		catch (SQLException e) {
//			throw new PersistenceException(e.getMessage());
//		}
//		finally {
//			try {
//				if (result != null)
//					result.close();
//				if (statement != null) 
//					statement.close();
//				if (connection!= null)
//					connection.close();
//			} catch (SQLException e) {
//				throw new PersistenceException(e.getMessage());
//			}
//		}
		return listaTweet;
	}
	
	
	/**
	 * 
	 * @param userId    id utente
	 * @param dataInizio  stringa che deve essre nel formato yyyy-mm-dd
	 * @param dataFine    stringa che deve essre nel formato yyyy-mm-dd
	 * @return tweets    ritorna la lista di tweets trovati
	 * @throws PersistenceException
	 */
	
	public List<Message> doRetrieveByUserIdAndDate(User user,Period period) throws PersistenceException {
		String dataInizio=period.getDataInizioPeriodo();
		String dataFine=period.getDataFinePeriodo();
		Message tweet = null;
		List<Message> listaTweet = new ArrayList<Message>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select id,text,date from message where date>=? and date<=? and userid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setDate(1, Date.valueOf(dataInizio));
			statement.setDate(2, Date.valueOf(dataFine));
			statement.setLong(3, user.getIduser());
			result = statement.executeQuery();
			while (result.next()) {
				tweet = new Message();
				tweet.setIdMessage(result.getLong(1));
				//oppure posso creare un UserProxy
				tweet.setUser(user);
				tweet.setText(result.getString(2));
				tweet.setDate(result.getDate(3).toString());
				listaTweet.add(tweet);
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
		return listaTweet;
	}
	
	
	public List<Message> retriveByUser(User u) throws PersistenceException {
		Message tweet = null;
		List<Message> listaTweet = new ArrayList<Message>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select id,date,text from message where  userid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, u.getIduser());
			result = statement.executeQuery();
			while (result.next()) {
				tweet = new Message();
				//tweet.setIdMessage(Long.toString(result.getLong("idtweet")));
				//tweet.setIdUser(result.getInt("user_dataset"));
			//	tweet.setDateTweet(result.getDate("date_msg").toString());
				tweet.setIdMessage(result.getLong(1));
				tweet.setDate(result.getDate(2).toString());
				tweet.setText(result.getString(3));
				listaTweet.add(tweet);
			}
		}
		catch (SQLException e) {
			logger.error("errore mentre sto recuperando i messaggi dell'utente"+u.getIduser());
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
		return listaTweet;
	}
	
	
	
	
}
