package com.velix.jmongo;

import java.io.IOException;

import com.velix.jmongo.protocol.IncomingMessage;
import com.velix.jmongo.protocol.OutgoingMessage;

public interface Connection {

	/**
	 * set the class used to process the protocal
	 * 
	 * @param protocol
	 */
	public void setProtocal(Protocol protocol);

	/**
	 * send an outgoing message over the connection
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void send(OutgoingMessage message) throws IOException;

	/**
	 * receive an incoming message over the connection
	 * 
	 * @return
	 * @throws IOException
	 */
	public IncomingMessage receive() throws IOException;

	/**
	 * try connect, if the connection is already connected, call this method has
	 * no effect
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException;

	/**
	 * close this connection, if the connection is already closed, call this
	 * method has no effect
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

	/**
	 * test wheather this connection is already connected
	 * 
	 * @return
	 */
	public boolean isConnected();

	/**
	 * set the attachment of this connection
	 * 
	 * @param attachment
	 */
	public void setAttachment(Object attachment);

	/**
	 * get the attachment of this connection
	 * 
	 * @return
	 */
	public Object getAttachment();
}
