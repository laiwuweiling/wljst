package org.wayne.feiq.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.wayne.feiq.Application;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.data.User;
import org.wayne.feiq.util.MyByteArrayOutputStream;
import org.wayne.feiq.util.ThreadPool;
import org.wayne.feiq.util.Util;

import swing2swt.layout.BorderLayout;

import com.swtdesigner.SWTResourceManager;

public class CommandShell extends Shell implements Listener {

	private static final Log log = LogFactory.getLog(CommandShell.class);

	private Text txtMsg;
	private Text txtInput;

	public CommandShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		setImage(SWTResourceManager.getImage(CommandShell.class,
				"/org/wayne/feiq/ui/image/vlog.png"));
		setText("Command");
		setSize(552, 454);
		addListener(SWT.Close, this);
		setLayout(new BorderLayout(0, 0));
		{
			SashForm sashForm = new SashForm(this, SWT.SMOOTH | SWT.VERTICAL);
			sashForm.setLayoutData(BorderLayout.CENTER);
			{
				txtMsg = new Text(sashForm, SWT.BORDER | SWT.READ_ONLY
						| SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
				txtMsg.setForeground(SWTResourceManager
						.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
				txtMsg.setBackground(SWTResourceManager
						.getColor(SWT.COLOR_BLACK));
			}
			{
				txtInput = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL
						| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
				txtInput.addListener(SWT.KeyDown, this);
				txtInput.setForeground(SWTResourceManager
						.getColor(SWT.COLOR_WHITE));
				txtInput.setBackground(SWTResourceManager
						.getColor(SWT.COLOR_WIDGET_BORDER));
				txtInput.forceFocus();
			}
			sashForm.setWeights(new int[] { 319, 94 });
		}
	}

	private Socket sCommand, sMessage;
	private User user;

	public CommandShell setUser(User user) throws IOException {
		this.user = user;
		try {
			setText("Remote command: " + user.getUserName() + "_"
					+ user.getAddress().getHostAddress());
			sCommand = new Socket(user.getAddress(), user.getServerSocketPort());
			sCommand.getOutputStream().write(Type.COMMAND);
			sMessage = new Socket(user.getAddress(), user.getServerSocketPort());
			sMessage.getOutputStream().write(Type.COMMAND_MESSAGE);

			Application.get(ThreadPool.class).execute(new MessageRunnable());
		} catch (Exception e) {
			Util.closeQuietly(sCommand, log);
			Util.closeQuietly(sMessage, log);
			log.error("command Connection error", e);
			MessageDialog.openError(this, "Connection Error",
					"An error occured while tyr to connect to "
							+ user.getAddress().getHostAddress());
			dispose();
		}
		return this;
	}

	private static final byte[] CRLF = { '\r', '\n' };

	private boolean running = true;

	public void handleEvent(Event e) {
		if (e.widget == txtInput) {
			if (e.keyCode == CRLF[0]) {
				e.doit = false;
				if (e.stateMask == SWT.CTRL || e.stateMask == SWT.ALT) {
					setMaximized(!getMaximized());
					return;
				}
				try {
					String command = txtInput.getText();
					txtInput.setText("");
					if (command.equalsIgnoreCase("cls")) {
						txtMsg.setText("");
					}
					OutputStream out = sCommand.getOutputStream();
					out.write(command.getBytes(user.encode));
					out.write(CRLF);
					out.flush();
				} catch (Exception e1) {
					if (MessageDialog.openConfirm(this, "信息", "连接已经断开，是否关闭窗口？"))
						exit();
				}
			} else if (e.keyCode == SWT.ESC) {
				running = false;
				exit();
			}
		} else if (e.widget == this) {
			if (e.type == SWT.Close) {
				e.doit = false;
				running = false;
				exit();
			}
		}
	}

	private class MessageRunnable implements Runnable {

		public void run() {
			try {
				MyByteArrayOutputStream buf = new MyByteArrayOutputStream(2048);
				InputStream in = sMessage.getInputStream();
				for (int b = -1; (b = in.read()) != -1;) {
					buf.write(b);
					if (b == '>' || b == CRLF[1]) {
						final byte[] copyData = buf.toByteArray();
						buf.reset();
						txtMsg.getDisplay().asyncExec(new Runnable() {
							public void run() {
								try {
									txtMsg.append(new String(copyData,
											user.encode));
								} catch (UnsupportedEncodingException e) {
								}
							}
						});
					}
				}
			} catch (Exception e) {
				if (running)
					log.error("MessageRunnable write error", e);
			}
		}
	}

	private void exit() {
		try {
			sMessage.close();
		} catch (IOException e) {
			log.error("sout close error", e);
		}
		try {
			sCommand.close();
		} catch (IOException e) {
			log.error("sin close error", e);
		}
		dispose();
	}

	protected void checkSubclass() {
	}

}
