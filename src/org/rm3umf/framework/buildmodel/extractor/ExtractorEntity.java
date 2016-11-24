package org.rm3umf.framework.buildmodel.extractor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import util.MD5;
import util.UtilText;
import org.apache.log4j.*;
import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.Period;
import org.rm3umf.domain.PseudoFragment;
import org.rm3umf.domain.SignalComponent;
import org.rm3umf.domain.User;
import org.rm3umf.framework.importing.DatasetException;
import org.rm3umf.net.openCalais.FactoryRequest;
import org.rm3umf.net.openCalais.OpenCalaisService;
import org.rm3umf.net.openCalais.RequestCalais;
import org.rm3umf.net.openCalais.ResponseCalais;
import org.rm3umf.persistenza.PersistenceException;


public class ExtractorEntity implements StrategyExtraction{

	private static final Logger logger = Logger.getLogger(ExtractorEntity.class);
 	
	private OpenCalaisService opencalais;
	
	private boolean exploreResource;
		
	
	
	public  ExtractorEntity(){
		this.exploreResource=false;
		this.opencalais=new OpenCalaisService();
	}
	
	/**
	 * Specificare se si vogliono esplorare gli url che si incontrano
	 * @param exploreResource
	 */
	public ExtractorEntity(boolean exploreResource){
		this.exploreResource=exploreResource;
		this.opencalais=new OpenCalaisService();
	}
	
	public List<SignalComponent> extract(PseudoFragment pseudoDocument) throws ExtractorException {
		logger.debug("costruisco i signal component per lo pseuodo-doc :"+pseudoDocument);
		List<SignalComponent> listSigComp=new LinkedList<SignalComponent>();
		//estraggo la richiesta dallo pseudoDocument
		
		RequestCalais request=null;
		try{
			request = FactoryRequest.getInstance().getRequest(pseudoDocument,this.exploreResource);
		}catch(IOException e){
			System.err.println("errore durante la creazione della query");
		}
		//questa � per prova......
		List<ResponseCalais> responses=new LinkedList<ResponseCalais>();
			//faccio la richiesta solo se ci sta qualcolsa da richiedere
			if(request.length()>0){
				logger.info("REQUEST");
				logger.info("lenght="+request.length()+" ");
				logger.info(request.getText());
				logger.info("END REQUEST");
				responses=this.opencalais.getEntity(request);
			}
		int max=0;
		User user = pseudoDocument.getUser();
		Period period = pseudoDocument.getPeriod();
		
//		//questa mappa serve perch� 
//		Map<String,SignalCompoennt>
		
		for(ResponseCalais response:responses){
			SignalComponent sigComp=new SignalComponent();
			sigComp.setPeriod(period);
			sigComp.setUser(user);
			//creo il concept
			Concept concept= new Concept();
			//l'id potrei costruirlo con l'url che forse mi fa anche una sorta di disanbiguazione
			String nameConcept = response.getName().toUpperCase();
			String type = response.getType();
			String uri = response.getUri();
			try{
				concept.setId(MD5.getInstance().hashData(uri));
			}catch(NoSuchAlgorithmException e){
				logger.error("errore mentre calcolo MD5");
				e.getMessage();
			}
			concept.setNameConcept(nameConcept);
			concept.setType(type);
			sigComp.setConcept(concept);
			int occurences=response.getOccurences();
			//serve per calcolare tf
			if(max<occurences){
				max=occurences;
			}
			sigComp.setOccorence(occurences);
			sigComp.setTf(response.getRelevance());
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
	
	public void getEntities (long tweetId, int level) {
		
	}
	
	private Connection getConnection() throws DatasetException {
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
				throw new PersistenceException(e.getMessage());		}
		catch(Exception e) {
			throw new DatasetException(e.getMessage());
		}
		return connection;
	}

}
