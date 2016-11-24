package org.rm3umf.framework.importing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.User;
import org.rm3umf.framework.importing.DatasetAdapter;
import org.rm3umf.framework.importing.DatasetException;
import org.rm3umf.framework.importing.DatasetUmap;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class NuovoDataset implements DatasetAdapter {

	private static final Logger log = Logger.getLogger(DatasetUmap.class);

	@Override
	public List<User> getUser() throws DatasetException {
		log.info("recupero utenti dal Dataset");
		List<User> listaUser=new ArrayList<User>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = getConnection();
			String retrieve = "select id_utente, screen_name from utenti";
			statement = connection.prepareStatement(retrieve);
			result = statement.executeQuery();
			String screen_name;
			List<String> list;
			while(result.next()){
				long userId = result.getLong(1);
				screen_name = result.getString(2);
				list = new ArrayList<String>();
				list.add(screen_name);
				User u = new User();
				u.setIduser(userId);
				//setto gli username
				u.setUsernames(list);
				//aggiungo l'utente alla lista
				listaUser.add(u);
			}
		}
		catch (Exception e) {
			log.error("errore nel recupero");
			if (e.getMessage().startsWith("Duplicate entry"))
				throw new IndexOutOfBoundsException();
		}
		log.info("fine recupero utenti da Dataset");
		return listaUser;
	}

	@Override
	public List<Message> getMessagesByUser(User user) throws DatasetException {
		MongoCollection<Document> c = MongoConnection.getInstance().getConnection().getDatabase("twitter").getCollection("tweets");
		Document query = new Document()
				.append("id_user", user.getIduser())
				.append("created_at", new Document()
						.append("$gte", "2016"));
		FindIterable<Document> results = c.find(query);
		List<Message> list = new ArrayList<Message>();
		Message m;
		for (Document result : results) {
			m = new Message();
			m.setIdMessage(result.getLong("id"));
			m.setDate(result.getString("created_at"));
			m.setText(result.getString("originalTweet"));
			m.setUser(user);
			list.add(m);
		}
		return list;
	}

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
}
