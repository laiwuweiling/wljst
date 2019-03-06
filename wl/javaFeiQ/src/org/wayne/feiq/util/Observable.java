/*
 * @(#)Observable.java	1.39 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.wayne.feiq.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Observable {

	private static final Log log = LogFactory.getLog(Observable.class);

	private List<Listener> listeners;

	public Observable() {
		listeners = new ArrayList<Listener>();
	}

	public synchronized boolean addListener(Listener o) {
		if (!listeners.contains(o)) {
			return listeners.add(o);
		}
		return false;
	}

	public synchronized boolean removeListener(Listener o) {
		return listeners.remove(o);
	}

	public boolean notifyListeners(String eventName, Object... args) {
		for (Listener l : listeners) {
			try {
				if (!l.doListener(eventName, args))
					return false;
			} catch (Exception e) {
				log.error("do listener error {eventName:" + eventName
						+ ", listener: " + l + '}', e);
			}
		}
		return true;
	}

	public synchronized void removeAllListener() {
		listeners.removeAll(listeners);
	}

	public synchronized int countListeners() {
		return listeners.size();
	}
}
