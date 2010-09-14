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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.pool.impl.GenericObjectPool;

import com.velix.bson.BSONDocument;

public class MongoImpl implements Mongo {

	// private static final Logger LOG = Logger.getLogger(MongoImpl.class);

	private final DelegatedConnectionPool connectionPool;
	private PoolableConnectionFactory factory;
	private boolean closed;
	private final ConcurrentHashMap<String, MongoDB> dbs = new ConcurrentHashMap<String, MongoDB>();
	private MongoAdmin mongoAdmin;
	private final Lock adminLock = new ReentrantLock();
	private InetSocketAddress address;
	private Set<InetSocketAddress> allHosts;
	private Configuration configuration;

	public MongoImpl(Configuration configuration, String... hosts) {
		this(configuration, getUniqueHosts(Arrays.asList(hosts)));
	}

	public MongoImpl(Configuration configuration,
			InetSocketAddress... addressList) {
		Set<InetSocketAddress> hostSet = new HashSet<InetSocketAddress>(
				addressList.length);
		for (InetSocketAddress as : addressList) {
			hostSet.add(as);
		}
		connectionPool = new DelegatedConnectionPool();
		this.configuration = configuration;
		findPrimary(hostSet);
	}

	public MongoImpl(Configuration configuration, Set<InetSocketAddress> hostSet) {
		connectionPool = new DelegatedConnectionPool();
		this.configuration = configuration;
		findPrimary(hostSet);
	}

	private static Set<InetSocketAddress> getUniqueHosts(List<String> hosts) {
		if (null == hosts) {
			return Collections.emptySet();
		}
		Set<InetSocketAddress> ret = new HashSet<InetSocketAddress>(hosts
				.size());
		for (String host : hosts) {
			ret.add(parseInetSocketAddress(host));
		}
		return ret;
	}

	private static InetSocketAddress parseInetSocketAddress(String host) {
		InetSocketAddress inetAddress;
		int port = DEFAULT_PORT;
		int i = host.indexOf(':');
		if (i > 0) {
			port = Integer.parseInt(host.substring(i + 1).trim());
			inetAddress = new InetSocketAddress(host.substring(0, i).trim(),
					port);
		} else {
			inetAddress = new InetSocketAddress(host, port);
		}
		return inetAddress;
	}

	public void replicaSetsCheck() {
		findPrimary(allHosts);
	}

	private synchronized void findPrimary(Set<InetSocketAddress> hostSet) {
		connectionPool.getPoolLock().lock();
		connectionPool.closeDelegate();
		SimpleConnectionPool delegate = new SimpleConnectionPool(
				new MongoProtocol());
		connectionPool.setDelegate(delegate);
		try {
			this.allHosts = new HashSet<InetSocketAddress>(hostSet.size());
			InetSocketAddress lastPrimary = null;
			for (InetSocketAddress inetAddress : hostSet) {
				allHosts.add(inetAddress);
				InetSocketAddress primary = checkPrimary(inetAddress, delegate);
				if (null == primary || primary.equals(lastPrimary)) {
					// throw new MongoException("can not find master db");
					continue;
				}
				if (null != lastPrimary && !lastPrimary.equals(primary)) {
					throw new MongoException("multi master db " + lastPrimary
							+ " and " + primary);
				} else {
					lastPrimary = primary;
				}
			}
			if (null == lastPrimary) {
				throw new MongoException("can not find master db");
			}
			this.address = lastPrimary;
			factory = new PoolableConnectionFactory(this.address,
					new MongoProtocol());
			connectionPool.setDelegate(new CommonsConnectionPool(
					new GenericObjectPool(factory, this.configuration)));
		} finally {
			connectionPool.getPoolLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	private InetSocketAddress checkPrimary(InetSocketAddress inetAddress,
			SimpleConnectionPool delegate) {
		if (!inetAddress.equals(this.address)) {
			delegate.setAddress(inetAddress);
		}
		MongoAdmin mongoAdmin = this.getAdmin();
		BSONDocument command = new MongoDocument("ismaster", 1);
		try {
			CommandResult result = mongoAdmin.runCommand(command, true);
			List<String> hosts = (List<String>) result.get("hosts");
			this.allHosts.addAll(getUniqueHosts(hosts));
			boolean isMaster = (Boolean) result.get("ismaster");
			if (isMaster) {
				return inetAddress;
			} else {
				String master = (String) result.get("primary");
				return checkPrimary(parseInetSocketAddress(master), delegate);
			}
		} catch (Exception e) {
			return null;
		}
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
