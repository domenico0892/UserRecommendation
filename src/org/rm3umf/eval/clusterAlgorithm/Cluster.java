package org.rm3umf.eval.clusterAlgorithm;

import java.util.LinkedList;
import java.util.List;

import org.rm3umf.domain.UserModel;
import org.rm3umf.framework.eval.similarityFunction.Similarity;






public class Cluster {
	
	private List<UserModel> elements;
	private UserModel medoid;
	private int numeroCluster;
	private Similarity similarity;
	
	
	public Cluster(int numeroCluster,UserModel medoid,Similarity similarity){
		this.elements=new LinkedList<UserModel>();
		this.medoid=medoid;
		this.numeroCluster=numeroCluster;
		this.similarity=similarity; //
	}
	

	
	
	public String toString(){
		return "[CLUSTER "+numeroCluster+" medoids:"+medoid+" size:"+elements.size()+"]";
	}




	public void addElement(UserModel medoid) {
		this.elements.add(medoid);
		
	}
	
	public double getSimilarity(UserModel um){
		return similarity.getSimilarity(this.medoid,um);
	}




	public List<UserModel> getElements() {
		return this.elements;
	}
	
	public UserModel calculateNewMedoids(){
		double maxTotalError=calculateTotalError(this.elements,this.medoid);
		UserModel newMedoid=this.medoid;
		for(UserModel userCorr:this.elements){
			double corrTotaError=calculateTotalError(this.elements,userCorr);
			if(corrTotaError>maxTotalError ){
				maxTotalError=corrTotaError;
				newMedoid=userCorr;
			}
		}
		if(!this.medoid.equals(newMedoid)){
			this.medoid=newMedoid;
		}
		return this.medoid;
	}
	
	/**
	 * Calcola l'errore totale dato un cluster e il suo mediod
	 * @param cluster
	 * @param mediod
	 * @return
	 */
	private double calculateTotalError(List<UserModel> cluster,UserModel mediod){
		double totalError=0.0;
		
		//calcolo l'errore totale
		for(UserModel userCorr:cluster){
			totalError+=similarity.getSimilarity(userCorr, mediod);			
		}
		return totalError;
		
	}


	public UserModel getMedoid() {
		// TODO Auto-generated method stub
		return this.medoid;
	}
	

}
