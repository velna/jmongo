package com.velix.jmongo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.velix.bson.BSONDocument;
import com.velix.jmongo.CommandResult;
import com.velix.jmongo.Connection;
import com.velix.jmongo.ConnectionPool;
import com.velix.jmongo.Mongo;
import com.velix.jmongo.MongoAttachment;
import com.velix.jmongo.MongoAuthenticationException;
import com.velix.jmongo.MongoCollection;
import com.velix.jmongo.MongoCommandFailureException;
import com.velix.jmongo.MongoDB;
import com.velix.jmongo.MongoException;
import com.velix.jmongo.protocol.QueryMessage;
import com.velix.jmongo.protocol.ReplyMessage;
import com.velix.jmongo.util.MongoUtils;

public class MongoDBImpl implements MongoDB {
	protected String name;
	protected ConnectionPool pool;
	protected Mongo mongo;
	protected String username;
	protected String password;

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

	@Override
	public void setAuthentication(String username, String password) {
		this.username = username;
		this.password = MongoUtils.md5(username + ":mongo:" + password);
	}

	public void authenticate(Connection connection)
			throws MongoAuthenticationException {
		if (null == username) {
			return;
		}
		MongoAttachment attachment = (MongoAttachment) connection
				.getAttachment();
		if (!attachment.isDBAuthenticated(name)) {
			try {
				QueryMessage queryMessage = new QueryMessage();
				queryMessage.setFullCollectionName(name + ".$cmd");
				queryMessage.setNumberToReturn(-1);

				BSONDocument cmd = new BSONDocument();
				cmd.put("getnonce", 1);
				queryMessage.setQuery(cmd);
				connection.send(queryMessage);
				ReplyMessage replyMessage = (ReplyMessage) connection.receive();
				CommandResult result = new CommandResult(replyMessage
						.getDocuments());
				if (!result.isOk()) {
					throw new MongoAuthenticationException(result
							.getErrorMessage());
				}

				String nonce = (String) result.get("nonce");
				if (null == nonce || "".equals(nonce.trim())) {
					throw new MongoAuthenticationException("empty nonce");
				}

				cmd.clear();
				cmd.put("authenticate", 1);
				cmd.put("user", username);
				cmd.put("nonce", nonce);
				cmd.put("key", MongoUtils.md5(nonce + username + password));
				connection.send(queryMessage);
				replyMessage = (ReplyMessage) connection.receive();
				result = new CommandResult(replyMessage.getDocuments());
				if (!result.isOk()) {
					throw new MongoAuthenticationException(result
							.getErrorMessage());
				}
				attachment.addAuthenticatedDB(name);
			} catch (IOException e) {
				throw new MongoAuthenticationException(e);
			}

		}
	}
}
