package org.wayne.feiq.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

public interface RequestDispatcher {

	Runnable getHandler(Socket client) throws IOException;

	void doUdp(byte type, InetAddress address, ObjectInputStream in)
			throws Exception;
}
