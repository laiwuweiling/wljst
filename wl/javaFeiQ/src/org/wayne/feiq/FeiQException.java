package org.wayne.feiq;

public class FeiQException extends RuntimeException {
	private static final long serialVersionUID = -7297407062282652098L;

	protected int errorCode;

	public FeiQException() {
		super();
	}

	public FeiQException(String message, Throwable cause) {
		super(message, cause);
	}

	public FeiQException(String message) {
		super(message);
	}

	public FeiQException(Throwable cause) {
		super(cause);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public FeiQException setErrorCode(int errorCode) {
		this.errorCode = errorCode;
		return this;
	}

}
