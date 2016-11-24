/*
 * @(#)Resource.java	0.2 10/17/06
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */package org.rm3umf.net.downloader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Provides an abstract class to store a downloaded resource, where some metadata
 * (e.g., CONTENT_TYPE, STATUS_CODE, etc) are stored in a in-memory
 * <code>Map</code>.
 * <p>
 * Extend this class to implement a storage for the resourse's content.
 * 
 * @author Fabio Gasparetti
 * @version 0.2, 10/17/06
 */
public abstract class ResourceDownloader implements Cloneable {
	public static final String CONTENT_TYPE = "ContentType";

	public static final String REASON_PHRASE = "ReasonPhrase";

	public static final String STATUS_CODE = "StatusCode";

	public static final String CONTENT_LENGTH = "ContentLength";

	public static final String URL = "Url";

	public static final String CHAR_SET = "CharSet";

	/** For storing meta-data */
	protected TreeMap map = new TreeMap();

	// protected static Logger logger =
	// Logger.getLogger("org.tabularium.net.Resource");

	/**
	 * Returns the content type of the resource.
	 */
	public String getContentType() {
		return (String) map.get(ResourceToFile.CONTENT_TYPE);
	}

	/**
	 * Returns the content length of the resource.
	 */
	public int getContentLength() {
		String s = (String) map.get(ResourceToFile.CONTENT_LENGTH);
		return s != null ? Integer.parseInt(s) : -1;
	}

	/**
	 * Returns the reason phrase returned by the remote server.
	 */
	public String getReasonPhrase() {
		return (String) map.get(ResourceToFile.REASON_PHRASE);
	}

	/**
	 * Returns the <code>url</code> that points to the resource.
	 */
	public String getURL() {
		return (String) map.get(ResourceToFile.URL);
	}

	/**
	 * Returns the char-set (if available) of the the resource.
	 */
	public String getCharSet() {
		return (String) map.get(ResourceToFile.CHAR_SET);
	}

	/**
	 * Returns the status code returned by the remote server.
	 */
	public int getStatusCode() {
		String s = (String) map.get(ResourceToFile.STATUS_CODE);
		return s != null ? Integer.parseInt(s) : -1;
	}

	/**
	 * The method is public to let user add customized meta-data.
	 */
	public void setValue(String name, String value) {
		map.put(name, value);
	}

	/**
	 * The method is public to let user add customized meta-data.
	 */
	public void setValue(String name, int value) {
		map.put(name, (new Integer(value)).toString());
	}

	/**
	 * The method is public to let user add customized meta-data.
	 */
	public void setValue(String name, long value) {
		map.put(name, (new Long(value)).toString());
	}

	/**
	 * Returns the set of meta-data keys.
	 */
	public Set values() {
		return map.keySet();
	}

	/**
	 * Creates a copy of the meta-data of a given resource <code>res</code>
	 * and store them in the current object.
	 * <p>
	 * Used by <code>ResourceFactory</code> to create a new resource from a
	 * given one that contains only meta-data.
	 */
	public void cloneMetadata(ResourceDownloader res) {
		map = (TreeMap) res.map.clone();
	}

	/**
	 * This default implementation copies only the map of meta-data. Descendents
	 * of <code>Resource</code> class are required to clone the content of the
	 * resource.
	 */
	protected Object clone() throws CloneNotSupportedException {
		ResourceDownloader res = (ResourceDownloader) super.clone();
		res.map = (TreeMap) map.clone();
		return res;
	}

	/**
	 * Get the resource content as a <code>byte[]</code> array.
	 * 
	 * @return
	 * @throws IOException
	 */
	abstract public byte[] getObject() throws IOException;

	/**
	 * Get the resource content as a <code>InputStream</code>.
	 * 
	 * @return
	 * @throws IOException
	 */
	abstract public InputStream getObjectAsStream() throws IOException;

	/**
	 * Set the resource content.
	 * 
	 * @return
	 * @throws IOException
	 */
	abstract public void setObject(byte[] obj) throws IOException;

	/**
	 * Append data to the content of the resource stored so far.
	 * 
	 * @return
	 * @throws IOException
	 */
	abstract public void append(byte[] obj, int size) throws IOException;

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("-- Resource URL: " + this.getURL() + "\n");
		sb.append("Content Type: " + this.getContentType() + "\n");
		sb.append("CharSet: " + this.getCharSet() + "\n");
		sb.append("Status Code: " + this.getStatusCode() + "\n");
		sb.append("ReasonPhrase: " + this.getReasonPhrase() + "\n");
		sb.append("Body: \n");
		try {
			byte bytes[] = this.getObject();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(bytes[i] + " ");
			}
		} catch (Exception e) {
			sb.append("error reading body, ex:" + e.toString());
		}
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
