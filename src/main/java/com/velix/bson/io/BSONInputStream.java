package com.velix.bson.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BSONInputStream extends InputStream {

	private InputStream in;

	private long count;

	public BSONInputStream(InputStream in) {
		super();
		this.in = in;
	}

	@Override
	public int read() throws IOException {
		count++;
		return in.read();
	}

	public Integer readInteger() throws IOException {
		byte[] buf = new byte[4];
		int len = this.read(buf);
		if (len < 0) {
			return null;
		} else if (len != 4) {
			throw new IOException("can not read integer from the stream");
		}
		return (((int) buf[0]) & 0xff) | (((int) buf[1]) << 8 & 0xff00)
				| (((int) buf[2]) << 16 & 0xff0000)
				| (((int) buf[3]) << 24 & 0xff000000);
	}

	public Long readLong() throws IOException {
		byte[] buf = new byte[8];
		int len = this.read(buf);
		if (len < 0) {
			return null;
		} else if (len != 8) {
			throw new IOException("can not read long from the stream");
		}

		return (((long) buf[0]) & 0xffL) | (((long) buf[1]) << 8 & 0xff00L)
				| (((long) buf[2]) << 16 & 0xff0000L)
				| (((long) buf[3]) << 24 & 0xff000000L)
				| (((long) buf[4]) << 32 & 0xff00000000L)
				| (((long) buf[5]) << 40 & 0xff0000000000L)
				| (((long) buf[6]) << 48 & 0xff000000000000L)
				| (((long) buf[7]) << 56 & 0xff00000000000000L);
	}

	public String readCString() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b;
		while ((b = this.read()) >= 0) {
			if (b == 0) {
				return new String(out.toByteArray(), "UTF-8");
			} else {
				out.write(b);
			}
		}
		throw new IOException("can not read cstring from the stream");
	}

	public String readString() throws IOException {
		Integer len = this.readInteger();
		if (null == len) {
			return null;
		}
		byte[] buf = new byte[len];
		int readLen = this.read(buf);
		if (readLen != len) {
			throw new IOException("can not read string from the stream");
		}
		return new String(buf, 0, readLen - 1, "UTF-8");
	}

	public long getCount() {
		return count;
	}

}
