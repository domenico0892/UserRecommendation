package org.rm3umf.lucene;




import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.*;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import util.UtilText;


public class Search {

	private static final Logger logger = Logger.getLogger(Search.class);

	private IndexSearcher searcher;


	public Search(String path_index){

		try {

			IndexReader reader = IndexReader.open(path_index);
			this.searcher = new IndexSearcher(reader);
			this.searcher.maxDoc();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void close() {
		try {
			searcher.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Questo metodo restituisce gli utenti più simili secondo la metrica relevantUser in cui vengono
	 * cosiderati solo gli utenti seguiti che vengono seguiti senza seguire reciprocamente. 
	 * @param relevantFollower
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public  List<Long> searchPseudoDocument(String pseudodocument) throws ParseException, IOException{
		
		
		PerFieldAnalyzerWrapper aWrapper=new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer());
//		aWrapper.addAnalyzer("userid", new WhitespaceAnalyzer());
//		aWrapper.addAnalyzer("follower", new WhitespaceAnalyzer());
//		aWrapper.addAnalyzer("followed", new WhitespaceAnalyzer());
//		aWrapper.addAnalyzer("link", new WhitespaceAnalyzer());
		
		StandardAnalyzer sa = new StandardAnalyzer();
		aWrapper.addAnalyzer("pseudodocument", sa);
		
		
		List<Long> listSimilarUsers = new LinkedList<Long>();
		
		
		Query queryPseudo=null;

		Term termPseudodocument= new Term("pseudodocument", "("+QueryParser.escape(pseudodocument.toLowerCase())+")");
		queryPseudo = new TermQuery(termPseudodocument);
		
	
		BooleanQuery queryOR = new BooleanQuery();
		queryOR.setMaxClauseCount(1000000000);
		queryOR.add(queryPseudo,BooleanClause.Occur.SHOULD);
		QueryParser parser = new QueryParser("pseudodocument", aWrapper);
		Query queryFinale= parser.parse(queryOR.toString());
		
		// Search for the query
		Hits hits = this.searcher.search(queryFinale);

		// Examine the Hits object to see if there were any matches
		int hitCount = hits.length();

		/**
		 * Viasualizza i risultati della query effettuata
		 */
		if (hitCount == 0){
			logger.info("No matches were found :");
			//System.out.println(queryOR);
			//System.out.println(pseudodocument);
		}else{
			// Iterate over the Documents in the Hits object
			for (int i = 1; i < hitCount; i++){
				Document doc = hits.doc(i);
				//in posizione 0 metto il title 
				String userid = doc.get("userid");
				listSimilarUsers.add(Long.parseLong(userid));
			}
		}
		return listSimilarUsers;
	}
	
	

	/**
	 * Metodo che ad partire da una stringa della pagina parsata e un insieme di link ci recupera nell'indice
	 * il documento pi� simile restituendo una lista con testo parsato link e url.
	 * 
	 * @param html
	 * @param links
	 * @return list vengono restituiti html link e url del doc pi� simile a quello di input
	 * @throws ParseException
	 * @throws IOException
	 */

	public  List<Long> searchByFollowerAndFollowed(String follower,String followed) throws ParseException, IOException{

		PerFieldAnalyzerWrapper aWrapper=new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer());
		//System.out.println("standard analizer max value:"+);
		aWrapper.addAnalyzer("follower", new WhitespaceAnalyzer());
		aWrapper.addAnalyzer("followed", new WhitespaceAnalyzer());


		// Build a Query object
		//QueryParser parser = new QueryParser("links", aWrapper);



		//Query queryHtml= parser.parse("html:"+html+" OR links:\""+links+"\"");

		//		MultiFieldQueryParser parser = new MultiFieldQueryParser(
		//                new String[] {"bodytext", "title"},
		//                aWrapper);
		//		



		/*NON RISCO A FARE LA QUERY SU TUTTI E DUE I CAMPI*/
		//	Query queryHtml= parser.parse("html:\""+html+"\" OR links:("+links+")");

		//		String query = "html:("+html+") OR links:("+links+")";
		//		
		//html = QueryParser.escape(html);
		//links = QueryParser.escape(links);
		logger.debug("follower :"+follower);
		logger.debug("followed :"+followed);
		/*Costruisco la query pesata sui followed e sui follower*/


		Query queryFollowed=null;
		Query queryFollower=null;

		//FOLLOWER
		if(follower.length()!=0){
			Term termFollower = new Term("follower", "("+follower+")");
			queryFollower = new TermQuery(termFollower);
		}

		//FOLLOWED
		if(followed.length()!=0){
			Term termFollowed = new Term("followed", "("+followed+")");
			queryFollowed = new TermQuery(termFollowed);
		}
		//		BooleanClause bc1 = new BooleanClause(queryText, BooleanClause.Occur.SHOULD);
		//		BooleanClause bc2 = new BooleanClause(queryLinks, BooleanClause.Occur.SHOULD);
		BooleanQuery queryOR = new BooleanQuery();

		queryOR.setMaxClauseCount(1000000);

		if(queryFollower!=null){
			queryOR.add(queryFollower,BooleanClause.Occur.SHOULD);
		}
		if(queryFollowed!=null){
			queryOR.add(queryFollowed,BooleanClause.Occur.SHOULD);
		}

		//logger.debug("query OR "+queryOR.toString());

		QueryParser parser = new QueryParser("follower", aWrapper);

		Query queryFinale= parser.parse(queryOR.toString());
		// Search for the query
		Hits hits = this.searcher.search(queryFinale);
		// Examine the Hits object to see if there were any matches
		int hitCount = hits.length();

		//lista da restituire
		List<Long> userList=new ArrayList<Long>();


		/**
		 * Viasualizza i risultati della query effettuata
		 */
		if (hitCount == 0){
			logger.debug("No matches were found");
		}else{
			for (int i = 1; i < hitCount; i++){
				Document doc = hits.doc(i);
				//aggiungo l'utente recuperato alla lista
//				if(i!=0)
//				 logger.info("DOC RECUPERATO "+i+" : "+Long.parseLong(doc.get("userid")));
				userList.add(Long.parseLong(doc.get("userid")));
			}
		}


		return userList;
	}

	/**
	 * Questo metodo restituisce gli utenti più simili secondo la metrica relevantUser in cui vengono
	 * cosiderati solo gli utenti seguiti che vengono seguiti senza seguire reciprocamente. 
	 * @param relevantFollower
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public  List<Long> searchByRelevantFollower(String relevantFollower) throws ParseException, IOException{

		WhitespaceAnalyzer whiteAnalyzer=new WhitespaceAnalyzer();
		List<Long> listSimilarUsers = new LinkedList<Long>();
		Query query=null;

		//FOLLOWER
		if(relevantFollower.length()!=0){
			Term termFollower = new Term("relevantFollower", "("+relevantFollower+")");
			query = new TermQuery(termFollower);
		}
		
		BooleanQuery queryOR = new BooleanQuery();
		

		queryOR.setMaxClauseCount(1000000);

		if(query!=null){
			queryOR.add(query,BooleanClause.Occur.SHOULD);
		}
		

		logger.debug("query OR "+queryOR.toString());
		
		// Build a Query object
		QueryParser parser = new QueryParser("relevantFollower", whiteAnalyzer);
		Query queryFinale= parser.parse(queryOR.toString());

		// Search for the query
		Hits hits = this.searcher.search(queryFinale);

		// Examine the Hits object to see if there were any matches
		int hitCount = hits.length();


		/**
		 * Viasualizza i risultati della query effettuata
		 */
		if (hitCount == 0){
			logger.info("No matches were found for \"" + relevantFollower + "\"");
		}else{
			// Iterate over the Documents in the Hits object
			for (int i = 0; i < hitCount; i++){
				Document doc = hits.doc(i);
				List<String> infoRicetta = new LinkedList<String>();
				//in posizione 0 metto il title 
				String userid = doc.get("userid");
				listSimilarUsers.add(Long.parseLong(userid));
			}
		}
		return listSimilarUsers;
	}





	public String getUserFollowed(long userid) throws ParseException, IOException  {

		PerFieldAnalyzerWrapper aWrapper=new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer());
		//aWrapper.addAnalyzer("userid", new WhitespaceAnalyzer());
		// Build a Query object
		QueryParser parser = new QueryParser("userid", aWrapper);
		Query queryUser= parser.parse("userid:("+userid+"~0.99)");
		//System.out.println(queryHtml.toString());
		Hits hits = this.searcher.search(queryUser);

		// Examine the Hits object to see if there were any matches
		int hitCount = hits.length();
		String output="";
		if (hitCount != 0){
			output=hits.doc(0).get("followed");
		}
		return output;
	}
	
	/**
	 * Recupera lo pseudodocument associato ad un utente dato ID dell'utente
	 * @param userid
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public String getPseudodocument(long userid) throws ParseException, IOException  {
		IndexReader ir = this.searcher.getIndexReader();
		
		
		PerFieldAnalyzerWrapper aWrapper=new PerFieldAnalyzerWrapper(new StandardAnalyzer());
		//aWrapper.addAnalyzer("userid", new WhitespaceAnalyzer());
		// Build a Query object
		QueryParser parser = new QueryParser("userid", aWrapper);
		Query queryUser= parser.parse("userid:("+userid+"~0.99)");
		//System.out.println(queryHtml.toString());
		Hits hits = this.searcher.search(queryUser);

		// Examine the Hits object to see if there were any matches
		int hitCount = hits.length();
		String output="";
		if (hitCount != 0){
			output=hits.doc(0).get("pseudodocument");
		}
		System.out.println("length pseudoFragment="+output.length());
		return output;
	}


	public String getUserFollower(long userid) throws ParseException, IOException  {

		PerFieldAnalyzerWrapper aWrapper=new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer());
		//aWrapper.addAnalyzer("userid", new WhitespaceAnalyzer());
		// Build a Query object
		QueryParser parser = new QueryParser("userid", aWrapper);
		Query queryUser= parser.parse("userid:("+userid+"~0.99)");
		//System.out.println(queryHtml.toString());
		Hits hits = this.searcher.search(queryUser);

		// Examine the Hits object to see if there were any matches
		int hitCount = hits.length();

		String output="";
		if (hitCount != 0){
			output=hits.doc(0).get("follower");
		}
		return output;
	}
	/**
	 * Restituisce la stringa di tutti i relevantFollower dell'utente userid passato come parametro
	 * 
	 * @param userid - utente di cui si vogliono i rilevan user 
	 * @return stringaRelevantUser 
	 * @throws ParseException
	 * @throws IOException
	 */
	public String getRelevantFollower(long userid) throws ParseException, IOException  {

		PerFieldAnalyzerWrapper aWrapper=new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer());
		//aWrapper.addAnalyzer("userid", new WhitespaceAnalyzer());
		// Build a Query object
		QueryParser parser = new QueryParser("userid", aWrapper);
		Query queryUser= parser.parse("userid:("+userid+"~0.99)");
		
		Hits hits = this.searcher.search(queryUser);

		// Examine the Hits object to see if there were any matches
		int hitCount = hits.length();

		String output="";
		if (hitCount != 0){
			output=hits.doc(0).get("relevantFollower");
		}
		return output;
	}








}



	
	
	
	


