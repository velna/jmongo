package com.velix.jmongo;

public abstract class AbstractConnection implements Connection {
	private Object attachment;

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}
}
