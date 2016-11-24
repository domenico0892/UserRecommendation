/*
 * @(#)PriorityQueue.java	0.1 12/28/04
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */
package org.rm3umf.net.downloader;

import java.util.Map;

/**
 * Extends the <code>Queue</code> with priorities for each url.
 * <p>
 * The interface extends the functionalities of <code>Queue</code>. It means
 * that the enqueue methods without priority parameters are still available. They should
 * insert new urls in the queue with a default priority.
 * 
 * @author Fabio Gasparetti, Federico Vigna
 * @version 0.1, 01/01/06
 */

public interface PriorityQueue extends Queue {
    /**
     * Enqueue an url in the queue with the specified priority.
     * 
     * @param url
     * @param priority
     */
    public void enqueue(String url, double priority) throws QueueException;
    
    /**
     * Enqueue a set of urls with the specified priority.
     * <p>
     * The map is composed of: <code>String</code> keys and <code>Double</code> values. 
     */
    public void enqueue(Map urls) throws QueueException;

    /**
     * Returns the priority of the given url.  
     * <p>
     * If the url is not enqueued, it returns <tt>-1</tt>.
     */
    public double getPriority(String url) throws QueueException;
}
