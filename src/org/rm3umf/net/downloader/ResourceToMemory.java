/*
 * @(#)ResourceToMemory.java	0.1 10/17/06
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */

package org.rm3umf.net.downloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * An implemetation of a <code>Resource</code> class where the content is stored in a <code>byte[]</code> array.
 *  
 * @author Fabio Gasparetti
 * @version 0.1, 10/17/06
 */
public class ResourceToMemory extends ResourceDownloader {
    protected byte[] obj = new byte[0];

    ResourceToMemory() {  
    	super();
    }

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Resource#getOject()
     */
    public byte[] getObject() throws IOException {
        return obj;
    }

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Resource#setOject()
     */
    public void setObject(byte[] obj) throws IOException {
        this.obj = obj;
    }

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.IResource#getObjectAsStream()
     */
    public InputStream getObjectAsStream() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream (this.obj);
        return in;
    }

    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Resource#append()
     */
    public void append(byte[] data, int size) throws IOException {
    	obj = (byte[])Downloader.resizeArray(obj, size+obj.length);
		System.arraycopy(data, 0, obj, obj.length-size, size);
    } 
    
    /* (non-Javadoc)
     * @see org.tabularium.net.downloader.Resource#clone()
     */
	public Object clone()
		throws CloneNotSupportedException {
		ResourceToMemory res = (ResourceToMemory)super.clone();
		res.obj = new byte[obj.length];
		System.arraycopy(obj, 0, res.obj, 0, obj.length);
		return res;
	}
}
