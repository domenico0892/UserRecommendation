package org.rm3umf.framework.importing;

import java.util.List;
import java.util.Set;

import org.rm3umf.net.twitter.FacadeTwitter4j;

import twitter4j.TwitterException;

/**
 * Questa classe ha la responsabilità di arricchire il modello interrogando Twitter ottenendo le
 * informazioni aggiuntive
 * @author giulz
 *
 */
public class SocialEnricher {
	
	
	/**
	 * Questo metodo interroga Twitter chiedendogli i followers dell'utente identificato dall'id
	 * se il profilo è privato ritornerrà null
	 * @param userid
	 * @return listaFollower
	 */
	
	public Set<Long> getFollower(long userid){
		Set<Long> listaFollower = null;
		
		try{
			
			listaFollower=FacadeTwitter4j.getInstace().getFollowers(userid);
		}catch(TwitterException e){
			System.err.println("impossibile recuperare di follower di "+userid);
			
		}
		return listaFollower;
	}
	
	

	/**
	 * Questo metodo interroga Twitter chiedendogli i followers dell'utente identificato dall'id
	 * se il profilo è privato ritornerrà null
	 * @param userid
	 * @return listaFollower
	 */
	
	public Set<Long> getFollowed(long userid){
		Set<Long> listaFollowed = null;
		
		try{
			listaFollowed=FacadeTwitter4j.getInstace().getFolloweds(userid);
		}catch(TwitterException e){
			System.err.println("impossibile recuperare di followed di "+userid);
		}
		return listaFollowed;
	}

	
	
		
	

	
	
	

}
