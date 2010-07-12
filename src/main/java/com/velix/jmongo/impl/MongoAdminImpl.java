package com.velix.jmongo.impl;

import java.util.ArrayList;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.jmongo.CommandResult;
import com.velix.jmongo.ConnectionPool;
import com.velix.jmongo.Mongo;
import com.velix.jmongo.MongoAdmin;
import com.velix.jmongo.MongoException;

public class MongoAdminImpl extends MongoDBImpl implements MongoAdmin {

	public MongoAdminImpl(ConnectionPool connectionPool, Mongo mongo) {
		super(connectionPool, "admin", mongo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDBNames() throws MongoException {
		BSONDocument cmd = new BSONDocument();
		cmd.put("listDatabases ", 1);
		CommandResult result = runCommand(cmd, true);
		if (!result.isOk()) {
			throw new MongoException(result.getErrorMessage());
		}
		List<BSONDocument> dbList = (List<BSONDocument>) result.getFirstDoc()
				.getArray("databases");
		List<String> ret = new ArrayList<String>(dbList.size());
		for (BSONDocument doc : dbList) {
			ret.add(doc.getString("name"));
		}
		return ret;
	}

	public Mongo getMongo() {
		return mongo;
	}

}
