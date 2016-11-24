package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.domain.User;
import org.rm3umf.persistenza.DataSource;
import org.rm3umf.persistenza.PeriodDAO;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.persistenza.SignalComponentDAO;





public class SignalComponentDAOpostgreSQL implements SignalComponentDAO{

	
	public void save(SignalComponent sigComp) throws PersistenceException {
		DataSource ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		if (this.doRetrieveByKey(sigComp.getUser(),sigComp.getPeriod(),sigComp.getConcept()) !=  null) {
			this.doUpdate(sigComp, connection);
		}
		else
			this.doInsert(sigComp, connection);
	}

	private void doInsert(SignalComponent sigComp, Connection connection) throws PersistenceException {
		PreparedStatement statement = null;
		try {			
			String insert = "INSERT INTO signalcomponent(userid,conceptid,periodid,tf,idf,tfidf,value) VALUES (?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(insert);
			statement.setLong(1, sigComp.getUser().getIduser());
			statement.setString(2,sigComp.getConcept().getId());
			statement.setInt(3,sigComp.getPeriod().getIdPeriodo());
			statement.setDouble(4,sigComp.getTf());
			statement.setDouble(5,sigComp.getIdf());
			statement.setDouble(6, sigComp.getTfidf());
			statement.setInt(7,sigComp.getOccorence());
			statement.executeUpdate();
		}
		catch (SQLException e) {
			if (!e.getMessage().startsWith("'NaN' is not a valid numeric or approximate numeric value"))
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
	
	

	private void doUpdate(SignalComponent sigComp, Connection connection) throws PersistenceException {
		PreparedStatement statement = null;
		try {
			String update = "UPDATE signalcomponent SET tf=?,idf=?,tfidf=?,value=? " +
					"WHERE userid=? AND periodid=? AND conceptid=?";
			statement = connection.prepareStatement(update);
			statement.setDouble(1, sigComp.getTf());
			statement.setDouble(2, sigComp.getIdf());
			statement.setDouble(3, sigComp.getTfidf());
			statement.setInt(4,sigComp.getOccorence());
			statement.setLong(5, sigComp.getUser().getIduser());
			statement.setInt(6, sigComp.getPeriod().getIdPeriodo());
			statement.setString(7, sigComp.getConcept().getId());
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
	 * Questo metodo restituisce tutti i SignalComponent presenti all'interno del periodo
	 * identificato da periodid
	 */
	
	public List<SignalComponent> doRetrieveByPeriodid(int periodid) throws PersistenceException {
		List<SignalComponent> listaSignalComp = new ArrayList<SignalComponent>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "SELECT  userid , conceptid " +
					          "FROM signalcomponent " +
					          "WHERE periodid=? ";
			statement = connection.prepareStatement(retrieve);
			statement.setInt(1, periodid);
			result = statement.executeQuery();
			while (result.next()) {
				SignalComponent signalComp =new SignalComponent();
				//USER
				User user = new UserProxy();
				user.setIduser(result.getInt(1));//userid
				signalComp.setUser(user);
				//PERIOD
				Period period = new PeriodProxy();
				period.setIdPeriodo(periodid);
				//CONCEPT
				Concept c=new ConceptProxy();
				c.setId(result.getString(2)); //recupero il conceptid
				signalComp.setConcept(c);
				
				//aggiungo il signalComp alla lista
				listaSignalComp.add(signalComp);
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
		return listaSignalComp;
	}
	
	
	public List<SignalComponent> doRetrieveByUserid(long userid) throws PersistenceException {
		List<SignalComponent> listaSignalComp = new ArrayList<SignalComponent>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select idf,tf,value,periodid,conceptid from signalcomponent where userid=?";

			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, userid);
			result = statement.executeQuery();
			while (result.next()) {
				SignalComponent signalComponent =new SignalComponent();
				signalComponent.setIdf(result.getFloat(1));
				signalComponent.setTf(result.getFloat(2));
				signalComponent.setOccorence(result.getInt(3));
				
				//Periodo
				int idPeriod=result.getInt(4);
				Period period=new PeriodProxy();
				period.setIdPeriodo(idPeriod);
				signalComponent.setPeriod(period);
				//Concept
				String idConcept=result.getString(5);
				Concept concept = new ConceptProxy();
				concept.setId(idConcept);
				signalComponent.setConcept(concept);
				
				
				listaSignalComp.add(signalComponent);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
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
		return listaSignalComp;
	}
	
	public List<SignalComponent>  doRetrieveByConceptid(String conceptid) throws PersistenceException {
		List<SignalComponent> listaSignalComp = new ArrayList<SignalComponent>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "SELECT idf,tf,periodid,conceptid,value " +
					          "FROM signalcomponent " +
					          "WHERE conceptid=?";

			statement = connection.prepareStatement(retrieve);
			statement.setString(1, conceptid);
			result = statement.executeQuery();
			while (result.next()) {
				
				SignalComponent signalComponent =new SignalComponent();
				signalComponent.setIdf(result.getFloat(1));
				signalComponent.setTf(result.getFloat(2));
				signalComponent.setOccorence(result.getInt(3));
				//Periodo
				int idPeriod=result.getInt(3);
				Period period=new PeriodProxy();
				period.setIdPeriodo(idPeriod);
				signalComponent.setPeriod(period);
				
				//Concept
				String idConcept=result.getString(4);
				Concept concept = new ConceptProxy();
				concept.setId(idConcept);
				signalComponent.setConcept(concept);
				
				
				listaSignalComp.add(signalComponent);
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
		return listaSignalComp;
	}
	
	
	/**
	 * Restituisce in base al user period e concept il singnalComponent identificato univocamente
	 * @param user
	 * @param period
	 * @param concept
	 * @return signalComponent - il signal componente identificato univocamente
	 * @throws PersistenceException
	 */
	public SignalComponent doRetrieveByKey(User user,Period period, Concept concept) throws PersistenceException {
		
		SignalComponent signalComponent=null;
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select idf,tf,periodid,value from signalcomponent where userid=? and conceptid=? and periodid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, user.getIduser());
			statement.setString(2, concept.getId());
			statement.setInt(3,period.getIdPeriodo() );
			result = statement.executeQuery();
			if(result.next()) {
				signalComponent =new SignalComponent();
				signalComponent.setPeriod(period);
				signalComponent.setConcept(concept);
				signalComponent.setUser(user);
				signalComponent.setIdf(result.getFloat("idf"));
				signalComponent.setTf(result.getFloat("tf"));
				signalComponent.setOccorence(result.getInt("value"));
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
		return signalComponent;
	}
	
	/**
	 * DELETE
	 */

	/**
	 * Cancella un singolo SignalComponent 
	 */
	public void delete(SignalComponent signalComponent)	throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "DELETE FROM signalcomponent where userid=? and conceptid=? and periodid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, signalComponent.getUser().getIduser());
			statement.setString(2,signalComponent.getConcept().getId());
			statement.setInt(3,signalComponent.getPeriod().getIdPeriodo() );
			statement.executeUpdate();
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
	}

	public void deleteByConceptid(String conceptid)	throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "DELETE FROM signalcomponent " +
					"WHERE conceptid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setString(1,conceptid );
			statement.executeUpdate();
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
	}

	public void deleteAll() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from signalcomponent";
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
