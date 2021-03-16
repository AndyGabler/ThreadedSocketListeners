package com.gabler.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Representation of a communication with a client from a server's perspective
 * @author Andy Gabler
 * @since 2018/08/13
 */
public class ChatThread extends Thread {

	private static final Logger LOGGER = Logger.getLogger("ChatThread");

	private volatile Server server = null;
	private volatile Socket client = null;
	
	private volatile Scanner inbound = null;
	private volatile PrintStream outbound = null;
	
	private volatile boolean isRunning = true;
	
	/**
	 * Representation of a communication with a client from a server's perspective
	 * @param server The server
	 * @param connectionSocket The connection to the client
	 */
	public ChatThread(Server server, Socket connectionSocket) {
		this.server = server;
		client = connectionSocket;
		
		makeMessengers();
	}
	
	/**
	 * Make the messengers that send message and receive messages
	 * from the Client
	 */
	private void makeMessengers() {
		
		try {
			inbound = new Scanner(client.getInputStream());
			outbound = new PrintStream(client.getOutputStream());
		} catch (IOException error) {
			error.printStackTrace();
		}
		
	}
	
	/**
	 * Start the listener
	 */
	public void run() {
		while (isRunning) {
			
			String message = null;
			
			if (inbound.hasNextLine()) {
				LOGGER.fine("Found a message");
				message = inbound.nextLine();
			}
			
			if (message == null) {
				LOGGER.fine("Client disconnected.");
				terminate();
				continue;
			}

			LOGGER.fine("\"" + message + "\" was message.");
			server.clientMessage(message, this);
		}
	}
	
	/**
	 * Send a message to the client
	 * @param message The message
	 * @param origin Where the message comes from
	 */
	public synchronized void sendMessage(String message, ChatThread origin) {
	
		if (message == null) {
			return;
		}
		
		LOGGER.fine("Sending message to client.");
		
		ServerConfiguration config = server.getConfiguration();
		boolean returnToSender = false;
		
		if (config != null) {
			returnToSender = config.sendsAck(this);
		}
		
		if (origin != this || returnToSender) {
			outbound.println(message);
		}
		
	}
	
	/**
	 * Kill the thread
	 */
	public synchronized void terminate() {
		isRunning = false;
		server.removeConnection(this);
	}
	
	/**
	 * Get the {@code PrintStream} to talk to client
	 * @return The {@code PrintStream}
	 */
	synchronized PrintStream getPrinter() {
		return outbound;
	}
	
}
