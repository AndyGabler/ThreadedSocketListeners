package com.gabler.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import com.gabler.shared.net.NetMessenger;

/**
 * Client talking to server with configurable back-and-forth
 * @author Andy Gabler
 * @since 2018/08/09
 */
public class Client extends NetMessenger<ClientConfiguration> {

	private static final Logger LOGGER = Logger.getLogger("Client");

	private final BiFunction<String, Integer, Socket> socketFactory;
	private String hostName;
	private int portNumber;

	private volatile PrintWriter outbound = null;
	
	private volatile ListeningThread listeningThread = null;
	
	/**
	 * Client talking to server with configurable back-and-forth
	 * @param hostname The hostname
	 * @param port The port
	 */
	public Client(String hostname, int port) {
		this((host, portNumber) -> {
			try {
				return new Socket(host, portNumber);
			} catch (IOException exception) {
				throw new RuntimeException(exception);
			}
		}, hostname, port);
	}

	/**
	 * Client talking to server with configurable back-and-forth
	 * @param aSocketFactory Socket factory
	 * @param hostname The hostname
	 * @param port The port
	 */
	public Client(BiFunction<String, Integer, Socket> aSocketFactory, String hostname, int port) {
		socketFactory = aSocketFactory;
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
			
			final Socket connection = socketFactory.apply(hostName, portNumber);
			
			final Scanner inbound = new Scanner(connection.getInputStream());
			outbound = new PrintWriter(connection.getOutputStream());
			
			LOGGER.info("Starting message listening thread");
			
			listeningThread = new ListeningThread(inbound, this);
			
			if (getConfiguration() != null) {
				getConfiguration().joinAction(outbound);
			}
			
		} catch (Exception error) {
			throw new ClientStartException(error);
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
		outbound.flush();
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
