package com.velix.bson;


public abstract class AbstractElement<V> implements Element<V> {

	private static final long serialVersionUID = -4539456858012008597L;

	private V value;

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
