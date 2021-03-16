package com.gabler.client;

/**
 * Error while starting {@code Client}
 * @author Andy Gabler
 * @since 2018/08/15
 */
public class ClientStartException extends Exception {

	/**
	 * Serial version
	 */
	private static final long serialVersionUID = -2234329115013822277L;
	
	/**
	 * Message at beginning of exception
	 */
	private static final String EXCEPTION_MESSAGE_START = "Error in Starting Client";
	
	/**
	 * Error while starting {@code Client}
	 */
	public ClientStartException() {
		super(EXCEPTION_MESSAGE_START);
	}
	
	/**
	 * Error while starting {@code Client}
	 * @param message Message
	 */
	private ClientStartException(String message) {
		super(EXCEPTION_MESSAGE_START + ": " + message);
	}
	
	/**
	 * Error while starting {@code Client}
	 * @return Error while starting {@code Client}
	 */
	public static ClientStartException make() {
		return new ClientStartException();
	}
	
	/**
	 * Error while starting {@code Client}
	 * @param message The message
	 * @return Error while starting {@code Client}
	 */
	public static ClientStartException make(String message) {
		if (message == null || message.isEmpty()) {
			return make();
		}
		
		return new ClientStartException(message);
	}
	
}
