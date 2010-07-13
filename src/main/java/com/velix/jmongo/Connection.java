package com.velix.jmongo;

import java.io.IOException;

import com.velix.jmongo.protocol.IncomingMessage;
import com.velix.jmongo.protocol.OutgoingMessage;


public interface Connection {

	public void setProtocal(Protocol protocol);

	public void send(OutgoingMessage message) throws IOException;

	public IncomingMessage receive() throws IOException;

	public void connect() throws IOException;

	public void close() throws IOException;

	public boolean isConnected();
}
