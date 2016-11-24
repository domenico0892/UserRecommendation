package org.rm3umf.framework.eval.valutation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.rm3umf.framework.eval.ValutationFunction;
import org.rm3umf.framework.eval.Result;

/**
 * Questa classe implementa una strategia per verificare che il risultato dell'applicazione di una funzione di similaritò
 * (Result) sia corretto.
 * In particolare è necessaria un'altra funzione che dato un utente sia in grado di ricavare un ranking rispetto a quell'utente.
 * Per ogni utente si confrontano i due ranking, che chiamaremo R1 e R2, utilizzando la funzione di Kendall modificata 
 * (Fagin e altri "Comparing top k lists") e  normalizzata. Infine si effettua la media della distanza media tra i vari i ranking
 * degli utenti secondo le 2 funzioni.
 * 
 * ATTENZIONE: bisonga settare il parametri PENALITY che serve per stabilire cosa bisogna fare quando non si hanno
 * 			   abbastanza informazioni per confrontare R1 e R2 (PENALITY=0 approccio ottimistico , PENALITY=1 approcci pessimistico)
 *  
 * PENALITY=1
 * RandomSimilarity = 0.0066789739006126525 
 * JaccardNoWeight  = 0.07318365981402458
 * VectorSpaceModel = 0.08223713444245136
 * TemporalFunction(3) = 0.08871774426623702 | 0.08006976049338715 | 0.0953420686000775

 * CrossCorrelazione(-1) = 0.061899196254222875
 * 
 * PENALITY=0.5
 * 
 * 
 * @author giulz
 * @see Kwak e altri "What is Twitter, a social network or news media ?"
 * @see Fagin e altri "Comparing top k lists"
 */

public class MeanKendallTau implements ValutationFunction{
	
	private CompareFunction valutationFunction;
	private String nameFunction ;
	
	
	//Parametro dell'agoritmo : deve essere compreso tra 0 e 1
	public static final double PENALITY =  1.;
	
	
	public MeanKendallTau(CompareFunction valutationFunction){
		this.valutationFunction=valutationFunction;
		this.nameFunction="MKTau("+valutationFunction.getName()+")";
		
	}

	/**
	 * Retituisce la Kendall tau media tra il ranking del risultato e quello di un'atra fun di ranking (ad esempio
	 * basta su follower e following e implementata grazie a Lucene) 
	 * 
	 * @param result - il risultato dell'applicazione di una funzione di similarità tra utenti
	 * @return meanKendallTau - la Kendall tau media tra i ranking degli  utenti (del risultato) e quello della fun di ranking
	 */
	public double valutate(Result result) {
		int n = result.getN();
		
		double globalKendal=0;
		Set<Long> listUser = result.getUser();
		int userCardin =0;
		
		for(Long userid:listUser){
			userCardin++;
			System.out.println("USER : ("+userCardin+")"+ userid);
			
			//Recupero gli n utente migliori secondo la fun di valutazione
			List<Long> rilevanteUsers = this.valutationFunction.bestUsers(userid,n);
			

			//Recupero gli n best user rispetto all'utente userid in result
			List<Long> listToVatutate = result.getBestUsers(userid);
			
			double totalElem=listToVatutate.size();
			System.out.println("SIMILARITY FUNCTION");
		
			double localKendall =  kendallTau(rilevanteUsers,listToVatutate);
			System.out.println("user="+userid+"  K_user= "+localKendall+" ("+totalElem+")");
			 
			//aggiungo K alla global kendal
			globalKendal+=localKendall;
			
			System.out.println("Global_Kendal_Tau="+globalKendal);	
		}
		//faccio la media
		return  globalKendal/userCardin;
	}

	
	
	public static double kendallTau(List<Long> R1, List<Long> R2){
		
		
		//Conterrà il merge delle due liste cior R1 Union R2 
		Set<Long> mergedList= new HashSet<Long>();
		
		mergedList.addAll(R1);
		mergedList.addAll(R2);
		
		double totalElem=mergedList.size();
				
		double kendal_tau=0;
		//scorro la prima  lista
		for(Long r1:mergedList){
			//scorro la seconda lista
			for(Long r2: mergedList){
				double r1_R1 = R1.indexOf(r1); //mem la posizione di r1 nel ranking R1
				double r2_R1 = R1.indexOf(r2); //mem la posizione di r2 nel ranking R1
				
				double r1_R2 = R2.indexOf(r1); //mem la posizione di r1 nel ranking R2
				double r2_R2 = R2.indexOf(r2); //mem la posizione di r2 nel ranking R2
				//System.out.println(" r1="+r1+" - r2="+r2 );
				//System.out.println("R1(r1="+r1_R1+" r2="+r2_R1+") - R2(r1="+r1_R2+" r2="+r2_R2+")");
				
				/*
				 * CASO 1: r1 e r2 compaiono in entrabe le liste ma in ordine opposto
				 */
				//r1 e r2 appaiono in etrambe le top k list ma in ordine opposto
				if((r1_R1!=-1 && r2_R1!=-1 && r1_R2!=-1 && r2_R2!=-1)){
						if((r1_R1<r2_R1  &&  r1_R2>r2_R2) || (r1_R1>r2_R1  &&  r1_R2<r2_R2)) 
							kendal_tau++;
			   //	    System.out.println("3");
				}	
				
				/*
				 * CASO 2 : se ranking(r1) migliore di ranking(r2) in una lista e solo r2 compare nell'altra
				 */
				//Se r1 ha un ranking migliore di r2 in R1 e nell'altra lista R2 appare solo r2
				else if((r1_R1 != -1 && r2_R1 != -1 &&  r1_R1<r2_R1) && (r1_R2==-1 && r2_R2!=-1 )){
					kendal_tau++;
				}
				//Se r1 ha un ranking migliore di r1 in R1 e nell'altra lista R2 appare solo r2
				else if((r1_R2 != -1 && r2_R1 != -1 &&  r1_R2<r2_R2) && (r1_R1==-1 && r2_R1!=-1 )){
					kendal_tau++;
				}
				
				/*
				 * CASO 3 : r1 appare solo in una lista e r2 compare solo nell'altra
				 */
				// se solo r1 appare  in R1 e solo r2 apparere in R2
				else if((r1_R1!=-1 && r2_R1==-1)&&(r1_R2==-1 && r2_R2!=-1) ){
				//	System.out.println("1");
					kendal_tau++;
				//se solo r2 apppare in R1 e solo r1 appare in R2	
				}
				else if((r1_R2!=-1 && r2_R2==-1)&&(r1_R1==-1 && r2_R1!=-1) ){
					//	System.out.println("1");
						kendal_tau++;
				}
				
				/*
				 *CASO 4 : r1 e r2 appaiono o solo in R1 o solo in R2
				 */
				//r1 e r2 appaiono in solo in R1
				else if((r1_R1 !=-1 && r2_R1!=-1 )&&(r1_R2 ==-1 && r2_R2 ==-1 )  ){
				      kendal_tau+=PENALITY;	
				      
				}
				//r1 e r2 appaiono solo in R2
				else if ((r1_R2!=-1 && r2_R2!=-1)&&(r1_R1==-1 && r2_R1==-1) ){
					kendal_tau+=PENALITY;
				}	
			}
		}
		//Calcolo la kendal_tau normalizzata
		// K = 1-(kendal_tau/k^2)
		double den = totalElem*totalElem;
		double K = 1 - (kendal_tau/den);
		
		return K;
		
		
	}
	
	
	public String getNameFunction() {
		return this.nameFunction;
	}
	
	public static void main(String[] args){
		
		//List 1
		List<Long> test=new LinkedList<Long>();
		long l=1;
		test.add(l);
		long l1=2;
		test.add(l1);
		long l2=3;
		test.add(l2);
		
		//List 2
		List<Long> test2 = new LinkedList<Long>();
		long a=1;
		test2.add(a);
		long a1=2;
		test2.add(a1);
		long a2=3;
		test2.add(a2);
		
		
		//test2.remove(0);
	//	long l4=3;
		//test2.add(l4);
		
		System.out.println("kendall_tau="+kendallTau(test,test2));
		
	
		
	}
	
	

}
