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

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;


public class MongoImpl implements Mongo {

	private ConnectionPool connectionPool;

	private boolean closed;

	private final ConcurrentHashMap<String, MongoDB> dbs = new ConcurrentHashMap<String, MongoDB>();

	private MongoAdmin mongoAdmin;

	private final Lock adminLock = new ReentrantLock();

	private InetSocketAddress address;

	private Configuration configuration;

	public MongoImpl(String host, int port, Configuration configuration) {
		address = new InetSocketAddress(host, port);
		this.configuration = configuration;
		PoolableObjectFactory factory = new PoolableConnectionFactory(address,
				new MongoProtocol());
		connectionPool = new CommonsConnectionPool(new GenericObjectPool(
				factory, this.configuration));
		// connectionPool = new SimpleConnectionPool(address, new
		// MongoProtocol());
	}

	@Override
	public MongoDB getDB(String dbName) throws IllegalStateException {
		check();
		if (!dbs.containsKey(dbName)) {
			dbs.putIfAbsent(dbName, new MongoDBImpl(connectionPool, dbName,
					this));
		}
		return dbs.get(dbName);
	}

	@Override
	public MongoDB getDB(String dbName, String username, String password)
			throws IllegalStateException {
		MongoDB db = getDB(dbName);
		db.setAuthentication(username, password);
		return db;
	}

	@Override
	public void close() {
		closed = true;
		connectionPool.close();
	}

	@Override
	public MongoAdmin getAdmin() throws IllegalStateException {
		check();
		if (null == mongoAdmin) {
			adminLock.lock();
			try {
				if (null == mongoAdmin) {
					mongoAdmin = new MongoAdminImpl(connectionPool, this);
				}
			} finally {
				adminLock.unlock();
			}
		}
		return mongoAdmin;
	}

	private void check() {
		if (closed) {
			throw new IllegalStateException("mongo is already closed");
		}
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder(super.toString());
		ret.append("{");
		ret.append("address:").append(this.address);
		ret.append(", closed:").append(this.closed);
		ret.append("}");
		return ret.toString();
	}

}
