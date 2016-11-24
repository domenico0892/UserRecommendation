package org.rm3umf.framework.importing;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.Resource;
import org.rm3umf.net.downloader.CrawlerListener;
import org.rm3umf.net.downloader.Downloader;
import org.rm3umf.net.downloader.InMemoryQueue;
import org.rm3umf.net.downloader.Queue;
import org.rm3umf.net.downloader.QueueException;
import org.rm3umf.net.downloader.ResourceDownloader;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;


/**
 * Questo classe serve ad estrarre le risorse presenti all'interno del testo di un messaggio e ad creare la classe
 * Resource che incapsula la risorsa. Le risorse sono pagine web accedibili che sono state linkate nel messaggio. 
 * 
 * @author Giulz
 *
 */

public class ResourcExtractor {
	
	private final static int NUMBER_THREAD=20;
	
	private static final Logger logger=Logger.getLogger("filtri.filter2ExtractLink.FilterLink");
	
	private Downloader downloader;
	private Queue queue;
	private CrawlerListener listener;
	
	public ResourcExtractor(){
		//java.util.logging.Level.INFO;
		
		queue = new InMemoryQueue();
		downloader = new Downloader(queue);
		downloader.setMaxThreads(NUMBER_THREAD);
		// Listener
		this.listener=new CrawlerListener();
		downloader.addListener(this.listener);
		downloader.setFollowRedirect(true);
	}
	
	public void start(){
		downloader.start();
	}
	
	public void finish() throws QueueException{
		downloader.waitDone();
	}
	
	
	public  void addResource(List<Message> messages) throws QueueException, IOException   {
		//Estraggo tutti gli url presenti all'interno dei messaggi 
		Set<String> listaUrl =new HashSet<String>(); 
		for(Message message:messages)
			listaUrl.addAll(extract(message));			
		
		List<ResourceDownloader> crawlerResources =  download(listaUrl);
		
		//devo traformare le risoerse del  e salvarle
		for(ResourceDownloader res:crawlerResources){
		    String url= res.getURL();
		    String page =new String(res.getObject());
			Resource resource = new Resource();
			String md5 = resource.getMD5(url);
			resource.setId(md5);
			resource.setUrl(url);
			resource.setPage(page);
			
			try {
				logger.info("save :"+resource);
				AAFacadePersistence.getInstance().resourceSave(resource);
			} catch (PersistenceException e) {
				logger.error("impossibile salvare la risorsa:"+e.getMessage());	
				e.printStackTrace();
				//res.save();
			
			}
			
		}
		//svuoto la coda delle risorse scaricate dal listener
		this.listener.resources.clear();
		
	
	}
		
	
	
	

	private Set<String> extract(Message message) {
		
		//String regex = "\\s+https?://(www)?\\.?[\\w]+\\.[a-z]{2,3}\\s";
		String regex ="http://[^ |,]+";
			
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(message.getText());

		Set<String> listaUrl=new HashSet<String>();
		while (matcher.find()){
			//setto name trasformandolo in minuscolo
			String url=matcher.group();
			listaUrl.add(url);
			 
		}
		
		//pero ogni hashtag treo
		return listaUrl;	
	}
	
	
//	protected Resource store(String url) throws Exception {
//		byte[] page = null;
//		Resource res;
//		// use downloader to retrieve
//		List urls = new ArrayList();
//		urls.add(url); //urls � una lsita
//		startup.downloader.addURLs(urls);
//		System.out.println("Server.store : url"+urls);
//		startup.downloader.waitEmptyQueue();
//	
//		if (startup.crawlerListener.res != null) // res = null if page is not html or is not reachable
//			//startup.rawPageStorer.store(startup.crawlerListener.res);
//			System.out.print("");
//		else {
//			logger.info("Unable to retrieve: "+url);
//		}
//		return startup.crawlerListener.res;
//	}
	
	

	/*
	 * Effettua il download delle risorse presenti all'interno della lista che gli viene passato come
	 * parametro
	 * 
	 */
	private List<ResourceDownloader>  download(Set<String> urls) throws QueueException, IOException {
		List<ResourceDownloader> resources = null;
		
		downloader.addURLs(urls);
		downloader.waitEmptyQueue();
		if (this.listener.resources.size()> 0) {
			resources = this.listener.resources;
			
		}
		return resources;
	}

	
}
