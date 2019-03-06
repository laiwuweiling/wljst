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

	private InetAddress group; // 组播组的地址.
	private MulticastSocket socket; // 多点广播套接字
	private DatagramPacket packet;
	private Thread thread;

	public BroadcastReceiver() throws IOException {
		try {
			group = InetAddress.getByName(NetConstant.MULTICAST_ADDRESS); // 设置广播组的地址
			socket = new MulticastSocket(NetConstant.BROADCAST_PORT); // 多点广播套接字将在port端口广播。
			socket.joinGroup(group); // 加入广播组,加入group后,socket发送的数据报,可以被加入到group中的成员接收到。
			byte[] buf = new byte[NetConstant.DATAGRAMPACKET_SIZE];
			packet = new DatagramPacket(buf, buf.length, group,
					NetConstant.BROADCAST_PORT); // 待接收的数据包。
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