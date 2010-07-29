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

	/**
	 * get db by name, authenticate the connection using username and password
	 * 
	 * @param dbName
	 * @param username
	 * @param password
	 * @return
	 * @throws IllegalStateException
	 *             if this instance if already closed
	 */
	MongoDB getDB(String dbName, String username, String password)
			throws IllegalStateException, MongoAuthenticationException;

	void close();
}
