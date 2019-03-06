package org.wayne.feiq.net.listeners;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.wayne.feiq.data.Type;
import org.wayne.feiq.data.User;
import org.wayne.feiq.util.Listener;

public class UserListener implements Listener {

	private Map<InetAddress, User> users = new HashMap<InetAddress, User>();

	public Map<InetAddress, User> getUsers() {
		return users;
	}

	public User getUser(InetAddress address) {
		return users.get(address);
	}

	public boolean doListener(String eventName, Object... args) {
		byte type = (Byte) args[0];
		if (type == Type.USER_ONLINE) {
			User user = (User) args[1];
			users.put(user.getAddress(), user);
		} else if (type == Type.USER_OFFLINE) {
			users.remove(((User) args[1]).getAddress());
		}
		return true;
	}

}
