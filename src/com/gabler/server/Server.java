package com.gabler.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.gabler.shared.net.NetMessenger;

/**
 * Multi-Client Server that is configurable to send custom messages and responses
 * @author Andy Gabler
 * @since 2018/08/09
 */
public class Server extends NetMessenger<ServerConfiguration> {

	private static final Logger LOGGER = Logger.getLogger("Server");

	private int portNumber = -1;
	private ServerSocket serverSocket = null;
	private ServerThread thread = null;
	
	/**
	 * Multi-Client Server that is configurable to send custom messages and responses
	 * @param port The port number
	 */
	public Server(int port) {
		LOGGER.info("Binding server to: " + port);
		portNumber = port;
	}
	
	private volatile ArrayList<ChatThread> connections = new ArrayList<ChatThread>();
	
	/**
	 * Start the server
	 * @throws IOException If the port is being used
	 */
	public void start() throws IOException {
		
		LOGGER.info("Server thread started");
		
		if (thread != null || serverSocket != null) {
			return;
		}
		
		indicateStart();
		
		serverSocket = new ServerSocket(portNumber);
		thread = new ServerThread(serverSocket);
		
		LOGGER.info("Sending request to start server thread");
		
		thread.start();
	}
	
	/**
	 * Add a connection
	 * @param connection The connection
	 */
	public synchronized void addConnection(Socket connection) {
		
		if (!checkActionAllowed("addConnection")) {
			return;
		}
		
		LOGGER.fine("Received connection from new remote.");
		
		if (connection == null) {
			LOGGER.fine("New connection was null and terminated");
			return;
		}

		LOGGER.fine("Connection was validated and will be added");
		
		ChatThread chatHead = new ChatThread(this, connection);
		
		connections.add(chatHead);
		
		if (getConfiguration() != null) {
			getConfiguration().joinAction(chatHead.getPrinter());
		}
		
		chatHead.start();
	}
	
	/**
	 * Remove {@code ChatThread} from connections
	 * @param thread The {@code ChatThread}
	 */
	public synchronized void removeConnection(ChatThread thread) {
		
		if (!checkActionAllowed("removeConnection")) {
			return;
		}
		
		connections.remove(thread);
	}
	
	/**
	 * Broadcast a message to all clients
	 * @param message The message from a client
	 * @param thread The sender
	 */
	public synchronized void broadcast(String message, ChatThread thread) {
		
		if (!checkActionAllowed("broadcast")) {
			return;
		}
		
		for (ChatThread connection : connections) {
			
			String response = message;
			
			if (getConfiguration() != null) {
				response = getConfiguration().handleIncoming(message, thread, connection);
				LOGGER.fine("\"" + message + "\" converted to \"" + response + "\"");
			}
			
			connection.sendMessage(response, thread);
			
		}
	}
	
	/**
	 * Terminate the server
	 */
	@Override
	public synchronized void terminate() {
		
		if (!checkActionAllowed("terminate")) {
			return;
		}

		LOGGER.fine("Thread terminated");
		
		if (getConfiguration() != null && 
			!getConfiguration().canTerminate()) {
			return;
		}
		
		thread.terminate();
		
		for (ChatThread thread : connections) {
			thread.terminate();
		}
		
		if (getConfiguration() != null) {
			getConfiguration().serverTerminationAction();
		}
		
		serverSocket = null;
	}
	
	public synchronized void clientMessage(String message, ChatThread aThread) {
		ServerConfiguration config = this.getConfiguration();
		if (config != null) {
			config.clientSentMessage(message, aThread);
		} else {
			broadcast(message, aThread);
		}
	}
	
	/**
	 * Connection listening thread of the server
	 * @author Andy Gabler
	 * @code 2018/08/09
	 */
	private class ServerThread extends Thread {
		
		private volatile ServerSocket listeningSocket = null;
		private volatile boolean isRunning = true;
		
		/**
		 * Connection listening thread of the server
		 * @param socket The socket
		 */
		public ServerThread(ServerSocket socket) {
			listeningSocket = socket;
		}
		
		/**
		 * Start listener
		 */
		public void run() {
			while (isRunning) {
				
				Socket newConnection = null;
				
				try {
					newConnection = listeningSocket.accept();
				} catch (IOException error) {
					continue;
				}

				LOGGER.fine("Started a new connection");
				
				addConnection(newConnection);
			}
		}
	
		/**
		 * Kill
		 */
		public void terminate() {
			isRunning = false;
			
			if (getConfiguration() != null) {
				getConfiguration().threadTerminationAction();
			}
			
		}
		
	}
	
}
