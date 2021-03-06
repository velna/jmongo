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
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import com.velix.bson.BSONDocument;
import com.velix.jmongo.protocol.GetMoreMessage;
import com.velix.jmongo.protocol.KillCursorsMessage;
import com.velix.jmongo.protocol.QueryMessage;
import com.velix.jmongo.protocol.ReplyMessage;

public class CursorIterator<T extends BSONDocument> implements Iterator<T> {

	private final static Logger LOG = Logger.getLogger(CursorIterator.class);

	private Boolean hasNext = null;
	private ReplyMessage<T> replyMessage;
	private QueryMessage queryMessage;
	private Iterator<T> resultIterator;
	private ConnectionPool pool;
	private boolean getMore = false;
	private boolean closed = false;
	private Cursor<T> cursor;
	private int numberReturned;
	private int numberToReturn;

	public CursorIterator(ConnectionPool pool, Cursor<T> cursor) {
		this.pool = pool;
		this.cursor = cursor;
		prepareQueryMessage();
	}

	private void prepareQueryMessage() {
		// cal numberToReturn
		numberToReturn = cursor.getLimit();
		if (cursor.getBatchSize() > 0) {
			if (numberToReturn == 0) {
				numberToReturn = cursor.getBatchSize();
			} else {
				numberToReturn = Math
						.min(numberToReturn, cursor.getBatchSize());
			}
		}

		// prepare queryMessage
		queryMessage = new QueryMessage();
		queryMessage
				.setFullCollectionName(cursor.getCollection().getFullName());
		queryMessage.setNumberToReturn(numberToReturn);
		queryMessage.setNumberToSkip(cursor.getSkip());
		if (cursor.isExplain() || cursor.isSnapshot()
				|| (null != cursor.getSort() && !cursor.getSort().isEmpty())
				|| null != cursor.getHint()) {
			BSONDocument query = new MongoDocument();
			query.put("query", cursor.getQuery());
			if (null != cursor.getSort() && !cursor.getSort().isEmpty()) {
				query.put("orderby", cursor.getSort());
			}
			if (cursor.isExplain()) {
				query.put("$explain", true);
			}
			if (cursor.isSnapshot()) {
				query.put("$snapshot", true);
			}
			if (null != cursor.getHint()) {
				query.put("$hint", cursor.getHint());
			}
			queryMessage.setQuery(query);
		} else {
			queryMessage.setQuery(cursor.getQuery());
		}
		queryMessage.setReturnFieldSelector(cursor.getFields());
		queryMessage.setTailable(cursor.isTailableCursor());
		queryMessage.setNoCursorTimeout(cursor.isNoCursorTimeout());
		queryMessage.setSlaveOk(cursor.isSlaveOk());
		queryMessage.setAwaitData(cursor.isAwaitData());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasNext() throws IllegalStateException {
		if (closed) {
			return false;
		}
		if (Boolean.FALSE == hasNext) {
			return false;
		}
		if (cursor.getLimit() > 0 && cursor.getLimit() <= this.numberReturned) {
			return false;
		}
		if (null != resultIterator) {
			if (resultIterator.hasNext()) {
				hasNext = Boolean.TRUE;
			} else if (replyMessage.getCursorID() == 0) {
				hasNext = Boolean.FALSE;
			}
		} else {
			try {
				Connection connection = getConnection();
				try {
					if (getMore) {
						GetMoreMessage getMoreMessage = new GetMoreMessage();
						getMoreMessage.setFullCollectionName(cursor
								.getCollection().getFullName());
						getMoreMessage.setNumberToReturn(numberToReturn);
						getMoreMessage.setCursorID(replyMessage.getCursorID());
						connection.send(getMoreMessage);
					} else {
						connection.send(queryMessage);
						getMore = true;
					}
					replyMessage = (ReplyMessage<T>) connection.receive(cursor
							.getCollection().getObjectClass());
					if (null != replyMessage) {
						resultIterator = replyMessage.getDocuments().iterator();
					} else {
						resultIterator = null;
					}
					if (null != resultIterator) {
						hasNext = resultIterator.hasNext();
					} else {
						hasNext = Boolean.FALSE;
					}
				} finally {
					connection.close();
				}
			} catch (IOException e) {
				throw new MongoException(e);
			}
		}
		return hasNext;
	}

	@Override
	public T next() throws IllegalStateException {
		check();
		if (hasNext()) {
			numberReturned++;
			T ret = resultIterator.next();
			if (ret instanceof MongoCollectionAware) {
				((MongoCollectionAware) ret).setMongoCollection(cursor
						.getCollection());
			}
			return ret;
		}
		throw new NoSuchElementException("no more documents");
	}

	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("remove() is not supported yet");
	}

	public void close() throws IOException {
		closed = true;
		killCursor();
	}

	private void killCursor() throws IOException {
		if (null != replyMessage && replyMessage.getCursorID() != 0) {
			Connection connection = getConnection();
			try {
				KillCursorsMessage message = new KillCursorsMessage();
				message.setNumberOfCursorIDs(1);
				List<Long> cursorIdList = new ArrayList<Long>(1);
				cursorIdList.add(replyMessage.getCursorID());
				message.setCursorIDs(cursorIdList);
				connection.send(message);
			} finally {
				connection.close();
			}
		}
	}

	private Connection getConnection() throws IOException {
		Connection connection = pool.getConnection();
		this.cursor.getCollection().getDB().authenticate(connection);
		return connection;
	}

	private void check() {
		if (closed) {
			throw new IllegalStateException("cursor is already closed");
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			killCursor();
		} catch (Throwable e) {
			LOG.error("error when kill cursors: ", e);
		}
	}
}