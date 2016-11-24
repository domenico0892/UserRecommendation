package org.rm3umf.persistenza.postgreSQL;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.User;
import org.rm3umf.persistenza.ConceptDAO;
import org.rm3umf.persistenza.PersistenceException;

public class SignalGlobalDAOpostgreSQL {


	public void save(Signal signal) throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();

		PreparedStatement statement = null;
		try {			
			String insert = "insert into signalglobal(conceptid,userid,signal) values (?,?,?)";
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

			statement.setArray(3,connection.createArrayOf("numeric",array)); 
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
			String retrieve = "SELECT distinct( userid ) " +
					          "FROM signal ";
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
