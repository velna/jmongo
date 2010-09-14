package com.velix.jmongo;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DelegatedConnectionPool implements ConnectionPool {

	private ConnectionPool pool;
	private final Lock poolLock = new ReentrantLock();

	@Override
	public void clear() {
		if (null != pool) {
			pool.clear();
		}
	}

	@Override
	public void close() {
		if (null != pool) {
			pool.close();
		}
	}

	@Override
	public Connection getConnection() throws IOException,
			NoSuchElementException, IllegalStateException {
		poolLock.lock();
		try {
			return pool.getConnection();
		} finally {
			poolLock.unlock();
		}
	}

	public void setDelegate(ConnectionPool pool) {
		if (null == pool) {
			throw new IllegalArgumentException("pool can not be null");
		}
		this.pool = pool;
	}

	public ConnectionPool getDelegate() {
		return pool;
	}

	public Lock getPoolLock() {
		return poolLock;
	}

	public void closeDelegate() {
		close();
	}

}
