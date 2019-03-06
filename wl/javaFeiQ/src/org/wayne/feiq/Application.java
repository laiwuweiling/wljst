package org.wayne.feiq;

import java.util.HashMap;
import java.util.Map;

import org.wayne.feiq.util.DEventable;

public class Application extends DEventable {

	public static final String KEY_SELF_USER = "ownUser";
	public static final String EVENT_USER_ON = "useron";
	public static final String EVENT_USER_OFF = "useroff";
	public static final String EVENT_SYSTEM_EXIT = "systemexit";
	public static final String EVENT_MESSAGE_RECEIVED = "newMessageReceived";
	public static final String EVENT_FILE_SEND = "fileSend";

	/**
	 * 
	 */
	private Map<Object, Object> map;

	private Application() {
		super(EVENT_USER_ON, EVENT_USER_OFF, EVENT_SYSTEM_EXIT,
				EVENT_MESSAGE_RECEIVED, EVENT_FILE_SEND);
		map = new HashMap<Object, Object>();
	}

	private static final Application THIS = new Application();

	//放一个小插曲
	public static Application getInstance() {
		return THIS;
		//return null;
	}

	@SuppressWarnings("unchecked")
	public static <E> E get(Object key) {
		return (E) THIS.map.get(key);
	}

	@SuppressWarnings("unchecked")
	public static <E> E get(Class<E> clazz) {
		return (E) THIS.map.get(clazz);
	}

	public static <E> E set(Object key, E v) {
		THIS.map.put(key, v);
		return v;
	}

	@SuppressWarnings("unchecked")
	public static <E> E remove(Object key) {
		return (E) THIS.map.remove(key);
	}

	public static Map<Object, Object> getMap() {
		return THIS.map;
	}

}
