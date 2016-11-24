package org.rm3umf.persistenza;

import java.sql.Connection;

public interface DataSource {
	public Connection getConnection() throws PersistenceException;

}