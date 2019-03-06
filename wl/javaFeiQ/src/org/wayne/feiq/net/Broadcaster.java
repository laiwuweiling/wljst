package org.wayne.feiq.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.data.User;
import org.wayne.feiq.util.MyByteArrayOutputStream;

public class Broadcaster {

	private static final Log log = LogFactory.getLog(Broadcaster.class);

	private InetAddress group = null; // 组播组的地址.
	private MulticastSocket socket = null; // 多点广播套接字.

	public Broadcaster() {
		try {
			group = InetAddress.getByName(NetConstant.MULTICAST_ADDRESS); // 设置广播组的地址
			socket = new MulticastSocket(NetConstant.BROADCAST_PORT); // 多点广播套接字将在port端口广播。
			socket.setTimeToLive(NetConstant.BROADCAST_TIMETOLIVE); // 多点广播套接字发送数据报范围为本地网络。
			socket.joinGroup(group); // 加入广播组,加入group后,socket发送的数据报，
			// 可以被加入到group中的成员接收到。
		} catch (Exception e) {
			log.error("init broadCaster failed", e);
		}
	}

	public void send(byte state, User user) throws IOException {
		MyByteArrayOutputStream bout = new MyByteArrayOutputStream(
				NetConstant.DATAGRAMPACKET_SIZE);
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.write(state);
		out.writeObject(user);
		out.flush();
		DatagramPacket packet = new DatagramPacket(bout.getBuf(), bout.size(),
				group, NetConstant.BROADCAST_PORT);
		log.debug(user);
		socket.send(packet); // 广播数据包。
	}

	public void dispose() {
		group = null;
		socket.close();
		socket = null;
		log.debug("broadcaster disposed");
	}

}
