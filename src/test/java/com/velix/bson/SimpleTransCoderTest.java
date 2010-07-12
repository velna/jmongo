package com.velix.bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.bson.SimpleTransCoder;
import com.velix.bson.util.HexDump;

import junit.framework.TestCase;

public class SimpleTransCoderTest extends TestCase {

	private SimpleTransCoder transCoder = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		transCoder = new SimpleTransCoder();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		transCoder = null;
	}

	public void _testEncode() throws IOException {
		BSONDocument doc = new BSONDocument();
		doc.put("hello", "world");
		byte[] bs = transCoder.encode(doc);
		HexDump.dump(bs);
		doc = transCoder.decode(bs);
		System.out.println(doc);
		bs = transCoder.encode(doc);
		HexDump.dump(bs);
	}

	public void testEncodeArray() throws IOException {
		BSONDocument doc = new BSONDocument();

		List<Object> list = new ArrayList<Object>();
		list.add("awesome");
		list.add(5.05);
		list.add(1986);
		doc.put("BSON", list);
		byte[] bs = transCoder.encode(doc);
		HexDump.dump(bs);
		doc = transCoder.decode(bs);
		System.out.println(doc);
		bs = transCoder.encode(doc);
		HexDump.dump(bs);
	}

}
