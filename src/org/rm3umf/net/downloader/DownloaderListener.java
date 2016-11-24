/*
 * @(#)DownloaderListener.java	0.1 12/28/04
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */

package org.rm3umf.net.downloader;

import java.util.EventListener;

/**
 * A Listener interface for the downloader events.
 *
 * @author  Fabio Gasparetti
 * @version 0.1, 12/28/04
 */
public interface DownloaderListener extends EventListener {

	/**
	 * 
	 * @param ev
	 */
    public void downloadCompleted(DownloadEvent ev);

    /**
     * An empty resource is returned if an error occurs.
     * The method downloadCompleted is not invoked.
     * 
     * Note: events' exception can be null. 
     */
    public void error(DownloadEvent ev);

    /**
     * 
     * @param ev
     */
    public void queueEmpty(DownloadEvent ev);

}
