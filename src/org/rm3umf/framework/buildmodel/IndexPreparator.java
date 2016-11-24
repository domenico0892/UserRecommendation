package org.rm3umf.framework.buildmodel;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rm3umf.domain.Message;
import org.rm3umf.domain.User;
import org.rm3umf.domain.UserModel;
import org.rm3umf.lucene.FacadeLucene;
import org.rm3umf.persistenza.AAFacadePersistence;
import org.rm3umf.persistenza.PersistenceException;

import util.UtilText;

public class IndexPreparator {
	
	
	private String pathIndex;
	
	
	public IndexPreparator(String pathIndex){
		this.pathIndex=pathIndex;
		
	}
	
	public void prepareIndex(List<UserModel> usermodels) throws PersistenceException{
		
		/*
		 * Preparo il sistema alla validazione del modello costruito
		 * E' qui che devo indicizzare con Lucene perchè le rappresentazioni utente senza una cerata soglia di segnali
		 * non le devo neanche indicizzare perchè se non ho abbastanza segnali non riesco a confrontare un UM  
		 * efficacemente con gli altri. 
		 */
		
		FacadeLucene facadeLucene=new FacadeLucene(this.pathIndex);
		facadeLucene.iniziaIndicizzazione();
		//recupero tutti i modelli utente rrelativi agli utente che hanno almento un segnale
		List<UserModel> modelliUtente = AAFacadePersistence.getInstance().userModelRetriveAll(); 
		for(UserModel um: modelliUtente){
			
			//verifico quanti sengali ha la rappresentazione
			//int numOfSignal = um.getSignals().size();
			//Se um ha più segnali della soglia allora indicizzalo 
		
				//se è maggiore indicizziamo 
				User user = um.getUser();
				//recupero dal db i followers e i followed 
				Set<Long> listaFollower=AAFacadePersistence.getInstance().userGetFollower(user.getIduser());
				Set<Long> listaFollowed=AAFacadePersistence.getInstance().userGetFollowed(user.getIduser());
				//costruisco la lista (followed diff followers)
//				Set<Long> rilevantFollowers=new HashSet<Long>(listaFollowed);
//				rilevantFollowers.removeAll(listaFollower);
				Set<Long> rilevantFollowers=null;
				
				//String pseudodocument = getPseudoDocument(um.getUser());
				String pseudodocument = "";
				facadeLucene.addDocument(user,listaFollower,listaFollowed,rilevantFollowers,pseudodocument);
				
				//faccio una free
				um=null;	
				
			}
		
		//chiudo l'indice
		facadeLucene.fineIndicizzazione();
		
		
	}
	
	
	
	private String getPseudoDocument(User user) throws PersistenceException{
		List<Message> listMessage=AAFacadePersistence.getInstance().messageRetriveByUser(user);
		String pseudoDocument = "";
		for(Message m:listMessage){
			pseudoDocument=pseudoDocument+" \n"+m.getText();
		}
		
		//System.out.println(pseudoDocument);
		return UtilText.getInstance().removeStopWord(pseudoDocument);
		
	}
	
	
	
	

}
