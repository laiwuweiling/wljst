package org.wayne.feiq.util;

import java.io.ByteArrayOutputStream;

public class MyByteArrayOutputStream extends ByteArrayOutputStream {

	public MyByteArrayOutputStream() {
	}

	public MyByteArrayOutputStream(int size) {
		super(size);
	}

	public byte[] getBuf() {
		return this.buf;
	}

}
