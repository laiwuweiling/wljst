package org.wayne.feiq;

import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.wayne.feiq.cfg.Config;
import org.wayne.feiq.cfg.ConfigLoader;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.data.User;
import org.wayne.feiq.net.BroadcastReceiver;
import org.wayne.feiq.net.Broadcaster;
import org.wayne.feiq.net.MainService;
import org.wayne.feiq.net.NetFactory;
import org.wayne.feiq.net.UDPReceiver;
import org.wayne.feiq.net.UDPSender;
import org.wayne.feiq.net.helper.BroadcastHelper;
import org.wayne.feiq.net.impl.DNetFactory;
import org.wayne.feiq.net.listeners.SystemExitListener;
import org.wayne.feiq.net.listeners.UserListener;
import org.wayne.feiq.ui.ErrorShell;
import org.wayne.feiq.ui.MainShell;
import org.wayne.feiq.util.Listener;
import org.wayne.feiq.util.ThreadPool;

public class Main {

	private static final Log log = LogFactory.getLog(Main.class);

	public static void main(String[] args) {

		try {// the list order of starting servers is fixed, not allowed to be
			// changed
			User self = new User();
			//System.getProperty确定当前的系统属性
			self.setOsUserName(System.getProperty("user.name"));
			self.setAddress(InetAddress.getLocalHost());
			self.setGroupName("马庄社区");
			Application.set(Application.KEY_SELF_USER, self);
			Application.set(Config.class, ConfigLoader.buildConfig());
			Application.set(NetFactory.class, new DNetFactory());
			Application.set(ThreadPool.class, new ThreadPool());
			Application.set(UDPReceiver.class, new UDPReceiver()).start();
			Application.set(UDPSender.class, new UDPSender());
			Application.set(MainService.class, new MainService()).start();
			Application.set(Broadcaster.class, new Broadcaster());
			Application.set(BroadcastReceiver.class, new BroadcastReceiver())
					.start();
			Application.getInstance().on(Application.EVENT_SYSTEM_EXIT,
					new SystemExitListener());
			Listener userListener = Application.getInstance().addListener(
					new UserListener(), Application.EVENT_USER_ON,
					Application.EVENT_USER_OFF);
			Application.set(UserListener.class, userListener);

		} catch (Exception e) {
			log.fatal("init receivers failed", e);
			try {
				new ErrorShell(Display.getDefault()).show();
			} catch (Exception e1) {
			}
			//终止当前运行的java虚拟机
			System.exit(0);
		}

		MainShell shell = null;
		try {
			Display display = Display.getDefault();
			shell = new MainShell(display);
			Config config = Application.get(Config.class);
			if (config.getMainShellRec() != null)
				shell.setBounds(config.getMainShellRec());
			shell.open();
			shell.layout();
			shell.forceActive();
			BroadcastHelper.broadcastState(Type.USER_ONLINE);
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			display.dispose();
		} catch (Exception e) {
			log.fatal("system error", e);
			MessageDialog.openError(shell, "Error", "system error");
			System.exit(0);
		}

	}

}
