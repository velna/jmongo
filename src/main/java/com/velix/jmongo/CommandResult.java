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

public class CommandResult {

	private boolean ok;
	private String errorMessage;
	private List<? extends BSONDocument> docs;

	public <T extends BSONDocument> CommandResult(List<T> docs) {
		if (null == docs) {
			throw new IllegalArgumentException("docs can not be null");
		}
		this.docs = docs;
		if (null != docs && docs.size() > 0) {
			BSONDocument firstDoc = docs.get(0);
			Double c = (Double) firstDoc.get("ok");
			ok = null == c || c.longValue() == 1;
			errorMessage = (String) firstDoc.get("$err");
			if (null == errorMessage) {
				errorMessage = (String) firstDoc.get("err");
			}
		}
	}

	public boolean isOk() {
		return ok;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public BSONDocument getFirstDoc() {
		if (null == docs || docs.isEmpty()) {
			return null;
		} else {
			return docs.get(0);
		}
	}

	public Object get(String name) {
		if (null == docs || docs.isEmpty()) {
			return null;
		} else {
			return docs.get(0).get(name);
		}
	}

}
