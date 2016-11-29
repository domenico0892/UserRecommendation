package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


import org.apache.log4j.Logger;
import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.User;
import org.rm3umf.persistenza.ConceptDAO;
import org.rm3umf.persistenza.DataSource;
import org.rm3umf.persistenza.PersistenceException;


public class ConceptDAOpostgreSQL implements ConceptDAO{

	private Logger logger = Logger.getLogger(ConceptDAOpostgreSQL.class);

	public Concept doRetrieveById(String id) throws PersistenceException {
		Concept concept=null;
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select id,name,type from concept where id=?";
			statement = connection.prepareStatement(retrieve);
			statement.setString(1, id);
			result = statement.executeQuery();
			if(result.next()){
				concept = new Concept();
				concept.setId(result.getString(1));
				concept.setNameConcept(result.getString(2));
				//dovre mettere il type quando necessario
			}
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
		return concept;
	}
    
	/**
	 * Restituisce i Concept che sono stati referenziati da meno di una certa soglia
	 * dai SignalComponent
	 */ 
	public List<Concept> doRetrieveConceptInrilenvate(int soglia) throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		List<Concept> listConcept =new LinkedList<Concept>();
		try {
			connection = ds.getConnection();
			String retrieve = "SELECT id,name,type " +
					          "FROM concept " +
					          "WHERE numberinstance<?";
			statement = connection.prepareStatement(retrieve);
			statement.setInt(1, soglia);
			result = statement.executeQuery();
			while(result.next()){
				Concept concept = new Concept();
				concept.setId(result.getString(1));
				concept.setNameConcept(result.getString(2));
				//dovre mettere il type quando necessario
				listConcept.add(concept);	
			}
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
		return listConcept;
	}
	
	public String retrieveNameConcept(String id) throws PersistenceException {
		String nameConcept=null;
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "SELECT name FROM concept WHERE id=?";
			statement = connection.prepareStatement(retrieve);
			statement.setString(1, id);
			result = statement.executeQuery();
			if(result.next()){
				nameConcept=result.getString(1);
			}
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
		return nameConcept;
	}


	public void save(Concept concept) throws PersistenceException {
		DataSource ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		if (this.doRetrieveById(concept.getId()) !=  null) {
			//this.doUpdate(concept, connection); //incrementa il references
		}
		else
			this.doInsert(concept, connection);
	}

	private void doInsert(Concept concept, Connection connection) throws PersistenceException {
		PreparedStatement statement = null;
		try {			
			String insert = "insert into concept(id,name,type,numberinstance) values (?,?,?,?)";
			statement = connection.prepareStatement(insert);
			statement.setString(1, concept.getId());
			statement.setString(2, concept.getNameConcept());
			statement.setString(3, concept.getType());
			statement.setInt(4, 1);
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

	private void doUpdate(Concept concept, Connection connection) throws PersistenceException {
		PreparedStatement statement = null;
		try {
			String update = "UPDATE concept SET numberinstance=numberinstance+1 WHERE id=?";
			statement = connection.prepareStatement(update);
			statement.setString(1, concept.getId());
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
	 * 
	 */

	public List<Concept> getConceptForUserAndPeriod(User user,Period period) throws PersistenceException {
		List<Concept> conceptForPeriod=new LinkedList<Concept>();
		Concept concept=null;
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select c.conceptid " +
					"from instanceconcept  c , message  m " +
					"where m.id=c.messageid " +
					"and m.userid=?  " +
					"and m.date>=? and m.date<=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, user.getIduser());
			statement.setDate(2, Date.valueOf(period.getDataInizioPeriodo()));
			statement.setDate(3, Date.valueOf(period.getDataFinePeriodo()));
			result = statement.executeQuery();
			while(result.next()){
				concept = new ConceptProxy();
				concept.setId(result.getString(1));
				//concept.setNameConcept(result.getString(2));
				//dovre mettere il type quando necessario
				conceptForPeriod.add(concept);
			}
		}catch (SQLException e) {
			logger.error("errore durante il recupero dei concept");
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
		return conceptForPeriod;
	}
	
	/**
	 * Recipera tutti i concept presenti nel DB
	 * @return lista di tutti i concept presenti nel DB
	 * @throws PersistenceException
	 */
	
	public List<Concept> doRetrieveAll() throws PersistenceException {
		List<Concept> conceptForPeriod=new LinkedList<Concept>();
		Concept concept=null;
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select id,name,type " +
					           "from concept";
			statement = connection.prepareStatement(retrieve);
			
			result = statement.executeQuery();
			while(result.next()){
				concept = new Concept();
				concept.setId(result.getString(1));
				concept.setNameConcept(result.getString(2));
				concept.setType(result.getString(3));
				conceptForPeriod.add(concept);
			}
		}catch (SQLException e) {
			logger.error("errore durante il recupero dei concept");
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
		return conceptForPeriod;
	}

	public void delete(Concept concept) throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "DELETE FROM concept " +
					         "WHERE id=?";
			statement = connection.prepareStatement(update);
			statement.setString(1, concept.getId());
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

	public void deleteAll() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from concept";
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
