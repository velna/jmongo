package com.velix.jmongo.protocal;

import java.nio.ByteBuffer;

public interface IncomingMessage {
	public boolean read(ByteBuffer buffer);
}
