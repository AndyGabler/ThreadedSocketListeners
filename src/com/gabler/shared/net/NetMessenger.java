package com.gabler.shared.net;

import com.gabler.client.ClientConfiguration;
import com.gabler.server.ServerConfiguration;

import java.util.logging.Logger;

/**
 * Supertype for representations of communications with external systems
 * @author Andy Gabler
 * @param ConfigType Type of configuration object to use
 * @since 2018/08/18
 */
public abstract class NetMessenger<ConfigType extends NetConfiguration> {

	private static final Logger LOGGER = Logger.getLogger("NetMessenger");

	private volatile boolean started = false;
	
	private volatile ConfigType operationSheet = null;
	
	/**
	 * Should an action be allowed? Validation message.
	 * @param methodName Name of method that was called. This is only for output.
	 * @return True if allowed. False otherwise
	 */
	protected synchronized boolean checkActionAllowed(String methodName) {	
		boolean allowed = allowCondition();
		String message = "";
		
		if (!allowed && methodName != null && !methodName.isEmpty()) {
			message = methodName + " cannot be used. Failed validation";
		} else if (allowed) {
			message = null;
		} else {
			message = "Validation failed.";
		}
		
		if (message == null) {
			return true;
		}

		LOGGER.warning(message);
		return false;
	}
	
	/**
	 * Indicate that the {@code NetMessenger} has started
	 */
	protected synchronized void indicateStart() {
		started = true;
	}
	
	/**
	 * Methods allowed?
	 * @return True if allowed, false otherwise
	 */
	private synchronized boolean allowCondition() {
		
		boolean configOkay = false;
		
		if (getConfiguration() != null) {
			configOkay = getConfiguration().canCallMethod();
		} else {
			configOkay = true;
		}
		
		return started && configOkay;
	}
	
	/**
	 * The configuration
	 * @return The configuration
	 */
	public synchronized ConfigType getConfiguration() {
		return operationSheet;
	}
	
	/**
	 * Set the configuration of the server
	 * @param operations The configuration
	 */
	public void setOperations(ConfigType operations) {
		operationSheet = operations;
		
		if (operationSheet == null) {
			return;
		}
		
		//Kind of have to cheat here and know something about the config
		if (operationSheet instanceof ServerConfiguration) {
			((ServerConfiguration) operationSheet).setServer(this);
		} else if (operationSheet instanceof ClientConfiguration) {
			((ClientConfiguration) operationSheet).setClient(this);
		}
		
	}
	
	/**
	 * Terminate the {@code NetMessenger}
	 */
	public abstract void terminate();
	
}
