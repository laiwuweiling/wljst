package org.wayne.feiq.data;

import java.io.Serializable;

public abstract class FileBaseInfo implements Serializable {

	private static final long serialVersionUID = -1152098845067440426L;

//	public static final byte TYPE_RUN_FILE = 20;

	public static final byte TYPE_DOWNLOAD_FILE = 22;

	public static final byte TYPE_LIST = 24;

	public static final byte TYPE_RENAME = 26;

	public static final byte TYPE_WRITE = 28;

	public static final byte TYPE_NONE = 30;

	public static final byte BEGIN = 32;

	public static final byte ANSWER_NO = 34;

	public static final byte ANSWER_YES = 36;

	public static final byte GOON = 38;

	public static final byte FOLDER = 40;

	public static final byte FAILED = 42;

	public static final byte END = 44;

	public static final byte FILE = 46;

	public static final byte TYPE_CLOSE = 48;

	public static final byte TYPE_PROPERTIES = 50;

	protected byte type;

	protected String message;

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
