package org.rm3umf.framework.importing;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
//import org.rm3umf.net.downloader.QueueException;
import org.rm3umf.persistenza.PersistenceException;


public class StartupImp {
	
	public static void main(String[] args) throws DatasetException, PersistenceException, IOException{
		Logger root = Logger.getRootLogger();
		BasicConfigurator.configure();
		root.setLevel(Level.INFO);
		Importer importing = new Importer();
		//System.out.println(java.lang.Runtime.getRuntime().maxMemory()); 
		importing.start();
	}

}
