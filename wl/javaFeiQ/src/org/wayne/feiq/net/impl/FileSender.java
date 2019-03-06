package org.wayne.feiq.net.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.data.FileBaseInfo;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.data.User;
import org.wayne.feiq.util.Util;

public class FileSender implements Runnable {

	private static final Log log = LogFactory.getLog(FileSender.class);

	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private List<String> files;
	private String clientSaveDir;

	public FileSender(User user, List<String> files, String clientSaveDir)
			throws IOException {
		try {
			this.files = files;
			this.clientSaveDir = clientSaveDir;
			socket = new Socket(user.getAddress(), user.getServerSocketPort());
			socket.getOutputStream().write(Type.FILE_SEND);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			Util.closeQuietly(socket, log);
			log.error("file sender init error", e);
			throw e;
		}
	}

	public void run() {
		if (log.isDebugEnabled())
			log.debug("file sender begin to work, " + socket.getInetAddress()
					+ ", " + files);
		try {
			doSendFile();
			if (log.isDebugEnabled())
				log.debug("file sender finished work, "
						+ socket.getInetAddress() + ", " + files);
		} catch (Exception e) {
			log.error("file sender error, " + socket.getInetAddress() + ", "
					+ files);
		} finally {
			Util.closeQuietly(out, log);
			Util.closeQuietly(in, log);
			Util.closeQuietly(socket, log);
		}
	}

	private void doSendFile() throws IOException {
		out.writeObject(clientSaveDir);// 第一次发送信息
		out.flush();
		if (in.read() == FileBaseInfo.ANSWER_NO)// 第一次接收信息
			return;

		out.writeLong(getTotalLengthFromString(files));// 第二次发送信息
		out.flush();

		for (String sf : files) {
			File f = new File(sf);
			if (!f.exists()) {
				out.write(FileBaseInfo.FAILED);// 第三次发送信息(失败)
				out.flush();
				break;
			}
			writeFileToClient(f, f.getParent());
		}
		out.write(FileBaseInfo.END);
		out.flush();

	}

	private long getTotalLengthFromString(List<String> files) {
		File[] fs = new File[files.size()];
		for (int i = 0; i < fs.length; ++i) {
			fs[i] = new File(files.get(i));
		}
		return getTotalLength(fs);
	}

	private long getTotalLength(File[] files) {
		long total = 0;
		for (File f : files) {
			if (f.isDirectory()) {
				total += getTotalLength(f.listFiles());
			} else {
				total += f.length();
			}
		}
		return total;
	}

	private void writeFileToClient(File f, String base) throws IOException {
		String name = f.getPath().substring(base.length());
		if (!f.canRead()) {
			out.write(FileBaseInfo.FAILED);
			out.writeObject("file \"" + name + "\" can't be read!");
			out.flush();
			return;
		}
		log.debug("send file: " + f);
		if (f.isDirectory()) {
			out.write(FileBaseInfo.GOON);
			out.write(FileBaseInfo.FOLDER);
			out.writeObject(name);
			out.flush();
			for (File ff : f.listFiles()) {
				writeFileToClient(ff, base);
			}
		} else {
			out.write(FileBaseInfo.GOON);
			out.write(FileBaseInfo.FILE);
			out.writeObject(name);
			out.flush();
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(f);
				long fileLength = f.length();
				out.writeLong(fileLength);
				IOUtils.copy(fin, out);
			} catch (Exception e) {
				log.error("copy file to client error," + f, e);
			} finally {
				Util.closeQuietly(fin, log);
			}
		}
	}

}
