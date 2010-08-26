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

import java.util.List;

import com.velix.bson.BSONDocument;

public interface MongoCollection {
	<T extends BSONDocument> Cursor<T> find(BSONDocument query);

	void setObjectClass(Class<? extends BSONDocument> clazz);

	Class<? extends BSONDocument> getObjectClass();

	void save(BSONDocument doc) throws MongoWriteException, MongoException;

	void save(List<BSONDocument> docList) throws MongoWriteException,
			MongoException;

	void remove(BSONDocument query, boolean singleRemove)
			throws MongoWriteException, MongoException;

	void update(BSONDocument query, BSONDocument data, boolean upsert,
			boolean multiUpdate) throws MongoWriteException, MongoException;

	long count(BSONDocument query, List<String> fields)
			throws MongoCommandFailureException;

	String getName();

	String getFullName();

	MongoDB getDB();

	void setSafeMode(boolean safe);

	boolean isSafeMode();
}
