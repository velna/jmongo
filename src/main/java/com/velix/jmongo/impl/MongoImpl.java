package com.velix.jmongo.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.velix.jmongo.Configuration;
import com.velix.jmongo.ConnectionPool;
import com.velix.jmongo.Mongo;
import com.velix.jmongo.MongoAdmin;
import com.velix.jmongo.MongoDB;
import com.velix.jmongo.MongoException;

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
		PoolableObjectFactory factory = new PoolableConnectionFactory(address);
		connectionPool = new ConnectionPoolImpl(new GenericObjectPool(factory,
				this.configuration));

	}

	@Override
	public MongoDB getDB(String dbName) throws MongoException {
		check();
		try {
			if (!dbs.containsKey(dbName)) {
				dbs.putIfAbsent(dbName, new MongoDBImpl(connectionPool, dbName,
						this));
			}
			return dbs.get(dbName);
		} catch (Exception e) {
			throw new MongoException(e);
		}
	}

	@Override
	public void close() throws MongoException {
		closed = true;
		try {
			connectionPool.close();
		} catch (Exception e) {
			throw new MongoException(e);
		}
	}

	@Override
	public MongoAdmin getAdmin() {
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
			throw new MongoException("mongo is already closed");
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
