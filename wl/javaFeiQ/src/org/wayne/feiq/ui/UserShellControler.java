package org.wayne.feiq.ui;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.wayne.feiq.Application;
import org.wayne.feiq.data.SFile;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.data.User;
import org.wayne.feiq.net.UDPSender;
import org.wayne.feiq.ui.helper.ShellHelper;

public class UserShellControler {

	private static final Log log = LogFactory.getLog(UserShellControler.class);

	private UserShell userShell;

	public UserShellControler(UserShell shell) {
		this.userShell = shell;
	}

	public void doRemoteCommand() {
		ShellHelper.openCommandShell(userShell.user, null);
	}

	public void doFileShare() {
		ShellHelper.openShell(FileShareShell.class, userShell.user, null);
	}

	public void doClose() {
		userShell.dispose();
	}

	public boolean doSendMsg() {
		String msg = userShell.txtInput.getText();
		if (msg.length() == 0) {
			MessageDialog.openWarning(userShell, "空信息", "不能发送空信息");
			return false;
		}
		User self = Application.get(Application.KEY_SELF_USER);
		userShell.appendMessage(self.getUserName(), msg);
		userShell.txtInput.setText("");
		try {
			Application.get(UDPSender.class).send(Type.MESSAGE, msg,
					userShell.getUser());
		} catch (IOException ex) {
			log.error("send message error", ex);
			MessageDialog.openError(userShell, "错误", "发送信息时发生错误");
		}
		return true;
	}

	public void doSendFile(String[] files) {
		try {
			StringBuilder msg = new StringBuilder(200);
			msg.append("发送文件，等待对方接收");
			for (String f : files)
				msg.append("\r\n\t").append(f);
			User self = Application.get(Application.KEY_SELF_USER);
			userShell.appendMessage(self.getUserName(), msg.toString());
			SFile[] sFiles = new SFile[files.length];
			for (int i = 0; i < sFiles.length; ++i) {
				sFiles[i] = new SFile(new File(files[i]));
			}
			Application.get(UDPSender.class).send(Type.FILE_SEND, sFiles,
					userShell.getUser());
		} catch (IOException e) {
			log.error("send file error", e);
			MessageDialog.openError(userShell, "错误", "发送文件时发生错误");
		}
	}

}
