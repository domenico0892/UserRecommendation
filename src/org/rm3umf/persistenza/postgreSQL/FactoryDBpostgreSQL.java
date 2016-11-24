package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rm3umf.persistenza.FactoryDB;
import org.rm3umf.persistenza.PersistenceException;
/**
 * Questa classe permette di creare tutte le tabelle necessarie al funzionamento del sistema
 * @author giulz
 *
 */
public class FactoryDBpostgreSQL implements FactoryDB{
	
	
	
	String queryConcept="CREATE TABLE IF NOT EXISTS concept(id character varying(32) NOT NULL,"+
                        "name character varying,"+
                        "type character varying,"+
                        "numberinstance integer,"+
                        "CONSTRAINT concept_primarykey PRIMARY KEY (id)" +
                        ")";
	String queryFollowed="CREATE TABLE IF NOT EXISTS followed("+
						"userid bigint NOT NULL,"+
						"followed bigint NOT NULL,"+
						"CONSTRAINT friendrelation_pkey PRIMARY KEY (userid, followed),"+
						"CONSTRAINT friendrelation_userid_fkey FOREIGN KEY (userid) "+
						"REFERENCES users (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"+
						")";
    String queryFollower="CREATE TABLE IF NOT EXISTS follower" +
    					"("+
						"userid bigint NOT NULL,"+
						"follower bigint NOT NULL,"+
						"CONSTRAINT follower_pkey PRIMARY KEY (follower, userid),"+
						"CONSTRAINT follower_userid_fkey FOREIGN KEY (userid) "+
						"REFERENCES users (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"+
						")";
    
    String queryMessage="CREATE TABLE IF NOT EXISTS message("+
    					"id bigint NOT NULL,"+
    					"userid bigint,"+
    					"date timestamp without time zone,"+
    					"\"text\" character varying(255),"+
    					"CONSTRAINT message_pkey PRIMARY KEY (id),"+
    					"CONSTRAINT foreign_key FOREIGN KEY (userid) "+
    						"REFERENCES users (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE CASCADE"+
    					")";
    
    String queryPeriod="CREATE TABLE IF NOT EXISTS period("+
    					"id integer NOT NULL,"+
    					"startdate timestamp without time zone,"+
    					"enddate timestamp without time zone,"+
    					"CONSTRAINT period_primarykey PRIMARY KEY (id)"+
    					")";
    
    String queryResource="CREATE TABLE IF NOT EXISTS resource("+
    				"id character varying(32) NOT NULL,"+
    				"url character varying NOT NULL,"+
    				"page text,"+
    				"CONSTRAINT resource_primary_key PRIMARY KEY (id)"+
    				")";
    
	String querySignal="CREATE TABLE IF NOT EXISTS signal("+
						"userid bigint NOT NULL,"+
						"conceptid character(32) NOT NULL,"+
						"signal numeric[],"+
						"CONSTRAINT signal_pkey PRIMARY KEY (userid, conceptid),"+
						"CONSTRAINT signal_conceptid_fkey FOREIGN KEY (conceptid) "+
						"REFERENCES concept (id) MATCH SIMPLE"+
						"ON UPDATE NO ACTION ON DELETE CASCADE,"+
						"CONSTRAINT signal_userid_fkey FOREIGN KEY (userid) "+
						"REFERENCES users (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"+
						")";
	
	String querySignalComponent="CREATE TABLE IF NOT EXISTS signalcomponent("+
								"userid bigint NOT NULL,"+
								"conceptid character varying(32) NOT NULL,"+
								"periodid integer NOT NULL,"+
								"\"value\" integer,"+
								"tf numeric,"+
								"idf numeric,"+
								"tfidf numeric,"+
								"CONSTRAINT signalcomponent_pkey PRIMARY KEY (userid, periodid, conceptid),"+
								"CONSTRAINT conceptid FOREIGN KEY (conceptid) " +
								"			REFERENCES concept (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE CASCADE,"+
								"CONSTRAINT signalcomponent_periodid_fkey FOREIGN KEY (periodid)  " +
								"			REFERENCES period (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,"+
								"CONSTRAINT signalcomponent_userid_fkey FOREIGN KEY (userid) "+
											"REFERENCES users (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION"+
								")";
	String querySignalGlobal="CREATE TABLE IF NOT EXISTS signalglobal("+
							"conceptid character varying(32),"+
							"id integer,"+
							"signal numeric[],"+
							"CONSTRAINT fkey_concept FOREIGN KEY (conceptid)"+
							"REFERENCES concept (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"+
							")";
	
	String queryUsername="CREATE TABLE IF NOT EXISTS username("+
						"username text NOT NULL,"+
						"userid bigint NOT NULL,"+
						"CONSTRAINT username_pkey PRIMARY KEY (username, userid),"+
						"CONSTRAINT username_userid_fkey FOREIGN KEY (userid)"+
						"REFERENCES users (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE"+
						")";
	String queryUsers="CREATE TABLE IF NOT EXISTS users("+
					  "id bigint NOT NULL,"+
					  "CONSTRAINT primary_key PRIMARY KEY (id)"+
					  ")";
			
    /**
     * Crea tutte le tabelle del sistema nel database nel database se non sono giapresenti
     */
	public void createDB() throws PersistenceException {
    	executeQuery(queryUsers);
    	executeQuery(queryConcept);
		executeQuery(queryFollowed);
		executeQuery(queryFollower);
		executeQuery(queryMessage);
		executeQuery(queryPeriod);
		executeQuery(queryResource);
		executeQuery(querySignal);
		executeQuery(querySignalComponent);
		executeQuery(querySignalGlobal);
		executeQuery(queryUsername);
	}
    
	/**
	 * Esegue una query di upadate
	 * @param query
	 * @throws PersistenceException
	 */
    private void executeQuery(String query) throws PersistenceException{
    	DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = query;
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
	
	

}
