package com.velix.jmongo.impl;

import java.net.InetSocketAddress;

import org.apache.commons.pool.PoolableObjectFactory;

import com.velix.jmongo.Connection;

public class PoolableConnectionFactory implements PoolableObjectFactory {

	private InetSocketAddress address;

	public PoolableConnectionFactory(InetSocketAddress address) {
		this.address = address;
	}

	@Override
	public void activateObject(Object obj) throws Exception {
		Connection connection = (Connection) obj;
		connection.connect();
	}

	@Override
	public void destroyObject(Object obj) throws Exception {
		((Connection) obj).close();
	}

	@Override
	public Object makeObject() throws Exception {
		Connection connection = new NIOConnection(address,
				MongoProtocal.PROTOCAL);
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
