package org.wayne.feiq.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class StreamUtil {

	private StreamUtil() {
	}

	public static void copy(InputStream in, OutputStream out)
			throws IOException {
		copy(in, out, -1, null);
	}

	/**
	 * copy in to out
	 * 
	 * @param in
	 * @param out
	 * @param length
	 *            if length is -1 read all
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out, long length)
			throws IOException {
		copy(in, out, length, null);
	}

	public static void copy(InputStream in, final RandomAccessFile out,
			long length, Progress progress) throws IOException {
		out.setLength(length);
		copy(in, new Writeable() {
			public void write(byte[] b, int off, int len) throws IOException {
				out.write(b, off, len);
			}
		}, length, progress);
	}

	public static void copy(InputStream in, final OutputStream out,
			long length, Progress progress) throws IOException {
		copy(in, new Writeable() {
			public void write(byte[] b, int off, int len) throws IOException {
				out.write(b, off, len);
			}
		}, length, progress);
		out.flush();
	}

	public static interface Writeable {
		public void write(byte b[], int off, int len) throws IOException;
	}

	public static void copy(InputStream in, Writeable out, long length,
			Progress progress) throws IOException {
		final byte[] buffer = new byte[1024];
		int count = 0, total = 0;
		while ((count = in.read(buffer)) != -1) {
			out.write(buffer, 0, count);
			if (progress != null) {
				progress.addValue(count);
			}
			if (length > 0) {
				total += count;
				if (total == length) {
					break;
				}
			}
		}
	}

	public static void writeInt(OutputStream out, int val) throws IOException {
		byte[] bs = new byte[4];
		bs[3] = (byte) (val >>> 0);
		bs[2] = (byte) (val >>> 8);
		bs[1] = (byte) (val >>> 16);
		bs[0] = (byte) (val >>> 24);
		out.write(bs);
	}

	public static int readInt(InputStream in) throws IOException {
		byte[] bs = new byte[4];
		in.read(bs);
		return ((bs[3] & 0xFF) << 0) + ((bs[2] & 0xFF) << 8)
				+ ((bs[1] & 0xFF) << 16) + ((bs[0] & 0xFF) << 24);
	}

	public interface Progress {
		void addValue(int value);
	}
}
