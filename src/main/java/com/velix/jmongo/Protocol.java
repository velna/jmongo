package com.velix.jmongo;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.velix.jmongo.protocol.IncomingMessage;
import com.velix.jmongo.protocol.OutgoingMessage;


public interface Protocol {
	public IncomingMessage receive(SocketChannel channel, Selector selector)
			throws IOException;

	public void send(OutgoingMessage message, SocketChannel channel,
			Selector selector) throws IOException;
}
