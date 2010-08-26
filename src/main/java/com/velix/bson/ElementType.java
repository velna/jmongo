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
package com.velix.bson;

import java.util.HashMap;
import java.util.Map;

public enum ElementType implements BSON {
	/**
	 * Floating point
	 */
	FLOATING_POINT((byte) 0x01),
	/**
	 * UTF-8 string
	 */
	UTF8_STRING((byte) 0x02),
	/**
	 * Embedded document
	 */
	EMBEDDED_DOCUMENT((byte) 0x03),
	/**
	 * Array
	 */
	ARRAY((byte) 0x04),
	/**
	 * Binary data
	 */
	BINARY((byte) 0x05),
	/**
	 * ObjectId
	 */
	OBJECT_ID((byte) 0x07),
	/**
	 * Boolean
	 */
	BOOLEAN((byte) 0x08),
	/**
	 * UTC datetime
	 */
	UTC_DATETIME((byte) 0x09),
	/**
	 * Null value
	 */
	NULL((byte) 0x0A),
	/**
	 * Regular expression
	 */
	REGULAR_EXPRESSION((byte) 0x0B),
	/**
	 * JavaScript code
	 */
	JAVASCRIPT_CODE((byte) 0x0D),
	/**
	 * Symbol
	 */
	SYMBOL((byte) 0x0E),
	/**
	 * JavaScript code w/ scope
	 */
	JAVASCRIPT_CODE_W_SCOPE((byte) 0x0F),
	/**
	 * 32-bit Integer
	 */
	INTEGER_32((byte) 0x10),
	/**
	 * Timestamp
	 */
	TIMESTAMP((byte) 0x11),
	/**
	 * 64-bit Integer
	 */
	INTEGER_64((byte) 0x12),
	/**
	 * Min key
	 */
	MIN_KEY((byte) 0xFF),
	/**
	 * Max key
	 */
	MAX_KEY((byte) 0x7F);

	private final byte value;

	private final static Map<Byte, ElementType> TYPE_MAP = new HashMap<Byte, ElementType>();
	static {
		for (ElementType e : ElementType.values()) {
			TYPE_MAP.put(e.value, e);
		}
	}

	private ElementType(final byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public static ElementType valueOf(byte value) {
		return TYPE_MAP.get(value);
	}
}
