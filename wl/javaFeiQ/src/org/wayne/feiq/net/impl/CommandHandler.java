package org.wayne.feiq.net.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.Application;
import org.wayne.feiq.util.MyByteArrayOutputStream;
import org.wayne.feiq.util.ThreadPool;

public class CommandHandler implements Runnable {

	private static final Log log = LogFactory.getLog(CommandHandler.class);

	private Socket sCommand, sMessage;
	private Process cmd = null;
	private boolean running = true;

	public CommandHandler(Socket sCommand, Socket sMessage) {
		this.sCommand = sCommand;
		this.sMessage = sMessage;
	}

	public void run() {
		ThreadPool pool = Application.get(ThreadPool.class);
		try {
			String cmdName = null;
			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				cmdName = "cmd";
			} else if (osName.startsWith("Linux")) {
				cmdName = "gnome-terminal";
			}
			if (cmdName == null)
				throw new UnsupportedOperationException("un know os command");
			cmd = Runtime.getRuntime().exec(cmdName);
			OutputStream messageOut = sMessage.getOutputStream();
			pool.execute(new ExecuteCommandRunnable());
			pool.execute(new OutMessageRunnable(cmd.getErrorStream(),
					messageOut));
			pool.execute(new OutMessageRunnable(cmd.getInputStream(),
					messageOut));
			log.debug("command handler begin to work");
		} catch (Exception e) {
			if (running)
				log.error("command handler failed to work", e);
		}
	}
	
	private class ExecuteCommandRunnable implements Runnable {
		public void run() {
			try {
				MyByteArrayOutputStream buf = new MyByteArrayOutputStream(2048);
				OutputStream cmdout = cmd.getOutputStream();
				InputStream comIn = sCommand.getInputStream();
				for (int b = -1; (b = comIn.read()) != -1;) {
					buf.write(b);
					if (b == '\n') {
						try {
							cmdout.write(buf.getBuf(), 0, buf.size());
							cmdout.flush();
						} catch (Exception e) {
							running = false;
							break;
						}
						buf.reset();
					}
				}
			} catch (Exception e) {
				if (running)
					log.error("ExecuteCommandRunnable error", e);
			} finally {
				try {
					sCommand.close();
					sMessage.close();
					log.debug("socket command and command message are closed");
				} catch (IOException e) {
					log.error("close socket command error", e);
				}
				cmd.destroy();
				log.debug("command destroyed");
			}
		}
	}

	private class OutMessageRunnable implements Runnable {

		private InputStream in;
		private OutputStream out;

		public OutMessageRunnable(InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
		}

		public void run() {
			try {
				for (int b = -1; (b = in.read()) != -1;) {
					out.write(b);
					// if (b == '\n' || b == '>')
					out.flush();
				}
			} catch (Exception e) {
				if (running)
					log.error("OutMessageRunnable error", e);
			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}

	}

}
