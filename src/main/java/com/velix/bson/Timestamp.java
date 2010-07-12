package com.velix.bson;


public class Timestamp extends AbstractElement<Long> {

	private static final long serialVersionUID = -6419612108997483217L;

	public Timestamp() {
		super();
	}

	public Timestamp(Long value) {
		setValue(value);
	}

	@Override
	public ElementType getElementType() {
		return ElementType.TIMESTAMP;
	}

}
