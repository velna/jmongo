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
package com.velix.bson.util;

import java.io.IOException;

public class BSONUtils {

	public static long bitSet(long holder, int i, boolean set) {
		if (i < 0 || i > 63) {
			throw new IllegalArgumentException(
					"i should from 0 inclusive and 64 exclusive");
		}
		if (set) {
			return holder | (1 << i);
		} else {
			int ii = 1 << i;
			return (holder | ii) ^ ii;
		}
	}

	public static boolean isBitSet(long holder, int i) {
		if (i < 0 || i > 63) {
			throw new IllegalArgumentException(
					"i should from 0 inclusive and 64 exclusive");
		}
		return (holder & (1 << i)) > 0;
	}

	public static int stringByteLength(String s) throws IOException {
		byte[] bs = s.getBytes("UTF-8");
		return bs.length + 5;
	}

	public static int cstringByteLength(String s) throws IOException {
		byte[] bs = s.getBytes("UTF-8");
		return bs.length + 1;
	}

}
