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
import java.util.Arrays;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.jmongo.protocol.DeleteMessage;
import com.velix.jmongo.protocol.InsertMessage;
import com.velix.jmongo.protocol.OutgoingMessage;
import com.velix.jmongo.protocol.QueryMessage;
import com.velix.jmongo.protocol.ReplyMessage;
import com.velix.jmongo.protocol.UpdateMessage;

public class MongoCollectionImpl implements MongoCollection {

	protected ConnectionPool pool;
	protected String name;
	protected MongoDB db;
	protected String fullName;
	protected boolean safeMode;
	protected Class<? extends BSONDocument> objectClass = MongoDocument.class;

	public MongoCollectionImpl(ConnectionPool pool, String collectionName,
			MongoDB db) {
		this.pool = pool;
		this.name = collectionName;
		this.db = db;
		fullName = new StringBuilder().append(db.getName()).append(".").append(
				name).toString();
	}

	@Override
	public long count(BSONDocument query, List<String> fields)
			throws MongoCommandFailureException {
		BSONDocument cmd = new MongoDocument();
		cmd.put("count", name);
		if (null != fields && fields.size() > 0) {
			List<String> fs = new ArrayList<String>(fields.size());
			for (String f : fields) {
				fs.add(f);
			}
			cmd.put("fields", fs);
		}
		cmd.put("query", query);
		CommandResult result = db.runCommand(cmd, true);
		Double c = (Double) result.get("n");
		if (null != c) {
			return c.longValue();
		}
		return 0;
	}

	@Override
	public <T extends BSONDocument> Cursor<T> find(BSONDocument query) {
		return new CursorImpl<T>(pool, query, this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void remove(BSONDocument query, boolean singleRemove)
			throws MongoWriteException, MongoException {
		DeleteMessage msg = new DeleteMessage();
		msg.setFullCollectionName(fullName);
		msg.setSelector(query);
		msg.setSingleRemove(singleRemove);
		this.sendMessage(msg, this.safeMode);
	}

	@Override
	public void save(BSONDocument doc) throws MongoWriteException,
			MongoException {
		save(Arrays.asList(doc));
	}

	@Override
	public void save(List<BSONDocument> docList) throws MongoWriteException,
			MongoException {
		InsertMessage msg = new InsertMessage();
		msg.setDocuments(docList);
		msg.setFullCollectionName(fullName);
		sendMessage(msg, this.safeMode);
	}

	private void sendMessage(OutgoingMessage msg, boolean checkSafe) {
		Connection connection = null;
		try {
			connection = pool.getConnection();
			this.getDB().authenticate(connection);
			connection.send(msg);
			if (checkSafe) {
				BSONDocument cmd = new MongoDocument();
				cmd.put("getlasterror", 1);
				QueryMessage queryMsg = new QueryMessage();
				queryMsg.setFullCollectionName(this.db.getName() + ".$cmd");
				queryMsg.setNumberToReturn(-1);
				queryMsg.setQuery(cmd);
				connection.send(queryMsg);
				ReplyMessage<?> reply = (ReplyMessage<?>) connection
						.receive(null);
				CommandResult result = new CommandResult(reply.getDocuments());
				if (!result.isOk() || null != result.getErrorMessage()) {
					throw new MongoWriteException(result.getErrorMessage());
				}
			}
		} catch (IOException e) {
			throw new MongoException("", e);
		} finally {
			if (null != connection) {
				try {
					connection.close();
				} catch (IOException e) {
					throw new MongoException("", e);
				}
			}
		}
	}

	@Override
	public void update(BSONDocument query, BSONDocument data, boolean upsert,
			boolean multiUpdate) throws MongoWriteException, MongoException {
		UpdateMessage msg = new UpdateMessage();
		msg.setFullCollectionName(fullName);
		msg.setSelector(query);
		msg.setUpdate(data);
		msg.setUpsert(upsert);
		msg.setMultiUpdate(multiUpdate);
		sendMessage(msg, this.safeMode);
	}

	@Override
	public MongoDB getDB() {
		return db;
	}

	@Override
	public String getFullName() {
		return fullName;
	}

	@Override
	public void setSafeMode(boolean safe) {
		this.safeMode = safe;
	}

	@Override
	public boolean isSafeMode() {
		return safeMode;
	}

	@Override
	public void setObjectClass(Class<? extends BSONDocument> clazz) {
		if (null == clazz) {
			throw new IllegalArgumentException("object class can not be null");
		}
		this.objectClass = clazz;
	}

	@Override
	public Class<? extends BSONDocument> getObjectClass() {
		return objectClass;
	}
}
