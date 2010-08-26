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


public class Binary extends AbstractElement<byte[]> {

	private static final long serialVersionUID = -475495378136741936L;

	public static enum SubType implements BSON {
		/**
		 * Function
		 */
		FUNCTION((byte) 0x01),
		/**
		 * Binary
		 */
		BINARY((byte) 0x00),
		/**
		 * UUID
		 */
		UUID((byte) 0x03),
		/**
		 * MD5
		 */
		MD5((byte) 0x05),
		/**
		 * User defined
		 */
		USER_DEFINED((byte) 0x80);

		private final byte value;

		private SubType(final byte value) {
			this.value = value;
		}

		public byte getValue() {
			return value;
		}

		public static SubType valueOf(byte value) {
			for (SubType e : SubType.values()) {
				if (e.value == value) {
					return e;
				}
			}
			return null;
		}
	}

	private SubType subType;

	public Binary(byte[] value, SubType subType) {
		setValue(value);
		this.subType = subType;
	}

	public SubType getSubType() {
		return subType;
	}

	@Override
	public ElementType getElementType() {
		return ElementType.BINARY;
	}

}
