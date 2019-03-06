package org.wayne.feiq.util;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;

import com.swtdesigner.SWTResourceManager;

public class Util {

	public static void closeQuietly(Closeable closeable) {
		closeQuietly(closeable, null);
	}

	public static void closeQuietly(Closeable closeable, Log log) {
		if (closeable != null)
			try {
				closeable.close();
			} catch (IOException e) {
				if (log != null)
					log.error("close Exception", e);
			}
	}

	public static void closeQuietly(Socket socket) {
		closeQuietly(socket, null);
	}

	public static void closeQuietly(Socket socket, Log log) {
		if (socket != null)
			try {
				socket.close();
			} catch (IOException e) {
				if (log != null)
					log.error("close socket Exception", e);
			}
	}

	public static void closeQuietly(Object closeable) {
		closeQuietly(closeable, null);
	}

	public static void closeQuietly(Object closeable, Log log) {
		if (closeable != null)
			try {
				closeable.getClass().getMethod("close").invoke(closeable);
			} catch (Exception e) {
				if (log != null)
					log.error("close Exception", e);
			}
	}

	private static final BigDecimal UNIT = new BigDecimal(1024);
	private static final BigDecimal KB = UNIT;
	private static final BigDecimal MB = KB.multiply(UNIT);
	private static final BigDecimal GB = MB.multiply(UNIT);

	public static String byteCountToDisplaySize(long size) {
		String display = "";
		BigDecimal length = new BigDecimal(size);
		if (length.longValue() > GB.longValue()) {
			display = length.divide(GB).setScale(2, RoundingMode.HALF_UP)
					+ " GB";
		} else if (length.longValue() > MB.longValue()) {
			display = length.divide(MB).setScale(2, RoundingMode.HALF_UP)
					+ " MB";
		} else {
			display = length.divide(KB).setScale(2, RoundingMode.HALF_UP)
					+ " KB";
		}
		return display;
	}

	/**
	 * find file program
	 * 
	 * @param file
	 * @param device
	 * @return 0: {@link Program}, 1: {@link Image}, 2: description
	 */
	public static Object[] findFileProgram(String file, Device device) {
		Object[] res = new Object[3];
		Program prog = null;
		int lastDotIndex = file.lastIndexOf('.');
		if (lastDotIndex != -1) {
			String ex = file.substring(lastDotIndex + 1);
			if (ex.length() > 0)
				prog = Program.findProgram(ex);
		}
		res[0] = prog;
		if (prog != null) {
			res[2] = prog.getName();
			ImageData data = prog.getImageData();
			if (data != null) {
				res[1] = new Image(device, data);
			}
		} else {
			res[2] = "нд╪Ч";
		}
		if (res[1] == null) {
			res[1] = SWTResourceManager.getImage(Util.class,
					"/org/wayne/feiq/ui/image/file.gif");
		}
		return res;
	}

}
