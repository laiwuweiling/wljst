package org.wayne.feiq.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.data.User;
import org.wayne.feiq.util.MyByteArrayOutputStream;

public class UDPSender {

	private static final Log log = LogFactory.getLog(UDPSender.class);

	private DatagramSocket socket;
	private MyByteArrayOutputStream bout;

	public UDPSender() {
		try {
			socket = new DatagramSocket();
			bout = new MyByteArrayOutputStream(NetConstant.DATAGRAMPACKET_SIZE);
		} catch (SocketException e) {
			log.error("init UDPSender error", e);
		}
	}

	public synchronized void send(byte type, Object data, User user)
			throws IOException {
		bout.reset();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.write(type);
		out.writeObject(data);
		out.flush();
		out.close();
		DatagramPacket packet = new DatagramPacket(bout.getBuf(), bout.size(),
				user.getAddress(), user.getUdpServerPort());
		log.debug(data);
		socket.send(packet);
	}

	public void dispose() {
		bout = null;
		socket.close();
		socket = null;
		log.debug("udp sender disposed");
	}

}
