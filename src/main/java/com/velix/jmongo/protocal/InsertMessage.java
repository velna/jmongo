package com.velix.jmongo.protocal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.bson.BSONOutputStream;
import com.velix.bson.TransCoderFactory;


public class InsertMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private String fullCollectionName;
	private List<BSONDocument> documents;

	public InsertMessage() {
		messageHeader = new MessageHeader();
	}

	@Override
	public void write(OutputStream output) throws IOException {
		BSONOutputStream out = new BSONOutputStream(output);
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeCString(this.fullCollectionName);
		if (null != documents) {
			for (BSONDocument doc : documents) {
				out.write(TransCoderFactory.getInstance().encode(doc));
			}
		}
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

	public List<BSONDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<BSONDocument> documents) {
		this.documents = documents;
	}

}
