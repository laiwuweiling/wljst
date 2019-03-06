package org.wayne.feiq.net.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.Application;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.data.User;
import org.wayne.feiq.net.RequestDispatcher;
import org.wayne.feiq.net.listeners.UserListener;
import org.wayne.feiq.ui.FileDownloadShell;
import org.wayne.feiq.util.ThreadPool;

public class DRequestDispatcher implements RequestDispatcher {

	private static final Log log = LogFactory.getLog(DRequestDispatcher.class);

	public Runnable getHandler(final Socket client) throws IOException {
		byte type = (byte) client.getInputStream().read();
		if (type == Type.COMMAND || type == Type.COMMAND_MESSAGE) {
			return findCommandHandler(client, type);
		} else if (type == Type.FILE_SHARE) {
			return new FileShareHandler(client);
		} else if (type == Type.FILE_SEND) {
			return new Runnable() {
				public void run() {
					FileDownloadShell.startNewShell(client);
				}
			};
		}
		return null;
	}

	private Map<String, Socket> tmpSockets = new HashMap<String, Socket>();

	private CommandHandler findCommandHandler(Socket client, byte type) {
		CommandHandler handler = null;
		String keyCommand = client.getInetAddress().getHostAddress() + '_'
				+ Integer.toString(Type.COMMAND);
		String keyComMessage = client.getInetAddress().getHostAddress() + '_'
				+ Integer.toString(Type.COMMAND_MESSAGE);

		// 假设已经存在的key
		String exKey = type == Type.COMMAND ? keyComMessage : keyCommand;
		Socket exSocket = tmpSockets.get(exKey);

		if (exSocket == null) {
			tmpSockets.put(type == Type.COMMAND ? keyCommand : keyComMessage,
					client);
		} else {
			tmpSockets.remove(exKey);
			if (type == Type.COMMAND)
				handler = new CommandHandler(client, exSocket);
			else
				handler = new CommandHandler(exSocket, client);
		}
		return handler;
	}

	@SuppressWarnings("unchecked")
	public void doUdp(byte type, InetAddress address, ObjectInputStream in)
			throws Exception {
		if (type == Type.USER_ONLINE) {
			User user = (User) in.readObject();
			user.setAddress(address);
			Application.getInstance().fireEvent(Application.EVENT_USER_ON,
					true, new Byte(type), user);
		} else if (type == Type.MESSAGE) {
			Object data = in.readObject();
			Application.getInstance().fireEvent(
					Application.EVENT_MESSAGE_RECEIVED, true, new Byte(type),
					address, data);
		} else if (type == Type.FILE_SEND) {
			Application.getInstance().fireEvent(Application.EVENT_FILE_SEND,
					true, new Byte(type), address, in.readObject());
		} else if (type == Type.FILE_DOWNLOAD) {
			try {
				Object[] data = (Object[]) in.readObject();
				Application.getInstance();
				Application.get(ThreadPool.class).execute(
						new FileSender(Application.get(
								UserListener.class).getUser(address),
								(List<String>) data[0], (String) data[1]));
			} catch (Exception e) {
				log.error("error while try to connect");
			}
		}
	}

}
