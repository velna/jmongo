package com.velix.jmongo;

public interface Mongo {
	MongoAdmin getAdmin() throws IllegalStateException;

	MongoDB getDB(String dbName) throws IllegalStateException;

	void close();
}
