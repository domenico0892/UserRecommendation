/*
 * @(#)StaticQueue.java	0.1 05/01/05
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */

package org.rm3umf.net.downloader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * In-memory implementation of the <code>Queue</code> interface, where it is
 * possible to enqueue the same url more the once during the download.
 * 
 * 
 * @see InMemoryNoDupQueue
 * @author Fabio Gasparetti
 * @version 0.1, 05/01/06
 */
public class InMemoryQueue implements Queue {
	protected Collection urls = null;

	public InMemoryQueue() {
		urls = new ArrayList();
	}

	/** (non-Javadoc)
	 * @see org.rm3umf.net.downloader.Queue#get()
	 */
	public String get() throws QueueException {
		String url = null;
		Iterator iter = urls.iterator();
		if (iter.hasNext()) {
			url = (String) iter.next();
		}
		return url;
	}

	/** (non-Javadoc)
	 * @see org.rm3umf.net.downloader.Queue#get()
	 */
	public synchronized String pop() throws QueueException {
		String url = null;
		Iterator iter = urls.iterator();
		if (iter.hasNext()) {
			url = (String) iter.next();
			urls.remove(url);
		}
		return url;
	}

	/** (non-Javadoc)
	 * @see org.rm3umf.net.downloader.Queue#enqueue(String)
	 */
	public void enqueue(String url) throws QueueException {
		// ArrayList append elements to the end
		urls.add(url);
	}

	/** (non-Javadoc)
	 * @see org.rm3umf.net.downloader.Queue#enqueue(Collection)
	 */
	public void enqueue(Collection l) throws QueueException {
		// ArrayList append elements to the end
		this.urls.addAll(l);
	}

	/** (non-Javadoc)
	 * @see org.rm3umf.net.downloader.Queue#size()
	 */
	public long size() throws QueueException {
		return urls.size();
	}

	/** (non-Javadoc)
	 * @see org.rm3umf.net.downloader.Queue#clear()
	 */
	public void clear() throws QueueException {
		urls.clear();
	}
}
