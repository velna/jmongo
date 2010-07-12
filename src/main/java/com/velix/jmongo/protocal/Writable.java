package com.velix.jmongo.protocal;

import java.io.IOException;
import java.io.OutputStream;

public interface Writable {
	public void write(OutputStream out) throws IOException;
}
