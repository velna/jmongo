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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.velix.bson.BSONDocument;
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
			BSONDocument cmd = new MongoDocument();
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
		BSONDocument cmd = new MongoDocument();
		cmd.put("dropDatabase ", 1);
		CommandResult result = runCommand(cmd, true);
		return result.isOk();
	}

	@Override
	public boolean dropCollection(String collectionName)
			throws MongoCommandFailureException {
		BSONDocument cmd = new MongoDocument();
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

				BSONDocument cmd = new MongoDocument();
				cmd.put("getnonce", 1);
				queryMessage.setQuery(cmd);
				connection.send(queryMessage);
				ReplyMessage replyMessage = (ReplyMessage) connection
						.receive(null);
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
				replyMessage = (ReplyMessage) connection.receive(null);
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

	@Override
	public MongoGridFS getGridFS(String gridFSName) {
		if (!collctions.containsKey(gridFSName)) {
			collctions.putIfAbsent(gridFSName, new MongoGridFSImpl(pool,
					gridFSName, this));
		}
		return (MongoGridFS) collctions.get(gridFSName);
	}
}
