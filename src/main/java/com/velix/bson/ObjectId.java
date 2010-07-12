package com.velix.bson;


public class ObjectId extends AbstractElement<byte[]> {

	private static final long serialVersionUID = 3892785050074060931L;

	public ObjectId(byte[] value) {
		if (null == value || value.length != 12) {
			throw new IllegalArgumentException(
					"value must be length of 12 bytes");
		}
		setValue(value);
	}

	@Override
	public ElementType getElementType() {
		return ElementType.OBJECT_ID;
	}

}
