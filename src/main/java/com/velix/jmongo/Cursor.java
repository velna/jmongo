package com.velix.jmongo;

import java.util.List;

import com.velix.bson.BSONDocument;


public interface Cursor extends Iterable<BSONDocument> {
	Cursor limit(int limit);

	int getLimit();

	Cursor batchSize(int batchSize);

	int getBatchSize();

	Cursor skip(int skip);

	int getSkip();

	Cursor fields(BSONDocument fields);

	BSONDocument getFields();

	Cursor sort(BSONDocument sort);

	BSONDocument getSort();

	Cursor setTailableCursor(boolean tailableCursor);

	boolean isTailableCursor();

	Cursor setNoCursorTimeout(boolean noCursorTimeout);

	boolean isNoCursorTimeout();

	Cursor setSlaveOk(boolean slaveOk);

	boolean isSlaveOk();

	Cursor setAwaitData(boolean awaitData);

	boolean isAwaitData();

	BSONDocument getQuery();

	MongoCollection getCollection();

	void close();

	List<BSONDocument> toList();
}
