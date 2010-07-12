package com.velix.bson;


public class Symbol extends AbstractElement<String> {

	private static final long serialVersionUID = 7360974348063642173L;

	public Symbol() {
		super();
	}

	public Symbol(String value) {
		setValue(value);
	}

	@Override
	public ElementType getElementType() {
		return ElementType.SYMBOL;
	}

}
