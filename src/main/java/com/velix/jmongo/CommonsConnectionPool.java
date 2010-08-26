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
import java.util.NoSuchElementException;

import org.apache.commons.pool.ObjectPool;
import org.apache.log4j.Logger;

import com.velix.jmongo.protocol.IncomingMessage;
import com.velix.jmongo.protocol.OutgoingMessage;

public class CommonsConnectionPool implements ConnectionPool {
	private static final Logger LOG = Logger
			.getLogger(CommonsConnectionPool.class);

	private ObjectPool pool;

	public CommonsConnectionPool(ObjectPool pool) {
		this.pool = pool;
	}

	@Override
	public Connection getConnection() throws IOException,
			NoSuchElementException, IllegalStateException {
		try {
			return new PooledConnection((Connection) pool.borrowObject());
		} catch (RuntimeException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			pool.close();
		} catch (Exception e) {
			LOG.error("exception when close connection pool: ", e);
		}
	}

	private class PooledConnection implements Connection {

		private Connection connection;

		public PooledConnection(Connection connection) {
			this.connection = connection;
		}

		@Override
		public void close() throws IOException {
			try {
				CommonsConnectionPool.this.pool.returnObject(connection);
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
		public IncomingMessage receive(Class<?> clazz) throws IOException {
			return connection.receive(clazz);
		}

		@Override
		public void send(OutgoingMessage message) throws IOException {
			connection.send(message);
		}

		@Override
		public void setProtocal(Protocol protocol) {
			connection.setProtocal(protocol);
		}

		@Override
		public Object getAttachment() {
			return connection.getAttachment();
		}

		@Override
		public void setAttachment(Object attachment) {
			connection.setAttachment(attachment);
		}

	}

}
