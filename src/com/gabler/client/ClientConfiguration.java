package com.gabler.client;

import com.gabler.shared.net.NetConfiguration;
import com.gabler.shared.net.NetMessenger;

/**
 * Client operations to configure the client
 * @author Andy Gabler
 * @since 2018/08/15
 */
public abstract class ClientConfiguration extends NetConfiguration {

	protected Client client = null;
	
	/**
	 * <p>Set the {@code Client} that the {@code ClientOperations} looks at</p>
	 * <p><b>DO NOT CALL OUTSIDE OF {@code Client}</b></p>
	 * @param clientToUse The {@code Client}
	 */
	public void setClient(NetMessenger<?> clientToUse) {
		
		if (client != null || !(clientToUse instanceof Client)) {
			return;
		}
		client = (Client) clientToUse;
	}
	
	/**
	 * Termination action for the {@code Client}
	 */
	public abstract void clientTerminationAction();
	
}
