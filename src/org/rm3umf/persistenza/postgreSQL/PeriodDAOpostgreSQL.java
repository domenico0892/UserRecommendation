package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.rm3umf.domain.Period;
import org.rm3umf.persistenza.PeriodDAO;
import org.rm3umf.persistenza.PersistenceException;





public class PeriodDAOpostgreSQL implements PeriodDAO{

	private static final Logger logger=Logger.getLogger("persistenza.postgreSQL.PeriodDAOpostgreSQL");

	public String getMaxDate() throws PersistenceException {
		String maxDate=null;
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "SELECT max(date) from message";
			statement = connection.prepareStatement(retrieve);
			result = statement.executeQuery();
			if(result.next()){
			maxDate =result.getDate(1).toString();
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
		return maxDate;
	}

	@Override
	public String getMinDate() throws PersistenceException {
		String minDate=null;
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "SELECT min(date) from message";
			statement = connection.prepareStatement(retrieve);
			result = statement.executeQuery();
			if(result.next()){
				minDate =result.getDate(1).toString();
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
		return minDate;
	}


	public void save(Period period) throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {			
			String insert = "insert into period (id,startdate,enddate) values (?,?,?)";
			statement = connection.prepareStatement(insert);
			statement.setInt(1, period.getIdPeriodo());
			statement.setDate(2,Date.valueOf(period.getDataInizioPeriodo()));
			statement.setDate(3, Date.valueOf(period.getDataFinePeriodo()));
			statement.executeUpdate();			
		}
		catch (SQLException e) {
			logger.error("errore durante il salvataggio del periodo :"+period);
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
		logger.info("periodo salvato:"+period);
		
	}
	
	public void delete() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from period ";
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
	
	public List<Period> doRetriveAll() throws PersistenceException{
		Period period = null;
		List<Period> listaPeriodi = new ArrayList<Period>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select id,startdate,enddate from period";
			statement = connection.prepareStatement(retrieve);
			result = statement.executeQuery();
			while (result.next()) {
				period = new Period();
				period.setIdPeriodo(result.getInt("id"));
				period.setDataInizioPeriodo(result.getDate("startdate").toString());
				period.setDataFinePeriodo(result.getDate("enddate").toString());
				listaPeriodi.add(period);
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
		return listaPeriodi;
		
	}
	
	
	public Period doRetriveById(int id) throws PersistenceException{
		Period period = null;
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select * from period where id=?";
			statement = connection.prepareStatement(retrieve);
			statement.setInt(1, id);
			result = statement.executeQuery();
			if (result.next()) {
				period = new Period();
				period.setIdPeriodo(result.getInt("id"));
				period.setDataInizioPeriodo(result.getDate("startdate").toString());
				period.setDataFinePeriodo(result.getDate("enddate").toString());
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
		return period;
	}

	@Override
	public String retriveDataFine(int idPeriodo) throws PersistenceException {
			String dataFine="";
			DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
			Connection connection = null;
			PreparedStatement statement = null;
			ResultSet result = null;
			try {
				connection = ds.getConnection();
				String retrieve = "select enddate from period where id=?";
				statement = connection.prepareStatement(retrieve);
				statement.setInt(1, idPeriodo);
				result = statement.executeQuery();
				if (result.next()) {
					dataFine=result.getDate(1).toString();
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
			return dataFine;
		}
	

	@Override
	public String retriveDataInizio(int idPeriodo) throws PersistenceException {
		String dataInizio="";
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select startdate from period where id=?";
			statement = connection.prepareStatement(retrieve);
			statement.setInt(1, idPeriodo);
			result = statement.executeQuery();
			if (result.next()) {
				dataInizio=result.getDate(1).toString();
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
		return dataInizio;
	
	}

}
