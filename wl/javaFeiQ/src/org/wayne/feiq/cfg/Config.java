package org.wayne.feiq.cfg;

import org.eclipse.swt.graphics.Rectangle;

public abstract class Config {

	public static final byte SHORTCUT_CTRL_ENTER = 11;
	public static final byte SHORTCUT_ENTER = 13;

	protected byte sendMsgShortcut = SHORTCUT_ENTER;
	protected String defaultSavePath = "";
	protected String msgSound = "resource/msg.wav";
	protected Rectangle mainShellRec;

	protected Config() {
	}

	public byte getSendMsgShortcut() {
		return sendMsgShortcut;
	}

	public void setSendMsgShortcut(byte sendMsgShortcut) {
		this.sendMsgShortcut = sendMsgShortcut;
	}

	public String getDefaultSavePath() {
		return defaultSavePath;
	}

	public void setDefaultSavePath(String defaultSavePath) {
		this.defaultSavePath = defaultSavePath;
	}

	public String getMsgSound() {
		return msgSound;
	}

	public void setMsgSound(String msgSound) {
		this.msgSound = msgSound;
	}

	public Rectangle getMainShellRec() {
		return mainShellRec;
	}

	public void setMainShellRec(Rectangle mainShellRec) {
		this.mainShellRec = mainShellRec;
	}

	public abstract void save();

}
