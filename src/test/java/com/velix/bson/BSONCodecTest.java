package com.velix.bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.velix.bson.io.BSONCodec;
import com.velix.bson.util.HexDump;

public class BSONCodecTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void _testEncode() throws IOException {
		BSONDocument doc = new BSONDocument();
		doc.put("hello", "world");
		byte[] bs = BSONCodec.encode(doc);
		HexDump.dump(bs);
		doc = BSONCodec.decode(bs);
		System.out.println(doc);
		bs = BSONCodec.encode(doc);
		HexDump.dump(bs);
	}

	public void testEncodeArray() throws IOException {
		BSONDocument doc = new BSONDocument();

		List<Object> list = new ArrayList<Object>();
		list.add("awesome");
		list.add(5.05);
		list.add(1986);
		doc.put("BSON", list);
		byte[] bs = BSONCodec.encode(doc);
		HexDump.dump(bs);
		doc = BSONCodec.decode(bs);
		System.out.println(doc);
		bs = BSONCodec.encode(doc);
		HexDump.dump(bs);
	}

}
