package com.velix.jmongo.protocal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.bson.io.BSONCodec;
import com.velix.bson.io.BSONOutputStream;

public class InsertMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private String fullCollectionName;
	private List<BSONDocument> documents;

	public InsertMessage() {
		messageHeader = new MessageHeader(OperationCode.OP_INSERT);
	}

	@Override
	public void write(OutputStream output) throws IOException {
		BSONOutputStream out = new BSONOutputStream(1024);
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeCString(this.fullCollectionName);
		if (null != documents) {
			for (BSONDocument doc : documents) {
				byte[] data = BSONCodec.encode(doc);
				out.write(data);
			}
		}
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

	public List<BSONDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<BSONDocument> documents) {
		this.documents = documents;
	}

}
