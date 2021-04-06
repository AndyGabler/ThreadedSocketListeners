package com.gabler.client.runner;

import java.util.Scanner;
import java.util.regex.Pattern;

import com.gabler.client.Client;

/**
 * Run an implementation of the client
 * @author Andy Gabler
 * @since 2018/08/09
 */
public class Driver {

	private static Scanner scanner = new Scanner(System.in);
	
	/**
	 * Make a {@code Client}
	 * @param args Hostname and port number
	 */
	public static void main(String[] args) {

		String hostName = null;
		int portNumber = -1;
		
		if (args.length >= 2) {
			hostName = args[0];
			
			boolean givenInteger = Pattern.compile("[0-9]+").matcher(args[1]).matches();
			
			if (givenInteger) {
				portNumber = Integer.getInteger(args[1]);
			}
			
		} else if (args.length == 1) {
			hostName = args[0];
		}
		
		if (hostName == null) {
			hostName = getHost();
		}
		
		if (portNumber <= 0) {
			portNumber = getPort();
			
			if (portNumber <= 0) {
				return;
			}
		}
		makeClient(hostName, portNumber);
	}
	
	/**
	 * Get hostname from the user
	 * @return The hostname
	 */
	private static String getHost() {
		return getHost(scanner);
	}
	
	/**
	 * Get hostname from the user
	 * @param scanner The input
	 * @return The name
	 */
	private static String getHost(Scanner scanner) {
		System.out.println("Please Enter A Hostname Or Type \"quit\":");
		
		if (scanner.hasNext()) {
			String name = scanner.next();
			if (name.length() != 0) {
				return name;
			}
		}
		
		return getHost(scanner);
	}
	
	/**
	 * Get the port from the user
	 * @return The port
	 */
	private static int getPort() {
		
		return getPort(scanner);
	}
	
	/**
	 * Get the port from the user
	 * @param scanner The input
	 * @return The port
	 */
	private static int getPort(Scanner scanner) {
		
		System.out.println("Please Enter A Port Number Or Type \"quit\":");
		
		if (scanner.hasNextInt()) {
			int port = scanner.nextInt();
			return port;
		} else if (scanner.nextLine().toLowerCase().contains("quit")) {
			return -1;
		}
		
		return getPort(scanner);
	}
	
	/**
	 * Make the client and start it
	 * @param host The hostname
	 * @param port The port
	 */
	private static void makeClient(String host, int port) {
		Client client = null;

		try {
			client = new Client(host, port);
			client.startConnection();
		} catch (Exception error) {
			System.out.println("Issue starting client connection: \n" + error);
		}

		if (client != null) {
			clientInteract(client);
		}

	}

	private static void clientInteract(Client client) {
		System.out.println("Enter message to send (type \"quit\" to quit):");
		Scanner scanner = new Scanner(System.in);

		String line = "";

		if (scanner.hasNextLine()) {
			line = scanner.nextLine();
		}

		if (line.contains("quit")) {
			scanner.close();
			return;
		}
		client.sendMessage(line);
		clientInteract(client);
	}

}
