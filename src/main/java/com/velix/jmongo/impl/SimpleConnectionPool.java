package com.velix.jmongo.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

import com.velix.jmongo.Connection;
import com.velix.jmongo.ConnectionPool;
import com.velix.jmongo.Protocol;
import com.velix.jmongo.protocol.IncomingMessage;
import com.velix.jmongo.protocol.OutgoingMessage;

public class SimpleConnectionPool implements ConnectionPool {

	private Connection connection;

	// private ConcurrentLinkedQueue<Connection> freeConnections = new
	// ConcurrentLinkedQueue<Connection>();

	public SimpleConnectionPool(InetSocketAddress address, Protocol protocol) {
		connection = new NIOConnection(address, protocol);
		try {
			connection.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {

	}

	@Override
	public Connection getConnection() throws IOException,
			NoSuchElementException, IllegalStateException {
		return new PooledConnection(connection);
	}

	private class PooledConnection implements Connection {

		private Connection connection;

		public PooledConnection(Connection connection) {
			this.connection = connection;
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public void connect() throws IOException {
			connection.connect();
		}

		@Override
		public boolean isConnected() {
			return connection.isConnected();
		}

		@Override
		public IncomingMessage receive() throws IOException {
			return connection.receive();
		}

		@Override
		public void send(OutgoingMessage message) throws IOException {
			connection.send(message);
		}

		@Override
		public void setProtocal(Protocol protocol) {
			connection.setProtocal(protocol);
		}

	}

}
