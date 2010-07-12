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
