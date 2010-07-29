package com.velix.jmongo.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MongoUtils {
	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String md5(String message) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] buffer = message.getBytes("UTF-8");
			digest.update(buffer);
			return encodeHex(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String encodeHex(byte[] data) {
		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS_LOWER[0x0F & data[i]];
		}
		return new String(out);
	}

	public static byte[] decodeHex(char[] data) throws IllegalArgumentException {
		int len = data.length;
		if ((len & 0x01) != 0) {
			throw new IllegalArgumentException("Odd number of characters.");
		}

		byte[] out = new byte[len >> 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}
		return out;
	}

	protected static int toDigit(char ch, int index)
			throws IllegalArgumentException {
		int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new IllegalArgumentException("Illegal hexadecimal character "
					+ ch + " at index " + index);
		}
		return digit;
	}
}
