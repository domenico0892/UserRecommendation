/*
 * @(#)DebugListener.java	0.1 02/03/05
 *
 * Copyright 2005 Fabio Gasparetti. All rights reserved.
 */

package org.rm3umf.net.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.*;

/**
 * Debugs downloader through the Logger class.
 * 
 * @author Fabio Gasparetti
 * @version 0.1, 02/03/05
 */
public class DebugListener implements DownloaderListener {
	private Logger logger = Logger.getLogger("org.tabularium.net.downloader");
	private int count=0;
	
	private List<ResourceDownloader> resources=new LinkedList<ResourceDownloader>();

	public void downloadCompleted(DownloadEvent ev) {
		
		System.out.println("list size:"+resources.size());
		logger.log(Level.INFO, "Downloading completed for the resource: "
				+ ev.getResource().getURL());
		logger.log(Level.FINE, "--- Start Resource Dumping ---");
		logger.log(Level.FINE, ev.getResource().toString());
		logger.log(Level.FINE, "--- End Resource Dumping ---");
		
		resources.add(ev.getResource());
		FileOutputStream os = null;
		
		
		try {
			File filename = File.createTempFile("debugListener", ".dat");
			os = new FileOutputStream(filename, false);
			os.write(ev.getResource().getObject());
			os.flush();
			os.getFD().sync();
			os.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "i/o error");
		}
	}

	public void error(DownloadEvent ev) {		
		logger.log(Level.INFO, "Error: "+ev.toString());
		if (ev.getException() != null) 
			ev.getException().printStackTrace();
	}

	public void queueEmpty(DownloadEvent ev) {
		logger.log(Level.INFO, "Queue empty");
	}
}
