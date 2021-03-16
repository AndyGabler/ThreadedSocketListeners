package com.gabler.client;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

import com.gabler.shared.net.NetMessenger;

/**
 * Client talking to server with configurable back-and-forth
 * @author Andy Gabler
 * @since 2018/08/09
 */
public class Client extends NetMessenger<ClientConfiguration> {

	private static final Logger LOGGER = Logger.getLogger("Client");

	private String hostName = "";
	private int portNumber = -1;

	private volatile PrintStream outbound = null;
	
	private volatile ListeningThread listeningThread = null;
	
	/**
	 * Client talking to server with configurable back-and-forth
	 * @param hostname The hostname
	 * @param port The port
	 */
	public Client(String hostname, int port) {
		hostName = hostname;
		portNumber = port;
	}
	
	/**
	 * Start connection with the server specified
	 * @throws ClientStartException If issue with connection
	 */
	public void startConnection() throws ClientStartException {

		LOGGER.info("Client connecting to server");
		
		try {
			
			indicateStart();
			
			final Socket connection = new Socket(hostName, portNumber);
			
			final Scanner inbound = new Scanner(connection.getInputStream());
			outbound = new PrintStream(connection.getOutputStream());
			
			LOGGER.info("Starting message listening thread");
			
			listeningThread = new ListeningThread(inbound, this);
			
			if (getConfiguration() != null) {
				getConfiguration().joinAction(outbound);
			}
			
		} catch (Exception error) {
			throw ClientStartException.make("\nNested exception: " + error.getMessage());
		}
		
		listeningThread.start();
	}
	
	/**
	 * Receive a message from the Server
	 * @param message The message
	 */
	synchronized void receiveMessage(String message) {
		
		if (!checkActionAllowed("receiveMessage")) {
			return;
		}
		
		if (getConfiguration() != null) {
			String response = getConfiguration().handleIncoming(message, null, null);
			
			if (response != null) {
				LOGGER.fine("Sending a response back to server");
				sendMessage(response);
			}
			
		} else {
			LOGGER.fine("Message from server was:\n" + message);
		}
		
	}
	
	/**
	 * Send a message to the server
	 * @param message The message
	 */
	public synchronized void sendMessage(String message) {
		
		if (!checkActionAllowed("sendMessage")) {
			return;
		}
		
		outbound.println(message);
	}

	/**
	 * Terminate the {@code NetMessenger}
	 */
	@Override
	public void terminate() {
		
		if (!checkActionAllowed("terminate")) {
			return;
		}
		
		listeningThread.terminate();
		
	}
	
}