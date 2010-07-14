package com.velix.jmongo;

import java.util.List;

import com.velix.bson.BSONDocument;

public interface MongoDB {
	String getName();

	List<String> getCollectionNames();

	MongoCollection getCollection(String collectionName);

	MongoCollection createCollection(String collectionName, BSONDocument options)
			throws MongoCommandFailureException;

	boolean dropCollection(String collectionName)
			throws MongoCommandFailureException;

	CommandResult runCommand(BSONDocument cmd, boolean shouldReply)
			throws MongoCommandFailureException;

	boolean drop() throws MongoCommandFailureException;

	Mongo getMongo();
}
