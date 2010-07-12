package com.velix.jmongo;

import java.util.List;

import com.velix.bson.BSONDocument;


public interface MongoDB {
	String getName();

	List<String> getCollectionNames();

	MongoCollection getCollection(String collectionName);

	MongoCollection createCollection(String collectionName, BSONDocument options);

	boolean dropCollection(String collectionName);

	CommandResult runCommand(BSONDocument cmd, boolean shouldReply);

	boolean drop();

	Mongo getMongo();
}
