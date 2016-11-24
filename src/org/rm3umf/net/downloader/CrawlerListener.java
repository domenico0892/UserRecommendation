package org.rm3umf.net.downloader;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.*;
import org.rm3umf.net.downloader.DownloadEvent;
import org.rm3umf.net.downloader.DownloaderListener;
import org.rm3umf.net.downloader.ResourceDownloader;



/**
 * Used to interact with the downloader and store the downloaded resources.
 * See Server.store().
 */
public class CrawlerListener implements DownloaderListener {
	// it can be null
	public List<ResourceDownloader> resources = null;

	protected Logger logger = Logger.getLogger(CrawlerListener.class.getName());

	public CrawlerListener() {
		this.resources=new LinkedList<ResourceDownloader>();
	}

	public void downloadCompleted(DownloadEvent parm1) {
		ResourceDownloader res;
		/** @todo think about escaping urls */
		res = parm1.getResource();
		
		resources.add(res);
		String ct = res.getContentType();
		System.out.println("resources size :" + resources.size());
		// ignore docs downloaded without success or no-text docs.
		if ((res.getStatusCode() != 200) || (res.getContentLength() <= 0) || (("text/plain".compareToIgnoreCase(ct) != 0) && (!"text/html".startsWith(ct.toLowerCase()))   )) {
			logger.info("ignoring resource: "+res.getURL()+" with status code:"+res.getStatusCode()+" content-type:"+ct+" content-length:"+res.getContentLength());
			res = null;
		}
		
	}

	public void error(DownloadEvent parm1) {
		ResourceDownloader res=parm1.getResource();
		this.resources.add(res);
		System.err.println("DownloadEvent : imposible download page:"+res.getURL()+ " cause:"+ parm1.getException().toString());
		//parm1.getException().printStackTrace();
	}

	public void queueEmpty(DownloadEvent parm1) {
		
	}
}