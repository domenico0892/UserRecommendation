package org.rm3umf.net.openCalais;

import java.io.IOException;
import java.net.URL;

import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rm3umf.net.URLParser;

import twitter4j.internal.logging.Logger;
import util.Cronometro;

import mx.bigdata.jcalais.CalaisClient;
import mx.bigdata.jcalais.CalaisConfig;
import mx.bigdata.jcalais.CalaisObject;
import mx.bigdata.jcalais.CalaisResponse;
import mx.bigdata.jcalais.rest.CalaisRestClient;

public class OpenCalaisService {
	
	private static Logger logger=Logger.getLogger(OpenCalaisService.class);
	
	private CalaisClient client;
	private int numberRequest;
	private CalaisConfig config;  
	private Cronometro crono;
	private URLParser urlParser;
	
	private boolean EXPANDS_URL=true;
	
	public OpenCalaisService(){
		this.client = new CalaisRestClient("za3j772ehvq3avmxdmjn5cds");
		//calais config
		this.config = new CalaisConfig();
		this.config.set(CalaisConfig.UserParam.EXTERNAL_ID, "User generated ID");
		this.config.set(CalaisConfig.ProcessingParam.CALCULATE_RELEVANCE_SCORE, "true");
		this.numberRequest=0;
		this.crono=new Cronometro();
	//	this.urlParser=new URLParser();
		
	}
	
	
	

	
	
//	public List<ResponseCalais> getEntityByUrls(List<String> urls){
//
//		
//		Map<String,ResponseCalais> name2responses=new HashMap<String, ResponseCalais>();
//
//		
//
//		for(String url:urls){
//			logger.debug("size request:"+url.length()); 
//			this.numberRequest++;
//			if(numberRequest>3){
//				try{
//					long tempoFermo=3500-crono.leggi();
//					if(tempoFermo > 0){
//						System.err.println("Opencalais : aspetto "+tempoFermo+"ms");
//						Thread.sleep(tempoFermo);
//					}
//					this.numberRequest=0;
//					crono.azzera();
//				}catch(InterruptedException ie){
//					System.err.println("errore mentre aspetto 3 sec");
//					ie.getStackTrace();
//				}
//			}
//
//			//devo splittare la richiesta in tante sotto richieste
//			CalaisResponse response=null;
//			try{
//				logger.debug("inoltro la richiesta ad opencalais:"+url);
//				crono.avanza();
//				response = client.analyze(new URL(url),this.config);
//			}catch(IOException ie){
//				System.err.println("errore mentre interrogo OpenCalais");
//				ie.getStackTrace();
//			}
//			if(response!=null){
//				String name;
//				String type;
//				String relevance;
//				int occurences;
//				//scandisco tutte le entita
//				for (CalaisObject entity : response.getEntities()) {
//					name=entity.getField("name");
//					if(name!=null){
//						type= entity.getField("_type");
//						relevance=entity.getField("relevance");
//						occurences=getOccurence(entity.getField("instances"));
//						//creo la nuova risposta calais
//						ResponseCalais responseCalais=new ResponseCalais();
//						responseCalais.setName(name);
//						responseCalais.setType(type);
//						responseCalais.setRelevance(Double.valueOf(entity.getField("relevance")));
//						responseCalais.setUri(entity.getField("_uri"));
//						responseCalais.setOccurences(occurences);
//						//metto la risposta dentro la mappa
//						ResponseCalais curr=name2responses.get(name);
//						if(curr==null){
//							name2responses.put(name,responseCalais);
//						}else{
//							int occurence=curr.getOccurences()+responseCalais.getOccurences();
//							curr.setOccurences(occurence);
//						}
//					}
//				}
//			}//faccio tutte le richieste
//		}//
//		return new LinkedList<ResponseCalais>(name2responses.values());
//		
//	}
	
	/**
	 * 	
	 * @param requestText
	 * @return
	 */
	
	
	public List<ResponseCalais> getEntity(RequestCalais request) {	
		
		
		List<String> listaRequest=new LinkedList<String>();

		Map<String,ResponseCalais> name2responses=new HashMap<String, ResponseCalais>();


		/*
		 * RICHIESTA TESTO 
		 */
		String requestText=request.getText();
		
		//Divido la richiesta se maggiore di 100000 caratteri in pi� sotto richieste
		int requestLength=requestText.length();
		if(requestLength>100000){
			int start=0;
			int end = 99999;
			while(end<requestLength){
				listaRequest.add(requestText.substring(start,end));
				start+=100000;
				end+=100000;
			}
			end=requestLength;
			listaRequest.add(requestText.substring(start,end));
		}else{
			listaRequest.add(requestText);
		}
		//non mi serve pi� la richiesta originale
		requestText="";
		
		List<CalaisResponse> listResponse = new LinkedList<CalaisResponse>();

		for(String subRequest:listaRequest){
			//devo splittare la richiesta in tante sotto richieste
			this.numberRequest++;
			if(numberRequest>3  ){
				try{
					long tempoFermo=1000-crono.leggi();
					if(tempoFermo > 0){
						System.err.println("Opencalais : aspetto "+tempoFermo+"ms");
						Thread.sleep(tempoFermo);
					}
					this.numberRequest=0;
					crono.avanzaDaCapo();
				}catch(InterruptedException ie){
					System.err.println("errore mentre aspetto 3 sec");
					ie.getStackTrace();
				}
			}
						
			CalaisResponse response=null;
			try{
				logger.debug("inoltro la richiesta ad opencalais:"+subRequest);
				crono.avanza();
				
				response = client.analyze(subRequest,this.config);
			}catch(IOException ie){
				System.err.println("errore mentre interrogo OpenCalais");
				ie.printStackTrace();
			}
			if(response!=null){
				listResponse.add(response);

			}
		}
		/*RICHIESTA URLS*/
		//ricerco le entit� negli url 
//		if(EXPANDS_URL){
//			List<String> urls=request.getUrls();
//			for(String url:urls){
//				logger.info("esploro l'url "+url);
//				this.numberRequest++;
//				if(numberRequest>3  ){
//					try{
//						long tempoFermo=1000-crono.leggi();
//						if(tempoFermo > 0){
//							System.err.println("Opencalais : aspetto "+tempoFermo+"ms");
//							Thread.sleep(tempoFermo);
//						}
//						this.numberRequest=0;
//						crono.avanzaDaCapo();
//					}catch(InterruptedException ie){
//						System.err.println("errore mentre aspetto 3 sec");
//						ie.getStackTrace();
//					}
//				}
//				
//				CalaisResponse response=null;
//				try{
//					
//					logger.info("inoltro la richiesta ad opencalais:"+url);
//					
//					String parsedPage =  urlParser.gerContextUrl(url);
//					
//					response = client.analyze(parsedPage,this.config);
//				}catch(Exception ie){
//					System.err.println("errore mentre interrogo OpenCalais");
//					ie.printStackTrace();
//				}
//				if(response!=null){
//					listResponse.add(response);
//				}
//			}
//		}
			/**
			 * Analizzo tutte le risposte
			 */
			for(CalaisResponse response:listResponse){
				String name;
				String type;
				String uri;
				String relevance;
				int occurences;
				//scandisco tutte le entita 
				//questo perch� pu� accadere che una richiesta venga splittata in 2 sottorichieste.
				//in questo caso devo fare il merge delle sotto-risposte
				for (CalaisObject entity : response.getEntities()) {
					name=entity.getField("name");
					if(name!=null){
						uri=entity.getField("_uri");
						name=name.toUpperCase();
						occurences=getOccurence(entity.getField("instances"));
						ResponseCalais curr=name2responses.get(uri);
						if(curr==null){
							type= entity.getField("_type");
							relevance=entity.getField("relevance");
							occurences=getOccurence(entity.getField("instances"));
							//creo la nuova risposta calais
							ResponseCalais responseCalais=new ResponseCalais();
							responseCalais.setName(name);
							responseCalais.setType(type);
							responseCalais.setRelevance(Double.valueOf(entity.getField("relevance")));
							responseCalais.setUri(uri);
							responseCalais.setOccurences(occurences);
							//metto la risposta dentro la mappa
							name2responses.put(uri,responseCalais);
						}else{
							//se gi� ci sta aggiorno solo le occorenze
							int occurence=curr.getOccurences()+occurences;
							curr.setOccurences(occurence);
						}
					}
				}
			}//faccio tutte le richieste
		
		return new LinkedList<ResponseCalais>(name2responses.values());
	}
	
	public  void aspetto(Cronometro crono){
		crono.avanza();
		
		System.out.println("tempo:"+crono.toString()+" request:"+numberRequest );
		numberRequest++;
		if(numberRequest>3  ){
			try{
				long tempoFermo=3100-crono.leggi();
				if(tempoFermo > 0){
					System.err.println("Opencalais : aspetto "+tempoFermo+"ms");
					Thread.sleep(tempoFermo);
				}
				this.numberRequest=0;
				crono.avanzaDaCapo();
			}catch(InterruptedException ie){
				System.err.println("errore mentre aspetto 3 sec");
				ie.getStackTrace();
			}
		}
		System.out.println("tempo:"+crono.leggi()+" request:"+numberRequest );
	}

	
	/**
	 * Questo metodo a partire dalla risposta di opencalais "instances" mi calcola quate occorrenze ci sono nel 
	 * testo
	 * @param instances - la stringa di opencalais che ci dice le occorenze
	 * @return occurence - le occorrenze dell'entit� che ci sono nel testo
	 */
	public static int getOccurence(String instances){
		int occurence=0;
		
		//String regex = "\\s+https?://(www)?\\.?[\\w]+\\.[a-z]{2,3}\\s";
		String regex ="\\{[^\\}]+\\}";
			
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(instances);
		while (matcher.find()){
			//setto name trasformandolo in minuscolo
			String url=matcher.group();
			//System.out.println(url);
			occurence++;
			 
		}
		//pero ogni hashtag treo
		return occurence;
		
	}
	
//	public static void main (String[] args){
//		String instances="[{detection=[of enhancing a wide range of applications,]Barack Obama[ used including search, faceted browsing and], prefix=of enhancing a wide range of applications,, exact=Barack Obama, suffix= used including search, faceted browsing and, offset=75, length=12}, {detection=[ links from DBpedia into other data sources,barak ]Obama[ the Linked Open Data   cloud is pulled closer to], prefix= links from DBpedia into other data sources,barak , exact=Obama, suffix= the Linked Open Data   cloud is pulled closer to, offset=592, length=5}]";
//		System.out.println(getOccurence(instances));
//	}
}