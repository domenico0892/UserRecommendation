package org.rm3umf.framework.eval.valutation;

import java.util.List;

import org.rm3umf.lucene.FacadeLucene;

public class LuceneFriendFollower implements CompareFunction{

	private String name;

	public LuceneFriendFollower(){
		this.name="LuceneFriendFollower";
		//preparo lucene alla ricerca
		FacadeLucene.getInstance().prepareSearching();
	}

	public List<Long> bestUsers(long userid, int n) {
		List<Long> rilevanteUsers = FacadeLucene.getInstance().retriveByFollowerAndFriend(userid);
		if(rilevanteUsers.size()>n)
			rilevanteUsers=rilevanteUsers.subList(0, n);
		return rilevanteUsers;

	}

	public String getName() {		
		return this.name;
	}


}
