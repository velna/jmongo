package com.velix.jmongo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


import com.velix.bson.BSONDocument;
import com.velix.jmongo.Connection;
import com.velix.jmongo.Cursor;
import com.velix.jmongo.MongoException;
import com.velix.jmongo.protocal.GetMoreMessage;
import com.velix.jmongo.protocal.KillCursorsMessage;
import com.velix.jmongo.protocal.QueryMessage;
import com.velix.jmongo.protocal.ReplyMessage;

public class CursorIterator implements Iterator<BSONDocument> {

	private Boolean hasNext = null;
	private ReplyMessage replyMessage;
	private QueryMessage queryMessage;
	private Iterator<BSONDocument> resultIterator;
	private Connection connection;
	private boolean getMore = false;
	private boolean closed = false;
	private Cursor cursor;
	private int numberReturned;
	private int numberToReturn;

	public CursorIterator(Connection connection, Cursor cursor) {
		this.connection = connection;
		this.cursor = cursor;

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
		queryMessage.setQuery(cursor.getQuery());
		queryMessage.setReturnFieldSelector(cursor.getFields());
		queryMessage.setTailable(cursor.isTailableCursor());
		queryMessage.setNoCursorTimeout(cursor.isNoCursorTimeout());
		queryMessage.setSlaveOk(cursor.isSlaveOk());
		queryMessage.setAwaitData(cursor.isAwaitData());
	}

	@Override
	public boolean hasNext() {
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
				if (getMore) {
					GetMoreMessage getMoreMessage = new GetMoreMessage();
					getMoreMessage.setFullCollectionName(cursor.getCollection()
							.getFullName());
					getMoreMessage.setNumberToReturn(numberToReturn);
					getMoreMessage.setCursorID(replyMessage.getCursorID());
					connection.send(getMoreMessage);
				} else {
					connection.send(queryMessage);
					getMore = true;
				}
				replyMessage = (ReplyMessage) connection.receive();
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
			} catch (IOException e) {
				throw new MongoException(e);
			}
		}
		return hasNext;
	}

	@Override
	public BSONDocument next() {
		check();
		if (hasNext()) {
			numberReturned++;
			return resultIterator.next();
		}
		throw new NoSuchElementException("no more documents");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove() is not supported yet");
	}

	public void close() throws IOException {
		closed = true;
		try {
			if (null != replyMessage && replyMessage.getCursorID() != 0) {
				KillCursorsMessage message = new KillCursorsMessage();
				message.setNumberOfCursorIDs(1);
				List<Long> cursorIdList = new ArrayList<Long>(1);
				cursorIdList.add(replyMessage.getCursorID());
				message.setCursorIDs(cursorIdList);
				connection.send(message);
			}
		} finally {
			connection.close();
		}
	}

	private void check() {
		if (closed) {
			throw new MongoException("cursor is already closed");
		}
	}
}