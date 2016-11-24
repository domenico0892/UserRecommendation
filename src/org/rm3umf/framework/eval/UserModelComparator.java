package org.rm3umf.framework.eval;

import java.util.Comparator;

import org.rm3umf.domain.UserModel;

/**
 * 
 * @author giulz
 *
 */
public class UserModelComparator implements Comparator<UserModel>{

	public int compare(UserModel o1, UserModel o2) {
		return Long.compare(o1.getUser().getIduser(),o2.getUser().getIduser());
	}

}
