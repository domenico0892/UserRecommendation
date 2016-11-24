package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import util.ArrayManager;
import util.Cronometro;

import java.security.interfaces.RSAKey;
import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.User;
import org.rm3umf.persistenza.ConceptDAO;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.persistenza.SignalDAO;

import com.mysql.jdbc.Blob;

public class SignalDAOpostgreSQL implements SignalDAO{

	private ConceptDAO conceptDAO=new ConceptDAOpostgreSQL();

	public void save(Signal signal) throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();

		PreparedStatement statement = null;
		try {			
			String insert = "insert into new_news_recommendation.signal(conceptid,userid,new_news_recommendation.signal.signal) values (?,?,?)";
			statement = connection.prepareStatement(insert);
			statement.setString(1, signal.getConcept().getId());
			statement.setLong(2, signal.getUser().getIduser());
			//statement.setBlob(3, signal);

			double[] arrayFloat=signal.getSignal();
			//Array array=connection.createArrayOf("numeric",arrayString);
			
			Double[] array=new Double[arrayFloat.length];
			//trasformo l'array di float in un array di Float
			for(int i=0;i<arrayFloat.length;i++){
				array[i]=Double.valueOf(arrayFloat[i]);
			}

//			statement.setArray(3,connection.createArrayOf("numeric",array));
			String s = ArrayManager.arrayToText(arrayFloat);
			statement.setString(3, s);
			statement.executeUpdate();		
		}
		catch (SQLException e) {
			if (e.getMessage().startsWith("Communications link failure")) {
				save(signal);
			}
			else
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
	 * Cancella la relazione signal
	 */

	public void deleteAll() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from signal ";
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

	

	public List<Signal> doRetrieveByUser(User user) throws PersistenceException {
		Signal signal=null;
		List<Signal> signalUser=new LinkedList<Signal>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select conceptid,new_news_recommendation.signal.signal from new_news_recommendation.signal where userid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, user.getIduser());
			result = statement.executeQuery();
			while (result.next()) {
				//System.out.println("creo oggetti signal:"+Cronometro.getInstance());
				signal = new Signal();
				//Setto il concept
				Concept concept=new ConceptProxy();
				concept.setId(result.getString(1));
				signal.setConcept(concept);
				//setto l'utente
				signal.setUser(user);
				
				//XXX vorrei trovare un modon po pi� efficiente ma non so come fare
				/*Array array=result.getArray(2);
				ResultSet rs=array.getResultSet();
				List<Float> list= new LinkedList<Float>(); 
				while(rs.next()){
					list.add(rs.getFloat(2));
				}
				double arrayFloat[]=new double[list.size()];
				
				for(int i=0; i<list.size();i++){
					arrayFloat[i]=list.get(i);
				}*/
//				byte arrayFloat[] = (byte[]) result.getObject(2);
//				DobuarrayFloat.toString();
				
				
				
				signal.setSignal(ArrayManager.textToArray(result.getString(2)));
				signalUser.add(signal);
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
		return signalUser;
	}

	@Override
	public void delete(Signal signal) throws PersistenceException {
		//Cronometro.getInstance().avanza();

		//Identifica il sengnale da cancellare
		long userid = signal.getUser().getIduser();
		String conceptid=signal.getConcept().getId();
		
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "DELETE FROM signal " +
				              "WHERE userid=? and conceptid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, userid);
			statement.setString(2, conceptid);
			statement.executeUpdate();
		}catch (SQLException e) {
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
	}

	
	public List<Long> retriveUserid() throws PersistenceException {
		List<Long> users=new LinkedList<Long>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "SELECT distinct(userid) " +
					          "FROM `signal`";
			statement = connection.prepareStatement(retrieve);
			result = statement.executeQuery();
			
			while (result.next()) {
				Long userid  = result.getLong(1);
				users.add(userid);
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
		return users;
	}


}
