package org.rm3umf.framework.importing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.rm3umf.persistenza.DataSource;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.persistenza.postgreSQL.DataSourcePostgreSQL;

import com.mongodb.MongoClient;

public class MongoConnection {

	private static MongoConnection instance = null;
	private MongoClient client;

	private MongoConnection() {
		this.client = null;
	}

	public static MongoConnection getInstance() {
		if(instance == null) {
			instance = new MongoConnection();
		}
		return instance;
	}

	public MongoClient getConnection() {
		if (this.client == null)
			this.client = new MongoClient();
		return this.client;
	}
}