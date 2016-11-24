package org.rm3umf.framework.main;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rm3umf.framework.buildmodel.BuildModel;
import org.rm3umf.framework.buildmodel.BuildModelException;
import org.rm3umf.framework.buildmodel.extractor.ExtractorException;
import org.rm3umf.persistenza.PersistenceException;


public class Main {
	
	public static void main(String[] args) throws  IOException, PersistenceException, BuildModelException, ExtractorException{
		
		
		//String myString = IOUtils.toString("", "UTF-8");
		//UTF8.readString(null);
		
		Logger root = Logger.getRootLogger();
		BasicConfigurator.configure();
		root.setLevel(Level.INFO);
		BuildModel bs=new BuildModel();
		bs.start();
	}

}
