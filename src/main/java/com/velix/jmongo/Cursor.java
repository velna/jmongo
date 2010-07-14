package com.velix.jmongo;

import java.util.Iterator;
import java.util.List;

import com.velix.bson.BSONDocument;

public interface Cursor extends Iterable<BSONDocument> {
	Cursor limit(int limit) throws IllegalStateException;

	int getLimit();

	Cursor batchSize(int batchSize) throws IllegalStateException;

	int getBatchSize();

	Cursor skip(int skip) throws IllegalStateException;

	int getSkip();

	Cursor fields(BSONDocument fields) throws IllegalStateException;

	BSONDocument getFields();

	Cursor sort(BSONDocument sort) throws IllegalStateException;

	BSONDocument getSort();

	Cursor setTailableCursor(boolean tailableCursor)
			throws IllegalStateException;

	boolean isTailableCursor();

	Cursor setNoCursorTimeout(boolean noCursorTimeout)
			throws IllegalStateException;

	boolean isNoCursorTimeout();

	Cursor setSlaveOk(boolean slaveOk) throws IllegalStateException;

	boolean isSlaveOk();

	Cursor setAwaitData(boolean awaitData) throws IllegalStateException;

	boolean isAwaitData();

	BSONDocument getQuery();

	MongoCollection getCollection();

	void close();

	List<BSONDocument> toList() throws IllegalStateException;

	public Iterator<BSONDocument> iterator() throws IllegalStateException;
}
