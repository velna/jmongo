package com.velix.jmongo.impl;

import java.io.InputStream;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.jmongo.ConnectionPool;
import com.velix.jmongo.GridFSFile;
import com.velix.jmongo.MongoDB;
import com.velix.jmongo.MongoException;
import com.velix.jmongo.MongoGridFS;
import com.velix.jmongo.MongoWriteException;

public class MongoGridFSImpl extends MongoCollectionImpl implements MongoGridFS {

	public MongoGridFSImpl(ConnectionPool pool, String collectionName,
			MongoDB db) {
		super(pool, collectionName, db);
		// TODO Auto-generated constructor stub
	}

	@Override
	public GridFSFile createFile(InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GridFSFile> findFiles(BSONDocument query)
			throws MongoWriteException, MongoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(GridFSFile file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(GridFSFile file) {
		// TODO Auto-generated method stub

	}

}
