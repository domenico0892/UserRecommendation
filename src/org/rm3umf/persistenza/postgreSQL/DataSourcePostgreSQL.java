package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.rm3umf.persistenza.*;

public class DataSourcePostgreSQL implements DataSource{
	private static DataSourcePostgreSQL instance = null;
	private BasicDataSource ds;

	private DataSourcePostgreSQL() {
		ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUsername("root");
		ds.setPassword("ai-lab");
		ds.setUrl("jdbc:mysql://localhost:3306/new_news_recommendation?useSSL=false&serverTimezone=UTC");
		ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
	}

	public static DataSourcePostgreSQL getInstance() {
		if(instance == null) {
			instance = new DataSourcePostgreSQL();
		}
		return instance;
	}

	public Connection getConnection() throws PersistenceException {
		try {
//			System.out.println("Num active: " + this.ds.getNumActive() + "Num idle: " + this.ds.getNumIdle());
			return this.ds.getConnection();
		} 
		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
	}

}