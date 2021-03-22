package com.gabler.client;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Listens to a {@code Scanner} repeatedly in a separate thread.
 * Meant to be used for a {@code Client}
 * @author Andy Gabler
 * @since 2018/08/15
 */
public class ListeningThread extends Thread {

	private static final Logger LOGGER = Logger.getLogger("ListeningThread");

	private volatile Scanner listener = null;
	private volatile Client client = null;
	
	private volatile boolean isRunning = true;
	
	/**
	 * Listens to a {@code Scanner} repeatedly in a separate thread.
	 * Meant to be used for a {@code Client}
	 * @param listener The listener
	 * @param client The client
	 */
	public ListeningThread(Scanner listener, Client client) {
		this.listener = listener;
		this.client = client;
	}
	
	/**
	 * Run the thread
	 */
	public void run() {
		while (isRunning) {
			
			String message = null;
			
			if (listener.hasNextLine()) {
				message = listener.nextLine();
				LOGGER.info("Found message from server.");
			}
			
			if (message == null) {
				LOGGER.info("Server disconnected. Terminating listener");
				doTermination();
				continue;
			}
			
			client.receiveMessage(message);
		}
		
		if (client.getConfiguration() != null) {
			client.getConfiguration().threadTerminationAction();
		}
	}
	
	/**
	 * Request to terminate the {@code ListeningThread}
	 */
	public synchronized void terminate() {
		
		if (client.getConfiguration() != null && !client.getConfiguration().canTerminate()) {
			LOGGER.info("Terminate request rejected.");
			return;
		}
		
		doTermination();
		
	}
	
	/**
	 * Do the actual termination
	 */
	private synchronized void doTermination() {
		LOGGER.info("Client terminated.");
		
		isRunning = false;
		
		if (client.getConfiguration() != null) {
			client.getConfiguration().clientTerminationAction();
		}
	}
	
}
