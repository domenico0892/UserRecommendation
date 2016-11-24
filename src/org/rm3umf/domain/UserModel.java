package org.rm3umf.domain;
import java.util.List;




public class UserModel {
	
	private User user;
	private List<Signal> signals;
	
	
	public UserModel(){
		
	}
	
	public UserModel(User user,List<Signal> listSignal){
		this.user=user;
		this.signals=listSignal;	
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Signal> getSignals() {
		return signals;
	}

	public void setSignals(List<Signal> signals) {
		this.signals = signals;
	}
	
	public boolean equals(Object um){
		return this.user.getIduser()==(((UserModel)um).getUser().getIduser() );
	}
	
	
	public String toString(){
		return "[USER MODEL :"+user.getIduser()+" numSignal="+signals.size()+"]";
	}

	
	

}
