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

	private InetAddress group = null; // �鲥��ĵ�ַ.
	private MulticastSocket socket = null; // ���㲥�׽���.

	public Broadcaster() {
		try {
			group = InetAddress.getByName(NetConstant.MULTICAST_ADDRESS); // ���ù㲥��ĵ�ַ
			socket = new MulticastSocket(NetConstant.BROADCAST_PORT); // ���㲥�׽��ֽ���port�˿ڹ㲥��
			socket.setTimeToLive(NetConstant.BROADCAST_TIMETOLIVE); // ���㲥�׽��ַ������ݱ���ΧΪ�������硣
			socket.joinGroup(group); // ����㲥��,����group��,socket���͵����ݱ���
			// ���Ա����뵽group�еĳ�Ա���յ���
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
		socket.send(packet); // �㲥���ݰ���
	}

	public void dispose() {
		group = null;
		socket.close();
		socket = null;
		log.debug("broadcaster disposed");
	}

}
