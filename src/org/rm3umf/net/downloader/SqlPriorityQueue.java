/*
 * @(#)SqlPriorityQueue.java	0.1 12/28/04
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */
package org.rm3umf.net.downloader;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Persistence implementation of <code>PriorityQueue</code> based on data-base
 * that can be managed through a jdbc driver. Duplicate downloads of the same
 * url are not allowed.
 * <p>
 * Urls with higher priorities will be served first.
 * 
 * @author Fabio Gasparetti, Federico Vigna
 * @version 0.1, 01/01/06
 */

public class SqlPriorityQueue extends SqlQueue implements PriorityQueue {
	protected double DEFAULT_PRIORITY = 1.0;

	// for ants system
	protected static final String readLock = "LOCK TABLES urls READ, priorities READ;";

	protected static final String writeLock = "LOCK TABLES urls WRITE, priorities WRITE;";

	protected static final String unlock = "UNLOCK TABLES;";

	protected static final String urlsTable = "CREATE TABLE urls "
			+ "(id INT NOT NULL AUTO_INCREMENT, " + "url BLOB NOT NULL,  "
			+ "PRIMARY KEY id (id), " + "KEY url_idx (url(64))) "
			+ "MAX_ROWS=100000000 AVG_ROW_LENGTH=100;";

	protected static final String priorityTable = "CREATE TABLE priorities "
			+ "(id INT NOT NULL AUTO_INCREMENT, " + "id_url INT NOT NULL, "
			+ "priority DOUBLE NOT NULL, " + "PRIMARY KEY id (id), "
			+ "KEY priority_idx (priority), " + "KEY id_url_idx (id_url));";

	/* URL SQLs (see SqlQueue for the rest) */
	protected static final String getUrlId = "SELECT id FROM urls WHERE url=?;";

	protected static final String getPriority = "SELECT priority FROM priorities, urls WHERE priorities.id_url=urls.id AND url=?;";

	protected static final String setPriority = "INSERT INTO priorities (priority, id_url) VALUES (?,?);";

	protected static final String updatePriority = "UPDATE priorities SET priority=? WHERE id_url=?;";

	protected static final String deletePriority = "DELETE FROM priorities WHERE id_url=?;";

	protected static final String getOrderedUrls = "SELECT url FROM priorities, urls WHERE priorities.id_url=urls.id ORDER BY priority DESC;";

	protected static final String clearPriorities = "DELETE FROM priorities;";

	public void clear() throws QueueException {
		try {
			PreparedStatement stat;
			stat = connection.prepareStatement(writeLock);
			stat.execute();
			stat.close();
			stat = connection.prepareStatement(clearUrls);
			stat.executeUpdate();
			stat.close();
			stat = connection.prepareStatement(clearPriorities);
			stat.executeUpdate();
			stat.close();
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("MySql exception: " + ex.toString());
		}
	}

	public void enqueue(String url, double priority) throws QueueException {
		try {
			PreparedStatement stat;
			stat = connection.prepareStatement(writeLock);
			stat.execute();
			stat.close();
			if (!getUrl(url))
				addUrl(url);
			updatePriority(url, priority);
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("MySql exception: " + ex.toString());
		}
	}

	public void enqueue(Map urls) throws QueueException {
		try {
			PreparedStatement stat;
			stat = connection.prepareStatement(writeLock);
			stat.execute();
			stat.close();
			Iterator iter = urls.keySet().iterator();
			while (iter.hasNext()) {
				String url = (String) iter.next();
				double priority = ((Double) urls.get(url)).doubleValue();
				if (!getUrl(url))
					addUrl(url);
				updatePriority(url, priority);
			}
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("MySql exception: " + ex.toString());
		}

	}

	public String get() throws QueueException {
		String url = null;
		try {
			ResultSet rs;
			PreparedStatement stat;
			stat = connection.prepareStatement(writeLock);
			stat.execute();
			stat.close();
			stat = connection.prepareStatement(getOrderedUrls);
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
			throw new QueueException("MySql exception: " + ex.toString());
		}
		return url;
	}

	public String pop() throws QueueException {
		String url = null;
		try {
			ResultSet rs;
			PreparedStatement stat;
			stat = connection.prepareStatement(writeLock);
			stat.execute();
			stat.close();
			stat = connection.prepareStatement(getOrderedUrls);
			rs = stat.executeQuery();
			if (rs.next()) {
				// get first url from table
				url = rs.getString(1);
				if (url != null) {
					// beware of removing priority before updating url table
					removePriority(url);
					remove(url);
				}
			}
			rs.close();
			stat.close();
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("MySql exception: " + ex.toString());
		}
		return url;
	}

	/*
	 * 
	 */
	protected int getUrlId(String url) throws java.sql.SQLException {
		ResultSet rs;
		PreparedStatement stat;
		int id = -1;
		stat = connection.prepareStatement(getUrlId);
		stat.setString(1, url);
		rs = stat.executeQuery();
		if (rs.next()) {
			id = rs.getInt(1);
		}
		rs.close();
		stat.close();
		return id;
	}

	/*
	 * 
	 */
	public double getPriority(String url) throws QueueException {
		double priority = -1d;
		try {
			ResultSet rs;
			PreparedStatement stat;
			stat = connection.prepareStatement(readLock);
			stat.execute();
			stat.close();
			stat = connection.prepareStatement(getPriority);
			stat.setString(1, url);
			rs = stat.executeQuery();
			if (rs.next()) {
				priority = rs.getDouble(1);
			}
			rs.close();
			stat.close();
			stat = connection.prepareStatement(unlock);
			stat.execute();
			stat.close();
		} catch (Exception ex) {
			throw new QueueException("MySql exception: " + ex.toString());
		}
		return priority;
	}

	/*
	 * 
	 */
	protected void removePriority(String url) throws IOException {
		try {
			int id = getUrlId(url);
			if (id != -1) {
				PreparedStatement stat;
				stat = connection.prepareStatement(deletePriority);
				stat.setInt(1, id);
				stat.executeUpdate();
				stat.close();
			}
		} catch (Exception ex) {
			throw new IOException("MySql exception: " + ex.toString());
		}
	}

	/*
	 * 
	 */
	protected void updatePriority(String url, double priority)
			throws IOException {
		try {
			int id = getUrlId(url);
			if (id == -1)
				return;
			PreparedStatement stat;
			ResultSet rs;
			boolean foundPriority;
			stat = connection.prepareStatement(getPriority);
			stat.setString(1, url);
			rs = stat.executeQuery();
			foundPriority = rs.next();
			rs.close();
			stat.close();
			if (!foundPriority) {
				stat = connection.prepareStatement(setPriority);
				stat.setDouble(1, priority);
				stat.setInt(2, id);
				stat.execute();
				stat.close();
			} else {
				stat = connection.prepareStatement(updatePriority);
				stat.setDouble(1, priority);
				stat.setInt(2, id);
				stat.executeUpdate();
				stat.close();
			}
		} catch (Exception ex) {
			throw new IOException("MySql exception: " + ex.toString());
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
		stat = connection.prepareStatement(priorityTable);
		stat.execute();
		stat.close();
	}

	public static void main(String[] args) throws Exception {

		SqlPriorityQueue q = new SqlPriorityQueue();
		q.init("com.mysql.jdbc.Driver",
				"jdbc:mysql://127.0.0.1/queue?user=root",
				"jdbc:mysql://127.0.0.1/mysql?user=root", "queue", true);
		for (int i = 0; i < 1000000; i++) {
			System.out.println("i:" + i);
			System.out.println(q.size());
			q.enqueue("www.0.9.com", 0.3);
			System.out.println(q.size());
			q.enqueue("www.2.9.com", 0.4);
			System.out.println(q.size());
			q.enqueue("www.4.9.com", 0.2);
			System.out.println(q.getPriority("www.4.9.com"));
			System.out.println(q.getPriority("www.2.9.com"));
			System.out.println(q.getPriority("www.0.9.com"));
			System.out.println(q.size());
			System.out.println(q.pop());
			System.out.println(q.size());
			System.out.println(q.pop());
			System.out.println(q.size());
			System.out.println(q.pop());
			System.out.println(q.size());
			System.out.println(q.pop());
			System.out.println(q.size());
		}
	}
}
