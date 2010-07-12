package com.velix.bson;

import java.io.IOException;
import java.io.OutputStream;

public class BSONOutputStream extends OutputStream {

	private static final String ENCDOING = "UTF-8";

	private OutputStream out;

	public BSONOutputStream(OutputStream out) {
		super();
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
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
		write((int) (l));
		write((int) (l >> 8));
		write((int) (l >> 16));
		write((int) (l >> 24));
		write((int) (l >> 32));
		write((int) (l >> 40));
		write((int) (l >> 48));
		write((int) (l >> 56));
	}

	public void writeString(String s) throws IOException {
		byte[] bs = s.getBytes(ENCDOING);
		writeInteger(bs.length + 1);
		write(bs);
		write(0);
	}

}
