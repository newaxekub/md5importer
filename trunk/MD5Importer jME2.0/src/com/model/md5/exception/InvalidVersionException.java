package com.model.md5.exception;

/**
 * <code>InvalidVersionException</code> is thrown when an unsupported MD5 format
 * version is used.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 05-02-2008 18:51 EST
 * @version 1.0.1
 */
public class InvalidVersionException extends RuntimeException{
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 7440063515649141651L;
	/**
	 * Text message.
	 */
	private static final String message = "Invalid MD5 format version: ";

	/**
	 * Constructor of <code>InvalidVersionException</code>.
	 * @param version The version of the given MD5 file.
	 */
	public InvalidVersionException(int version) {
		super(InvalidVersionException.message + version);
	}
}
