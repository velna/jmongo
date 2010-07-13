package com.velix.jmongo.protocal;

import java.io.IOException;
import java.io.OutputStream;

import com.velix.bson.BSONDocument;
import com.velix.bson.io.BSONCodec;
import com.velix.bson.io.BSONOutputStream;
import com.velix.bson.util.BSONUtils;


public class DeleteMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private String fullCollectionName;
	private int flags;
	private BSONDocument selector;

	public DeleteMessage() {
		messageHeader = new MessageHeader(OperationCode.OP_DELETE);
		selector = new BSONDocument();
	}

	@Override
	public void write(OutputStream output) throws IOException {
		BSONOutputStream out = new BSONOutputStream(1024);
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeCString(this.fullCollectionName);
		out.writeInteger(this.flags);
		out.write(BSONCodec.encode(selector));
		out.set(0, out.size());
		out.writeTo(output);
	}

	public MessageHeader getMessageHeader() {
		return messageHeader;
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

	public void setSingleRemove(boolean b) {
		this.flags = (int) BSONUtils.bitSet(this.flags, 0, b);
	}

	public BSONDocument getSelector() {
		return selector;
	}

	public void setSelector(BSONDocument selector) {
		this.selector = selector;
	}

}
