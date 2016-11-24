package org.rm3umf.framework.buildmodel.extractor;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.MD5;
import org.apache.log4j.*;
import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.PseudoFragment;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.domain.User;
import org.rm3umf.framework.importing.DatasetException;
import org.rm3umf.persistenza.PersistenceException;


public class ExtractorDBpediaType implements StrategyExtraction{

	private static final Logger logger = Logger.getLogger(ExtractorDBpediaType.class);

	public List<SignalComponent> extract(PseudoFragment pseudoDocument) throws ExtractorException {
		logger.debug("costruisco i signal component per lo pseuodo-doc :"+pseudoDocument);
		List<SignalComponent> listSigComp=new LinkedList<SignalComponent>();
//		Map<String, String> results = new HashMap<String, String>();
		Map<String, Integer> occType = new HashMap<String, Integer>();
//		String pseudoFragmentText = "";
		for (Message m : pseudoDocument.getMessages()) {
			try {
				getTypes(m.getIdMessage(), 3, occType);
			} catch (DatasetException | SQLException | PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			pseudoFragmentText = pseudoFragmentText + " "+ m.getText();
		}
		int max=0;
		User user = pseudoDocument.getUser();
		Period period = pseudoDocument.getPeriod();
		//		//questa mappa serve perch� 
		//		Map<String,SignalCompoennt>

		for(String type : occType.keySet()){
			SignalComponent sigComp=new SignalComponent();
			sigComp.setPeriod(period);
			sigComp.setUser(user);
			//creo il concept
			Concept concept= new Concept();
			//l'id potrei costruirlo con l'url che forse mi fa anche una sorta di disanbiguazione
			
			//String type = results.get(type);
			String nameConcept = type.toUpperCase();
			String uri = type;
			try{
				concept.setId(MD5.getInstance().hashData(uri));
			}catch(NoSuchAlgorithmException e){
				logger.error("errore mentre calcolo MD5");
				e.getMessage();
			}
			concept.setNameConcept(nameConcept);
			concept.setType(type);
			sigComp.setConcept(concept);
//			 System.err.println(occType.get(type));
			int occurences=occType.get(type);
			//serve per calcolare tf
			if(max<occurences){
				max=occurences;
			}
			sigComp.setOccorence(occurences);
			sigComp.setTf(1);
			//aggiungo il sig comp alla lista
			listSigComp.add(sigComp);
		}

		//calcola tf
		for(SignalComponent signalComp:listSigComp){
			double tf=signalComp.getOccorence()/(double)max;
			signalComp.setTf(tf);

		}//fine user

		//proviamo ad esplorare i link..
		return listSigComp; 


	}

	@Override
	public void exploreResource(boolean exploreResource) {
		// TODO Auto-generated method stub

	}

	public int getOccurrences (String key, String text) {
		try {
			Pattern p = Pattern.compile(key);

			Matcher m = p.matcher(text);
			int o = 0;
			while (m.find())
				o++;
			return o;}
		catch (java.util.regex.PatternSyntaxException e) {
			return 0;
		}
	}


	public void getTypes (long tweetId, int level, Map<String, Integer> occType) throws DatasetException, SQLException, PersistenceException {
		String query = "select * from semanticsTweetsEntity where tweetId = ?";
		Connection conn = getConnection();
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setLong(1, tweetId);
		ResultSet rs = ps.executeQuery();
		String name, type;
		Integer i;
		while (rs.next()) {
//			name = rs.getString("name");
			type = getType(rs.getString("type"), level);
			if (!occType.containsKey(type))
				i = 1;
			else {
				i = occType.get(type);
				i = i + 1;
			}
			occType.put(type, i);
//			results.put(name, type);
		}
		conn.close();
	}

	private Connection getConnection() throws DatasetException, PersistenceException {
		//		String driver = "com.mysql.jdbc.Driver";
		//		String dbURI = "jdbc:mysql://localhost/dataset";
		//		String userName = "root";
		//		String password = "mysql";
		String driver = "com.mysql.jdbc.Driver";
		String dbURI = "jdbc:mysql://localhost:3306/news_recommendation?useSSL=false&serverTimezone=UTC";
		String userName = "root";
		String password = "ai-lab";

		Connection connection;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(dbURI,userName, password);
		} catch (ClassNotFoundException e) {
			throw new DatasetException(e.getMessage());
		} catch(SQLException e) {
			if (e.getMessage().startsWith("Communications link failure")) {
				return getConnection();
			}
			else
				throw new PersistenceException(e.getMessage());		
		}
		catch(Exception e) {
			throw new DatasetException(e.getMessage());
		}
		return connection;
	}

	public String getType (String type, int level) {
		if (type.length() < 2) {
			return "";
		}
		String[] types = type.split(";");
		for (int i=0; i<types.length;i++) {
			if (i==level)
				return types[i];
		}
		return "";
	}
}
