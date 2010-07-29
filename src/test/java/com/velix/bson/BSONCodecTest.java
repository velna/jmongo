package com.velix.bson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.velix.bson.io.BSONDecoder;
import com.velix.bson.io.BSONEncoder;
import com.velix.bson.io.BSONInput;
import com.velix.bson.io.BSONOutput;
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

	public void testEncodeArray2() throws IOException {
		BSONOutput out = new BSONOutput(1024);
		BSONDocument doc = new BSONDocument();

		List<Object> list = new ArrayList<Object>();
		list.add("awesome");
		list.add(5.05);
		list.add(1986);
		doc.put("BSON", list);
		BSONEncoder.encode(doc, out);
		byte[] bs = out.toByteArray();
		HexDump.dump(bs);
		BSONInput in = new BSONInput(10 << 10);
		in.reset(ByteBuffer.wrap(bs));
		doc = BSONDecoder.decode(in);
		System.out.println(doc);
		BSONEncoder.encode(doc, out);
		bs = out.toByteArray();
		HexDump.dump(bs);
	}

}
