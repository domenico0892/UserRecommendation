package org.rm3umf.persistenza;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.rm3umf.domain.*;
import org.rm3umf.persistenza.postgreSQL.*;




public class AAFacadePersistence {

	private static AAFacadePersistence instance;
	private MessageDAO messageDAO;
	private UserDAO userDAO;
	private PeriodDAO periodDAO;
	private ConceptDAO conceptDAO;
	private SignalComponentDAO signalComponentDAO;
	private SignalDAO signalDAO;

	private PseudoDocumentDAO pseudoDocumentDAO;
	private ResourceDAOpostgreSQL resourceDAO;

	public AAFacadePersistence(){
		this.messageDAO=new MessageDAOpostgreSQL();
		this.userDAO=new UserDAOpostgreSQL();
		this.periodDAO=new PeriodDAOpostgreSQL();
		this.conceptDAO=new ConceptDAOpostgreSQL() ;
		this.signalComponentDAO=new SignalComponentDAOpostgreSQL();
		this.signalDAO=new SignalDAOpostgreSQL();
		this.pseudoDocumentDAO=new PseudoDocumentDAOpostgreSQL();
		this.resourceDAO=new ResourceDAOpostgreSQL();
	}

	public synchronized static AAFacadePersistence getInstance(){
		if(instance==null)
			instance=new AAFacadePersistence();
		return instance;	
	}



	/**
	 * Prepara il db alla fase di costruzione dei segnali
	 * @throws PersistenceException
	 */
	public void prepareDBBuilderSignal()throws PersistenceException{		
		AAFacadePersistence.getInstance().signalComponentDeleteAll();
		AAFacadePersistence.getInstance().signalDelete();
		AAFacadePersistence.getInstance().conceptDeleteAll();
		AAFacadePersistence.getInstance().periodDelete();
		AAFacadePersistence.getInstance().resourceDelete();
	}

	/**
	 * Prepara il db alla fase di importing 
	 */
	public void prepareDBImporting()throws PersistenceException{
		userDeleteFollowed();
		userDeleteFollower();
		signalComponentDeleteAll();
		signalDelete();
		resourceDelete();
		conceptDeleteAll();
		periodDelete();
		messageDelete();
		userDelete();
	}

	/**
	 * USER
	 */


	public void deleteForBuildModel() throws PersistenceException {
		String query = "truncate table concept;truncate table signalcomponent;truncate table period;truncate table `signal`;";
		Connection c = DataSourcePostgreSQL.getInstance().getConnection();
		try {
			PreparedStatement p = c.prepareStatement(query);
			p.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new PersistenceException(e.getMessage());
		}
		
		
	}
	public void userSave(User user) throws PersistenceException{
		userDAO.save(user);
	}

	public List<User> userRetriveAll() throws PersistenceException{
		return userDAO.doRetrieveAll();
	}

	public void userSaveFollowed(User user, Set<Long> listFollowed) throws PersistenceException{
		for(Long l:listFollowed){
			userDAO.saveFollowed(user.getIduser(),l);
		}
	}

	public void userSaveFollower(User user, Set<Long> listFollower) throws PersistenceException{
		for(Long l:listFollower){
			userDAO.saveFollower(user.getIduser(),l);
		}
	}

	public Set<Long> userGetFollowed(long userid) throws PersistenceException{
		return userDAO.retriveFollowedById(userid);
	}

	public Set<Long> userGetFollower(long userid) throws PersistenceException{
		return userDAO.retriveFollowerById(userid);
	}


	public void userDeleteFollowed() throws PersistenceException{
		userDAO.deleteFollowed();
	}

	public void userDeleteFollower() throws PersistenceException{
		userDAO.deleteFollower();
	}

	public void userDelete() throws PersistenceException{
		userDAO.deleteFollower();
		userDAO.deleteFollowed();
		userDAO.delete();
	}

	/**
	 *MESSAGE 
	 */

	public void messageSave(Message message) throws PersistenceException{
		this.messageDAO.save(message);
	}

	public List<Message> messageRetriveByUser(User u) throws PersistenceException{
		return messageDAO.retriveByUser(u);
	}

	public void messageDelete() throws PersistenceException{
		messageDAO.delete();
	}

	/**
	 * PERIOD
	 */
	public void periodDelete() throws PersistenceException{
		this.periodDAO.delete();		
	}

	public String periodGetMaxDate() throws PersistenceException{ 
		return	periodDAO.getMaxDate();
	}

	public String periodGetMinDate() throws PersistenceException{ 
		return	periodDAO.getMinDate();
	}

	public void periodSave(Period period) throws PersistenceException{
		this.periodDAO.save(period);
	}

	public List<Period> periodRetriveAll() throws PersistenceException{
		return this.periodDAO.doRetriveAll();
	}

	/**
	 * CONCEPT
	 */
	/**
	 * Cancella tutta la relazione concept
	 */
	public void conceptDeleteAll() throws PersistenceException{
		conceptDAO.deleteAll();
	}

	/**
	 * Recupera tutti i concept presenti nel DB
	 */
	public List<Concept> conceptRetrieveAll() throws PersistenceException{
		return conceptDAO.doRetrieveAll();
	}

	/**
	 *Cancella il concept passato come paramentro 
	 */
	public void conceptDelete(Concept concept) throws PersistenceException{
		conceptDAO.delete(concept);
	}

	/**
	 *Recupera concept che sono stati referenzaiti meno della soglai threshold 
	 */
	public List<Concept> conceptRetriveInrilevante(int threshold) throws PersistenceException{
		return conceptDAO.doRetrieveConceptInrilenvate(threshold);
	}

	/**
	 * PSEUDO-DOCUMENT
	 */
	public PseudoFragment pseudoDocumentGet(User user,Period period) throws PersistenceException {
		return pseudoDocumentDAO.doRetrieve(user, period);
	}

	public List<PseudoFragment> pseudoDocumentGetByPeriod(Period period) throws PersistenceException{
		return pseudoDocumentDAO.doRetriveByPeriod( period);
	}

	public List<Concept> getInstanceConcept2period(User user,Period period)throws PersistenceException{
		return conceptDAO.getConceptForUserAndPeriod(user, period);
	}

	/**
	 * SIGNAL COMPONENT
	 */

	/**
	 *Salva il signalComponent passato come parametro  
	 */
	public void signalComponentSave(SignalComponent sigComp) throws PersistenceException{
		//salvo e aggiorno il signalComponent (se � gia salvato si incremtea di uno il contatore nella relazione)
		conceptDAO.save(sigComp.getConcept()); //salvo il concept se non presente
		signalComponentDAO.save(sigComp);
	}

	/**
	 * Recupera tutti i SignalComponent per User
	 */
	public List<SignalComponent> signaComponentRetriveByUser(User user) throws PersistenceException{
		return signalComponentDAO.doRetrieveByUserid(user.getIduser());
	}

	/**
	 * Recupera tutti i SignalComponent per Period
	 */
	public List<SignalComponent> signalComponentRetriveByPeriod(Period period) throws PersistenceException{
		return signalComponentDAO.doRetrieveByPeriodid(period.getIdPeriodo());
	}

	/**
	 * Recupera tutti i SignalComponent per Concept
	 */
	public List<SignalComponent> signalComponentRetriveByConcept(Concept concept) throws PersistenceException{
		return signalComponentDAO.doRetrieveByConceptid(concept.getId());
	}

	/**
	 * Cancello il SignalComponent passato come parametro
	 */
	public void signalComponentDelete(SignalComponent signalComponent) throws PersistenceException{
		signalComponentDAO.delete(signalComponent);
	}

	public void signalComponentDeleteAll() throws PersistenceException{
		signalComponentDAO.deleteAll();
	}
	/**
	 * Cancella SignalComponent per concepid
	 */
	public void signalComponentDeleteByConcept(Concept concept) throws PersistenceException{
		signalComponentDAO.deleteByConceptid(concept.getId());
	}

	/**
	 * SIGNAL
	 */
	public void signalSave(Signal signal) throws PersistenceException{
		this.signalDAO.save(signal);
	}

	public void signalDelete() throws PersistenceException{
		this.signalDAO.deleteAll();
	}

	/**
	 * USER MODEL
	 */

	public UserModel userModelRetriveByUser(User user) throws PersistenceException{
		List<Signal> listSignal=signalDAO.doRetrieveByUser(user);
		UserModel um=new UserModel(user, listSignal);
		return um;
	}

	/**
	 * Recupera tutti gli user model presenti nel sistema (cioe tutti gli um relativi ad utenti che hanno almeno un 
	 * segnale
	 */
	public List<UserModel> userModelRetriveAll() throws PersistenceException{
		List<Long> listUsers=signalDAO.retriveUserid();
		List<UserModel> listUserRappresentation=new LinkedList<UserModel>();
		//per ogni utente devo recuperare la rappresentazione utente composta dall'insieme di segnali
		for(int i=0 ; i<listUsers.size();i++ ){

			long userid = listUsers.get(i);
			//recupero l'utente
			User u=userDAO.doRetriveUserById(userid);
			System.out.print("recupero user rappresentation di:"+u.getIduser());
			//recupero tutti i segnali per l'utente u
			List<Signal> listSignal=signalDAO.doRetrieveByUser(u);
			//creo l'oggetto user rappresentation relativo all'utente u
			UserModel ur=new UserModel(u, listSignal);
			System.out.println("->"+ur);
			listUserRappresentation.add(ur);
		}
		return listUserRappresentation;
	}

	/**
	 * Cancella lo UserModel passato come parametro (in pratica cancella i segnali)
	 */
	public void userModelDelete(UserModel um) throws PersistenceException{
		List<Signal> signals = um.getSignals();
		for(Signal s:signals)
			signalDAO.delete(s);
	}

	/**
	 * RESOURCE
	 */

	public void resourceSave(Resource resource) throws PersistenceException{
		this.resourceDAO.save(resource);
	}

	public Resource resourceGetById(String id) throws PersistenceException{
		return this.resourceDAO.doRetrieveById(id);
	}

	public void resourceDelete() throws PersistenceException{
		this.resourceDAO.delete();
	}





}
