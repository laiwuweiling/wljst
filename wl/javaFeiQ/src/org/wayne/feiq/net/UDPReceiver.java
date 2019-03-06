package org.wayne.feiq.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.Application;
import org.wayne.feiq.data.User;

public class UDPReceiver implements NetServer, Runnable {

	private static final Log log = LogFactory.getLog(UDPReceiver.class);

	private NetFactory netFactory;
	private DatagramSocket socket;
	private DatagramPacket packet;
	private Thread thread;

	public UDPReceiver() throws IOException {
		try {
			netFactory = Application.get(NetFactory.class);
			socket = new DatagramSocket();
			((User) Application.get(Application.KEY_SELF_USER))
					.setUdpServerPort(socket.getLocalPort());
			byte[] buf = new byte[NetConstant.DATAGRAMPACKET_SIZE];
			packet = new DatagramPacket(buf, buf.length);
			thread = new Thread(this, "udpReceiver");
		} catch (SocketException e) {
			log.fatal("init UDPReceiver error", e);
			throw e;
		}
	}

	private boolean running = true;

	public void run() {
		if (log.isDebugEnabled())
			log.debug("upd receiver started at port: " + socket.getLocalPort());
		while (!socket.isClosed()) {
			ObjectInputStream in = null;
			try {
				socket.receive(packet);
				byte[] data = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), packet.getOffset(), data, 0,
						data.length);
				in = new ObjectInputStream(new ByteArrayInputStream(data));
				byte type = (byte) in.read();
				log.debug("udp receive type: " + type);
				netFactory.getRequestDispatcher().doUdp(type,
						packet.getAddress(), in);
			} catch (Exception e) {
				if (running)
					log.error("udp receive data error", e);
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
		if (log.isDebugEnabled())
			log.debug("udp receiver stoped at port: " + socket.getLocalPort());
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		running = false;
		socket.close();
	}
}
