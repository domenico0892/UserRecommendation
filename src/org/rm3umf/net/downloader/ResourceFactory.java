/**
 * @(#)ResourceFactory.java	0.1 10/17/06
 *
 * Copyright 2006 Fabio Gasparetti. All rights reserved.
 */

package org.rm3umf.net.downloader;

/**
 * An interface for factory classes able to return new <code>Resource</code> objects for storing downloaded data. 
 * 
 * @author Fabio Gasparetti
 * @version 0.1, 10/17/06
 */
public interface ResourceFactory {
	/**
	 * Useful to inform the user about bad connections through Resource objects.
	 * 
	 * @return An empty <code>Resource</code> object.
	 */
	public ResourceDownloader emptyResource();
	
	/**
	 * According to the read meta-information (e.g., content-length, mime-type)
	 * returns a new resource that is able to store the related content.
	 * The metadata of the Resource parameter will be copied to the new returned
	 * object.
	 *  
	 * @return A new <code>Resource</code> object able to contain the object referenced by the <code>res</code> parameter.
	 */
	public ResourceDownloader newResource(ResourceDownloader res);
}
