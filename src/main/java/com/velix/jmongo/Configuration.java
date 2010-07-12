package com.velix.jmongo;

public class Configuration {
	private static Configuration intance;

	private int maxConnectRetry;

	public int getMaxConnectRetry() {
		return maxConnectRetry;
	}

	public void setMaxConnectRetry(int maxConnectRetry) {
		this.maxConnectRetry = maxConnectRetry;
	}

	public static Configuration getInstance() {
		if (null == intance) {
			intance = new Configuration();
		}
		return intance;
	}
}
