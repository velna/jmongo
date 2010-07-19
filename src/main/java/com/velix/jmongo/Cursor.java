package com.velix.jmongo;

import java.util.Iterator;
import java.util.List;

import com.velix.bson.BSONDocument;

public interface Cursor extends Iterable<BSONDocument> {
	/**
	 * limit the total number of elements of this cursor
	 * 
	 * @param limit
	 * @return
	 * @throws IllegalStateException
	 *             if this cursor is already closed
	 */
	Cursor limit(int limit) throws IllegalStateException;

	int getLimit();

	/**
	 * limit the number of elements in one batch of this cursor
	 * 
	 * @param batchSize
	 * @return
	 * @throws IllegalStateException
	 *             if this cursor is already closed
	 */
	Cursor batchSize(int batchSize) throws IllegalStateException;

	int getBatchSize();

	/**
	 * skip a given number of elements at the beginning of this cursor
	 * 
	 * @param skip
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor skip(int skip) throws IllegalStateException;

	int getSkip();

	/**
	 * set the fields to fetch
	 * 
	 * @param fields
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor fields(BSONDocument fields) throws IllegalStateException;

	BSONDocument getFields();

	/**
	 * set the sort used by this cursor
	 * 
	 * @param sort
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor sort(BSONDocument sort) throws IllegalStateException;

	BSONDocument getSort();

	/**
	 * set wheather this is a tailable cursor, default is false
	 * 
	 * @param tailableCursor
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor setTailableCursor(boolean tailableCursor)
			throws IllegalStateException;

	boolean isTailableCursor();

	/**
	 * disalbe cursor time outs, default is false
	 * 
	 * @param noCursorTimeout
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor setNoCursorTimeout(boolean noCursorTimeout)
			throws IllegalStateException;

	boolean isNoCursorTimeout();

	Cursor setSlaveOk(boolean slaveOk) throws IllegalStateException;

	boolean isSlaveOk();

	/**
	 * if tailable cursor is set to true, set this flag to true will cause the
	 * cursor call wait until some data is available
	 * 
	 * @param awaitData
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor setAwaitData(boolean awaitData) throws IllegalStateException;

	boolean isAwaitData();

	BSONDocument getQuery();

	MongoCollection getCollection();

	/**
	 * close this cursor, any used cursor will be killed
	 */
	void close();

	/**
	 * get all the elements of this cursor
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	List<BSONDocument> toList() throws IllegalStateException;

	/**
	 * get the iterator to use over this cursor
	 */
	public Iterator<BSONDocument> iterator() throws IllegalStateException;
}
