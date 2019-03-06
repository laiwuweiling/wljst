package org.wayne.feiq.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.Application;
import org.wayne.feiq.data.User;
import org.wayne.feiq.util.ThreadPool;

public class MainService implements NetServer, Runnable {

	private static final Log log = LogFactory.getLog(MainService.class);

	private NetFactory netFactory;
	private ServerSocket server;
	private Thread thread;

	public MainService() throws Exception {
		try {
			netFactory = Application.get(NetFactory.class);
			server = new ServerSocket();
			server.bind(new InetSocketAddress(0));
			((User) Application.get(Application.KEY_SELF_USER))
					.setServerSocketPort(server.getLocalPort());
			thread = new Thread(this, "mainService");
		} catch (Exception e) {
			log.fatal("MainService start failed", e);
			throw e;
		}
	}

	public ServerSocket getServerSocket() {
		return server;
	}

	private boolean running = true;

	public void run() {
		if (log.isDebugEnabled())
			log.debug("main service started at port: " + server.getLocalPort());
		ThreadPool pool = Application.get(ThreadPool.class);
		while (!server.isClosed()) {
			try {
				Socket client = server.accept();
				try {
					Runnable handler = netFactory.getRequestDispatcher()
							.getHandler(client);
					if (handler != null)
						pool.execute(handler);
				} catch (Exception e) {
					log.warn("unknown request type, ignore it", e);
					client.close();
				}
			} catch (SocketException se) {
				break;
			} catch (IOException e) {
				if (running)
					log.error(e.getMessage(), e);
			} catch (Exception ex) {
				break;
			}
		}
		if (log.isDebugEnabled())
			log.debug("main service stoped at port: " + server.getLocalPort());
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		try {
			running = false;
			server.close();
		} catch (IOException e) {
			log.error("stop main service socket error", e);
		}
	}
}
