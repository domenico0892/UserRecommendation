package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.rm3umf.domain.Resource;
import org.rm3umf.persistenza.DataSource;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.persistenza.ResourceDAO;


public class ResourceDAOpostgreSQL implements ResourceDAO{

	
	
	public void save(Resource resource) throws PersistenceException{
		
		if (this.doRetrieveById(resource.getId()) ==  null) {
			this.doInsert(resource);
		}	
	}
	
	public void doInsert(Resource resource) throws PersistenceException {
	
		Connection connection =null;
		String id=resource.getId();
		PreparedStatement statement = null;
		try {
			connection=DataSourcePostgreSQL.getInstance().getConnection();
			String insert = "insert into resource(id,url,page) values (?,?,?)";
			statement = connection.prepareStatement(insert);
			statement.setString(1,id );
			statement.setString(2,resource.getUrl());
			statement.setString(3,resource.getPage());
			statement.executeUpdate();
		}
		catch (SQLException e) {
			e.getStackTrace();
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


	
	public Resource doRetrieveById(String id) throws PersistenceException {
		Resource resource = null;
		DataSource ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select id,url,page from resource where id=? ";
			statement = connection.prepareStatement(retrieve);
			statement.setString(1, id);
			result = statement.executeQuery();
			if(result.next()){
				resource=new Resource();
				resource.setId(id);
				resource.setUrl(result.getString(2));
				resource.setPage(result.getString(3));
				
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
		return resource;
	}

	
	
	public void deleteInstance() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from resourceinstance ";
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
	
	public void delete() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from resource ";
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

	
	public void saveInstance(Long idMessage, Resource resource) throws PersistenceException{
		//salvo la risorsa
		save(resource);
		Connection connection = null;
		String idresource=resource.getId();
		PreparedStatement statement = null;
		try {
			connection=DataSourcePostgreSQL.getInstance().getConnection();
			String insert = "insert into resourceinstance(resourceid,messageid) values (?,?)";
			statement = connection.prepareStatement(insert);
			statement.setString(1,idresource );
			statement.setLong(2,idMessage);
			
			statement.executeUpdate();
		}
		catch (SQLException e) {
			e.getStackTrace();
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
