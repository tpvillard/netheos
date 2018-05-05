package com.biffbangpow.faq.config;

/**
 * An exception raised when configuration could not be read.
 *
 */
public class ConfigAccessException extends RuntimeException {

	/**
	 * Creates a configuration exception.
	 * @param t the associated throwable
	 */
	public ConfigAccessException(Throwable t) {
		super(t);
	}

}
