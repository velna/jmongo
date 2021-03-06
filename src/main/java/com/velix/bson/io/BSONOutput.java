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
package com.velix.bson.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class BSONOutput extends ByteArrayOutputStream {

	private static final String ENCDOING = "UTF-8";

	public BSONOutput() {
		super();
	}

	public BSONOutput(int size) {
		super(size);
	}

	public void writeCString(String s) throws IOException {
		write(s.getBytes(ENCDOING));
		write(0);
	}

	public void writeInteger(int i) throws IOException {
		write(i);
		write(i >> 8);
		write(i >> 16);
		write(i >> 24);
	}

	public void writeLong(long l) throws IOException {
		write((byte) l);
		write((byte) (l >> 8));
		write((byte) (l >> 16));
		write((byte) (l >> 24));
		write((byte) (l >> 32));
		write((byte) (l >> 40));
		write((byte) (l >> 48));
		write((byte) (l >> 56));
	}

	public void writeString(String s) throws IOException {
		byte[] bs = s.getBytes(ENCDOING);
		writeInteger(bs.length + 1);
		write(bs);
		write(0);
	}

	public void set(int index, int b) {
		int newcount = Math.max(count, index);
		if (newcount > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
		}
		buf[index] = (byte) b;
		count = newcount;
	}

	public void set(int index, byte[] b, int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0)
				|| ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		int newcount = Math.max(count, index + len);
		if (newcount > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
		}
		System.arraycopy(b, off, buf, index, len);
		count = newcount;
	}

	public void setInteger(int index, int i) {
		byte[] b = new byte[] { (byte) i, (byte) (i >> 8), (byte) (i >> 16),
				(byte) (i >> 24) };
		set(index, b, 0, b.length);
	}

	public void setLong(int index, long l) {
		byte[] b = new byte[] { (byte) l, (byte) (l >> 8), (byte) (l >> 16),
				(byte) (l >> 24), (byte) (l >> 32), (byte) (l >> 40),
				(byte) (l >> 48), (byte) (l >> 56) };
		set(index, b, 0, b.length);
	}
}
