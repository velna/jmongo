package com.velix.jmongo.protocal;

import java.io.IOException;
import java.io.OutputStream;

import com.velix.bson.BSONDocument;
import com.velix.bson.BSONOutputStream;
import com.velix.bson.TransCoderFactory;
import com.velix.bson.util.BSONUtils;


public class UpdateMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -5278293474222136247L;

	private MessageHeader messageHeader;
	private String fullCollectionName;
	private int flags;
	private BSONDocument selector;
	private BSONDocument update;

	public UpdateMessage() {
		messageHeader = new MessageHeader();
		this.selector = new BSONDocument();
		this.update = new BSONDocument();
	}

	@Override
	public void write(OutputStream output) throws IOException {
		BSONOutputStream out = new BSONOutputStream(output);
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeCString(this.fullCollectionName);
		out.writeInteger(flags);
		out.write(TransCoderFactory.getInstance().encode(selector));
		out.write(TransCoderFactory.getInstance().encode(update));
	}

	public void setUpsert(boolean upsert) {
		this.flags = (int) BSONUtils.bitSet(this.flags, 0, upsert);
	}

	public void setMultiUpdate(boolean multiUpdate) {
		this.flags = (int) BSONUtils.bitSet(this.flags, 1, multiUpdate);
	}

	public MessageHeader getMessageHeader() {
		return messageHeader;
	}

	public void setMessageHeader(MessageHeader messageHeader) {
		this.messageHeader = messageHeader;
	}

	public String getFullCollectionName() {
		return fullCollectionName;
	}

	public void setFullCollectionName(String fullCollectionName) {
		this.fullCollectionName = fullCollectionName;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public BSONDocument getSelector() {
		return selector;
	}

	public void setSelector(BSONDocument selector) {
		this.selector = selector;
	}

	public BSONDocument getUpdate() {
		return update;
	}

	public void setUpdate(BSONDocument update) {
		this.update = update;
	}
}
