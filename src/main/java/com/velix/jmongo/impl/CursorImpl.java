package com.velix.jmongo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.jmongo.ConnectionPool;
import com.velix.jmongo.Cursor;
import com.velix.jmongo.MongoCollection;
import com.velix.jmongo.MongoException;

public class CursorImpl implements Cursor {

	private ConnectionPool pool;
	private BSONDocument query;
	private MongoCollection collection;
	private CursorIterator cursorIterator;
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

	public CursorImpl(ConnectionPool pool, BSONDocument query,
			MongoCollection collection) {
		this.pool = pool;
		this.query = null == query ? new BSONDocument() : query;
		this.collection = collection;
	}

	@Override
	public Cursor fields(BSONDocument fields) {
		check();
		this.fields = fields;
		return this;
	}

	@Override
	public Cursor limit(int limit) {
		check();
		this.limit = limit;
		return this;
	}

	@Override
	public Cursor batchSize(int batchSize) {
		check();
		this.batchSize = batchSize == 1 ? 2 : batchSize;
		return this;
	}

	@Override
	public Cursor skip(int skip) {
		check();
		this.skip = skip;
		return this;
	}

	@Override
	public Cursor sort(BSONDocument sort) {
		check();
		this.sort = sort;
		return this;
	}

	@Override
	public Iterator<BSONDocument> iterator() {
		check();
		queryStarted = true;
		try {
			cursorIterator = new CursorIterator(pool.getConnection(), this);
			return cursorIterator;
		} catch (Exception e) {
			throw new MongoException(e);
		}
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
			throw new MongoException(e);
		}
	}

	private void check() {
		if (queryStarted) {
			throw new MongoException("cursor alread started");
		}
		if (closed) {
			throw new MongoException("cursor alread closed");
		}
	}

	@Override
	public List<BSONDocument> toList() {
		check();
		try {
			List<BSONDocument> ret = new ArrayList<BSONDocument>();
			Iterator<BSONDocument> i = this.iterator();
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
	public Cursor setNoCursorTimeout(boolean noCursorTimeout) {
		check();
		this.noCursorTimeout = noCursorTimeout;
		return this;
	}

	@Override
	public Cursor setTailableCursor(boolean tailableCursor) {
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
	public Cursor setAwaitData(boolean awaitData) {
		check();
		this.awaitData = awaitData;
		return this;
	}

	@Override
	public Cursor setSlaveOk(boolean slaveOk) {
		check();
		this.slaveOk = slaveOk;
		return this;
	}

}
