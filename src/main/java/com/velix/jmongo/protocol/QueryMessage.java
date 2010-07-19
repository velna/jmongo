package com.velix.jmongo.protocol;

import java.io.IOException;

import com.velix.bson.BSONDocument;
import com.velix.bson.io.BSONEncoder;
import com.velix.bson.io.BSONOutput;
import com.velix.bson.util.BSONUtils;

public class QueryMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private int options;
	private int numberToSkip;
	private int numberToReturn;
	private String fullCollectionName;
	private BSONDocument query;
	private BSONDocument returnFieldSelector;

	public QueryMessage() {
		messageHeader = new MessageHeader(OperationCode.OP_QUERY);
	}

	@Override
	public void write(BSONOutput out) throws IOException {
		messageHeader.write(out);
		out.writeInteger(options);
		out.writeCString(this.fullCollectionName);
		out.writeInteger(numberToSkip);
		out.writeInteger(numberToReturn);
		BSONEncoder.encode(query, out);
		if (null != returnFieldSelector) {
			BSONEncoder.encode(returnFieldSelector, out);
		}
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

	public int getOptions() {
		return options;
	}

	public void setOptions(int options) {
		this.options = options;
	}

	public void setNoCursorTimeout(boolean b) {
		this.options = (int) BSONUtils.bitSet(options, 4, b);
	}

	public void setTailable(boolean b) {
		this.options = (int) BSONUtils.bitSet(options, 1, b);
	}

	public void setSlaveOk(boolean b) {
		this.options = (int) BSONUtils.bitSet(options, 2, b);
	}

	public void setAwaitData(boolean b) {
		this.options = (int) BSONUtils.bitSet(options, 5, b);
	}

	public int getNumberToSkip() {
		return numberToSkip;
	}

	public void setNumberToSkip(int numberToSkip) {
		this.numberToSkip = numberToSkip;
	}

	public int getNumberToReturn() {
		return numberToReturn;
	}

	public void setNumberToReturn(int numberToReturn) {
		this.numberToReturn = numberToReturn;
	}

	public BSONDocument getQuery() {
		return query;
	}

	public void setQuery(BSONDocument query) {
		this.query = query;
	}

	public BSONDocument getReturnFieldSelector() {
		return returnFieldSelector;
	}

	public void setReturnFieldSelector(BSONDocument returnFieldSelector) {
		this.returnFieldSelector = returnFieldSelector;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("messageHeader:").append(messageHeader);
		builder.append(", options:").append(options);
		builder.append(", numberToSkip:").append(numberToSkip);
		builder.append(", numberToReturn:").append(numberToReturn);
		builder.append(", fullCollectionName:").append(fullCollectionName);
		builder.append(", query:").append(query);
		builder.append(", returnFieldSelector:").append(returnFieldSelector);
		builder.append("}");
		return builder.toString();
	}

}
