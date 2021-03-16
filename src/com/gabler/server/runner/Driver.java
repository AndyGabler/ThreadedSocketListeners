package com.gabler.server.runner;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.gabler.server.Server;

/**
 * Run an implementation of the server
 * @author Andy Gabler
 * @since 2018/08/09
 */
public class Driver {

	/**
	 * Start a server
	 * @param args Port number
	 */
	public static void main(String[] args) {

		if (args.length >= 1) {
			boolean givenInteger = Pattern.compile("[0-9]+").matcher(args[0]).matches();
			if (givenInteger) {
				makeServer(Integer.getInteger(args[0]));
				return;
			}
			System.out.println("Invalid port number passed in.");
		}
		
		int portNumber = getPort();
		
		if (portNumber <= 0) {
			return;
		}
		
		makeServer(portNumber);
	}
	
	/**
	 * Get port number from user input
	 * @return Port number. Negative if invalid
	 */
	private static int getPort() {
		
		Scanner scanner = new Scanner(System.in);
		return getPort(scanner);
	}
	
	/**
	 * Port number from user input
	 * @param scanner The input reader
	 * @return Port number. Negative if invalid
	 */
	private static int getPort(Scanner scanner) {
		
		System.out.println("Please Enter A Port Number Or Type \"quit\":");
		
		if (scanner.hasNextInt()) {
			int port = scanner.nextInt();
			scanner.close();
			return port;
		} else if (scanner.nextLine().toLowerCase().contains("quit")) {
			scanner.close();
			return -1;
		}
		
		return getPort(scanner);
	}
	
	/**
	 * Make the actual server
	 * @param port The portnumber
	 */
	private static void makeServer(int port) {
		try {
			new Server(port).start();
		} catch (IOException error) {
			System.out.println("Error in starting server connection");;
		}
	}
	
}
