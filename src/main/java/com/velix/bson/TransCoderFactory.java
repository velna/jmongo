package com.velix.bson;


public class TransCoderFactory {
	private static final TransCoder SIMPLE_TRANSCODER = new SimpleTransCoder();

	public static TransCoder getInstance() {
		return SIMPLE_TRANSCODER;
	}
}
