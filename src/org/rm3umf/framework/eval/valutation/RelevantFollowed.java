package org.rm3umf.framework.eval.valutation;

import java.util.List;
import java.util.Set;

import org.rm3umf.framework.eval.ValutationFunction;
import org.rm3umf.framework.eval.Result;
import org.rm3umf.lucene.FacadeLucene;

import twitter4j.internal.logging.Logger;



	/**
	 * Implementa un'euristica per la valutazione  di una funzione di similarità.
	 * In questa implementazione vengono condiderati rilevanti i primi N utenti restituiti dall'indice che considera
	 * i followed e i follower.
	 * Verrà dato un punteggio diverso a seconda della posizione in cui viene
	 * trovato un utente rilevante nel ranking effettuato seconda la funzione di similarità.
	 *    
	 * @author giulz
	 *
	 */
	public class RelevantFollowed implements ValutationFunction{
		
		
		private static Logger logger = Logger.getLogger(RelevantFollowed.class);
	
		
		
		public RelevantFollowed(){
			//proparo lucene alla ricerca
			FacadeLucene.getInstance().prepareSearching();
		}
		
		
		public double valutate(Result result) {
			int globalScore=0;
			Set<Long> listUser = result.getUser();
			int indice =0;
			for(Long userid:listUser){
				indice++;
				System.out.println("USER : ("+indice+")"+ userid);
				List<Long> rilevanteUsers = FacadeLucene.getInstance().retriveRelevantFollower(userid);
				List<Long> listToVatutate = result.getBestUsers(userid);
				System.out.println("RELEVANT FOLLOWER");
				
				//Modella il fatto che se trovo un utente rilevante alla prima posizione è diverso che se lo trovo alla decimo
				//e quindi lo score verrà incremetnato diversamente
				int score=10;
				for(Long u:listToVatutate){
					int index=rilevanteUsers.indexOf(u);
					System.out.println("     ("+index+")"+u);
					
					if(index<=10 && index!=-1){
						globalScore=globalScore+score;
						break;
					}
					score--;
				}
				
				System.out.println("relevantFolloweer="+globalScore);
				
			}
			
			return globalScore ;
		}


		public String getNameFunction() {
			// TODO Auto-generated method stub
			return null;
		}


}
