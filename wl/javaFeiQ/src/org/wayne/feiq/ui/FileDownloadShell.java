package org.wayne.feiq.ui;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.wayne.feiq.Application;
import org.wayne.feiq.data.FileBaseInfo;
import org.wayne.feiq.util.StreamUtil2;
import org.wayne.feiq.util.ThreadPool;
import org.wayne.feiq.util.Util;
import org.wayne.feiq.util.StreamUtil2.Progress;

import com.swtdesigner.SWTResourceManager;

public class FileDownloadShell extends Shell implements Listener, Runnable,
		Progress {

	private static final Log log = LogFactory.getLog(FileDownloadShell.class);

	public static void startNewShell(final Socket socket) {
		try {
			final Display display = Display.getDefault();
			display.syncExec(new Runnable() {
				public void run() {
					FileDownloadShell shell = new FileDownloadShell(display);
					shell.addDisposeListener(new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							Util.closeQuietly(socket, log);
						}
					});
					shell.socket = socket;
					Application.get(ThreadPool.class).execute(shell);
					shell.open();
					shell.layout();
					if (log.isDebugEnabled())
						log.debug("file download shell open,"
								+ socket.getInetAddress());
				}
			});
		} catch (Exception e) {
			log.error("file download shell failed to work,"
					+ socket.getInetAddress(), e);
		}
	}

	private Label lblFile;
	private ProgressBar progressBar;
	private Label lblMsg;

	private FileDownloadShell(Display display) {
		super(display, SWT.CLOSE | SWT.MIN | SWT.TITLE);
		addListener(SWT.Close, this);
		createContents();
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		setImage(SWTResourceManager.getImage(FileDownloadShell.class,
				"/org/wayne/feiq/ui/image/ico.png"));
		setSize(443, 141);
		setText("download file..");

		lblMsg = new Label(this, SWT.NONE);
		lblMsg.setText("Counting file size...");
		lblMsg.setBounds(10, 51, 350, 16);

		progressBar = new ProgressBar(this, SWT.SMOOTH);
		progressBar.setMaximum(100);
		progressBar.setBounds(10, 86, 417, 16);

		lblFile = new Label(this, SWT.NONE);
		lblFile.setBounds(10, 66, 350, 16);
		//
	}

	public void handleEvent(Event e) {
		if (e.widget == this) {
			if (e.type == SWT.Close) {
				e.doit = false;
				if (MessageDialog.openConfirm(this, "Cancel",
						"Do you really want to cancel it?")) {
					running = false;
					lblMsg.setText("Canceling...");
					lblFile.setText("");
					Util.closeQuietly(out, log);
					Util.closeQuietly(in, log);
					Util.closeQuietly(socket, log);
					dispose();
				}
			}
		}
	}

	private long maximum;
	private long currentmum;
	private long unitmum;
	private int currentSelection;

	public void setMaximum(long max) {
		maximum = max;
		unitmum = maximum / 100;
	}

	public void addValue(int value) {
		currentmum += value;
		if (currentmum < maximum) {
			final int selection = (int) (currentmum / unitmum);
			if (currentSelection < selection)
				getDisplay().syncExec(new Runnable() {
					public void run() {
						currentSelection = selection;
						progressBar.setSelection(selection);
					}
				});
		}
	}

	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean running = true;

	public void run() {
		if (log.isDebugEnabled())
			log.debug("file download shell begin to work,"
					+ socket.getInetAddress());
		try {
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			doDownloadFile();
			if (log.isDebugEnabled())
				log.debug("file download shell finished work,"
						+ socket.getInetAddress());
//			Application.get(UDPSender.class).send(Type.MESSAGE,
//					"Download finished", socket.getInetAddress());
		} catch (Exception e) {
			log.error("doDownloadFile error", e);
//			try {
//				Application.get(UDPSender.class).send(Type.MESSAGE,
//						"send file error: " + e.getMessage(),
//						socket.getInetAddress());
//			} catch (IOException e1) {
//				log.error("send file message error", e1);
//			}
		} finally {
			Util.closeQuietly(out, log);
			Util.closeQuietly(in, log);
		}
	}

	private void doDownloadFile() throws IOException {
		try {
			final String base = (String) in.readObject();// 第一次接收信息
			if (base == null) {
				out.write(FileBaseInfo.ANSWER_NO);// 第一次发送信息(取消)
				out.flush();
				return;
			}
			out.write(FileBaseInfo.ANSWER_YES);// 第一次发送信息(继续)
			out.flush();

			final long totalLength = in.readLong();// 第二次接收信息(总长度)
			log.debug("totalLength : " + totalLength);
			getDisplay().syncExec(new Runnable() {
				public void run() {
					setMaximum(totalLength);
					lblMsg.setText("Downloading file...");
				}
			});

			while (in.read() == FileBaseInfo.GOON) {// 连续接收信息
				byte fileOrFolder = (byte) in.read();
				String name = (String) in.readObject();
				final File f = new File(base + File.separatorChar + name);
				log.debug("download file: " + f);

				if (fileOrFolder == FileBaseInfo.FOLDER) {
					f.mkdir();
				} else {
					final long length = in.readLong();
					getDisplay().syncExec(new Runnable() {
						public void run() {
							lblFile.setText("File：\"" + f.getPath()
									+ "\" Size："
									+ Util.byteCountToDisplaySize(length));
						}
					});
					RandomAccessFile raout = null;
					try {
						raout = new RandomAccessFile(f, "rw");
						StreamUtil2.copy(in, raout, length,
								FileDownloadShell.this);
					} catch (Exception e) {
						if (running)
							log.error("establish file error", e);
						f.delete();
						throw e;
					} finally {
						Util.closeQuietly(raout, log);
					}
				}
			}
			getDisplay().syncExec(new Runnable() {
				public void run() {
					lblMsg.setText("Download finished");
					lblFile.setText("");
					openFile(base);
					FileDownloadShell.this.dispose();
				}
			});
		} catch (final Exception e) {
			if (running) {
				log.error("download file error", e);
				getDisplay().syncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(FileDownloadShell.this,
								"Error", e.getMessage());
						FileDownloadShell.this.dispose();
					}
				});
			}
		}
	}

	private void openFile(String fullName) {
		if (!MessageDialog.openConfirm(this, "Information", fullName
				+ " File saved success, do you want to open file?"))
			return;
		Program.launch(fullName);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
