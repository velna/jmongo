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

import java.util.concurrent.CountDownLatch;

import com.velix.bson.BSONDocument;

public class JMongoTest implements Runnable {

	private Mongo mongo;
	private CountDownLatch countDownLatch;
	private int times;

	public JMongoTest(CountDownLatch countDownLatch, int times)
			throws Exception {
		Configuration config = new Configuration();
		config.maxActive = 10;
		mongo = new MongoImpl(config, "127.0.0.1:27017");
		this.countDownLatch = countDownLatch;
		this.times = times;
	}

	public void run() {
		// Random random = new Random();
		for (int i = 0; i < times; i++) {
			try {
				MongoCollection collection = mongo.getDB("goojia")
						.getCollection("goojia_common.ppc_map");
				BSONDocument doc = new MongoDocument();
				doc.put("city", "sh");
				doc.put("category", 1);
				doc.put("relate_id", 123);
				doc.put("relate_id_2", 0);
				collection.find(doc).toList();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		countDownLatch.countDown();
		System.out.println("a");
	}

	public static void _main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		final int count = 1;
		CountDownLatch countDownLatch = new CountDownLatch(count);
		JMongoTest test = new JMongoTest(countDownLatch, 200000);
		for (int i = 0; i < count; i++) {
			new Thread(test).start();
		}
		countDownLatch.await();
		System.out.println(System.currentTimeMillis() - start);
		test.mongo.close();
	}

	public static void main(String[] args) throws Exception {
		Configuration config = new Configuration();
		Mongo mongo = new MongoImpl(config, "127.0.0.1:27018",
				"127.0.0.1:27017", "127.0.0.1:27019");
		MongoCollection collection = mongo.getDB("goojia").getCollection(
				"goojia_common.ppc_map");
		BSONDocument doc = new MongoDocument();
		doc.put("city", "sh");
		doc.put("category", 1);
		doc.put("relate_id", 123);
		doc.put("relate_id_2", 0);
		collection.save(doc);
		Thread.sleep(20000);
		doc = new MongoDocument();
		doc.put("city", "bj");
		doc.put("category", 12);
		doc.put("relate_id", 12344);
		doc.put("relate_id_2", 0);
		collection.save(doc);
		mongo.close();
	}
}
