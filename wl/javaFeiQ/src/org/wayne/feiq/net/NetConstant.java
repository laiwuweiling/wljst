package org.wayne.feiq.net;

public abstract class NetConstant {

	private NetConstant() {
	}

	/**
	 * DATAGRAMPACKET_SIZE
	 */
	public static final int DATAGRAMPACKET_SIZE = 8192;

	/**
	 * 多点广播套接字将在port端口广播
	 */
	public static final int BROADCAST_PORT = 54861;

	/**
	 * 多点广播套接字发送数据报范围为本地网络。
	 */
	public static final int BROADCAST_TIMETOLIVE = 1;

	/**
	 * 广播组的地址
	 */
	public static final String MULTICAST_ADDRESS = "224.58.69.251";

}
