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
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

public class SimpleConnectionPool implements ConnectionPool {

	private InetSocketAddress address;
	private Protocol protocol;

	// private ConcurrentLinkedQueue<Connection> freeConnections = new
	// ConcurrentLinkedQueue<Connection>();

	public SimpleConnectionPool(Protocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public void clear() {
	}

	@Override
	public void close() {

	}

	@Override
	public Connection getConnection() throws IOException,
			NoSuchElementException, IllegalStateException {
		Connection connection = new NIOConnection(address, protocol);
		connection.setAttachment(new MongoAttachment());
		connection.connect();
		return connection;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

}
