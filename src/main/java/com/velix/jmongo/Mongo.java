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

public interface Mongo {
	public static final int DEFAULT_PORT = 27017;

	MongoAdmin getAdmin() throws IllegalStateException;

	/**
	 * get db by name
	 * 
	 * @param dbName
	 * @return
	 * @throws IllegalStateException
	 *             if this instance if already closed
	 */
	MongoDB getDB(String dbName) throws IllegalStateException;

	/**
	 * get db by name, authenticate the connection using username and password
	 * 
	 * @param dbName
	 * @param username
	 * @param password
	 * @return
	 * @throws IllegalStateException
	 *             if this instance if already closed
	 */
	MongoDB getDB(String dbName, String username, String password)
			throws IllegalStateException, MongoAuthenticationException;

	void replicaSetsCheck();

	void close();
}
