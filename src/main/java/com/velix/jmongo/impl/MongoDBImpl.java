package com.velix.jmongo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.velix.bson.BSONDocument;
import com.velix.jmongo.CommandResult;
import com.velix.jmongo.ConnectionPool;
import com.velix.jmongo.Mongo;
import com.velix.jmongo.MongoCollection;
import com.velix.jmongo.MongoCommandFailureException;
import com.velix.jmongo.MongoDB;
import com.velix.jmongo.MongoException;

public class MongoDBImpl implements MongoDB {
	protected String name;
	protected ConnectionPool pool;
	protected Mongo mongo;

	private final ConcurrentHashMap<String, MongoCollection> collctions = new ConcurrentHashMap<String, MongoCollection>();

	public MongoDBImpl(ConnectionPool pool, String name, Mongo mongo) {
		this.pool = pool;
		this.name = name;
		this.mongo = mongo;
	}

	@Override
	public MongoCollection createCollection(String collectionName,
			BSONDocument options) throws MongoCommandFailureException {
		if (null != options) {
			BSONDocument cmd = new BSONDocument();
			cmd.put("create ", collectionName);
			cmd.putAll(options);
			CommandResult result = runCommand(cmd, true);
			if (!result.isOk()) {
				throw new MongoCommandFailureException(result.getErrorMessage());
			}
		}
		return getCollection(collectionName);
	}

	@Override
	public boolean drop() throws MongoCommandFailureException {
		BSONDocument cmd = new BSONDocument();
		cmd.put("dropDatabase ", 1);
		CommandResult result = runCommand(cmd, true);
		return result.isOk();
	}

	@Override
	public boolean dropCollection(String collectionName)
			throws MongoCommandFailureException {
		BSONDocument cmd = new BSONDocument();
		cmd.put("drop", name);
		CommandResult result = runCommand(cmd, true);
		return result.isOk();
	}

	@Override
	public CommandResult runCommand(BSONDocument cmd, boolean shouldReply)
			throws MongoCommandFailureException {
		MongoCollection collection = this.getCollection("$cmd");
		return new CommandResult(collection.find(cmd).limit(-1).toList());
	}

	@Override
	public MongoCollection getCollection(String collectionName) {
		if (!collctions.containsKey(collectionName)) {
			collctions.putIfAbsent(collectionName, new MongoCollectionImpl(
					pool, collectionName, this));
		}
		return collctions.get(collectionName);
	}

	@Override
	public List<String> getCollectionNames() {
		MongoCollection collection = this.getCollection("system.namespaces");
		List<BSONDocument> docList = collection.find(null).toList();
		List<String> ret = new ArrayList<String>(docList.size());
		for (BSONDocument doc : docList) {
			String ns = (String) doc.get("name");
			if (null == ns || ns.equals("")) {
				throw new MongoException("invalid collection name: [" + ns
						+ "]");
			}
			int idx = ns.indexOf(".");

			String root = ns.substring(0, idx);
			if (!root.equals(name))
				continue;

			if (ns.indexOf("$") >= 0)
				continue;

			String table = ns.substring(idx + 1);

			ret.add(table);
		}
		return ret;
	}

	@Override
	public String getName() {
		return name;
	}

	public Mongo getMongo() {
		return mongo;
	}

}
