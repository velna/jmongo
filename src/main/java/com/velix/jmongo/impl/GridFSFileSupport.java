package com.velix.jmongo.impl;

import java.io.InputStream;

import com.velix.jmongo.GridFSFile;
import com.velix.jmongo.MongoCollection;
import com.velix.jmongo.MongoCollectionAware;

class GridFSFileSupport extends GridFSFile implements MongoCollectionAware {
	private MongoCollection collection;

	@Override
	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	public MongoCollection getMongoCollection() {
		return collection;
	}

	public void setMongoCollection(MongoCollection collection) {
		this.collection = collection;
	}
}
