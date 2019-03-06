package org.wayne.feiq.net.helper;

import java.io.IOException;

import org.wayne.feiq.Application;
import org.wayne.feiq.data.User;
import org.wayne.feiq.net.Broadcaster;

public class BroadcastHelper {

	private BroadcastHelper() {
	}

	public static Broadcaster broadcastState(byte state) throws IOException {
		Broadcaster broadcaster = Application.get(Broadcaster.class);
		broadcaster.send(state, (User) Application
				.get(Application.KEY_SELF_USER));
		return broadcaster;
	}

}
