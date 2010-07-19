package com.velix.jmongo;

public interface Mongo {
	MongoAdmin getAdmin() throws IllegalStateException;

	/**
	 * get db by name
	 * 
	 * @param dbName
	 * @return
	 * @throws IllegalStateException
	 *             if this instance if already closed
	 */
	MongoDB getDB(String dbName) throws IllegalStateException;

	void close();
}
