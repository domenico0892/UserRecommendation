package org.rm3umf.net.twitter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import twitter4j.IDs;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.internal.logging.Logger;

/**
 * Questa classe permette di inoltrare delle richieste a Twitter per recuperare le informazioni relative agli
 * utenti di Twitter identificati dall'id. In particolare è possibile recuperare gli insiemi di follower e di 
 * followed(following) per un utente. 
 * Viene gestito il limite di richieste attendendo quando si raggiunge il limite.
 * 
 * @author giulz
 *
 */

public class FacadeTwitter4j {
	
	private static final Logger logger=Logger.getLogger(FacadeTwitter4j.class);
	
	private static FacadeTwitter4j instance;
	private int count;
	//private final int LIMIT=150;
	private Twitter twitter;
	
	public static synchronized FacadeTwitter4j getInstace(){
		if(instance==null){
			instance=new FacadeTwitter4j();
		}
		return instance;
	}
	
	public FacadeTwitter4j(){
		this.count=0;
		AccessToken at=new AccessToken("425250439-aiKbzkhuih5AMTcOTmEUVZKlmSqJq1mTS5ccEBOx","ASpawY2OPVzj9iIHWOJUHCqYq0gNoe7P3v1gLBD1GM");
		twitter =new TwitterFactory().getInstance(at);
	}
	
	/**
	 * Restituisce l'insieme di following dell'utente identificato da idUser
	 * @param idUser
	 * @return setFollowed - l'insieme di utenti di follower
	 * @throws TwitterException
	 */
	public  Set<Long> getFolloweds(long idUser) throws TwitterException{
		Set<Long> listaFriends=new HashSet<Long>();
		long cursor = -1;
		IDs ids;
		do{ count++;
			logger.info("("+this.count+")recupero followeds di user "+idUser);	
			//verifico se ho ragiunto il limite
			aspetta();
			ids=twitter.getFriendsIDs(idUser,cursor);
			for (long id : ids.getIDs()) {
				listaFriends.add(id);
			}
		} while ((cursor = ids.getNextCursor()) != 0);
		return listaFriends;
	}
	
	/**
	 * Restituisce l'insieme di follower  dell'utente identificato da idUser
	 * @param idUser
	 * @return setFollower 
	 * @throws TwitterException
	 */
	public  Set<Long> getFollowers(long idUser) throws TwitterException{
		Set<Long> listaFriends=new HashSet<Long>();
		long cursor = -1;
		IDs ids;
		do{ count++;
			logger.info("("+this.count+")recupero followers di user "+idUser);
			//verifico se ho ragiunto il limite
			aspetta();
			ids=twitter.getFollowersIDs(idUser,cursor);
			for (long id : ids.getIDs()) {
				listaFriends.add(id);
			}
		} while ((cursor = ids.getNextCursor()) != 0);
		return listaFriends;
	}
	
	/**
	 * Questo metodo è necessario per rispettare i limiti di richiesta da inoltrare alle
	 * Twitter API
	 * @throws TwitterException
	 */
	private void aspetta() throws TwitterException{
		//verifico se mi devo fermare
		try{
			
			int remainigHits=twitter.getRateLimitStatus().getRemainingHits();
			logger.info("remaining hits:"+remainigHits);
			
			while(remainigHits==0){
					logger.info("aspetto 1 minuto ");
					Thread.sleep(60000);
					remainigHits=twitter.getRateLimitStatus().getRemainingHits();
					logger.info("remaining hits:"+remainigHits);
				}
				//this.count=0;
			
		}catch(InterruptedException e){
			throw new TwitterException("errore durante l'attesa dell'ora");
		}
		
	}
	
	 
}
