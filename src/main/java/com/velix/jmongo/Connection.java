/**
 *  JMongo is a mongodb driver writtern in java.
 *  Copyright (C) 2010  Xiaohu Huang
 *
 *  JMongo is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JMongo is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JMongo.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	public IncomingMessage receive(Class<?> clazz) throws IOException;

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
