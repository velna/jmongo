package com.velix.jmongo.impl;

import java.io.IOException;

import org.apache.commons.pool.ObjectPool;
import org.apache.log4j.Logger;

import com.velix.jmongo.Connection;
import com.velix.jmongo.ConnectionPool;
import com.velix.jmongo.Protocol;
import com.velix.jmongo.protocol.IncomingMessage;
import com.velix.jmongo.protocol.OutgoingMessage;

public class ConnectionPoolImpl implements ConnectionPool {
	private static final Logger LOG = Logger
			.getLogger(ConnectionPoolImpl.class);

	private ObjectPool pool;

	public ConnectionPoolImpl(ObjectPool pool) {
		this.pool = pool;
	}

	@Override
	public Connection getConnection() throws Exception {
		return new PooledConnection((Connection) pool.borrowObject());
	}

	@Override
	public void close() throws Exception {
		pool.close();
	}

	private class PooledConnection implements Connection {

		private Connection connection;

		public PooledConnection(Connection connection) {
			this.connection = connection;
		}

		@Override
		public void close() throws IOException {
			try {
				ConnectionPoolImpl.this.pool.returnObject(connection);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				LOG.error("", e);
			}
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
