package org.rm3umf.domain;

import java.util.List;
import java.util.Set;

import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;


public class User {
	
	private long iduser;
	private List<String> usernames;
	private boolean isPublicProfile;
	
//	//questa informazione � utilizzata per la validazione del modello
//	private Set<Long> followed;
//	private Set<Long> follower;
//	private Set<Long> influentialFollowed; //solo tutti quei followed che non mi seguono 
	
	
	
	
	
	
	public List<String> getUsernames() {
		return usernames;
	}
	public void setUsernames(List<String> usernames) {
		this.usernames = usernames;
	}
	
	public long getIduser() {
		return iduser;
	}
	public void setIduser(long iduser) {
		this.iduser = iduser;
	}
	
	public void save() throws PersistenceException {
		AAFacadePersistence.getInstance().userSave(this);
	}
	
	public boolean getIsPublicProfile(){
		return this.isPublicProfile;
	}
	
	public void setIsPublicProfile(boolean isPublicProfile){
		this.isPublicProfile=isPublicProfile;
	}
	
	
	public String toString(){
		return "[USER: id="+this.iduser+"]";
	}
}
