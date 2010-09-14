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

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.log4j.Logger;

public class PoolableConnectionFactory implements PoolableObjectFactory {

	private static final Logger LOG = Logger
			.getLogger(PoolableConnectionFactory.class);

	private final InetSocketAddress address;
	private final Protocol protocol;

	public PoolableConnectionFactory(InetSocketAddress address,
			Protocol protocol) {
		this.address = address;
		this.protocol = protocol;
	}

	@Override
	public void activateObject(Object obj) throws Exception {
		Connection connection = (Connection) obj;
		connection.connect();
	}

	@Override
	public void destroyObject(Object obj) throws Exception {
		try {
			((Connection) obj).close();
		} catch (IOException e) {
			LOG.error("exception when close connection: ", e);
		}
	}

	@Override
	public Object makeObject() throws Exception {
		Connection connection = new NIOConnection(address, protocol);
		connection.setAttachment(new MongoAttachment());
		connection.connect();
		return connection;
	}

	@Override
	public void passivateObject(Object obj) throws Exception {
		// empty
	}

	@Override
	public boolean validateObject(Object obj) {
		Connection connection = (Connection) obj;
		return connection.isConnected();
	}

	public Protocol getProtocol() {
		return protocol;
	}

}
