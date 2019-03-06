package org.wayne.feiq.net;

public abstract class NetConstant {

	private NetConstant() {
	}

	/**
	 * DATAGRAMPACKET_SIZE
	 */
	public static final int DATAGRAMPACKET_SIZE = 8192;

	/**
	 * ���㲥�׽��ֽ���port�˿ڹ㲥
	 */
	public static final int BROADCAST_PORT = 54861;

	/**
	 * ���㲥�׽��ַ������ݱ���ΧΪ�������硣
	 */
	public static final int BROADCAST_TIMETOLIVE = 1;

	/**
	 * �㲥��ĵ�ַ
	 */
	public static final String MULTICAST_ADDRESS = "224.58.69.251";

}
