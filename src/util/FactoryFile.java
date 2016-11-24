package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FactoryFile {
	
	private static FactoryFile instance;
	
	
	
	public static FactoryFile getInstance(){
		if(instance==null) 
			instance=new FactoryFile();
		return instance;
	}
	
	public void writeLineOnFile(String path,String text){
		File file=new File(path);
		PrintWriter writer=null;
		try {
			writer = new PrintWriter(new BufferedWriter(	new FileWriter(file,true)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // non sovrascrivere, accoda
		
		writer.println(text);	
		writer.println("");
		writer.close();
		
	}

}
