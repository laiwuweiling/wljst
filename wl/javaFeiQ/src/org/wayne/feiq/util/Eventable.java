package org.wayne.feiq.util;

import java.util.List;

/**
 * eventable
 * 
 * @author Wayne.Wu
 * 
 */
public interface Eventable {

	void addEvent(String... eventNames);

	List<Observable> removeEvent(String... eventNames);

	/**
	 * add listener
	 * 
	 * @param eventName
	 * @param listener
	 * @return
	 * @see #on(String, Listener)
	 */
	boolean addListener(String eventName, Listener listener);

	Listener addListener(Listener listener, String... eventNames);

	/**
	 * remove listener
	 * 
	 * @param eventName
	 * @param listener
	 * @return
	 * @see #un(String, Listener)
	 */
	boolean removeListener(String eventName, Listener listener);

	Listener removeListener(Listener listener, String... eventNames);

	/**
	 * shortcut for {@link #addListener(String, Listener)}
	 * 
	 * @param eventName
	 * @param listener
	 * @return
	 * @see #un(String, Listener)
	 */
	boolean on(String eventName, Listener listener);

	/**
	 * shortcut for {@link #removeListener(String, Listener)}
	 * 
	 * @param eventName
	 * @param listener
	 * @return
	 * @see #on(String, Listener)
	 */
	boolean un(String eventName, Listener listener);

	/**
	 * fire event, synchro
	 * 
	 * @param eventName
	 * @param args
	 * @return
	 */
	boolean fireEvent1(String eventName, Object... args);

	/**
	 * fire event
	 * 
	 * @param eventName
	 * @param async
	 *            is async
	 * @param args
	 * @return
	 */
	boolean fireEvent(String eventName, boolean async, Object... args);

}