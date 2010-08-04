package com.velix.jmongo.impl;

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
import com.velix.jmongo.MongoDocument;
import com.velix.jmongo.MongoProtocolException;
import com.velix.jmongo.Protocol;
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
	public IncomingMessage receive(SocketChannel channel, Selector selector,
			Class<?> clazz) throws IOException {
		Class<? extends BSONDocument> c;
		if (null == clazz) {
			c = MongoDocument.class;
		} else if (BSONDocument.class.isAssignableFrom(clazz)) {
			c = (Class<? extends BSONDocument>) clazz;
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
									ReplyMessage message = new ReplyMessage(c);
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

	private void checkMessage(ReplyMessage message) {
		if (BSONUtils.isBitSet(message.getResponseFlag(), 1)) {
			List<BSONDocument> docList = message.getDocuments();
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
