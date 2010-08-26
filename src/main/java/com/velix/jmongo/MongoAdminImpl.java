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

import java.util.ArrayList;
import java.util.List;

import com.velix.bson.BSONDocument;

public class MongoAdminImpl extends MongoDBImpl implements MongoAdmin {

	public MongoAdminImpl(ConnectionPool connectionPool, Mongo mongo) {
		super(connectionPool, "admin", mongo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDBNames() throws MongoException {
		BSONDocument cmd = new MongoDocument();
		cmd.put("listDatabases ", 1);
		CommandResult result = runCommand(cmd, true);
		if (!result.isOk()) {
			throw new MongoException(result.getErrorMessage());
		}
		List<BSONDocument> dbList = (List<BSONDocument>) result.getFirstDoc()
				.get("databases");
		List<String> ret = new ArrayList<String>(dbList.size());
		for (BSONDocument doc : dbList) {
			ret.add((String) doc.get("name"));
		}
		return ret;
	}

	public Mongo getMongo() {
		return mongo;
	}

}
