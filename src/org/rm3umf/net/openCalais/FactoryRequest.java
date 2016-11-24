package org.rm3umf.net.openCalais;
import util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import org.rm3umf.domain.*;
import org.rm3umf.net.URLParser;
import org.rm3umf.persistenza.*;

import twitter4j.internal.logging.Logger;
import util.UtilText;

/**
 * Questa classe ha la responsabilit� di creare una richiesta da inoltrare ad OpenCalais a partire da uno 
 * pseudo document
 * @author Giulz
 *
 */
public class FactoryRequest {
	
	private static Logger logger=Logger.getLogger(FactoryRequest.class);
	
	private static FactoryRequest instance;
	private URLParser urlParser;
	private File fileUrl;
	
	public static FactoryRequest getInstance() throws IOException{
		if(instance==null){
			instance=new FactoryRequest();
		}
		return instance;
	}
	
	public FactoryRequest() throws IOException{
	//	this.urlParser=new URLParser();
		this.fileUrl=new File("./url.txt");
		FileWriter fw = new FileWriter(fileUrl, false);	
       
	}
	
	/**
	 * Questo metodo dato uno pseudoDocument restituisce una richiesta da inoltrare a OpenCalais
	 * @param pseudoDocument - lo pseudoDocument da cui bisogna costruire la richiesta
	 * @return request - la richiesta da inoltrare
	 * @throws IOException 
	 */
	public RequestCalais getRequest(PseudoFragment pseudoDocument , boolean addPagesToRequest) throws IOException {
		
		FileWriter fw = new FileWriter(fileUrl, true);	
        PrintWriter scrivi = new PrintWriter(fw);
  
		 RequestCalais request = new RequestCalais();
		 /*Estraggo tutti i tweets dallo pseudoDocument */
		 List<String> urls=new LinkedList<String>();
		 String textRequest="";	
		 //devo crearmi una stringa per inoltrare la richiesta
		 List<Message> listaMessages=pseudoDocument.getMessages();
		 for(Message message: listaMessages ){
			 String text=message.getText();
			 if(UtilText.getInstance().isEnglish(text)){
				// logger.info("tweet:"+text);
				 //salvo i link trovati	
				 urls.addAll(UtilText.getInstance().extractLinks(text));
				 textRequest=textRequest+(UtilText.getInstance().removeStopWord(message.getText()))+" .\n ";
			 //request=request+(message.getText())+"\n";
			 }
		 }
		 
		 
		 if(addPagesToRequest){
			 /*Esploro i link e li aggiungo alla richiesta*/
			 String parsedText="";
			 //espandi i link che trovi
			 for(String url:urls){
				 
				 Resource resource = new Resource();
				 
				 String id=null;
				 try {
					id=MD5.getInstance().hashData(url);
				
					resource.setId(id);
					resource.setUrl(url);
				 
					scrivi.print(url);
					parsedText=urlParser.gerContextUrl(url);
				 
					resource.setPage(parsedText);
					AAFacadePersistence.getInstance().resourceSave(resource);
				 } catch (Exception e) {
					 	System.err.println(parsedText);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 
				 
				 //controlla che sia inlese
				 if(parsedText.length() > 0 && UtilText.getInstance().isEnglish(parsedText)){
					 System.out.println("aggiungo la pagina alla richiesta");
				//	 textRequest=textRequest+" .\n "+UtilText.getInstance().removeStopWord(parsedText);
					 scrivi.print("  OK ("+parsedText.length()+")");
				 }else{
					 scrivi.print("  NO ("+parsedText.length()+")");
				 }
				 scrivi.println(" USER="+pseudoDocument.getUser().getIduser()+"- PERIOD="+pseudoDocument.getPeriod().getIdPeriodo());
			 }
		 }
		 request.setText(textRequest);
		 request.setUrls(urls);
		 scrivi.close();
	     fw.close();
		 logger.debug("la richiesta :"+textRequest);
		 return request;
		
	}

}
