package com.velix.bson;


public class JavascriptCode extends AbstractElement<String> {

	private static final long serialVersionUID = -3720111668683589429L;

	public JavascriptCode() {
		super();
	}

	public JavascriptCode(String value) {
		setValue(value);
	}

	@Override
	public ElementType getElementType() {
		return ElementType.JAVASCRIPT_CODE;
	}

}
