package org.rm3umf.framework.importing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SocialEnricherFromDB {


	private Connection getConnection() throws DatasetException {
		String driver = "com.mysql.jdbc.Driver";
		String dbURI = "jdbc:mysql://localhost/twitter_db";
		String userName = "root";
		String password = "ai-lab";

		Connection connection;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(dbURI,userName, password);
		} catch (ClassNotFoundException e) {
			throw new DatasetException(e.getMessage());
		} catch(SQLException e) {
			throw new DatasetException(e.getMessage());
		}
		catch(Exception e) {
			throw new DatasetException(e.getMessage());
		}
		return connection;
	}

	public Set<Long> getFollower(long userid) {
		Set<Long> results = new HashSet<Long>();
		try {

			Connection conn = getConnection();
			String query = "SELECT id_utente FROM twitter_db.relationships WHERE id_utente_seguito = ?";
			PreparedStatement stat = conn.prepareStatement(query);
			stat.setLong(1, userid);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				results.add(rs.getLong(1));
			}
			conn.close();
		}
		catch (SQLException e) {
			System.err.println("SQL Exception");
		}
		catch (DatasetException e) {
			System.err.println("Dataset Exception");
		}
		return results;
	}

	public Set<Long> getFollowed(long userid) {
		Set<Long> results = new HashSet<Long>();
		try {
			Connection conn = getConnection();
			String query = "SELECT id_utente_seguito FROM twitter_db.relationships WHERE id_utente = ?";
			PreparedStatement stat = conn.prepareStatement(query);
			stat.setLong(1, userid);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				results.add(rs.getLong(1));
			}
			conn.close();
		}
		catch (SQLException e) {
			System.err.println("SQL Exception");
		}
		catch (DatasetException e) {
			System.err.println("Dataset Exception");
		}
		return results;
	}
}
