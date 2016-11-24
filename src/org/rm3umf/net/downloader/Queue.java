/*
 * @(#)Queue.java	0.1 05/01/05
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */

package org.rm3umf.net.downloader;

import java.util.Collection;

/**
 * Interface of classes used by the downloader to store the urls to download.
 * <p>
 * The current implementation fo the downloader ensures that each thread accesses exclusively to the queue.
 * 
 * @author  Fabio Gasparetti
 * @version 0.1, 01/01/06
 */
public interface Queue {

	/**
	 * Returns an element in the queue removing it.
	 */
	public  String  pop() throws QueueException;

	/**
	 * Returns an element in the queue without removing it.
	 */
	public String get() throws QueueException;

	/**
	 * Enqueues a new url.
	 */
	public void enqueue(String url) throws QueueException;

	/**
	 * Enqueues a collection of urls.
	 */
	public void enqueue(Collection urls) throws QueueException;

	/**
	 * Clears the content in the queue. 
	 */
	public void clear() throws QueueException;

	/**
	 * Returns the number of urls in the queue.
	 */
	public long size() throws QueueException;
}
