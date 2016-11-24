/*
 * @(#)SqlQueue.java	0.1 01/01/04
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */
package org.rm3umf.net.downloader;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 * Persistence implementation of <code>Queue</code> interface based on
 * data-base that can be managed through a jdbc driver. Duplicate downloads of
 * the same url are not allowed.
 * 
 * @author Fabio Gasparetti
 * @version 0.1, 01/01/06
 */

public class SqlQueue implements Queue {
	protected Connection connection;

	protected String dbname = null;

	// for ants system
	protected static String readLock = "LOCK TABLES urls READ;";

	protected static String writeLock = "LOCK TABLES urls WRITE;";

	protected static final String unlock = "UNLOCK TABLES;";

	protected static String urlsTable = "CREATE TABLE urls "
			+ "(id INT NOT NULL AUTO_INCREMENT, " + "url BLOB NOT NULL ,  "
			+ "PRIMARY KEY id (id), " + "KEY url_idx (url(64))) "
			+ "MAX_ROWS=100000000 AVG_ROW_LENGTH=100;";

	/* URL SQLs */
	protected static String getUrl = "SELECT url FROM urls WHERE url=?;";

	protected static String getUrls = "SELECT url FROM urls;";

	protected static String addUrl = "INSERT INTO urls (url) VALUES (?);";

	protected static String clearUrls = "DELETE FROM urls;";

	protected static String numUrls = "SELECT COUNT(*) FROM urls;";

	protected static String deleteUrl = "DELETE FROM urls WHERE url=?;";

	/**
	 * Initializes the link database.
	 * <p>
	 * Calls this method after having created a new <code>SqlQueue</code> object.
	 * <p>
	 * An example:
	 * <code>init("com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1/db_name?user=root", "jdbc:mysql://127.0.0.1/mysql?user=root", "db_name", true);</code>
	 * <p>
	 * where <code>db_name</code> is the name of the link db.   
	 * 
	 * @param driver
	 *            the name of the class of the db driver
	 * @param url
	 *            the url used to access the db
	 * @param creationUrl
	 *            the url used by the driver to create a new db
	 * @param dbname
	 *            the name of the db
	 * @param reset
	 *            if true the db will be erased
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void init(String driver, String url, String creationUrl,
			String dbname, boolean reset) throws ClassNotFoundException,
			SQLException {
		PreparedStatement stat;
		this.dbname = dbname;
		if ((driver == null) || (url == null)) {
			throw new IllegalArgumentException();
		}

		try {
			Class.forName(driver).newInstance();
		} catch (Exception ex) {
			// IllegalAccessException and InstantiationException exceptions
			throw new ClassNotFoundException("Unable to instantiate " + driver
					+ " class");
		}

		try {
			connection = DriverManager.getConnection(url);
		} catch (SQLException ex) {
			if (ex.getErrorCode() == 1049) { // unknown database
				reset = true;
			} else {
				throw ex;
			}
		}

		if (reset) {
			createDb(creationUrl);
		}
		stat = connection.prepareStatement("USE " + dbname + ";");
		stat.execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tabularium.net.downloader.Queue#clear()
	 */
	public void clear() throws QueueException {
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(writeLock);
			stat.execute();
			stat.close();
			stat = connection.prepareStatement(clearUrls);
			stat.executeUpdate();
			stat.close();
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("Sql exception: " + ex.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tabularium.net.downloader.Queue#size()
	 */
	public long size() throws QueueException {
		long n = -1;
		ResultSet rs = null;
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(readLock);
			stat.execute();
			stat.close();
			stat = connection.prepareStatement(numUrls);
			rs = stat.executeQuery();
			if (rs.next())
				n = rs.getLong(1);
			rs.close();
			stat.close();
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("Sql exception: " + ex.toString());
		}
		return n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tabularium.net.downloader.Queue#enqueue(String)
	 */
	public void enqueue(String url) throws QueueException {
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(writeLock);
			stat.execute();
			stat.close();
			if (!getUrl(url))
				addUrl(url);
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("Sql exception: " + ex.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tabularium.net.downloader.Queue#enqueue(java.util.Collection)
	 */
	public void enqueue(Collection urls) throws QueueException {
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(writeLock);
			stat.execute();
			stat.close();
			Iterator iter = urls.iterator();
			while (iter.hasNext()) {
				String u = (String) iter.next();
				if (!getUrl(u))
					addUrl(u);
			}
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("Sql exception: " + ex.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tabularium.net.downloader.Queue#get()
	 */
	public String get() throws QueueException {
		String url = null;
		ResultSet rs = null;
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(writeLock);
			stat.execute();
			stat.close();
			stat = connection.prepareStatement(getUrls);
			rs = stat.executeQuery();
			if (rs.next()) {
				// get first url from table
				url = rs.getString(1);
			}
			rs.close();
			stat.close();
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("Sql exception: " + ex.toString());
		}
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tabularium.net.downloader.Queue#get()
	 */
	public String pop() throws QueueException {
		String url = null;
		ResultSet rs = null;
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(writeLock);
			stat.execute();
			stat.close();
			stat = connection.prepareStatement(getUrls);
			rs = stat.executeQuery();
			if (rs.next()) {
				// get first url from table
				url = rs.getString(1);
				if (url != null) {
					remove(url);
				}
			}
			rs.close();
			stat.close();
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("Sql exception: " + ex.toString());
		}
		return url;
	}

	/*
	 * 
	 */
	protected void addUrl(String url) throws java.sql.SQLException {
		PreparedStatement stat;
		stat = connection.prepareStatement(addUrl);
		stat.setString(1, url);
		stat.execute();
		stat.close();
	}

	/*
	 * 
	 */
	protected boolean getUrl(String url) throws java.sql.SQLException {
		ResultSet rs;
		PreparedStatement stat;
		stat = connection.prepareStatement(getUrl);
		stat.setString(1, url);
		rs = stat.executeQuery();
		boolean ret = rs.next();
		stat.close();
		rs.close();
		return ret;
	}

	/*
	 * 
	 */
	protected void remove(String url) throws IOException {
		try {
			PreparedStatement stat;
			stat = connection.prepareStatement(deleteUrl);
			stat.setString(1, url);
			stat.executeUpdate();
			stat.close();
		} catch (Exception ex) {
			throw new IOException("Sql exception: " + ex.toString());
		}
	}

	/*
	 * 
	 */
	protected void createDb(String url) throws SQLException {
		connection = DriverManager.getConnection(url);

		PreparedStatement stat;
		stat = connection.prepareStatement("DROP DATABASE IF EXISTS " + dbname
				+ ";");
		stat.execute();
		stat.close();
		stat = connection.prepareStatement("CREATE DATABASE " + dbname + ";");
		stat.execute();
		stat.close();
		stat = connection.prepareStatement("USE " + dbname + ";");
		stat.execute();
		stat.close();
		stat = connection.prepareStatement(urlsTable);
		stat.execute();
		stat.close();

	}

	public static void main(String[] args) throws Exception {

		SqlQueue q = new SqlQueue();
		q.init("com.mysql.jdbc.Driver",
				"jdbc:mysql://127.0.0.1/queue?user=root",
				"jdbc:mysql://127.0.0.1/mysql?user=root", "queue", true);
		for (int i = 0; i < 1000000; i++) {
			System.out.println("i:" + i);
			System.out.print(q.size());
			q.enqueue("www.0.9.com");
			System.out.print(q.size());
			System.out.println(q.pop());
		}
		/*
		 * System.out.println(q.size()); q.enqueue("www.0.9.com");
		 * System.out.println(q.size()); q.enqueue("www.2.9.com");
		 * System.out.println(q.size()); q.enqueue("www.4.9.com");
		 * System.out.println(q.size()); System.out.println(q.get());
		 * System.out.println(q.size()); System.out.println(q.get());
		 * System.out.println(q.size()); System.out.println(q.get());
		 * System.out.println(q.size()); System.out.println(q.get());
		 * System.out.println(q.size());
		 */
	}

}
