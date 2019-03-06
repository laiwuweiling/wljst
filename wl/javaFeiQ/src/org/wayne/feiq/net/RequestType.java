package org.wayne.feiq.net;

public abstract class RequestType {

	public static final byte COMMUNICATION_MSG = 20;
	public static final byte COMMUNICATION_IMG = 21;

	public static final byte FILE_SHARE = 30;
	public static final byte FILE_SEND = 31;

	private RequestType() {
	}

}
