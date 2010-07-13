package com.velix.jmongo;

import java.util.List;

import com.velix.bson.BSONDocument;

public interface MongoCollection {
	Cursor find(BSONDocument query);

	void save(BSONDocument doc);

	void save(List<BSONDocument> docList);

	void remove(BSONDocument query, boolean singleRemove);

	void update(BSONDocument query, BSONDocument data, boolean upsert,
			boolean multiUpdate);

	long count(BSONDocument query, List<String> fields);

	String getName();

	String getFullName();

	MongoDB getDB();

	void setSafeMode(boolean safe);

	boolean isSafeMode();
}
