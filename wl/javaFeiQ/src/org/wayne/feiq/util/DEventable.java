package org.wayne.feiq.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wayne.feiq.Application;

public class DEventable implements Eventable {

	private Map<String, Observable> events = new HashMap<String, Observable>();

	public DEventable() {
	}

	public DEventable(String... eventNames) {
		addEvent(eventNames);
	}

	public void addEvent(String... eventNames) {
		for (String name : eventNames) {
			Observable e = events.get(name);
			if (e == null) {
				events.put(name, new Observable());
			}
		}
	}

	public List<Observable> removeEvent(String... eventNames) {
		List<Observable> list = new ArrayList<Observable>(eventNames.length);
		for (String name : eventNames) {
			list.add(events.remove(name));
		}
		return list;
	}

	public boolean addListener(String eventName, Listener listener) {
		return ensureEventExist(eventName).addListener(listener);
	}

	public Listener addListener(Listener listener, String... eventNames) {
		for (String en : eventNames)
			addListener(en, listener);
		return listener;
	}

	public boolean removeListener(String eventName, Listener listener) {
		return ensureEventExist(eventName).removeListener(listener);
	}

	public Listener removeListener(Listener listener, String... eventNames) {
		for (String en : eventNames)
			removeListener(en, listener);
		return listener;
	}

	public boolean on(String eventName, Listener listener) {
		return addListener(eventName, listener);
	}

	public boolean un(String eventName, Listener listener) {
		return removeListener(eventName, listener);
	}

	public boolean fireEvent1(String eventName, Object... args) {
		return fireEvent(eventName, false, args);
	}

	public boolean fireEvent(final String eventName, boolean async,
			final Object... args) {
		final Observable observable = ensureEventExist(eventName);
		if (!async)
			return observable.notifyListeners(eventName, args);
		Application.get(ThreadPool.class).execute(new Runnable() {
			public void run() {
				observable.notifyListeners(eventName, args);
			}
		});
		return true;
	}

	private Observable ensureEventExist(String eventName) {
		Observable e = events.get(eventName);
		if (e == null)
			throw new RuntimeException("no such event was defined: '"
					+ eventName + '\'');
		return e;
	}

}
