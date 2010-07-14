package com.velix.jmongo.impl;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.log4j.Logger;

import com.velix.jmongo.Connection;
import com.velix.jmongo.Protocol;

public class PoolableConnectionFactory implements PoolableObjectFactory {

	private static final Logger LOG = Logger
			.getLogger(PoolableConnectionFactory.class);

	private InetSocketAddress address;
	private Protocol protocol;

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

}
