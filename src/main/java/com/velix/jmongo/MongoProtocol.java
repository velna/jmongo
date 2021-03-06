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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.bson.io.BSONInput;
import com.velix.bson.io.BSONOutput;
import com.velix.bson.util.BSONUtils;
import com.velix.jmongo.protocol.IncomingMessage;
import com.velix.jmongo.protocol.OutgoingMessage;
import com.velix.jmongo.protocol.ReplyMessage;

public class MongoProtocol implements Protocol {

	private final static int HEAD_SIZE = 4;

	private static ThreadLocal<BSONInput> LOCAL_IN = new ThreadLocal<BSONInput>() {
		protected BSONInput initialValue() {
			return new BSONInput(10 << 10);
		}
	};

	private static ThreadLocal<BSONOutput> LOCAL_OUT = new ThreadLocal<BSONOutput>() {
		protected BSONOutput initialValue() {
			return new BSONOutput(10 << 10);
		}
	};

	// private static ThreadLocal<ByteBuffer> LOCAL_BUFFER = new
	// ThreadLocal<ByteBuffer>() {
	// protected ByteBuffer initialValue() {
	// return ByteBuffer.allocate(10 << 10);
	// }
	// };

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BSONDocument> IncomingMessage receive(
			SocketChannel channel, Selector selector, Class<T> clazz)
			throws IOException {
		Class<T> c;
		if (null == clazz) {
			c = (Class<T>) MongoDocument.class;
		} else if (BSONDocument.class.isAssignableFrom(clazz)) {
			c = clazz;
		} else {
			throw new MongoProtocolException(BSONDocument.class
					+ " is not assignable from " + clazz);
		}
		try {
			int nextRemainCount = HEAD_SIZE;
			ByteBuffer buffer = ByteBuffer.allocate(HEAD_SIZE);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			boolean isNextHeader = true;
			while (true) {
				selector.select();
				Iterator<SelectionKey> i = selector.selectedKeys().iterator();
				while (i.hasNext()) {
					SelectionKey key = i.next();
					if (key.isReadable()) {
						channel.read(buffer);
						buffer.flip();
						if (buffer.remaining() >= nextRemainCount) {
							if (isNextHeader) {
								nextRemainCount = buffer.getInt() - HEAD_SIZE;
								buffer = ByteBuffer.allocate(nextRemainCount
										+ HEAD_SIZE);
								buffer.order(ByteOrder.LITTLE_ENDIAN);
								buffer.putInt(nextRemainCount);
								buffer.flip();
								isNextHeader = false;
							}
							if (!isNextHeader) {
								if (buffer.remaining() >= nextRemainCount) {
									// byte[] buf = new byte[nextRemainCount
									// + HEAD_SIZE];
									// buffer.get(buf);
									ReplyMessage<T> message = new ReplyMessage<T>(
											c);
									BSONInput in = LOCAL_IN.get();
									in.reset(buffer);
									message.read(in);
									// message.read(new
									// ByteArrayInputStream(buf));
									checkMessage(message);
									return message;
								}
							}
						}
						buffer.compact();
					}
				}
			}
		} catch (IOException e) {
			throw new MongoProtocolException("error receive message: ", e);
		}
	}

	private <T extends BSONDocument> void checkMessage(ReplyMessage<T> message) {
		if (BSONUtils.isBitSet(message.getResponseFlag(), 1)) {
			List<T> docList = message.getDocuments();
			StringBuilder err = new StringBuilder("error: ");
			if (null != docList) {
				for (BSONDocument doc : docList) {
					String error = (String) doc.get("$err");
					if (null != error) {
						err.append(error);
					}
				}
			}
			throw new MongoProtocolException(err.toString());
		}
	}

	@Override
	public void send(OutgoingMessage message, SocketChannel channel,
			Selector selector) throws IOException {
		BSONOutput out = LOCAL_OUT.get();
		out.reset();
		message.write(out);
		ByteBuffer buffer = ByteBuffer.wrap(out.toByteArray());
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		channel.write(buffer);
	}
}
