package com.velix.jmongo.protocol;

import java.io.IOException;

import com.velix.bson.BSONDocument;
import com.velix.bson.io.BSONEncoder;
import com.velix.bson.io.BSONOutput;
import com.velix.bson.util.BSONUtils;
import com.velix.jmongo.MongoDocument;

public class DeleteMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private String fullCollectionName;
	private int flags;
	private BSONDocument selector;

	public DeleteMessage() {
		messageHeader = new MessageHeader(OperationCode.OP_DELETE);
		selector = new MongoDocument();
	}

	@Override
	public void write(BSONOutput out) throws IOException {
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeCString(this.fullCollectionName);
		out.writeInteger(this.flags);
		BSONEncoder.encode(selector, out);
		out.set(0, out.size());
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
