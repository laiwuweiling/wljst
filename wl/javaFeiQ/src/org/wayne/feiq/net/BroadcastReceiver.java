package org.wayne.feiq.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.Application;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.data.User;

public class BroadcastReceiver implements Runnable, NetServer {
	private static final long serialVersionUID = 2702644632909267888L;

	private static final Log log = LogFactory.getLog(BroadcastReceiver.class);

	private InetAddress group; // �鲥��ĵ�ַ.
	private MulticastSocket socket; // ���㲥�׽���
	private DatagramPacket packet;
	private Thread thread;

	public BroadcastReceiver() throws IOException {
		try {
			group = InetAddress.getByName(NetConstant.MULTICAST_ADDRESS); // ���ù㲥��ĵ�ַ
			socket = new MulticastSocket(NetConstant.BROADCAST_PORT); // ���㲥�׽��ֽ���port�˿ڹ㲥��
			socket.joinGroup(group); // ����㲥��,����group��,socket���͵����ݱ�,���Ա����뵽group�еĳ�Ա���յ���
			byte[] buf = new byte[NetConstant.DATAGRAMPACKET_SIZE];
			packet = new DatagramPacket(buf, buf.length, group,
					NetConstant.BROADCAST_PORT); // �����յ����ݰ���
			thread = new Thread(this, "broadcastReceiver");
		} catch (SocketException e) {
			log.fatal("init broadcast receiver error", e);
			throw e;
		}
	}

	private boolean running = true;

	public void run() {
		if (log.isDebugEnabled())
			log.debug("broadcast receiver started at port: "
					+ socket.getLocalPort());
		while (!socket.isClosed()) {
			try {
				socket.receive(packet);
				ObjectInputStream in = new ObjectInputStream(
						new ByteArrayInputStream(packet.getData(), packet
								.getOffset(), packet.getLength()));
				byte type = (byte) in.read();
				User user = (User) in.readObject();
				user.setAddress(packet.getAddress());
				Application.get(UDPSender.class).send(Type.USER_ONLINE,
						Application.get(Application.KEY_SELF_USER), user);
				Application.getInstance().fireEvent(
						type == Type.USER_ONLINE ? Application.EVENT_USER_ON
								: Application.EVENT_USER_OFF, true,
						new Byte(type), user);
			} catch (Exception e) {
				if (running)
					log.error("receive packet error", e);
			}
		}
		if (log.isDebugEnabled())
			log.debug("broadcast receiver stoped at port: "
					+ socket.getLocalPort());
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		running = false;
		socket.close();
	}

}