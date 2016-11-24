package org.rm3umf.net;


import java.io.IOException;
import java.net.URL;

import org.rm3umf.framework.eval.Server;

import twitter4j.internal.logging.Logger;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.ArticleSentencesExtractor;
import de.l3s.boilerpipe.extractors.ExtractorBase;

/**
 * Questa classe la datogli un URL restituisce (se accessibile) la pagina html gi� parsata con l'ArticleExtractor
 * che di BoilerPipe che estrae solo il contenuto informativo principale della pagina.
 * @author Giulz
 *
 */

public class URLParser {
	
   private static final Logger logger = Logger.getLogger(URLParser.class);
   
   	private ExtractorBase extr;
	private Server server;
   	
   	public URLParser(){
		extr = ArticleSentencesExtractor.INSTANCE;
		server= new Server();
		
	}
	
	public  String gerContextUrl(String url){
		//scarica parsa restituisce
		logger.info("Accedo a url "+url);
		String testo="";
		
		try{
			//URL urlHtml = new URL(url);
			String testoHtml = server.downloadPage(url);
			logger.info("estraggo testo di "+url);
			testo = testoHtml;
			//testo = extr.getText(testoHtml);
			logger.info("Length testo estratto "+testo.length());
		}catch(IOException e){
			System.err.println("non posso recuperare la pagine dell'url "+url);
		}
//		catch(BoilerpipeProcessingException e){
//			System.err.println("boilerpipe non riesce ad processare la pagina"+url);
//		}
		
		return testo;
		
	}
	
	public static void main(String[] args){
		URLParser parser=new URLParser();
		System.out.println(parser.gerContextUrl("http://nyti.ms/cRhsBR"));
	}
	
}
