package com.velix.jmongo.protocal;

import java.io.IOException;
import java.io.InputStream;

public interface Readable {
	public void read(InputStream in) throws IOException;
}
