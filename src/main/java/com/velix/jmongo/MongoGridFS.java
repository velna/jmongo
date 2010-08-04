package com.velix.jmongo;

import java.io.InputStream;
import java.util.List;

import com.velix.bson.BSONDocument;

public interface MongoGridFS extends MongoCollection {

	GridFSFile createFile(InputStream inputStream);

	void save(GridFSFile file);

	List<GridFSFile> findFiles(BSONDocument query) throws MongoWriteException,
			MongoException;

	void remove(GridFSFile file);

}
