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

import java.util.Iterator;
import java.util.List;

import com.velix.bson.BSONDocument;

public interface Cursor<T extends BSONDocument> extends Iterable<T>, Cloneable {
	/**
	 * limit the total number of elements of this cursor
	 * 
	 * @param limit
	 * @return
	 * @throws IllegalStateException
	 *             if this cursor is already closed
	 */
	Cursor<T> limit(int limit) throws IllegalStateException;

	int getLimit();

	/**
	 * limit the number of elements in one batch of this cursor
	 * 
	 * @param batchSize
	 * @return
	 * @throws IllegalStateException
	 *             if this cursor is already closed
	 */
	Cursor<T> batchSize(int batchSize) throws IllegalStateException;

	int getBatchSize();

	/**
	 * skip a given number of elements at the beginning of this cursor
	 * 
	 * @param skip
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor<T> skip(int skip) throws IllegalStateException;

	int getSkip();

	/**
	 * set the fields to fetch
	 * 
	 * @param fields
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor<T> fields(BSONDocument fields) throws IllegalStateException;

	BSONDocument getFields();

	/**
	 * set the sort used by this cursor
	 * 
	 * @param sort
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor<T> sort(BSONDocument sort) throws IllegalStateException;

	BSONDocument getSort();

	/**
	 * set wheather this is a tailable cursor, default is false
	 * 
	 * @param tailableCursor
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor<T> setTailableCursor(boolean tailableCursor)
			throws IllegalStateException;

	boolean isTailableCursor();

	/**
	 * disalbe cursor time outs, default is false
	 * 
	 * @param noCursorTimeout
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor<T> setNoCursorTimeout(boolean noCursorTimeout)
			throws IllegalStateException;

	boolean isNoCursorTimeout();

	Cursor<T> setSlaveOk(boolean slaveOk) throws IllegalStateException;

	boolean isSlaveOk();

	/**
	 * if tailable cursor is set to true, set this flag to true will cause the
	 * cursor call wait until some data is available
	 * 
	 * @param awaitData
	 * @return
	 * @throws IllegalStateException
	 */
	Cursor<T> setAwaitData(boolean awaitData) throws IllegalStateException;

	boolean isAwaitData();

	Cursor<T> explain(boolean explain) throws IllegalStateException;

	boolean isExplain();

	Cursor<T> snapshot(boolean snapshot) throws IllegalStateException;

	boolean isSnapshot();

	Cursor<T> hint(String hint) throws IllegalStateException;

	String getHint();

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
	List<T> toList() throws IllegalStateException;

	/**
	 * get the iterator to use over this cursor
	 */
	public Iterator<T> iterator() throws IllegalStateException;
}
