/**
 * @(#)ResourceFactory.java	0.1 10/17/06
 *
 * Copyright 2006 Fabio Gasparetti. All rights reserved.
 */
package org.rm3umf.net.downloader;

/**
 * A default implementation of the <code>ResourceFactory</code> that stores everything into memory.
 * 
 * @author Fabio Gasparetti
 * @version 0.1, 10/17/06
 */
public class DefaultResourceFactory implements ResourceFactory {

	/**
	 * Useful to inform the user about bad connections through Resource objects.
	 * @return
	 */
	public ResourceDownloader emptyResource() {
		return new ResourceToMemory();
	}
	
	/**
 	 * Returns a new resource that is able to store the related content.
 	 * <p>
	 * Note: The metadata of the Resource parameter will be copied to the new returned
	 * object. 
	 * @return
	 */
	public ResourceDownloader newResource(ResourceDownloader res) {
		ResourceDownloader newRes = new ResourceToMemory();
		newRes.cloneMetadata(res);
		return newRes;
	}

}
