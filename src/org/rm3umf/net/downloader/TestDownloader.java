package org.rm3umf.net.downloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TestDownloader {

	private static Logger logger = Logger.getLogger("org.tabularium.net.downloader");

	
	/*
	 * Test only the persistence of the NoDupPersistencePriorityQueue
	 * implementation
	 */
	public static void testPersistence()throws Exception {
		Queue queue = new InMemoryQueue();
//		queue.init("com.mysql.jdbc.Driver",
//			"jdbc:mysql://127.0.0.1/queue?user=root",
//			"jdbc:mysql://127.0.0.1/mysql?user=root", "queue",
//				true);
		
		for (int i = 0; i < 1; i++){
			queue.clear();
			System.out.println("I: "+i);
			ArrayList l = new ArrayList();
			l.add("http://www.4shared.com");
			l.add("http://www.89.com");
			l.add("http://www.about.com");
			l.add("http://www.adobe.com");
			l.add("http://www.adultfriendfinder.com");
			l.add("http://www.aebn.net");
			l.add("http://www.alibaba.com");
			l.add("http://www.amazon.co.uk");
			l.add("http://www.amazon.com");
			l.add("http://www.aol.com");
			l.add("http://www.apple.com");
			l.add("http://www.badongo.com");
			l.add("http://www.badoo.com");
			l.add("http://www.bbc.co.uk");
			l.add("http://www.bebo.com");
			l.add("http://www.blogger.com");
			l.add("http://www.cnet.com");
			l.add("http://www.cnn.com");
			l.add("http://www.comcast.net");
			l.add("http://www.craigslist.org");
			l.add("http://www.dailymotion.com");
			l.add("http://www.deviantart.com");
			l.add("http://www.digg.com");
			l.add("http://www.digitalpoint.com");
			l.add("http://www.download.com");
			l.add("http://www.ebay.co.uk");
			l.add("http://www.ebay.com");
			l.add("http://www.facebook.com");
			l.add("http://www.facebox.com");
			l.add("http://www.flickr.com");
			l.add("http://www.fotolog.net");
			l.add("http://www.friendster.com");
			l.add("http://www.gamespot.com");
			l.add("http://www.geocities.com");
			l.add("http://www.gmx.net");
			l.add("http://www.go.com");
			l.add("http://www.google.ca");
			l.add("http://www.google.co.hu");
			l.add("http://www.google.co.in");
			l.add("http://www.google.co.th");
			l.add("http://www.google.co.uk");
			l.add("http://www.google.com");
			l.add("http://www.google.com.au");
			l.add("http://www.google.com.co");
			l.add("http://www.google.com.vn");
			l.add("http://www.google.lt");
			l.add("http://www.google.ro");
			l.add("http://www.google.sk");
			l.add("http://www.hi5.com");
			l.add("http://www.hp.com");
			l.add("http://www.icio.us");
			l.add("http://www.icq.com");
			l.add("http://www.ign.com");
			l.add("http://www.imageshack.us");
			l.add("http://www.imagevenue.com");
			l.add("http://www.imdb.com");
			l.add("http://www.information.com");
			l.add("http://www.invisionfree.com");
			l.add("http://www.iwiw.hu");
			l.add("http://www.linkedin.com");
			l.add("http://www.live.com");
			l.add("http://www.mapquest.com");
			l.add("http://www.megarotic.com");
			l.add("http://www.megaupload.com");
			l.add("http://www.metacafe.com");
			l.add("http://www.microsoft.com");
			l.add("http://www.miniclip.com");
			l.add("http://www.mininova.org");
			l.add("http://www.msn.com");
			l.add("http://www.myspace.com");
			l.add("http://www.mywebsearch.com");
			l.add("http://www.nastydollars.com");
			l.add("http://www.nba.com");
			l.add("http://www.nytimes.com");
			l.add("http://www.orkut.com");
			l.add("http://www.passport.net");
			l.add("http://www.photobucket.com");
			l.add("http://www.pornotube.com");
			l.add("http://www.rapidshare.com");
			l.add("http://www.rediff.com");
			l.add("http://www.reference.com");
			l.add("http://www.sendspace.com");
			l.add("http://www.soso.com");
			l.add("http://www.sourceforge.net");
			l.add("http://www.starware.com");
			l.add("http://www.statcounter.com");
			l.add("http://www.tagged.com");
			l.add("http://www.theplanet.com");
			l.add("http://www.torrentspy.com");
			l.add("http://www.tripod.com");
			l.add("http://www.typepad.com");
			l.add("http://www.vnexpress.net");
			l.add("http://www.weather.com");
			l.add("http://www.whenu.com");
			l.add("http://www.wordpress.com");
			l.add("http://www.wwe.com");
			l.add("http://www.xanga.com");
			l.add("http://www.yahoo.com");
			l.add("http://www.yourfilehost.com");
			l.add("http://www.youtube.com");
			//InMemoryQueue queue = new InMemoryQueue();
			Downloader d = new Downloader(queue);
			d.setMaxThreads(20);
			// Listener
			CrawlerListener listener=new CrawlerListener();
			d.addListener(listener);
		
			
	
			//d.setFollowRedirect(false);
			//d.setMaxSize(2000);
			d.addURLs(l);
			d.start();
			d.waitDone();
			int j=0;
			for(ResourceDownloader r: listener.resources){
				System.out.println(j++ +"-"+r.getURL());
				byte[] page = r.getObject();
				if(page!=null){
					String p=new String(page);
					System.out.println(p);
				}
			}
		}
		
		
		
	}

	/*
	 * Test only the persistence of the NoDupPersistencePriorityQueue
	 * implementation
	 */
	public static void testPersistenceAndPriority() {
		Map l = new TreeMap();
		l.put("http://www.google.it/", new Double(1.1));
		l.put("http://www.federicovigna.it/", new Double(1.4));
		l.put("http://localhost/php/cliniquefitness/", new Double(1.1));
		l.put("http://www.yahoo.it/", new Double(0.9));
		/*
		Downloader d = new Downloader(new NoDupPersistencePriorityQueue(), 1);
		logger.log(Level.INFO,"TEST testPersistenceAndPriority: All urls must be downloaded in priority sequence");

		// Listener
		d.addListener(new DebugListener());

		d.setFollowRedirect(true);
		d.setMaxSize(2000);
		try {
			d.addURLs(l);
			d.start();
			d.waitDone();
		} catch (LinkDbException e) {
			e.printStackTrace();
		} */
	}

	public static void testHostControl() {
		Map l = new TreeMap();
		l.put("http://www.google.it/", new Double(1.1));
		l.put("http://www.federicovigna.it/", new Double(1.4));
		l.put("http://localhost/php/cliniquefitness/", new Double(1.1));
		l.put("http://www.yahoo.it/", new Double(0.9));
		/*
		Downloader d = new Downloader(
				new NoDupPersistencePriorityQueue_wHostControl(), 1);
		logger
				.log(
						Level.INFO,
					"TEST HostControl: All urls must be downloaded in priority sequence \n"
								+ "and two of the three from the same host (localhost) will be \n"
								+ "stopped to control multiple host access for a time gap as set in \n"
								+ "LRUHost_TimeGap.MINIMUM_DOWNLOAD_TIME_GAP field.");
		// Listener
		d.addListener(new DebugListener());

		d.setFollowRedirect(true);
		d.setMaxSize(2000);
		try {
			d.addURLs(l);
			d.start();
			d.waitDone();
		} catch (LinkDbException e) {
			e.printStackTrace();
		} */
	}

	public static void test() {
		ArrayList l = new ArrayList();
		l.add("http://www.google.it");
		l.add("http://www.microsoft.com");
		l.add("http://www.repubblica.it");
		l.add("http://www.repubblica.it");
		/*
		Downloader d = new Downloader(1);

		// Listener
		d.addListener(new DebugListener());

		d.setFollowRedirect(true);
		d.setMaxSize(2000);
		d.addURLs(l);
		d.start();
		d.waitDone();

		// Derby DBMS should be shutdown every times application goes down
		DerbyLinkQueueDB.shutdown();
		*/
	}

	/*
	 * Test the Resource implementation in a Temp file
	 */
	public static void testResourceFile() {
		ArrayList l = new ArrayList();
		l.add("http://www.google.it");
		l.add("http://www.microsoft.com");
		/*
		Downloader d = new Downloader(1);
		logger
				.log(
						Level.INFO,
					"TEST ResourceFile implementation. Url downloaded will be temporary saved to system temp directory.");

		// Listener
		d.addListener(new DebugListener());

		d.setFollowRedirect(true);
		d.setMaxSize(2000);
		d.addURLs(l);
		d.start();
		d.waitDone();

		// Derby DBMS should be shutdown every times application goes down
		DerbyLinkQueueDB.shutdown();
		*/
	}

	/*
	 * 
	 */
	public static void testCPU() {
		ArrayList l = new ArrayList();
		// Host to be downloaded
		for (int i = 0; i < 100; i++) {
			l.add("http://www.repubblica.it");
			l.add("http://www.google.it");
			l.add("http://www.libero.it");
			l.add("http://www.yahoo.it");
		}
		/*
		Downloader d = new Downloader(new NoDupStaticQueue(), 1);
		logger.log(Level.INFO,
			"TEST CPU: Stress CPU with many thread and download.");
		// Listener(s)
		d.addListener(new DebugListener());
		d.setFollowRedirect(true);
		// d.setMaxSize(5000);
		d.addURLs(l);
		d.start();

		d.waitDone();
		*/
	}

	/*
	 * 
	 */
	public static void testStop() {
		ArrayList l = new ArrayList();
		// Download big resource to ensure that some threads is working during
		// the stop
		l.add("http://www.google.it");
		l
				.add("http://ardownload.adobe.com/pub/adobe/reader/win/7x/7.0.8/ita/psa30se_ytb612_a708_DLM_it_it.aom");
		l
				.add("http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/R-3.2.1-200609210945/eclipse-SDK-3.2.1-win32.zip&url=http://rm.mirror.garr.it/mirrors/eclipse/eclipse/downloads/drops/R-3.2.1-200609210945/eclipse-SDK-3.2.1-win32.zip&mirror_id=189");
		/*
		Downloader d = new Downloader(new NoDupStaticQueue(), 4);
		logger
				.log(
						Level.INFO,
					"TEST STOP: Attempt to download big resources and try to stop immediatly aborting http method.");
		// Listener(s)
		d.addListener(new DebugListener());
		d.setFollowRedirect(true);
		d.setMaxSize(50000000);
		d.addURLs(l);
		d.start();
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// STOP
		d.stop();
		*/
	}

	public static void main(String[] args) throws Exception {
        LogManager.getLogManager().readConfiguration(new java.io.FileInputStream("logging.properties"));
        
        try {
        testPersistence();
        } catch(Exception ex) {
        	System.out.println(ex.toString());
        	ex.printStackTrace();
        }
		// testPersistenceAndPriority();
		// testHostControl();
		// Downloader.testResourceFile();
		// testCPU();
		// Downloader.testStop();
	}
	
	/* TEST */
	/*
	 * private void setupLogger() { // Tolgo i precedenti /* for (int i=0;i<logger.getHandlers().length;i++){
	 * Handler h = logger.getHandlers()[i]; logger.removeHandler(h); }
	 */

	// logger.setFilter(new FilterLevel(Level.FINE));
	// logger.setLevel(Level.ALL);
	// Create a new handler to write to the console
	/*
	 * ConsoleHandler console = new ConsoleHandler(); console.setFilter(new
	 * FilterLevel(Level.FINE)); console.setLevel(Level.FINE);
	 * logger.addHandler(console);
	 */

	// create a new handler to write to a named file
	/*
	 * FileHandler file; try { file = new FileHandler("LoggingDemo.xml");
	 * logger.addHandler(file); } catch(IOException ioe) { //
	 * logger.warning("Could not create a file..."); }
	 *  }
	 */
	
}
