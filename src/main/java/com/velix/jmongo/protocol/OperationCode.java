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
package com.velix.jmongo.protocol;

import java.util.HashMap;
import java.util.Map;

public enum OperationCode {
	/**
	 * Reply to a client request. responseTo is set
	 */
	OP_REPLY(1),
	/**
	 * generic msg command followed by a string
	 */
	OP_MSG(1000),
	/**
	 * update document
	 */
	OP_UPDATE(2001),
	/**
	 * insert new document
	 */
	OP_INSERT(2002),
	/**
	 * formerly used for OP_GET_BY_OID
	 */
	RESERVED(2003),
	/**
	 * query a collection
	 */
	OP_QUERY(2004),
	/**
	 * Get more data from a query. See Cursors
	 */
	OP_GET_MORE(2005),
	/**
	 * Delete documents
	 */
	OP_DELETE(2006),
	/**
	 * Tell database client is done with a cursor
	 */
	OP_KILL_CURSORS(2007);

	private final int value;

	private final static Map<Integer, OperationCode> TYPE_MAP = new HashMap<Integer, OperationCode>();
	static {
		for (OperationCode e : OperationCode.values()) {
			TYPE_MAP.put(e.value, e);
		}
	}

	private OperationCode(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static OperationCode valueOf(int value) {
		return TYPE_MAP.get(value);
	}

}
