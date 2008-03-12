package com.model.md5.exception;

/**
 * InvalidVersionException is thrown when an unsupported MD5 format version
 * is used.
 * 
 * @author Yi Wang (Neakor)
 */
public class InvalidVersionException extends RuntimeException{
	// Serial version.
	private static final long serialVersionUID = 7440063515649141651L;
	// Message.
	private static final String message = "Invalid MD5 format version: ";

	/**
	 * Constructor of InvalidVersionException.
	 * @param version The version of the given MD5 file.
	 */
	public InvalidVersionException(int version) {
		super(InvalidVersionException.message + version);
	}
}
