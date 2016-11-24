package org.rm3umf.framework.importing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.User;

import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;


/**
 * Questa classe implementa il dataset di umap 2011 
 * @author Giulz
 *
 */

public class DatasetUmap implements DatasetAdapter{
	
	private String nameDataset;
	
	
	private static final Logger log = Logger.getLogger(DatasetUmap.class); 
	
	public List<User> getUser() throws DatasetException{
		log.info("recupero utenti dal Dataset");
		List<User> listaUser=new ArrayList<User>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			String retrieve = "select distinct userId  from tweets_sample";
			statement = connection.prepareStatement(retrieve);
			result = statement.executeQuery();
			while(result.next()){
				int userId=result.getInt(1);
				//effettuo un'altra query per recuperare gli username
				List<String> usernames=getUsernameByUserid(userId);
				log.info("recuperato dal dataset utente "+userId);
				User u= new User();
				u.setIduser(userId);
				//setto gli username
				u.setUsernames(usernames);
				//aggiungo l'utente alla lista
				listaUser.add(u);
				}
			
			log.info("fine recupero utenti da Dataset");
		}
		catch (SQLException e) {
			throw new DatasetException(e.getMessage());
		}catch (DatasetException e) {
			throw new DatasetException(e.getMessage());
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
				throw new DatasetException(e.getMessage());
			}
		}
		return listaUser;
	}


	public List<Message> getMessagesByUser(User user) throws DatasetException{
		log.info("recupero dal dataset i messaggi dell'utente "+user.getIduser());
		Message message = null;
		List<Message> listaMessages = new ArrayList<Message>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			String retrieve = "select id as idmessage,content,timeOfCrawl from tweets_sample where userId=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, user.getIduser());
			result = statement.executeQuery();
			while (result.next()) {
				message = new Message();
				message.setIdMessage(result.getLong(1));
				message.setText(result.getString(2));
				//devo aggiungere il campo data
				message.setDate(result.getDate(3).toString());
				message.setUser(user);
				listaMessages.add(message);
			}
		}
		catch (SQLException e) {
			log.error("errore durante il recupero dei messaggi dal dataset");
			throw new DatasetException(e.getMessage());
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
				throw new DatasetException(e.getMessage());
			}
		}
		return listaMessages;
	}

	
	public List<String> getUsernameByUserid(int userid) throws DatasetException{
		
		List<String> listaUsername=new ArrayList<String>();
		PreparedStatement statement = null;
		ResultSet result = null;
		Connection connection=null;
		try {
			connection=getConnection();
			String retrieve = "select distinct username from tweets_sample where userId=?";
			statement = connection.prepareStatement(retrieve);
			statement.setInt(1, userid);
			result = statement.executeQuery();
			while(result.next()){
				String username=result.getString(1);
				listaUsername.add(username);
			}
		}
		catch (SQLException e) {
			log.error("errore durante il recupero degli username dell'utente "+userid);
			throw new DatasetException(e.getMessage());
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
				throw new DatasetException(e.getMessage());
			}
		}
		return listaUsername;
	}



	/**
	 * Da la connessione verso il database da cui si voglio importare i dati
	 * @return
	 * @throws DatasetException
	 */
	private Connection getConnection() throws DatasetException {
//		String driver = "com.mysql.jdbc.Driver";
//		String dbURI = "jdbc:mysql://localhost/dataset";
//		String userName = "root";
//		String password = "mysql";
		String driver = "com.mysql.jdbc.Driver";
		String dbURI = "jdbc:mysql://localhost:3306/new_news_recommendation?useSSL=false&serverTimezone=UTC";
		String userName = "root";
		String password = "ai-lab";

		Connection connection;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(dbURI,userName, password);
		} catch (ClassNotFoundException e) {
			throw new DatasetException(e.getMessage());
		} catch(SQLException e) {
			throw new DatasetException(e.getMessage());
		}
		catch(Exception e) {
			throw new DatasetException(e.getMessage());
		}
		return connection;
	}


	

}
