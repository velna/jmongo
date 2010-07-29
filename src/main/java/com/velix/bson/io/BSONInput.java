package com.velix.bson.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BSONInput {
	private ByteBuffer buffer;

	private long count;

	private ByteArrayOutputStream byteStream;

	public BSONInput(int capacity) {
		byteStream = new ByteArrayOutputStream(capacity);
	}

	public void reset(ByteBuffer buffer) {
		this.buffer = buffer;
		this.buffer.order(ByteOrder.LITTLE_ENDIAN);
		count = 0;
	}

	public byte read() {
		count++;
		return buffer.get();
	}

	public int readInteger() {
		count += 4;
		return buffer.getInt();
	}

	public long readLong() {
		count += 8;
		return buffer.getLong();
	}

	public double readDouble() {
		count += 8;
		return buffer.getDouble();
	}

	public void read(byte[] buf) {
		count += buf.length;
		buffer.get(buf, 0, buf.length);
	}

	public String readCString() throws IOException {
		byteStream.reset();
		byte b;
		while (true) {
			b = buffer.get();
			if (b != 0) {
				byteStream.write(b);
			} else {
				count += byteStream.size() + 1;
				return new String(byteStream.toByteArray(), "UTF-8");
			}
		}
	}

	public String readString() throws IOException {
		int len = buffer.getInt();
		if (len < 0) {
			throw new IOException("can not read string from the stream");
		}
		if (len == 0) {
			buffer.get();
			count += 5;
			return "";
		}
		byte[] buf = new byte[len];
		buffer.get(buf);
		count += len + 4;
		return new String(buf, 0, len - 1, "UTF-8");
	}

	public long count() {
		return count;
	}
}
