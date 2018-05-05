package com.biffbangpow.faq.db;

/**
 * An exception raised when db in error.
 *
 */
public class FaqDAOException extends RuntimeException {

	/**
	 * Creates a FaqDAO exception.
	 * @param msg the associated message
	 * @param t the associated throwable
	 */
	public FaqDAOException(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * Creates a FaqDAO exception.
	 * @param msg the associated message
	 */
	public FaqDAOException(String msg) {
		super(msg);
	}

}
