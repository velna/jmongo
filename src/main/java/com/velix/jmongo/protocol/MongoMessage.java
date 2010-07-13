package com.velix.jmongo.protocol;

import java.io.Serializable;

public interface MongoMessage extends Serializable {
	public MessageHeader getMessageHeader();
}
