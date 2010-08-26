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

import java.util.HashSet;
import java.util.Set;

public class MongoAttachment {

	private Set<String> authenticatedDBs = new HashSet<String>();

	public Set<String> getAuthenticatedDBs() {
		return authenticatedDBs;
	}

	public boolean isDBAuthenticated(String dbname) {
		if (authenticatedDBs.contains("admin")) {
			return true;
		}
		return authenticatedDBs.contains(dbname);
	}

	public void addAuthenticatedDB(String dbname) {
		authenticatedDBs.add(dbname);
	}

	public boolean removeAuthenticatedDB(String dbname) {
		return authenticatedDBs.remove(dbname);
	}
}
