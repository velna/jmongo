package com.velix.jmongo.protocol;

import java.io.IOException;

import com.velix.bson.io.BSONOutputStream;

public class GetMoreMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private String fullCollectionName;
	private int numberToReturn;
	private long cursorID;

	public GetMoreMessage() {
		messageHeader = new MessageHeader(OperationCode.OP_GET_MORE);
	}

	@Override
	public void write(BSONOutputStream out) throws IOException {
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeCString(this.fullCollectionName);
		out.writeInteger(this.numberToReturn);
		out.writeLong(cursorID);
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

	public int getNumberToReturn() {
		return numberToReturn;
	}

	public void setNumberToReturn(int numberToReturn) {
		this.numberToReturn = numberToReturn;
	}

	public long getCursorID() {
		return cursorID;
	}

	public void setCursorID(long cursorID) {
		this.cursorID = cursorID;
	}

}
