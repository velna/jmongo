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
package com.velix.jmongo;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import com.velix.bson.BSONDocument;

public class Test {

	public static void main(String[] args) throws Exception {
		// testGridFS();
		// new ObjectId();
		// new ObjectId();
		test();
	}

	public static void test() {

		Mongo mongo = new MongoImpl("127.0.0.1", 27017, new Configuration());
		MongoDB db = mongo.getDB("goojia", "sa", "123456");
		MongoCollection collection = db.getCollection("goojia");
		collection.setSafeMode(true);
		BSONDocument query = new MongoDocument();
		query.put("name", "velna1");
		Cursor<BSONDocument> cursor = collection.find(query);
		for (BSONDocument doc : cursor) {
			System.out.println(doc);
		}
		cursor.close();
		collection.save(query);
		System.out.println(collection.count(new MongoDocument(), null));
		// System.out.println(db.getCollectionNames());
		mongo.close();
	}

	public static void testGridFS() throws Exception {

		Mongo mongo = new MongoImpl("127.0.0.1", 27017, new Configuration());
		MongoDB db = mongo.getDB("goojia", "sa", "123456");
		MongoGridFS gridFS = db.getGridFS("pic");
		gridFS.remove(new MongoDocument("filename", "连连看.swf"), true);
		Cursor<GridFSFile> cursor = gridFS.find(new MongoDocument("filename",
				"连连看.swf"));
		List<GridFSFile> files = cursor.toList();
		if (files.size() > 0) {
			GridFSFile file = files.get(0);
			FileOutputStream out = new FileOutputStream("d:\\a.swf");
			InputStream in = file.getInputStream();
			byte[] buf = new byte[256 * 1024];
			int length;
			while ((length = in.read(buf)) > 0) {
				out.write(buf, 0, length);
			}
			out.close();
		}
		// GridFSFile file = new GridFSFile(new FileInputStream("D:\\连连看.swf"));
		// file.setFilename("连连看.swf");
		// file.setContentType("application/flash");
		// file.setUploadDate(new Date());
		// gridFS.save(file);
		mongo.close();
	}
}
