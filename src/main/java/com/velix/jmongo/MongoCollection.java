package com.velix.jmongo;

import java.util.List;

import com.velix.bson.BSONDocument;

public interface MongoCollection {
	Cursor find(BSONDocument query);

	void save(BSONDocument doc) throws MongoWriteException, MongoException;

	void save(List<BSONDocument> docList) throws MongoWriteException,
			MongoException;

	void remove(BSONDocument query, boolean singleRemove)
			throws MongoWriteException, MongoException;

	void update(BSONDocument query, BSONDocument data, boolean upsert,
			boolean multiUpdate) throws MongoWriteException, MongoException;

	long count(BSONDocument query, List<String> fields)
			throws MongoCommandFailureException;

	String getName();

	String getFullName();

	MongoDB getDB();

	void setSafeMode(boolean safe);

	boolean isSafeMode();
}
