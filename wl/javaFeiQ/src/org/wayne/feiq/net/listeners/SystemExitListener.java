package org.wayne.feiq.net.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.Application;
import org.wayne.feiq.cfg.Config;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.net.BroadcastReceiver;
import org.wayne.feiq.net.MainService;
import org.wayne.feiq.net.UDPReceiver;
import org.wayne.feiq.net.UDPSender;
import org.wayne.feiq.net.helper.BroadcastHelper;
import org.wayne.feiq.util.Listener;
import org.wayne.feiq.util.ThreadPool;

public class SystemExitListener implements Listener {

	private static final Log log = LogFactory.getLog(SystemExitListener.class);

	public boolean doListener(String eventName, Object... args) {
		try {
			Application.get(Config.class).save();
			Application.get(UDPReceiver.class).stop();
			Application.get(MainService.class).stop();
			Application.get(BroadcastReceiver.class).stop();

			Application.get(UDPSender.class).dispose();
			BroadcastHelper.broadcastState(Type.USER_OFFLINE).dispose();
			Application.get(ThreadPool.class).dispose();
		} catch (Exception e) {
			log.error("system exit error", e);
		}
		return true;
	}
}