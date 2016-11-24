package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.rm3umf.persistenza.DataSource;
import org.rm3umf.persistenza.PersistenceException;


public class IdBrokerPg {

	public static int newId() throws PersistenceException {
		int newId = -1;
		DataSource ds = DataSourcePostgreSQL.getInstance();
		ResultSet result = null;
		PreparedStatement statement = null;
		Connection connection = null;
        Logger logger = Logger.getLogger("persistence.postgres.IdBroker");
		try {
			connection = ds.getConnection();
			// Attenzione SQL non standard: questo uso delle sequenze 
			//          e' valido sicuramente per Postgres and Oracle
			// codice SQL per creare la sequenza:
			//
			//CREATE SEQUENCE public.sequenza_id 
			//  START WITH 1 INCREMENT BY 1
			//  MINVALUE 0
			//  MAXVALUE 9999999
			//  NO CYCLE;
			//
			String sqlQuery = "SELECT nextval('sequenza_id') AS newId";  
			statement = connection.prepareStatement(sqlQuery);
			result = statement.executeQuery();
			if (result.next()) {
				newId = result.getInt("newId");
				logger.info("new id created: " + newId);
			}
			else {
				logger.severe("problems in creating id: " + newId);
				throw new PersistenceException("invalid id");
			}
			} catch(SQLException e) {
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
		return newId;		
	}
}
