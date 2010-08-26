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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.velix.bson.BSONDocument;

public class CursorImpl<T extends BSONDocument> implements Cursor<T> {

	private ConnectionPool pool;
	private BSONDocument query;
	private MongoCollection collection;
	private CursorIterator<T> cursorIterator;
	private boolean closed;
	private boolean queryStarted;
	private int limit;
	private int batchSize;
	private BSONDocument fields;
	private BSONDocument sort;
	private int skip;
	private boolean tailableCursor;
	private boolean noCursorTimeout;
	private boolean awaitData;
	private boolean slaveOk;
	private boolean explain;
	private boolean snapshot;
	private String hint;

	public CursorImpl(ConnectionPool pool, BSONDocument query,
			MongoCollection collection) {
		this.pool = pool;
		this.query = null == query ? new MongoDocument() : query;
		this.collection = collection;
	}

	@Override
	public Cursor<T> fields(BSONDocument fields) throws IllegalStateException {
		check();
		this.fields = fields;
		return this;
	}

	@Override
	public Cursor<T> limit(int limit) throws IllegalStateException {
		check();
		this.limit = limit;
		return this;
	}

	@Override
	public Cursor<T> batchSize(int batchSize) throws IllegalStateException {
		check();
		this.batchSize = batchSize == 1 ? 2 : batchSize;
		return this;
	}

	@Override
	public Cursor<T> skip(int skip) throws IllegalStateException {
		check();
		this.skip = skip;
		return this;
	}

	@Override
	public Cursor<T> sort(BSONDocument sort) throws IllegalStateException {
		check();
		this.sort = sort;
		return this;
	}

	@Override
	public Iterator<T> iterator() throws IllegalStateException {
		check();
		queryStarted = true;
		cursorIterator = new CursorIterator<T>(pool, this);
		return cursorIterator;
	}

	@Override
	public void close() {
		if (closed) {
			return;
		}
		closed = true;
		try {
			cursorIterator.close();
		} catch (IOException e) {
			throw new MongoException("exception when close cursor: ", e);
		}
	}

	private void check() {
		if (queryStarted) {
			throw new IllegalStateException("cursor alread started");
		}
		if (closed) {
			throw new IllegalStateException("cursor alread closed");
		}
	}

	@Override
	public List<T> toList() throws IllegalStateException {
		check();
		try {
			List<T> ret = new ArrayList<T>();
			Iterator<T> i = this.iterator();
			while (i.hasNext()) {
				ret.add(i.next());
			}
			return ret;
		} finally {
			close();
		}
	}

	@Override
	public int getBatchSize() {
		return batchSize;
	}

	@Override
	public BSONDocument getFields() {
		return fields;
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public int getSkip() {
		return skip;
	}

	@Override
	public BSONDocument getSort() {
		return sort;
	}

	@Override
	public BSONDocument getQuery() {
		return query;
	}

	@Override
	public MongoCollection getCollection() {
		return collection;
	}

	@Override
	public boolean isNoCursorTimeout() {
		return noCursorTimeout;
	}

	@Override
	public boolean isTailableCursor() {
		return this.tailableCursor;
	}

	@Override
	public Cursor<T> setNoCursorTimeout(boolean noCursorTimeout)
			throws IllegalStateException {
		check();
		this.noCursorTimeout = noCursorTimeout;
		return this;
	}

	@Override
	public Cursor<T> setTailableCursor(boolean tailableCursor)
			throws IllegalStateException {
		check();
		this.tailableCursor = tailableCursor;
		return this;
	}

	@Override
	public boolean isAwaitData() {
		return awaitData;
	}

	@Override
	public boolean isSlaveOk() {
		return slaveOk;
	}

	@Override
	public Cursor<T> setAwaitData(boolean awaitData) throws IllegalStateException {
		check();
		this.awaitData = awaitData;
		return this;
	}

	@Override
	public Cursor<T> setSlaveOk(boolean slaveOk) throws IllegalStateException {
		check();
		this.slaveOk = slaveOk;
		return this;
	}

	@Override
	public Cursor<T> explain(boolean explain) throws IllegalStateException {
		check();
		this.explain = explain;
		return this;
	}

	@Override
	public boolean isExplain() {
		return explain;
	}

	@Override
	public Cursor<T> snapshot(boolean snapshot) throws IllegalStateException {
		check();
		this.snapshot = snapshot;
		return this;
	}

	@Override
	public boolean isSnapshot() {
		return snapshot;
	}

	@Override
	public String getHint() {
		return hint;
	}

	@Override
	public Cursor<T> hint(String hint) throws IllegalStateException {
		check();
		this.hint = hint;
		return this;
	}

	@Override
	public Cursor<T> clone() throws CloneNotSupportedException {
		CursorImpl<T> ret = new CursorImpl<T>(pool, query, collection);
		ret.awaitData = this.awaitData;
		ret.batchSize = this.batchSize;
		ret.explain = this.explain;
		ret.fields = this.fields;
		ret.hint = this.hint;
		ret.limit = this.limit;
		ret.noCursorTimeout = this.noCursorTimeout;
		ret.query = this.query;
		ret.skip = this.skip;
		ret.slaveOk = this.slaveOk;
		ret.snapshot = this.snapshot;
		ret.sort = this.sort;
		ret.tailableCursor = this.tailableCursor;
		return ret;
	}

}
