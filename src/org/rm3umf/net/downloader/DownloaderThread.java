/*
 * @(#)DownloadThread.java	0.1 12/28/04
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */

package org.rm3umf.net.downloader;

import java.io.*;

import org.apache.log4j.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/**
 * A single crawler.
 * 
 * @author Fabio Gasparetti
 * @version 0.1, 12/28/04
 */
class DownloaderThread extends Thread {
	/**
	 * @todo multi-protocol implementation: one helper that gets an url and
	 *       returns a (Interface) Resource instance
	 */
	// for a full debugging add the DebugListener
	public static final int INIT = 0; // initialized

	public static final int STOP = 1; // no longer running

	public static final int IDLE = 2; // waiting for an url to download

	public static final int RUNNING = 3; // downloading

	public static final int DIM_READ_BUFFER = 1024; // input stream buffer

	protected int status;

	protected HttpClient client = null;

	protected HttpMethod method = null; // allows connection stops

	protected Downloader downloader = null;

	protected String url = null;

	protected ResourceFactory resFactory = null;

	// true if the conneciton has been aborted by the user
	protected boolean aborted = false;

	private Logger logger = Logger.getLogger("org.tabularium.net.downloader");

	DownloaderThread(Downloader downloader, HttpClient client,
			ResourceFactory resFactory) {
		this.downloader = downloader;
		this.client = client;
		this.resFactory = resFactory;
		status = INIT;
	}

	/**
	 * Pulls out an url from the queue and attempts to download it. If some
	 * exception occours an error-event is fired.
	 */
	public void run() {
		/**
		 * @todo fix "WARNING: Going to buffer response body of large or unknown
		 *       size. Using getResponseAsStream instead is recommended"
		 */
		status = RUNNING;

		int maxRedirectAttempts = downloader.getMaxRedirectAttempts();
		int maxSize = downloader.getMaxSize();

		Exception ex = null; // returned exception
		while (!downloader.stopped()) {
			try {
				url = downloader.getUrl();
			} catch (QueueException e) {
				url = null;
				downloader.fireError(downloader, null, e);
			}
			if (url == null) {
				synchronized (this) {
					try {
						/*
						 * the timout is needed in case Downloader.lazyStop() or
						 * Downloader.addUrl() are invoked just after the thread
						 * has called the method getUrl() and before the wait()
						 * call, in order to avoid leaving the thread waiting
						 * forever.
						 */
						status = IDLE;
						wait(1000);
					} catch (Exception ex1) {
					}
				}
				status = RUNNING;
				continue;
			}
			logger.log(Level.DEBUG, "Attempt to download: " + url);
			// create an empty resource useful as object to return if an error
			// occurs
			ResourceDownloader res = resFactory.emptyResource();
			res.setValue(ResourceDownloader.URL, url);

			try {
				// synchronized because alert() could alter the variable method
				synchronized (this) {
					// create a method instance.
					method = new GetMethod(url);
					// GetMethods follows redirect requests by default...
					// but I will take care of redirects
					method.setFollowRedirects(false);
				}
				// catches exceptions such as: IllegalArgumentException bad urls
			} catch (IllegalArgumentException e) {
				// when URI is invalid
				downloader.fireError(downloader, res, e);
				continue;
			} catch (IllegalStateException e) {
				// when protocol of the absolute URI is not recognised
				downloader.fireError(downloader, res, e);
				continue;
			} catch (Exception e) {
				// shouldn't be ever executed
				downloader.fireError(downloader, res, e);
				continue;
			}

			int statusCode = -1;
			int attempt = 0;
			/* @see RetryHandler */
			for (; statusCode == -1 && attempt < maxRedirectAttempts; attempt++) {
				statusCode = -1;
				try {
					//logger.fine("DOWNLOADER scarico ure:"+url);
					logger.log(Level.DEBUG, "Start download:" + url);
					statusCode = client.executeMethod(method);
				} catch (HttpException e) {
					// Signals that an HTTP or HttpClient exception has occurred
					ex = e;
					logger.log(Level.DEBUG,
							"A protocol exception occured on url:" + url
									+ " exception:" + e.toString());
				} catch (IOException e) {
					// an I/O (transport) error occurs.
					// shouldn't be called anylonger because of HttpException
					ex = e;
					logger.log(Level.DEBUG, "An I/O error occured on url:" + url
							+ " exception:" + e.toString());
				} catch (Exception e) {
					// catches some particular exceptions such as
					// IllegalArgumentException raised when an illegal url is
					// obtained in a redirection
					ex = e;
					logger.log(Level.DEBUG,
							"A general exception occured on url:" + url
									+ " exception:" + e.toString());
				}

				// check redirect codes; each redirection is a new attempt
				// see
				// http://jakarta.apache.org/commons/httpclient/redirects.html
				switch (statusCode) {
				// The most common redirect response codes
				case HttpStatus.SC_MOVED_TEMPORARILY:
				case HttpStatus.SC_MOVED_PERMANENTLY:
				case HttpStatus.SC_SEE_OTHER:
				case HttpStatus.SC_TEMPORARY_REDIRECT:
					// httpclient tutorial says:
					// It is vital that the response body is always read
					// regardless of the status returned by the server
					try {
						/* byte[] dummy = */method.getResponseBody();
					} catch (Exception e) { }

					// may I redirect?
					if (!downloader.followRedirects()) {
						// Follow-redirect is false
						// set no-more attempts allowed (exit)
						attempt = maxRedirectAttempts;
						ex = new HttpException(
								"Following-redirect disabled. Stop download of: "
										+ url);
						break;
					}
					// yes, so retrieve the new location
					Header locationHeader = null;
					locationHeader = method.getResponseHeader("location");
					if (locationHeader == null) {
						statusCode = -1;
						ex = new HttpException(
								"Redirection requested but no location in the header");
						break;
					}
					String redirectLocation = locationHeader.getValue();
					logger.log(Level.DEBUG, "Redirection. Status code: "
							+ statusCode + " on url:" + url + " to new url:"
							+ redirectLocation + " (Attempt " + (attempt+1) + " of "
							+ maxRedirectAttempts + ")");
					statusCode = -1; // retry
					try {
						synchronized (method) {
							// create a new method instance
							method.releaseConnection();
							method = new GetMethod(redirectLocation);
							method.setFollowRedirects(false);
						}
					} catch (Exception e) {
						// unrecoverable exception: IllegalArgumentException or
						// IllegalStateException
						// set no-more attempts allowed (exit)
						attempt = maxRedirectAttempts;
						ex = e;
					}
					break;
				case -1: // here 'cos of exception raised by HttpMethod.executeMethod()
					// the retry handler should have retried as much as it could
					// so set no-more attempts allowed (exit)
					attempt = maxRedirectAttempts;
					break;
				default: // here 'cos of other redirection codes 
					// ignores any other redirection codes and exit  
					// (set no-more attempts allowed)
					// here also becasue of 20x codes; 
					// attempt=max let the process go out the for loop 
					attempt = maxRedirectAttempts;
				}
			} // redirect attempt for


			// Check if we are here because we exceeded the max redirect
			// attempts or unrecoverable exception occurs
			if (statusCode == -1) {
				downloader.fireError(downloader, res, ex);
			} else if (attempt == maxRedirectAttempts) { // statusCode != -1
				// statusCode = some redirection code
				// do not propagate exceptions but set STATUS_CODE in the Resource
				res.setValue(ResourceDownloader.STATUS_CODE, statusCode);				
				downloader.fireError(downloader, res, null);
			} else {
				// read the response body through an InputStream
				try {
					byte[] data = new byte[DIM_READ_BUFFER];
					InputStream is = null;
					// not shure if aborting during getResponseBodyAsStream is
					// safe ... better synchronize!
					synchronized (method) {
						is = method.getResponseBodyAsStream();
					}
					if (is == null) 
						throw new IOException("Response body is not available.");
					// read meta-data
					res.setValue(ResourceDownloader.CHAR_SET, ((HttpMethodBase) method)
							.getResponseCharSet());
					res.setValue(ResourceDownloader.STATUS_CODE,
							((HttpMethodBase) method).getStatusCode());
					res.setValue(ResourceDownloader.REASON_PHRASE,
							((HttpMethodBase) method).getStatusLine()
									.getReasonPhrase());
					if (((HttpMethodBase) method).getResponseContentLength() >= 0) {
						res.setValue(ResourceDownloader.CONTENT_LENGTH,
								((HttpMethodBase) method)
										.getResponseContentLength());
					}
					Header contentType = ((HttpMethodBase) method)
							.getResponseHeader("Content-Type");
					if (contentType != null) {
						res.setValue(ResourceDownloader.CONTENT_TYPE, contentType
								.getValue());
					}
					res = resFactory.newResource(res);
					// read body
					int bytesRead;
					int totalRead = 0;
					boolean go = true;
					// ... up until there is no more data available
					while (go && ((bytesRead = is.read(data)) != -1)) {
						if ((maxSize > 0) && (totalRead + bytesRead > maxSize)) {
							bytesRead = maxSize - totalRead;
							go = false;
						}
						res.append(data, bytesRead);
						totalRead += bytesRead;
					}
					downloader.fireDownloadCompleted(downloader, res);
				} catch (IOException e) {
					// check if the exception is due to an user's abort
					if (aborted) {
						logger.log(Level.DEBUG, "Connection aborted by the user");
					} else {
						logger.log(Level.DEBUG, "Failed to get the response body for url:" + url);
						downloader.fireError(downloader, res, e);
					}
				} catch (Exception e) {
					// shouldn't be executed
					logger.log(Level.DEBUG, "Exception on "+url);
					downloader.fireError(downloader, res, e);
				}
			} // statusCode != -1
			method.releaseConnection();
		} // while (!downloader.stopped())
		status = STOP;
	}

	int status() {
		return status;
	}

	/**
	 * Note: aborting does not throw any error event.
	 */
	void abort() {
		aborted = true;
		// synchronized because the variable >method< could be altered by GetMethod in run() 
		synchronized (this) {
			if (method != null) {
				method.abort();
			}
		}
	}

}
