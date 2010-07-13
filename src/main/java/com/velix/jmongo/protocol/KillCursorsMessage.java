package com.velix.jmongo.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.velix.bson.io.BSONOutputStream;


public class KillCursorsMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private int numberOfCursorIDs;
	private List<Long> cursorIDs;

	public KillCursorsMessage() {
		messageHeader = new MessageHeader(OperationCode.OP_KILL_CURSORS);
	}

	public MessageHeader getMessageHeader() {
		return messageHeader;
	}

	@Override
	public void write(OutputStream output) throws IOException {
		BSONOutputStream out = new BSONOutputStream(1024);
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeInteger(numberOfCursorIDs);
		if (null != cursorIDs) {
			for (Long id : cursorIDs) {
				out.writeLong(id);
			}
		}
		out.set(0, out.size());
		out.writeTo(output);
	}

	public int getNumberOfCursorIDs() {
		return numberOfCursorIDs;
	}

	public void setNumberOfCursorIDs(int numberOfCursorIDs) {
		this.numberOfCursorIDs = numberOfCursorIDs;
	}

	public List<Long> getCursorIDs() {
		return cursorIDs;
	}

	public void setCursorIDs(List<Long> cursorIDs) {
		this.cursorIDs = cursorIDs;
	}

}
