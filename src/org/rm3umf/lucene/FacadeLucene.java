package org.rm3umf.lucene;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Set;
import org.apache.log4j.*;
import org.apache.lucene.queryParser.ParseException;
import org.rm3umf.domain.User;



/***
 * Questa classe mi permette di utilizzare i servizi offerti da Lucene
 * @author Giulz
 *
 */
public class FacadeLucene {
	
	private Logger logger =Logger.getLogger(FacadeLucene.class);
	private static FacadeLucene instance;
	private Indexer indexer;
	private Search searcher; 
	private String pathIndice;

	
	public static FacadeLucene getInstance(){
		if(instance==null)
			instance=new FacadeLucene("./index");
		return instance;
	}

	public FacadeLucene(String pathIndice){
		this.pathIndice=pathIndice;
	}
	
	
	/**
	 * Inizia la fase di indicizzazione creando l'Indexer
	 * @param pathIndex
	 * @throws IndexException 
	 */
	public void iniziaIndicizzazione() {
		//salvo il path dell'indice in modo tale da creare il searcher 

		logger.info("inizia indicizzazione");
		try {
			this.indexer= new Indexer(this.pathIndice);
		}catch(Exception e){
			logger.info("errore durante la creazione dell'indice");
			e.getStackTrace();
		}

	}

	
	public void prepareSearching(){
		System.err.println("prepare to seracing");
		this.searcher=new Search(this.pathIndice);
	}
	
	
	/**
	 * Finisci la fase di indicizzazione
	 */
	public void fineIndicizzazione(){
		logger.info("fine indicizzazione");
		this.indexer.close();
		
		
	}
	
	
	
	
	/**
	 *Questo metodo serve per aggiungere una linea all'indice che � in costruzione 
	 * @throws IOException 
	 **/
	public void addDocument(User user,Set<Long> listFollower,Set<Long> listFollowed,Set<Long> rilevanteFollowers,String pseudodocument  ) {
		logger.info("aggiundi riga all'indice"+ user.getIduser());
		String stringFollowed=list2string(listFollowed);
		String stringFollower=list2string(listFollower);
		//XXX
//		String stringRilevanteFollowers=list2string(rilevanteFollowers);
		String stringRilevanteFollowers="";
		try {
			this.indexer.index(String.valueOf(user.getIduser()),stringFollower,stringFollowed,stringRilevanteFollowers,pseudodocument);
		} catch (IOException e) {
			logger.info("errore durante l'inserimento di un document");
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Metodo che traforma una lista di stringhe in un'unica stringa separata da spazi bianchi
	 * @param listaStringhe
	 * @return stringa
	 */
	public   String list2string(Set<Long> lista){
		String stringa="";
		for(Long iduser:lista){
			stringa=stringa+" "+String.valueOf(iduser);	
		}
		return stringa;
		
			
	}
	/**
	 * Per la query a lucine
	 * @param lista
	 * @return
	 */
	
//	public static  String list2stringPerQuery(List<Link> lista){
//		String stringa="";
//		for(Link link:lista){
//			
//			String linkCorrente=link.getUrl();
//			linkCorrente=linkCorrente.substring(0, 4)+"\\"+linkCorrente.substring(4);
//			stringa=stringa+" "+linkCorrente;
//		}
//		return stringa;
//		
//			
//	}
	
	
	
	/**
	 * Metodo che mi restituisce la pagina gia parsata e link della pagina del seedSet pi�
	 * simile a quella passata come parametro
	 * @param page 
	 * @return listString
	 */
	
//	public List<Long> getSimilarUser(User user){
//		List<Long> listUser=null;
//		List<Long> listFollowed=user.getFollowed();
//		List<Long> listFollower=user.getFollower();
//		if(searcher!=null){
//			String  stringFollowed="";
//			if(listFollowed.size()!=0){
//				stringFollowed =list2string(listFollowed);
//			}
//			
//			String  stringFollower="";
//			//estraggo i link solo se ci sono
//			if(listFollower.size()!=0){
//				stringFollower =list2string(listFollower);
//			}
//
//			if(stringFollowed!=null || stringFollower !=null){
//				try{
//					listUser=this.searcher.search(stringFollower,stringFollowed);
//				}catch (Exception e) {
//					logger.info("errore durante la quersy a lucine");
//					e.printStackTrace();
//				}
//			}
//		}else{
//			throw new RuntimeException("Il searcher non � stato creato");
//		}
//		return listUser;
//	}
	
	/**
	 * Mi restituisce l'utente piu simile senza utilizzare il db ma sfruttando solo l'indice
	 * 
	 * ATTENZIONE: da calcella quando non verrà piu utilizzato da nessuno.
	 * @param user
	 * @return
	 */
	public List<Long> getSimilarUser2(User user){
		List<Long> listUser=null;

		if(searcher!=null){

			try{
			    
				String	stringFollowed =searcher.getUserFollowed(user.getIduser());
				
				String  stringFollower=searcher.getUserFollower(user.getIduser());
				
				logger.debug("followed:"+stringFollowed);
				logger.debug("followed:"+stringFollower);
				
				
				if(!stringFollowed.equals("") || !stringFollower.equals(""))
					listUser=this.searcher.searchByFollowerAndFollowed(stringFollower,stringFollowed);
			}catch (Exception e) {
					logger.info("errore durante la quersy a lucine");
					e.printStackTrace();
			}
		}else{
			throw new RuntimeException("Il searcher non è stato creato");
		}

		return listUser;
	}
	
	/**
	 * Dato uno userid mi restituisce i gli utenti rilevanti secondo la metrica 
	 * followed/followers 
	 */
	public List<Long> retriveByFollowerAndFriend(long userid){
		List<Long> listUser=null;

		if(searcher!=null){

			try{
			    
				String	stringFollowed =searcher.getUserFollowed(userid);			
				String  stringFollower=searcher.getUserFollower(userid);	
				logger.debug("followed:"+stringFollowed);
				logger.debug("followed:"+stringFollower);
				if(!stringFollowed.equals("") || !stringFollower.equals(""))
					listUser=this.searcher.searchByFollowerAndFollowed(stringFollower,stringFollowed);
			}catch (Exception e) {
					logger.info("errore durante la quersy a lucine");
					e.printStackTrace();
			}
		}else{
			throw new RuntimeException("Il searcher non è stato creato");
		}

		return listUser;
	} 
	
	
	/**
	 * Dato uno userid mi restituisce i gli utenti rilevanti secondo la metrica 
	 * followed/followers 
	 */
	public List<Long> retriveByPseudodocument(String pseudodocument){
		List<Long> listUser=null;

		if(searcher!=null){

			try{
			   	listUser=this.searcher.searchPseudoDocument(pseudodocument);
			}catch (Exception e) {
					logger.info("errore durante la quersy a lucine");
					e.printStackTrace();
			}
		}else{
			throw new RuntimeException("Il searcher non è stato creato");
		}

		return listUser;
	} 
	
	
	
	
	/**
	 * 
	 * @param userid
	 * @return
	 */
	public List<Long> retriveRelevantFollower(long userid){
		List<Long> listUser=null;

		if(searcher!=null){

			try{
			    
				String	stringRelevantUser =searcher.getRelevantFollower(userid);			
				logger.info("relevantUser:("+stringRelevantUser+")");
				
				if(!stringRelevantUser.equals("")){
					listUser=this.searcher.searchByRelevantFollower(stringRelevantUser);
				}else{
					//se non ha rilevant user facciamo la query con l'utente corrente
					//gli utenti che verranno restituiti avranno come rilevant user l'utente corrente
					
					listUser=this.searcher.searchByRelevantFollower(Long.toString(userid));
				}
			}catch (Exception e) {
					logger.info("errore durante la quersy a lucine");
					e.printStackTrace();
			}
		}else{
			throw new RuntimeException("Il searcher non è stato creato");
		}

		return listUser;
	} 
	
	/**
	 * 
	 * @param userid
	 * @return
	 */
	public List<Long> retriveByFollowed(long userid){
		List<Long> listUser=null;

		if(searcher!=null){

			try{
			    
				String	stringRelevantUser =searcher.getUserFollower(userid);			
				logger.info("relevantUser:("+stringRelevantUser+")");
				
				if(!stringRelevantUser.equals("")){
					listUser=this.searcher.searchByRelevantFollower(stringRelevantUser);
				}else{
					//se non ha rilevant user facciamo la query con l'utente corrente
					//gli utenti che verranno restituiti avranno come rilevant user l'utente corrente
					
					listUser=this.searcher.searchByRelevantFollower(Long.toString(userid));
				}
			}catch (Exception e) {
					logger.info("errore durante la quersy a lucine");
					e.printStackTrace();
			}
		}else{
			throw new RuntimeException("Il searcher non è stato creato");
		}

		return listUser;
	} 
	
	
	/**
	 * Recupero la pseudodocument associato al all'utente userid
	 * @param userid
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public String getPseudodocument(long userid) throws ParseException, IOException  {
		String	output = searcher.getPseudodocument(userid);			
		return output;
	}
	
		
}
		
		
		
		
	
	
	


