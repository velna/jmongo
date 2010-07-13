package com.velix.bson;

public interface Element<V> extends BSON {
	ElementType getElementType();

	V getValue();

}