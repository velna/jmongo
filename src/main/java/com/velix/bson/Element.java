package com.velix.bson;

public interface Element<V> extends BSON {
	public static final Element<Object> MAX_KEY = new MyElement(
			ElementType.MAX_KEY);
	public static final Element<Object> MIN_KEY = new MyElement(
			ElementType.MIN_KEY);
	public static final Element<Object> NULL = new MyElement(ElementType.NULL);

	ElementType getElementType();

	V getValue();

}

class MyElement implements Element<Object> {

	private static final long serialVersionUID = -2484425611850620587L;

	private final ElementType elementType;

	public MyElement(ElementType elementType) {
		this.elementType = elementType;
	}

	@Override
	public ElementType getElementType() {
		return elementType;
	}

	@Override
	public Object getValue() {
		return null;
	}

}