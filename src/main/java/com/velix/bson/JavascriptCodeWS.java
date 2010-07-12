package com.velix.bson;


public class JavascriptCodeWS extends AbstractElement<CodeWS> {

	private static final long serialVersionUID = -352956563968273102L;

	public JavascriptCodeWS() {
		super();
	}

	public JavascriptCodeWS(CodeWS value) {
		setValue(value);
	}

	@Override
	public ElementType getElementType() {
		return ElementType.JAVASCRIPT_CODE_W_SCOPE;
	}

}
