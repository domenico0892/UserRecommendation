/*
 * @(#)LinkDbException.java	0.1 01/01/06
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */
package org.rm3umf.net.downloader;

/**
 * Exceptions raised by <code>Queue</code> implementations.
 *  
 * @author  Fabio Gasparetti, Federico Vigna
 * @version 0.1, 01/01/06
 */
public class QueueException extends Exception {

    public QueueException() {
        super();
    }

    public QueueException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public QueueException(String arg0) {
        super(arg0);
    }

    public QueueException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
