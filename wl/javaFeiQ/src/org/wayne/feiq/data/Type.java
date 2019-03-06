package org.wayne.feiq.data;

public abstract class Type {

	public static final byte USER_ONLINE = 11;
	public static final byte USER_OFFLINE = 12;
	public static final byte MESSAGE = 20;
	public static final byte COMMAND = 30;
	public static final byte COMMAND_MESSAGE = 31;
	public static final byte FILE_SHARE = 40;
	public static final byte FILE_DOWNLOAD = 42;
	public static final byte FILE_SEND = 43;

	private Type() {
	}

}
