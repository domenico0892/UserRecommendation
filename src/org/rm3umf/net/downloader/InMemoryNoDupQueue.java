/*
 * @(#)InMemoryNoDupQueue.java	0.1 05/01/05
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */

package org.rm3umf.net.downloader;

import java.util.*;

/**
 * In-memory implementation of the <code>Queue</code> where url duplicates are
 * not allowed.
 * <p>
 * This implementation ensures that the downloader gets each url only once. The
 * same url can not be enqueued more the once, even if it has been already
 * downloaded.
 * 
 * @see InMemoryQueue
 * @author Fabio Gasparetti, Federico Vigna
 * @version 0.1, 01/01/06
 */
public class InMemoryNoDupQueue implements Queue {
	private Collection urls = null;

	private Collection urlsDone = null;

	public InMemoryNoDupQueue() {
		urls = new ArrayList();
		urlsDone = new ArrayList();
	}

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Queue#get()
     */
	public String get() throws QueueException {
		String url = null;
		Iterator iter = urls.iterator();
		if (iter.hasNext()) {
			url = (String) iter.next();
		}
		return url;
	}

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Queue#get()
     */
	public synchronized String pop() throws QueueException {
		String url = null;
		Iterator iter = urls.iterator();
		if (iter.hasNext()) {
			url = (String) iter.next();
			urls.remove(url);
			urlsDone.add(url);
		}
		return url;
	}

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Queue#enqueue(String)
     */
	public void enqueue(String url) throws QueueException {
		if (!urlsDone.contains(url)) 
			urls.add(url);
	}

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Queue#enqueue(Collection)
     */
	public void enqueue(Collection l) throws QueueException {
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			String url = (String) iter.next();
			if (!urlsDone.contains(url)) {
				this.urls.add(url);
			}
		}
	}

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Queue#size()
     */
	public long size() throws QueueException {
		return urls.size();
	}

    /** 
     * Clears the queue.
     * <p>
     * The list of urls already downloaded will be kept.
     * 
     * @see org.rm3umf.net.downloader.Queue#clear()
     */
	public void clear() throws QueueException {
		urls.clear();
	}
}
