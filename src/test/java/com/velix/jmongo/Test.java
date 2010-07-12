package com.velix.jmongo;

import com.velix.bson.BSONDocument;
import com.velix.jmongo.impl.MongoImpl;

public class Test {

	public static void main(String[] args) throws Exception {
		// NIOConnection connection = new NIOConnection("127.0.0.1", 27017);
		// connection.connect();
		//		
		// QueryMessage query = new QueryMessage();
		// query.getFullCollectionName().setValue("goojia.goojia");
		// query.getNumberToReturn().setValue(100);
		// query.getNumberToSkip().setValue(0);
		// query.getQuery().put("name", new UTF8String("velna"));
		//
		// QueryMessage query1 = new QueryMessage();
		// query1.getFullCollectionName().setValue("goojia.goojia");
		// query1.getNumberToReturn().setValue(100);
		// query1.getNumberToSkip().setValue(0);
		// query1.getQuery().put("name", new UTF8String("velna"));
		//		
		// Command command = new Command(query);
		// Command command1 = new Command(query1);
		// connection.send(command);
		// IncomingMessage message = command.getIncomingMessage();
		// System.out.println(message);
		// connection.send(command1);
		// IncomingMessage message1 = command1.getIncomingMessage();
		// System.out.println(message1);
		Mongo mongo = new MongoImpl("127.0.0.1", 27017, new Configuration());
		MongoDB db = mongo.getDB("goojia");
		MongoCollection collection = db.getCollection("goojia");
		BSONDocument query = new BSONDocument();
		query.put("name", "velna");
		Cursor cursor = collection.find(query);
		for (BSONDocument doc : cursor) {
			System.out.println(doc);
		}
		cursor.close();
		System.out.println(collection.count(new BSONDocument(), null));
		System.out.println(db.dropCollection(collection.getName()));
		System.out.println(db.getCollectionNames());
		mongo.close();
	}
}
