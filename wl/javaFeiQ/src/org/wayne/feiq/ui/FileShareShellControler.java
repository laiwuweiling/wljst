package org.wayne.feiq.ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.TableItem;
import org.wayne.feiq.Application;
import org.wayne.feiq.cfg.Config;
import org.wayne.feiq.data.FileBaseInfo;
import org.wayne.feiq.data.FileProperties;
import org.wayne.feiq.data.FileRequest;
import org.wayne.feiq.data.FileResponse;
import org.wayne.feiq.data.SFile;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.util.ThreadPool;
import org.wayne.feiq.util.Util;

import com.swtdesigner.SWTResourceManager;

public class FileShareShellControler {

	private static final Log log = LogFactory
			.getLog(FileShareShellControler.class);

	private FileShareShell shell;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	FileShareShellControler(FileShareShell shell) {
		this.shell = shell;
	}

	FileShareShellControler doConnect() throws IOException {
		try {
			socket = new Socket(shell.user.getAddress(), shell.user
					.getServerSocketPort());
			socket.getOutputStream().write(Type.FILE_SHARE);
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			Util.closeQuietly(socket, log);
			throw e;
		}
		return this;
	}

	void doClose() {
		try {
			FileRequest request = new FileRequest();
			request.setType(FileBaseInfo.TYPE_CLOSE);
			out.writeObject(request);
			out.flush();
			Util.closeQuietly(in, log);
			Util.closeQuietly(out, log);
			Util.closeQuietly(socket, log);
		} catch (Exception e) {
			log.error("close error", e);
		} finally {
			shell.dispose();
		}
	}

	private String currentDir = null;

	public String getCurrentDir() {
		return currentDir;
	}

	void setCurrentDir(String path) {
		if (path.length() < 2) {
			currentDir = "";
			return;
		}
		if (path.charAt(path.length() - 1) == shell.user.separatorChar) {
			currentDir = path.substring(0, path.length() - 1);
		} else {
			currentDir = path;
		}
	}

	void doListRequest(String path) {
		FileRequest request = new FileRequest();
		request.setType(FileBaseInfo.TYPE_LIST);
		request.setFile(path);

		doRequest(request);
	}

	void doFileDownload(String file) {
		List<String> files = new ArrayList<String>(1);
		files.add(file);
		doFileDownload(files);
	}

	void doFileDownload(List<String> files) {
		Config config = Application.get(Config.class);
		DirectoryDialog dlg = new DirectoryDialog(shell, SWT.SAVE);
		dlg.setText("保存到目录..");
		dlg.setMessage("将要下载的文件或文件夹保存到指定的目录");
		String filterPath = config.getDefaultSavePath();
		if (filterPath != null)
			dlg.setFilterPath(filterPath);
		String base = dlg.open();
		if (base != null) {
			config.setDefaultSavePath(base);
			FileRequest req = new FileRequest();
			req.setType(FileBaseInfo.TYPE_DOWNLOAD_FILE);
			req.setMessage(base);
			req.setFiles(files);
			doRequest(req);
		}
	}

	void doProperties(String fileName) {
		FileRequest req = new FileRequest();
		if (StringUtils.isEmpty(fileName)) {
			req.setFile(currentDir);
		} else {
			req.setFile(currentDir + shell.user.separatorChar + fileName);
		}
		req.setType(FileBaseInfo.TYPE_PROPERTIES);

		doRequest(req);
	}

	public void doRequest(FileRequest req) {
		try {
			out.writeObject(req);
			FileResponse response = (FileResponse) in.readObject();
			handleResponse(response);
		} catch (Exception e) {
			log.error("do request error", e);
			MessageDialog.openError(shell, "错误", e.getMessage());
		}
	}

	/**
	 * 处理服务器的返回结果
	 * 
	 * @param response
	 */
	private void handleResponse(final FileResponse response) {
		switch (response.getType()) {
		case FileBaseInfo.TYPE_LIST: {
			shell.handleListFiles(response);
			break;
		}
		case FileBaseInfo.TYPE_PROPERTIES: {
			handleFileProperties(response);
			break;
		}
		case FileBaseInfo.TYPE_DOWNLOAD_FILE: {
			if (!"success".equals(response.getMessage())) {
				MessageDialog.openInformation(shell, "文件共享信息", response
						.getMessage());
			}
			break;
		}
			// case FileBaseInfo.TYPE_RENAME: {
			// handleRenameFile(path, response);
			// break;
			// }
			// case FileBaseInfo.TYPE_WRITE: {
			// handleWriteFile(path, response);
			// break;
			// }
			// case FileBaseInfo.TYPE_RUN_FILE: {
			// MessageDialog.openInformation(shell, "消息",
			// response.getMessage());
			// break;
			// }
		case FileBaseInfo.TYPE_NONE:
		default:
			MessageDialog.openInformation(shell, "消息",
					response.getMessage() == null ? "对方无响应" : response
							.getMessage());
		}
	}

	private void handleFileProperties(FileResponse response) {
		FileProperties properties = response.getProperties();
		Application.get(ThreadPool.class)
				.execute(
						FilePropertiesShell.getNewShellRunnable(shell.user,
								properties));
	}

	public void setProgramImageAndName(SFile sfile, TableItem item) {
		Image image = null;
		String descripiton = null;
		if (sfile.isDirectory()) {
			image = SWTResourceManager.getImage(shell.getClass(),
					"/org/wayne/feiq/ui/image/folder.gif");
			descripiton = "文件夹";
		} else {
			Object[] o = Util.findFileProgram(sfile.getPath(), shell
					.getDisplay());
			image = (Image) o[1];
			descripiton = (String) o[2];
		}
		item.setImage(image);
		String size = "";
		if (sfile.isFile()) {
			size = Util.byteCountToDisplaySize(sfile.length());
		}
		item.setText(new String[] { sfile.getName(), descripiton, size,
				format.format(new Date(sfile.lastModified())) });

	}

	private static final SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

}
