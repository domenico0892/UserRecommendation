/**
 * @(#)ResourceToFile.java	0.1 10/17/06
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */
package org.rm3umf.net.downloader;

import java.io.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.channels.FileChannel;

/**
 * An implemetation of a <code>Resource</code> class where the content is stored in local temporary file.
 * 
 * By default, the file is stored in the default system temp directory and it  
 * is removed on exit. Overrides the <code>open()</code> to change the default behaviour.
 * 
 * @see #open()
 *  
 * @author Fabio Gasparetti
 * @version 0.1, 10/17/06
 */
public class ResourceToFile extends ResourceDownloader {
    /* prefix and suffix costant for the temp file */
    protected final static String tmpFilePrefix = "tmpResource";
    protected final static String tmpFileSuffix = ".tmp";

    /* File reference object */
    protected File filename = null;
    /* Data to be readed every time */
    protected int IOBUFFERSIZE = 1024;

	private Logger logger = Logger.getLogger("org.tabularium.net.ResourceToFile");

    ResourceToFile() { 
        try {
        	filename = open();

        } catch (IOException e) {
            logger.severe("Error creating tmp file to store Resource data");
        }
        
    }

    protected File open() throws IOException {
    	File fn = null;
        /* create a tmp file in the default temp system directory */
    	fn = File.createTempFile(tmpFilePrefix, tmpFileSuffix);
        // logger.fine("Created temp file for Resource data: " + objfile.getAbsolutePath());
        /* Delete temp file when program exits. */
    	fn.deleteOnExit();
    	return fn;
    }
    
    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Resource#getObject()
     */
    public byte[] getObject() throws IOException {
    	byte[] obj = new byte[0];
    	byte[] data = new byte[1024];
    	int bytesRead;
    	int totalRead = 0;
		FileInputStream is = null;
		BufferedInputStream bis = null; 
    	try {
    		is = new FileInputStream(filename);
    		bis = new BufferedInputStream (is); 
    		while ((bytesRead = bis.read(data, 0, IOBUFFERSIZE)) > 0) {
    			totalRead += bytesRead;
    			obj = (byte[]) Downloader.resizeArray(obj, totalRead);
    			System.arraycopy(data, 0, obj, totalRead - bytesRead, bytesRead);
    		}
    	} catch (FileNotFoundException e) {
            throw new FileNotFoundException("temp file not found");
    	} catch (IOException ex) {
    		// do nothing here
        	throw ex;
    	} finally {
        	if (bis != null) 
        		bis.close();
        	if (is != null) 
        		is.close();
        }     	
    	return obj;
    }
    
    /* 
     * The output InputStream is not buffered.
     * 
     * @see org.tabularium.net.downloader.Resource#getObjectAsStream()
     */
    public InputStream getObjectAsStream() throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("temp file not found");
        }
        return in;
    }


    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Resource#setObject(byte[])
     */
    public void setObject(byte[] obj) throws IOException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filename);
            // Write data
            os.write(obj);
            // Flush the data from the streams and writers into system buffers.
            // The data may or may not be written to disk.
            os.flush();
            // Block until the system buffers have been written to disk.
            // After this method returns, the data is guaranteed to have
            // been written to disk.
            os.getFD().sync();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("temp file not found");
        } catch (IOException ex) {
    		// do nothing here
        	throw ex;
        } finally {
            if (os != null)
            	os.close();
        }
    }

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Resource#getObjectOutputStream()
     */
    public OutputStream getObjectOutputStream() throws IOException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
        	throw new FileNotFoundException("temp file not found");
        }
        return os;
    }
    

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Resource#append()
     */    
    public void append(byte[] obj, int size) throws IOException {
        FileOutputStream os = null;
        try {
            os= new FileOutputStream(filename, true);
            // Write data
            os.write(obj, 0, size);
            // Flush the data from the streams and writers into system buffers.
            // The data may or may not be written to disk.
            os.flush();
            // Block until the system buffers have been written to disk.
            // After this method returns, the data is guaranteed to have
            // been written to disk.
            os.getFD().sync();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("temp file not found");
        } catch (IOException ex) {
        	// do nothing here
        	throw ex;
        } finally {
            if (os != null)
            	os.close();
        }
    } 

	public Object clone()
			throws CloneNotSupportedException {
		ResourceToFile res = (ResourceToFile)super.clone();
		try {
			copyFile(filename, res.filename);
		} catch (Exception ex) {
			throw new CloneNotSupportedException("Exception on resource cloning: "+ex.toString());
		}
		return res;
	}
	
	/**
	 * @see http://java.sun.com/developer/JDCTechTips/2002/tt0507.html
	 */
	protected static void copyFile(File src, File dest) 
		throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		FileChannel fcin = null;
		FileChannel fcout = null;

		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);

			fcin = in.getChannel();
			fcout = out.getChannel();

			fcin.transferTo(0, fcin.size(), fcout);
		} finally {
			if (fcin != null) 
				fcin.close();
			if (in != null) 
				in.close();
			if (fcout != null) 
				fcout.close();
			if (out != null) 
				out.close();
		}
	}
	
    /* TEST */
    private static void testStreaming() {        
        // Create a new Resource 
        ResourceToFile r = new ResourceToFile();
        // Put in a char every 8 bytes..
        byte data[] = new byte[64];
        for (int i=0;i<data.length;i++){
            data[i] = (byte)i;   
        }
        try {
            r.setObject(data);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
        // Dump data for debug
        System.out.print("Resource writing...");
        for (int i=0;i<data.length;i++){
            System.out.print(data[i]+" ");
        }
        System.out.println();
        
        System.out.println("**  Testing getObjectAsStream() ** ");
        // try to read the resource from the inputstream 
        InputStream in = null;
        try {
        	in = r.getObjectAsStream();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
        // read block of 8 byte every time
        byte[] readed = new byte[8];
        
        try {
            System.out.print("Resource reading...");
            while (in.read(readed)!=-1){
                System.out.print("\n\tReaded block :");
                for (int i=0;i<readed.length;i++){
                    System.out.print(readed[i]+" ");
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
                
        System.out.println("\n**  Testing getObject() ** ");
        // try to read the resource directly
        byte[] obj = null;
        try {
        	obj = r.getObject();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
        System.out.print("Resource reading...");
        for (int i=0;i<obj.length;i++){
            System.out.print(obj[i]+" ");
        }
    }
    
    public static void main(String[] args) {    	
    	//ResourceToFile res = new ResourceToFile();  
        // Set up logger to view everything and show message in the console too
        //res.logger.setLevel(Level.ALL);        
        // Create a new handler to write to the console
        ConsoleHandler console = new ConsoleHandler();
        //console.setFilter(new FilterLevel(Level.FINE));
        console.setLevel(Level.FINE);
        //res.logger.addHandler(console);        
        // test
        testStreaming(); 
    }
}
