package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.rm3umf.persistenza.*;

public class DataSourcePostgreSQLOldOld implements DataSource{
	private static DataSourcePostgreSQL instance = null;
	private Connection connection;
	private static int MAX_TENTATIVI = 10;
	private static int MAX_CHIAMATE = 300;
	private int chiamate;
	//private DataSourcePostgreSQL() {}
	
	private DataSourcePostgreSQLOldOld() throws PersistenceException {
		this.connection = connect();
		this.chiamate = 0;
	}

	public static DataSourcePostgreSQL getInstance() throws PersistenceException {
		if(instance == null) {
			instance = new DataSourcePostgreSQL();
		}
		return instance;
	}
	
	public Connection getConnection() throws PersistenceException {
		try {
			if (this.connection.isClosed()) { 
				this.connection = connect();
				return this.connection;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.chiamate++;
		if (chiamate == MAX_CHIAMATE) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.connection = connect();
			this.chiamate = 0;
		}
		return this.connection;
	}
	
	public void close() throws SQLException {
		this.connection.close();
		instance = null;
	}
	
	private Connection connect() throws PersistenceException {
		String driver = "com.mysql.jdbc.Driver";
		String dbURI = "jdbc:mysql://localhost:3306/new_news_recommendation";
		String userName = "root";
		String password = "ai-lab";

		Connection connection = null;
		int i = 0;
		while (connection == null && i < MAX_TENTATIVI) {
			System.out.println("tentativo #" + i + " di connessione al DB");
			i++;
			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(dbURI,userName, password);
			} catch (ClassNotFoundException e) {
				throw new PersistenceException(e.getMessage());
			} catch(SQLException e) {
				connection = null;
				System.err.println(e.getMessage());
				//throw new PersistenceException(e.getMessage());
			}
			catch(Exception e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		if (i == MAX_TENTATIVI) {
			throw new PersistenceException("max tentativi raggiunto");
		}
		return connection;
	}

}