package org.rm3umf.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.rm3umf.framework.eval.Result;


public class FactoryXml {
	
	
	
	/**
	 * Scrive un oggetto Result su un file xml nominandolo in base alla data di creazione e alla fuznione 
	 * di similarità che ha dato vita al Result 
	 * @param result
	 * @throws IOException
	 */
	
	public  void saveResult(Result result) throws IOException{
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MM-HH_mm");
		String nameFile="Result-"+result.getFunctionSimilarity()+"-("+sdf.format(gc.getTime())+").xml"; 
		Document doc=createDocument(result);
		write(doc,nameFile);
	}
	  
	
	/**
	 * Crea un oggetto document a partire dall'oggetto Result che incapsula il risultato dell'applicazione 
	 * di una funzione di similarità
	 * @param result
	 * @return documet
	 */
	public static Document createDocument(Result result) {
		Document document = DocumentHelper.createDocument();
		Element elemResult = document.addElement( "result" );
		elemResult.addAttribute("duration",Long.toString(result.getDuration()));
		elemResult.addAttribute("function",result.getFunctionSimilarity());
		

		Set<Long> users = result.getUser(); //recupero tutti gli utenti

		for(Long userid : users){
			Element elemUser = elemResult.addElement( "user" );
			elemUser.addAttribute( "id", Long.toString(userid) );
			//recupero gli utenti piu simili a userid ripsetto alla fun di similarità
			List<Long> listBest = result.getBestUsers(userid);

			for(Long bestid:listBest){
				Element best = elemUser.addElement("best");
				best.addText(Long.toString(bestid));
			} 
		}
		return document;
	}
	  /**
	   * Salva il document in un file con path uguale a pathfile
	   * @param document
	   * @param namefile
	   * @throws IOException
	   */
	  public static void write(Document document,String pathfile) throws IOException {
	        // lets write to a file
		    OutputFormat format = OutputFormat.createPrettyPrint();
	        XMLWriter writer = new XMLWriter(new FileWriter( pathfile ),format);
	        writer.write( document );
	        writer.close();
	    }
	  
	  /**
	   * 
	   * @param pathfile
	   * @throws IOException
	 * @throws DocumentException 
	   */
	  public static Result read(String pathfile) throws IOException, DocumentException {
		  
		  Result result = new Result();
		  
		  SAXReader reader = new SAXReader();
	      Document document = reader.read(new File(pathfile));
	      Element root = document.getRootElement();
	      
	      //Leggo gli attributi di root
	      long duration =Long.parseLong( root.attributeValue("duration"));
	      String function = root.attributeValue("function");
	      
	      result.setDuration(duration);
	      result.setFunctionSimilarity(function);
	      
	      System.out.println("root : "+root.getName()); 
	      System.out.println("duration="+duration);
	      System.out.println("function="+function);
	      
	      
	      for(Element user :(List<Element>) root.elements() ){
	    	   long userid=Long.parseLong(user.attributeValue("id"));
	    	   System.out.println(user.getName()+"="+userid);
	    	   
	    	   
	    	   for(Element best    : (List<Element>)user.elements("best")){
	    		   long userBest=Long.parseLong(best.getTextTrim());
	    		   System.out.println("      best="+userBest);
	    		   result.addBestUser(userid, userBest);
	    	   }
	    	   
	      }
	      return result;
	    }
	  
	  public static void main (String[] args) throws IOException, DocumentException{
		 Result result= read("Result--(14_05-15_07).xml");
		 for(Long userid : result.getUser()){
			 System.out.print(userid+"->");
			 for(Long best :result.getBestUsers(userid)){
				 System.out.print(best+",");
				 
			 }
			 System.out.println();
		 }
	  }

}
