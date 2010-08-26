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
import com.velix.jmongo.MongoDocument;

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
		BSONDocument doc = new MongoDocument();

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
		doc = BSONDecoder.decode(in, MongoDocument.class);
		System.out.println(doc);
		BSONEncoder.encode(doc, out);
		bs = out.toByteArray();
		HexDump.dump(bs);
	}

}
