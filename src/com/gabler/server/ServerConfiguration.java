package com.gabler.server;

import com.gabler.shared.net.NetConfiguration;
import com.gabler.shared.net.NetMessenger;

/**
 * Server configuration sheet
 * @author Andy Gabler
 * @since 2018/08/09
 */
public abstract class ServerConfiguration extends NetConfiguration {
	
	protected Server server = null;
	
	/**
	 * <p>Set the {@code Server} that the {@code ServerOperations} looks at</p>
	 * <p><b>DO NOT CALL OUTSIDE OF {@code Server}</b></p>
	 * @param serverToUse The {@code Server}
	 */
	public void setServer(NetMessenger<?> serverToUse) {
		
		if (server != null || !(serverToUse instanceof Server)) {
			return;
		}
		server = (Server) serverToUse;
	}
	
	/**
	 * Action for terminating the {@code Server}
	 */
	public abstract void serverTerminationAction();
	
	/**
	 * Does an acknowledge get sent back to the sender
	 * @param sender The sending client
	 * @return Is response returned to sender
	 */
	public abstract boolean sendsAck(ChatThread sender);
	
	/**
	 * Client has sent a message to the server
	 * @param message The message
	 * @param thread The thread it comes from
	 */
	public abstract void clientSentMessage(String message, ChatThread thread);
	
}
