package com.velix.jmongo.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;


import com.velix.bson.BSONDocument;
import com.velix.bson.util.BSONUtils;
import com.velix.jmongo.MongoException;
import com.velix.jmongo.Protocal;
import com.velix.jmongo.protocal.IncomingMessage;
import com.velix.jmongo.protocal.OutgoingMessage;
import com.velix.jmongo.protocal.ReplyMessage;

public class MongoProtocal implements Protocal {

	public final static Protocal PROTOCAL = new MongoProtocal();

	private final static int HEAD_SIZE = 4;

	private MongoProtocal() {

	}

	@Override
	public IncomingMessage receive(SocketChannel channel, Selector selector)
			throws IOException {
		int nextRemainCount = HEAD_SIZE;
		ByteBuffer buffer = ByteBuffer.allocateDirect(HEAD_SIZE);
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
							buffer = ByteBuffer.allocateDirect(nextRemainCount
									+ HEAD_SIZE);
							buffer.order(ByteOrder.LITTLE_ENDIAN);
							buffer.putInt(nextRemainCount);
							buffer.flip();
							isNextHeader = false;
						}
						if (!isNextHeader) {
							if (buffer.remaining() >= nextRemainCount) {
								int total = nextRemainCount + HEAD_SIZE;
								ByteArrayOutputStream byteOut = new ByteArrayOutputStream(
										total);
								byte[] buf = new byte[total];
								buffer.get(buf);
								byteOut.write(buf);
								ReplyMessage message = new ReplyMessage();
								message.read(new ByteArrayInputStream(byteOut
										.toByteArray()));
								checkMessage(message);
								return message;
							}
						}
					}
					buffer.compact();
				}
			}
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
			throw new MongoException(err.toString());
		}
	}

	@Override
	public void send(OutgoingMessage message, SocketChannel channel,
			Selector selector) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		message.write(out);
		channel.write(ByteBuffer.wrap(out.toByteArray()));
	}
}
