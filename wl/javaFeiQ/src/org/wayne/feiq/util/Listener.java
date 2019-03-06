package org.wayne.feiq.util;

public interface Listener {

	/**
	 * do listener
	 * 
	 * @param eventName
	 * @param args
	 * @return true to go on next listener, false to cancel the after listeners
	 */
	public boolean doListener(String eventName, Object... args);

}
