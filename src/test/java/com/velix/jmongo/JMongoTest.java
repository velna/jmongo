package com.velix.jmongo;

import java.util.concurrent.CountDownLatch;

import com.velix.bson.BSONDocument;
import com.velix.jmongo.impl.MongoImpl;

public class JMongoTest implements Runnable {

	private Mongo mongo;
	private CountDownLatch countDownLatch;
	private int times;

	public JMongoTest(CountDownLatch countDownLatch, int times)
			throws Exception {
		Configuration config = new Configuration();
		config.maxActive = 10;
		mongo = new MongoImpl("127.0.0.1", 27017, config);
		this.countDownLatch = countDownLatch;
		this.times = times;
	}

	public void run() {
		// Random random = new Random();
		for (int i = 0; i < times; i++) {
			try {
				MongoCollection collection = mongo.getDB("goojia")
						.getCollection("goojia_common.ppc_map");
				BSONDocument doc = new BSONDocument();
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

	public static void main(String[] args) throws Exception {
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
}
