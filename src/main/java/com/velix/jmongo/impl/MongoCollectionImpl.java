package com.velix.jmongo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.velix.bson.BSONDocument;
import com.velix.jmongo.CommandResult;
import com.velix.jmongo.Connection;
import com.velix.jmongo.ConnectionPool;
import com.velix.jmongo.Cursor;
import com.velix.jmongo.MongoCollection;
import com.velix.jmongo.MongoDB;
import com.velix.jmongo.MongoException;
import com.velix.jmongo.protocal.DeleteMessage;
import com.velix.jmongo.protocal.InsertMessage;
import com.velix.jmongo.protocal.OutgoingMessage;
import com.velix.jmongo.protocal.UpdateMessage;

public class MongoCollectionImpl implements MongoCollection {

	private ConnectionPool pool;
	private String name;
	private MongoDB db;
	private String fullName;

	public MongoCollectionImpl(ConnectionPool pool, String fullName) {
		this.pool = pool;
		this.name = fullName;
		this.fullName = fullName;
	}

	public MongoCollectionImpl(ConnectionPool pool, String collectionName,
			MongoDB db) {
		this.pool = pool;
		this.name = collectionName;
		this.db = db;
		fullName = new StringBuilder().append(db.getName()).append(".").append(
				name).toString();
	}

	@Override
	public long count(BSONDocument query, List<String> fields) {
		BSONDocument cmd = new BSONDocument();
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
	public Cursor find(BSONDocument query) {
		return new CursorImpl(pool, query, this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void remove(BSONDocument query, boolean singleRemove) {
		DeleteMessage msg = new DeleteMessage();
		msg.setFullCollectionName(fullName);
		msg.setSelector(query);
		msg.setSingleRemove(singleRemove);
		this.sendMessage(msg);
	}

	@Override
	public void save(BSONDocument doc) {
		save(Arrays.asList(doc));
	}

	@Override
	public void save(List<BSONDocument> docList) {
		InsertMessage msg = new InsertMessage();
		msg.setDocuments(docList);
		msg.setFullCollectionName(fullName);
		sendMessage(msg);
	}

	private void sendMessage(OutgoingMessage msg) {
		Connection connection = null;
		try {
			connection = pool.getConnection();
			connection.send(msg);
		} catch (Exception e) {
			throw new MongoException(e);
		} finally {
			if (null != connection) {
				try {
					connection.close();
				} catch (IOException e) {
					throw new MongoException(e);
				}
			}
		}
	}

	@Override
	public void update(BSONDocument query, BSONDocument data, boolean upsert,
			boolean multiUpdate) {
		UpdateMessage msg = new UpdateMessage();
		msg.setFullCollectionName(fullName);
		msg.setSelector(query);
		msg.setUpdate(data);
		msg.setUpsert(upsert);
		msg.setMultiUpdate(multiUpdate);
		sendMessage(msg);
	}

	@Override
	public MongoDB getDB() {
		return db;
	}

	@Override
	public String getFullName() {
		return fullName;
	}

}
