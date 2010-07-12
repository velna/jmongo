package com.velix.jmongo;

public interface Mongo {
	MongoAdmin getAdmin();

	MongoDB getDB(String dbName) throws MongoException;

	void close() throws MongoException;
}
