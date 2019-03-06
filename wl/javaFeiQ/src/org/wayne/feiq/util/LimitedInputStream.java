package org.wayne.feiq.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * 定长输入流
 * 
 * @author 5v
 * 
 */
public class LimitedInputStream extends InputStream {

	private InputStream in;
	protected long index, length;
	protected boolean closeable = false;

	/**
	 * 定长输入流，不关闭流
	 * 
	 * @param in
	 *            输入流
	 * @param length
	 *            长度
	 * @see #LimitedInputStream(InputStream, long, boolean)
	 */
	public LimitedInputStream(InputStream in, long length) {
		this(in, length, false);
	}

	/**
	 * 定长输入流
	 * 
	 * @param in
	 *            输入流
	 * @param length
	 *            长度
	 * @param closeable
	 *            是否可以关闭
	 */
	public LimitedInputStream(InputStream in, long length, boolean closeable) {
		this.in = in;
		this.index = 0;
		this.length = length;
		this.closeable = closeable;
	}

	public int available() throws IOException {
		return (int) (length - index);
	}

	public boolean markSupported() {
		return false;
	}

	public int read() throws IOException {
		if (index == length)
			return -1;
		int b = in.read();
		++index;
		return b;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		if (index == length)
			return -1;
		int count = len;
		if (index + count > length) {
			count = (int) (length - index);
		}
		count = in.read(b, off, count);
		index += count;
		return count;
	}

	public void close() throws IOException {
		if (in != null) {
			if (closeable) {
				in.close();
			}
			in = null;
		}
	}
}
