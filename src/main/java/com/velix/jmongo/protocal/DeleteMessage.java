package com.velix.jmongo.protocal;

import java.io.IOException;
import java.io.OutputStream;

import com.velix.bson.BSONDocument;
import com.velix.bson.BSONOutputStream;
import com.velix.bson.TransCoderFactory;
import com.velix.bson.util.BSONUtils;


public class DeleteMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private String fullCollectionName;
	private int flags;
	private BSONDocument selector;

	public DeleteMessage() {
		messageHeader = new MessageHeader();
		selector = new BSONDocument();
	}

	@Override
	public void write(OutputStream output) throws IOException {
		BSONOutputStream out = new BSONOutputStream(output);
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeCString(this.fullCollectionName);
		out.writeInteger(this.flags);
		out.write(TransCoderFactory.getInstance().encode(selector));
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
