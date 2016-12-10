package org.rm3umf.framework.buildSVOmodel;

public class SyncCount {
	
	private static SyncCount instance;
	private int n;
	
	private SyncCount() {
		this.n = 0;
	}
	
	public static synchronized SyncCount getInstance() {
		if (instance == null)
			instance = new SyncCount();
		return instance;
	}
	
	public synchronized void inc () {
		n = n + 1;
	}
	
	public synchronized int getCount() { return n; }

}
