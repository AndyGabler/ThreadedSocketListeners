package com.gabler.shared.net;

import java.io.PrintStream;

import com.gabler.server.ChatThread;

/**
 * Operation configuration for any connection
 * @author Andy Gabler
 * @since 2018/08/15
 */
public abstract class NetConfiguration {

	/**
	 * Can you terminate the {@code Thread}
	 * @return Terminable?
	 */
	public abstract boolean canTerminate();
	
	/**
	 * Action for terminating the {@code ServerThread}
	 */
	public abstract void threadTerminationAction();
	
	/**
	 * Convert a message from external to a response
	 * @param message The message from the client
	 * @param sender The sending client
	 * @param receiver The receiving client
	 * @return Response from the server. Null if no response
	 */
	public abstract String handleIncoming(String message, ChatThread sender, ChatThread receiver);

	/**
	 * Action for when a {@code Client} joins
	 * @param messenger The {@code PrintStream} to the receiving {@code Socket}
	 */
	public abstract void joinAction(PrintStream messenger);
	
	/**
	 * Validation as to whether or not {@code NetMessenger} operations can run
	 * @return True if you can use it. False if not.
	 */
	public abstract boolean canCallMethod();
	
}
