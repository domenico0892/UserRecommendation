/*
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */

package org.rm3umf.net.downloader;

import javax.swing.event.EventListenerList;
import java.util.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.params.*;

/**
 * A multi-threaded lightweight simple http resource downloader.
 * <p>
 * The download is based on the Apache's <code>HttpClient</code> library.
 * <p>
 * A typical invocation sequence is: <blockquote>
 * 
 * <pre>
 * Downloader d = new Downloader();
 * // remember to setup the downloader before start it! 
 * d.setMaxThreads(10);
 * d.setFollowRedirect(true);
 * // setup your listener to interact with the downloader
 * DebugListener listener = new DebugListener();
 * d.addListener(listener);
 * d.addURLs(urlList);
 * d.start();
 * d.waitDone();
 * </pre>
 * 
 * </blockquot>
 * <p>
 * The downloader takes care of redirect requests so it may happen that
 * HttpClient shows messages as "Redirect requested but followRedirects is
 * disabled" after <code>followRedirect</code> (see
 * {@link #setFollowRedirect(boolean)}) of Downloader has been set
 * <code>true</code>.
 * 
 * 
 * @author Fabio Gasparetti
 * @version 0.1, 02/03/05
 */
public class Downloader {
	public static class DummyListener implements DownloaderListener {
		ResourceDownloader res = null;

		public DummyListener() {

		}

		public void downloadCompleted(DownloadEvent ev) {
			res = ev.getResource();

		}

		public void error(DownloadEvent ev) {
			res = ev.getResource();
		}

		public void queueEmpty(DownloadEvent ev) {
		}
	}

	/**
	 * @todo have a look at import org.apache.commons.httpclient.params.* and
	 *       make classes for default configuration
	 */
	public static final int DEFAULT_MAX_THREADS = 10;

	/* @see DefaultHttpMethodRetryHandler */
	public static final int DEFAULT_MAX_ATTEMPTS = 3;

	/*
	 * The HTTP specification states: A user agent SHOULD NOT automatically
	 * redirect a request more than 5 times, since such redirections usually
	 * indicate an infinite loop.
	 */
	public static final int DEFAULT_MAX_REDIRECT_ATTEMPTS = 5;

	public static final int DEFAULT_TIMOUT = 10000;

	public static final boolean DEFAULT_FOLLOW_REDIRECTS = false;

	public static final boolean DEFAULT_EXIT_ON_EMPTYQUEUE = false;

	/**
	 * The maximum number of connections that will be created for any particular
	 * HostConfiguration
	 */
	public static final int DEFAULT_MAX_CONNECTIONS_PER_HOST = 2;

	/*
	 * @todo other relevant httpclient paramenters to include: http.useragent =
	 * Jakarta Commons-HttpClient/3.0.1 http.protocol.element-charset = US-ASCII
	 * http.protocol.content-charset = ISO-8859-1 http.socket.timeout = 10000
	 * http.connection-manager.max-total = 10
	 * http.connection-manager.max-per-host = {HostConfiguration[]=2}
	 * http.protocol.allow-circular-redirects = false
	 */

	private int maxAttempts = DEFAULT_MAX_ATTEMPTS;

	private int maxRedirectAttempts = DEFAULT_MAX_REDIRECT_ATTEMPTS;

	private int maxThreads = DEFAULT_MAX_THREADS;

	private int timeout = DEFAULT_TIMOUT;
	
	private String useragent = null;

	private int proxyPort = 0;

	private String proxyHost = null;

	private int maxConnectionsPerHost = DEFAULT_MAX_CONNECTIONS_PER_HOST;

	private boolean followRedirects = DEFAULT_FOLLOW_REDIRECTS;

	private boolean exitOnEmptyQueue = DEFAULT_EXIT_ON_EMPTYQUEUE;

	private int maxSize = -1;

	private boolean running = false;

	private Vector threads = new Vector();

	private ResourceFactory resFactory = null;

	private MultiThreadedHttpConnectionManager connectionManager = null;

 	private DefaultHttpMethodRetryHandler retryHandler = null;

 	private HttpClient client = null;

	private EventListenerList listenerList = new EventListenerList();

	private Queue queue = null;

	// private static Logger logger =
	// Logger.getLogger("org.tabularium.net.downloader");

	/**
	 * Creates a new instance of a downloader.
	 * <p>
	 * The default constructor creates a <code>InMemoryNoDupQueue</code>
	 * instance to store the urls to download. This queue ensures that each url
	 * can be downloaded only once.
	 */
	public Downloader() {
		this(new InMemoryNoDupQueue());
	}

	/**
	 * This constructor accepts a given implementation of a <code>Queue</code>
	 * interface to store the urls to download.
	 * 
	 * @param q
	 */
	public Downloader(Queue q) {
		/* httpclient setup */
		connectionManager = new MultiThreadedHttpConnectionManager();
		client = new HttpClient(connectionManager);
		retryHandler = new DefaultHttpMethodRetryHandler(maxAttempts, false);
		queue = q;
	}

	/* --- GET-SET METHODS --- */

	public void setResourceFactory(ResourceFactory rf) {
		resFactory = rf;
	}

	public void setTimeout(int t) {
		timeout = t;
	}

	public void setMaxConnectionsPerHost(int n) {
		maxConnectionsPerHost = n;
	}

	public void setMaxAttempts(int n) {
		maxAttempts = n;
	}

	public void setMaxThreads(int n) {
		maxThreads = n;
	}

	public void setMaxRedirectAttempts(int n) {
		maxRedirectAttempts = n;
	}

	public void setRedirectMaxAttempts(int n) {
		maxRedirectAttempts = n;
	}

	public void setRetryHandler(DefaultHttpMethodRetryHandler handler) {
		retryHandler = handler;
	}

	public void setFollowRedirect(boolean on) {
		followRedirects = on;
	}

	public void setMaxSize(int size) {
		maxSize = size;
	}
	
	public void setUserAgent(String agent) {
		useragent = agent;
	}

	public ResourceFactory getResourceFactory() {
		return resFactory;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getMaxConnectionsPerHost() {
		return maxConnectionsPerHost;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public int getMaxRedirectAttempts() {
		return maxRedirectAttempts;
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public boolean followRedirects() {
		return followRedirects;
	}
	
	public void setProxy(String host, int port) {
		proxyHost = host;
		proxyPort = port;
	}

	/**
	 * Enqueue a collection of urls to download represented by String objects.
	 * 
	 * @param urls
	 * @throws LinkDbException
	 *             if an error occurs accessing the queue
	 */
	synchronized public void addURLs(Collection urls) throws QueueException {
		queue.enqueue(urls);
		// wake up threads
		notifyAll();
	}

	/**
	 * Clear the content of the downloader's queue. Useful to free memory.
	 */
	synchronized public void clearQueue() throws QueueException {
		queue.clear();
	}

	/**
	 * Add a map of url(String) -> priority(Double) values to the queue.
	 * <p>
	 * If the queue does not support priorities, the urls will be enqueued with
	 * the same priority.
	 * 
	 * @param urls
	 * @throws LinkDbException
	 *             if an error occurs accessing the queue
	 */
	synchronized public void addURLs(Map urls) throws QueueException {
		// Check if queue support prioritized urls
		if (queue instanceof PriorityQueue) {
			PriorityQueue prioqueue = (PriorityQueue) queue;
			prioqueue.enqueue(urls);
		} else {
			queue.enqueue(urls.keySet());
		}
		// wake up threads
		notifyAll();
	}
	/**
	 * True if all the downloader threads are idle.
	 */
	synchronized public boolean idle() throws QueueException {
		boolean threadsIdle = true;
		Iterator iter = threads.iterator();
		while (iter.hasNext()) {
			DownloaderThread t = (DownloaderThread) iter.next();
			if (t.status() != DownloaderThread.IDLE) {
				threadsIdle = false;
				break;
			}
		}
		return queue.size() == 0 && threadsIdle;
	}

	/**
	 * Starts the download of the urls in the queue.
	 * 
	 * The downloader must not be running before calling start.
	 */
	synchronized public void start() {
		init();
		updateConfig();
		running = true;
		Iterator iter = threads.iterator();
		while (iter.hasNext()) {
			Thread t = (Thread) iter.next();
			t.start();
		}
	}

	/**
	 * If the configuration is altered after the first start call,
	 * call this method to update the internal configuration.
	 */
	synchronized public void updateConfig() {
		HttpConnectionManagerParams params = connectionManager.getParams();
		params
				.setMaxConnectionsPerHost(
						HostConfiguration.ANY_HOST_CONFIGURATION,
						maxConnectionsPerHost);
		// if the threads are more the connection allowed, increase the latter
		if (maxThreads < params.getMaxTotalConnections()) {
			params.setMaxTotalConnections(maxThreads);
		}
		// note: we set both timeouts with the same value
		params.setSoTimeout(timeout);
		params.setConnectionTimeout(timeout);
		// client.setTimeout(timeout);
		// HttpConnection.setConnectionTimeout(timeout);
		// equivalent code of deprecated client.setStrictMode(false)
		params.setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS,
				Boolean.FALSE);
		params.setParameter(HttpClientParams.REJECT_RELATIVE_REDIRECT,
				Boolean.FALSE);
		if (useragent != null) 
			params.setParameter(HttpClientParams.USER_AGENT, useragent);
		if (proxyHost != null)
			client.getHostConfiguration().setProxy(proxyHost, proxyPort);

		client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				retryHandler);
	}

	/**
	 * Ends the pending downloads before stopping the crawl.
	 * 
	 * @throws LinkDbException
	 *             if an error occurs accessing the queue
	 */
	synchronized public void lazyStop() throws QueueException {
		running = false;
		queue.clear();
		// wake up threads
		notifyAll();
	}

	/**
	 * Stop immediately the current download.
	 * 
	 * @throws LinkDbException
	 *             if an error occurs accessing the queue
	 */
	synchronized public void stop() throws QueueException {
		running = false;
		queue.clear();
		// stop threads
		Iterator iter = threads.iterator();
		while (iter.hasNext()) {
			DownloaderThread t = (DownloaderThread) iter.next();
			t.abort();
		}
	}

	/**
	 * Stops the threads of the downloader when the queue becomes empty.
	 */
	synchronized public void exitOnEmptyQueue(boolean on) {
		exitOnEmptyQueue = on;
		// wake up threads
		notifyAll();
	}

	/**
	 * A shortcut for downloading a single resource.
	 * The default configuration is: timeout = 10000, follow_redirects = false, redirect_attemps = 3. 
	 * 
	 * @todo Make default configurations as classes that you can passes to the
	 *       method.
	 */
	public static ResourceDownloader retrieve(String url) {
		Downloader d = new Downloader();
		DummyListener l = new DummyListener();
		try {
			d.setFollowRedirect(true);
			ArrayList urls = new ArrayList();
			urls.add(url);
			d.addURLs(urls);
			d.addListener(l);
			d.start();
			d.waitDone();
		} catch (QueueException ex) {
			// never here
		}
		return l.res;
	}

	/**
	 * Calling this method stops the execution till all the resources have been
	 * downloaded. No further urls can be enqueued.
	 * 
	 * @throws LinkDbException
	 *             if an error occurs accessing the queue
	 */
	public void waitDone() throws QueueException {
		exitOnEmptyQueue = true;
		boolean go = true;
		while (go) {
			Thread.currentThread().yield();

			synchronized (this) {
				if (queue.size() == 0) {
					Iterator iter = threads.iterator();
					go = false;
					while (iter.hasNext()) {
						DownloaderThread thread = (DownloaderThread) iter
								.next();
						if (thread.status != DownloaderThread.STOP) {
							go = true;
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * This method waits the completion of the current download activity and returns.
	 * 
	 * @throws LinkDbException
	 *             if an error occurs accessing the queue
	 */
	public void waitEmptyQueue() throws QueueException {
		boolean go = true;
		while (go) {
			Thread.currentThread().yield();

			synchronized (this) {
				if (queue.size() == 0) {
					Iterator iter = threads.iterator();
					go = false;
					while (iter.hasNext()) {
						DownloaderThread thread = (DownloaderThread) iter
								.next();
						if (thread.status != DownloaderThread.IDLE) {
							go = true;
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Register a <code>DownloaderListener</code> for events sent during the
	 * download.
	 */
	public void addListener(DownloaderListener l) {
		listenerList.add(DownloaderListener.class, l);
	}

	/**
	 * @param l
	 *            Unregister the specified listener from all event sent during
	 *            the download.
	 */
	public void removeListener(DownloaderListener l) {
		listenerList.remove(DownloaderListener.class, l);
	}

	/**
	 * Removes all registered listeners.
	 */
	public void removeAllListeners() {
		listenerList = new EventListenerList();
	}

	/* Called before downloader starts */
	private void init() {
		// set default resource factory
		// done here to avoid calling DefaultResourceFactory and seeing it
		// replaced
		// by a user-defined ResourceFactory
		if (resFactory == null) {
			resFactory = new DefaultResourceFactory();
		}
		// initializes threads
		threads.clear();
		for (int i = 0; i < maxThreads; i++) {
			Thread t = new DownloaderThread(this, client, resFactory);
			threads.add(i, t);
		}
	}
	
	protected void fireDownloadCompleted(Object source, ResourceDownloader res) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DownloaderListener.class) {
				DownloadEvent event = new DownloadEvent(source, res);
				((DownloaderListener) listeners[i + 1])
						.downloadCompleted(event);
			}
		}
	}

	protected void fireError(Object source, ResourceDownloader res, Exception ex) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DownloaderListener.class) {
				DownloadEvent event = new DownloadEvent(source, res, ex);
				((DownloaderListener) listeners[i + 1]).error(event);
			}
		}
	}

	protected void fireQueueEmpty(Object source) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DownloaderListener.class) {
				DownloadEvent event = new DownloadEvent(source);
				((DownloaderListener) listeners[i + 1]).queueEmpty(event);
			}
		}
	}

	synchronized public long queueSize() throws QueueException {
		return queue.size();
	}

	synchronized String getUrl() throws QueueException {
		return queue.pop();
	}

	/*
	 * <code>true</code> if the downloader has been stopped by the user or if
	 * the queue is empty and exitOnEmptyQueue is true.
	 */
	synchronized boolean stopped() {
		long size = 0l;
		// avoids any exception here so the thread does not have to worry
		try {
			size = queue.size();
		} catch (QueueException ex) {
			throw new RuntimeException("LinkDbException: " + ex.toString());
		}
		return !running || (exitOnEmptyQueue && (size == 0));
	}

	/*
	 * Reallocates an array with a new size, and copies the contents of the old
	 * array to the new array. Used by some classes of this package.
	 * 
	 * @param oldArray the old array, to be reallocated. @param newSize the new
	 * array size. @return A new array with the same contents.
	 */
	static Object resizeArray(Object oldArray, int newSize) {
		int oldSize = java.lang.reflect.Array.getLength(oldArray);
		Class elementType = oldArray.getClass().getComponentType();
		Object newArray = java.lang.reflect.Array.newInstance(elementType,
				newSize);
		int preserveLength = Math.min(oldSize, newSize);
		if (preserveLength > 0)
			System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
		return newArray;
	}
}
