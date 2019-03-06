package org.wayne.feiq.net.impl;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.Application;
import org.wayne.feiq.data.FileBaseInfo;
import org.wayne.feiq.data.FileProperties;
import org.wayne.feiq.data.FileRequest;
import org.wayne.feiq.data.FileResponse;
import org.wayne.feiq.data.SFile;
import org.wayne.feiq.net.listeners.UserListener;
import org.wayne.feiq.util.ThreadPool;
import org.wayne.feiq.util.Util;

public class FileShareHandler implements Runnable, Comparator<File> {

	private static final Log log = LogFactory.getLog(FileShareHandler.class);

	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean running = true;

	public FileShareHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			log.debug("file share handler begin to work");
			while (!socket.isClosed()) {
				try {
					handleRequest((FileRequest) in.readObject());
				} catch (EOFException eofe) {
					break;
				} catch (SocketException se) {
					break;
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					break;
				}
			}
		} catch (Exception e) {
			if (running)
				log.error("file share handler failed to work", e);
		}
		log.debug("file share handler finished work");
	}

	public void handleRequest(FileRequest request) throws IOException {
		FileResponse response = new FileResponse();
		switch (request.getType()) {
		case FileBaseInfo.TYPE_LIST: {
			doListFile(request, response);
			break;
		}
		case FileBaseInfo.TYPE_PROPERTIES: {
			doFileProperties(request, response);
			break;
		}
		case FileBaseInfo.TYPE_DOWNLOAD_FILE: {
			doSendFile(request, response);
			break;
		}
			// case FileBaseInfo.TYPE_WRITE: {
			// doWriteFile(request, response);
			// break;
			// }
			// case FileBaseInfo.TYPE_RENAME: {
			// doRenameFile(request, response);
			// break;
			// }
			// case FileBaseInfo.TYPE_RUN_FILE: {
			// doRunFile(request, response);
			// break;
			// }
		case FileBaseInfo.TYPE_CLOSE: {
			doClose(request, response);
			break;
		}
		case FileBaseInfo.TYPE_NONE:
		default:
			doNothing(request, response);
			break;
		}
	}

	private void doClose(FileRequest request, FileResponse response) {
		running = false;
		Util.closeQuietly(in, log);
		Util.closeQuietly(out, log);
		Util.closeQuietly(socket, log);
	}

	private void doNothing(FileRequest request, final FileResponse response) {
		response.setType(FileBaseInfo.TYPE_NONE);
	}

	private void doFileProperties(FileRequest request, FileResponse response)
			throws IOException {
		File f = new File(request.getFile());
		response.setType(FileBaseInfo.TYPE_PROPERTIES);
		response.setProperties(new FileProperties(f));
		out.writeObject(response);
		out.flush();
	}

	private void doSendFile(final FileRequest request,
			final FileResponse response) throws IOException {
		List<String> files = request.getFiles();
		response.setType(FileBaseInfo.TYPE_DOWNLOAD_FILE);
		if (files == null || files.size() == 0) {
			response.setMessage("error request, file shouldn't be empty");
			out.writeObject(response);
			out.flush();
		} else {
			response.setMessage("success");
			out.writeObject(response);
			out.flush();
			try {
				Application.get(ThreadPool.class).execute(
						new FileSender(Application.get(UserListener.class)
								.getUser(socket.getInetAddress()), request
								.getFiles(), request.getMessage()));
			} catch (Exception e) {
				log.error("error while try to connect", e);
			}
		}

	}

	private void doListFile(FileRequest request, FileResponse response)
			throws IOException {
		File[] filelist = null;
		String fileName = request.getFile();
		if (fileName == null || fileName.equals("") || fileName.equals("/")
				|| fileName.equals("\\")) {
			filelist = File.listRoots();
			List<SFile> sFiles = new ArrayList<SFile>(filelist.length);
			for (File f : filelist) {
				if (!f.canRead())
					continue;
				SFile sfile = new SFile(f);
				sFiles.add(sfile);
			}
			response.setType(FileResponse.TYPE_LIST);
			response.setFiles(sFiles);
			out.writeObject(response);
			out.flush();
			return;
		} else {
			File file = new File(fileName);
			if (!exists(file, response)) {
				out.writeObject(response);
				out.flush();
				return;
			}
			if (file.isFile()) {
				doSendFile(request, response);
				return;
			}
			filelist = file.listFiles();
		}
		if (filelist == null) {
			response.setType(FileResponse.TYPE_LIST);
			response.setMessage("file or folder is not exist");
			out.writeObject(response);
			out.flush();
			return;
		}
		Arrays.sort(filelist, this);
		List<SFile> sFiles = new ArrayList<SFile>(filelist.length);
		for (File f : filelist) {
			SFile sfile = new SFile(f);
			sFiles.add(sfile);
		}
		response.setType(FileBaseInfo.TYPE_LIST);
		response.setFiles(sFiles);
		out.writeObject(response);
		out.flush();
	}

	// private void doRunFile(FileRequest request, FileResponse response)
	// throws IOException {
	// response.setType(FileResponse.TYPE_RUN_FILE);
	//
	// try {
	// String file = request.getFile();
	// Program p = Program.findProgram(file);
	// if (p == null) {
	// response.setMessage("运行服务器的应用程序 失败, 服务器找不到可运行该文件的应用程序");
	// } else {
	// response.setMessage("运行服务器的应用程序 "
	// + (p.execute(file) ? "成功" : "失败"));
	// }
	// } catch (Exception e) {
	// response.setMessage("运行服务器的应用程序失败 : " + e.getMessage());
	// }
	// out.writeObject(response);
	// out.flush();
	// }

	private boolean exists(File f, FileResponse response) {
		if (!f.exists()) {
			response.setType(FileResponse.TYPE_DOWNLOAD_FILE);
			response.setMessage("文件或文件夹不允许访问或不存在");
			return false;
		}
		return true;
	}

	public int compare(File f1, File f2) {
		boolean isDir1 = f1.isDirectory();
		boolean isDir2 = f2.isDirectory();
		if (isDir1 && isDir2)
			return 0;
		if (isDir1)
			return -1;
		if (isDir2)
			return 1;
		return 0;
	}
}
