package org.rm3umf.lucene;





import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.apache.log4j.*;

import org.apache.lucene.document.Field;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;


public class Indexer{

	private IndexWriter writer;
	protected Logger logger = Logger.getLogger("fceval.algorithms.Arru_Zeppi.JaccarSimilality:Index");


	public Indexer(String pathIndex) throws IndexException{
		logger.info("creo l'indice");
		File f=new File(pathIndex);
		if(!f.exists()){
			this.logger.error("non esiste la directory dove creare l'inice");
			throw new IndexException("non esiste la directory in cui creare l'indice");
		}
		
		String idx=pathIndex;
		
		
		// Make an writer to create the index
		try{
			
			PerFieldAnalyzerWrapper aWrapper=new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer());
			aWrapper.addAnalyzer("userid", new WhitespaceAnalyzer());
			aWrapper.addAnalyzer("follower", new WhitespaceAnalyzer());
			aWrapper.addAnalyzer("followed", new WhitespaceAnalyzer());
			aWrapper.addAnalyzer("link", new WhitespaceAnalyzer());
			aWrapper.addAnalyzer("pseudodocument", new StandardAnalyzer());
			
			this.writer = new IndexWriter(idx, aWrapper , true);
			this.logger.info("creato IndexerWriter nel path "+ idx);
		}catch(IOException e){
			throw new IndexException("errore durante la creazione dell'IndexWriter");

		}
	}

/**
 * Chiudi indice
 */
	
	public void close(){
		try {
			writer.optimize();
			writer.close();
			this.logger.info("chiuso IndexWriter");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	/**
	 * Metodo che inserisce un document nell'indice
	 * @param html
	 * @param links
	 * @throws IOException
	 */
	public void index(String idUser,String follower,String followed,String relevantFollowers,String pseudodocument) throws IOException{
		// Add some Document objects containing quotes
		logger.debug("prova ad aggiungere document all'indice");
		writer.addDocument(createDocument(idUser,follower,followed,relevantFollowers,pseudodocument));
		this.logger.debug("aggiunta pagina all'indice");
	}
	
	/**
	 * Creo il documento da aggiungere all'indice
	 * @param html
	 * @param links
	 * @return
	 */
	private static  Document createDocument(String userid,String follower,String followed,String relevantFollower,String pseudodocument)
	{
		Document doc = new Document();
		doc.add(new Field("userid",userid,Field.Store.YES, Field.Index.TOKENIZED)); 
		//aggiungo il campo links
		doc.add(new Field("follower",follower,Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("followed",followed,Field.Store.YES,Field.Index.ANALYZED)); 
	//	doc.add(new Field("relevantFollower",relevantFollower,Field.Store.NO,Field.Index.NO)); 
		//doc.add(new Field("pseudodocument",pseudodocument,Field.Store.NO,Field.Index.ANALYZED)); 
		return doc;
	}





}
