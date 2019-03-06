package org.wayne.feiq.ui;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.wayne.feiq.Application;
import org.wayne.feiq.cfg.Config;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.net.helper.BroadcastHelper;

import swing2swt.layout.BorderLayout;

import com.swtdesigner.SWTResourceManager;

public class MainShell extends Shell implements Listener {

	private static final Log log = LogFactory.getLog(MainShell.class);

	private ToolBar toolBar;
	private ToolItem itemRefresh;
	private ToolItem itmPreferences;
	protected UserList userList;

	public MainShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new BorderLayout(0, 0));
		addListener(SWT.Close, this);
		setText("微领即时通");
		setSize(211, 482);
		setImage(SWTResourceManager.getImage(MainShell.class,
				"/org/wayne/feiq/ui/image/vlog.png"));
		createTray();
		{
			toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
			toolBar.setLayoutData(BorderLayout.NORTH);
			{
				itemRefresh = new ToolItem(toolBar, SWT.NONE);
				itemRefresh.setImage(SWTResourceManager.getImage(
						MainShell.class,
						"/org/wayne/feiq/ui/image/refresh_nav.gif"));
				itemRefresh.addListener(SWT.Selection, this);
				itemRefresh.setText("&Refresh");
			}
			{
				itmPreferences = new ToolItem(toolBar, SWT.NONE);
				itmPreferences.addListener(SWT.Selection, this);
				itmPreferences.setText("&Preferences");
			}
		}
		{
			userList = new UserList(this, SWT.BORDER | SWT.FULL_SELECTION);
		}
	}

	private MenuItem mtmTrayExit;
	private Menu trayMenu;
	private MenuItem mtmTrayOpenShell;
	private Tray tray;
	private TrayItem trayItem;

	private void createTray() {
		trayMenu = new Menu(this);

		mtmTrayOpenShell = new MenuItem(trayMenu, SWT.NONE);
		mtmTrayOpenShell.setImage(SWTResourceManager.getImage(MainShell.class,
				"/org/wayne/feiq/ui/image/home_nav.gif"));
		mtmTrayOpenShell.addListener(SWT.Selection, this);
		mtmTrayOpenShell.setText("打开主窗口(&O)");

		mtmTrayExit = new MenuItem(trayMenu, SWT.NONE);
		mtmTrayExit.setImage(SWTResourceManager.getImage(MainShell.class,
				"/org/wayne/feiq/ui/image/progress_stop.gif"));
		mtmTrayExit.addListener(SWT.Selection, this);
		mtmTrayExit.setText("退出(&X)");

		// 创建系统托盘
		tray = getDisplay().getSystemTray();
		// 如果系统不支持托盘部件
		if (tray == null) {
			return;
		}
		// 创建系统托盘的工作项
		trayItem = new TrayItem(tray, SWT.NONE);
		trayItem.setToolTipText("微领即时通");
		// 设置显示系统托盘项的图标，显示在桌面的右下角
		trayItem.setImage(SWTResourceManager.getImage(MainShell.class,
				"/org/wayne/feiq/ui/image/vlog.png"));
		// 为该系统托盘项注册事件
		trayItem.addListener(SWT.Show, this);
		trayItem.addListener(SWT.Hide, this);
		trayItem.addListener(SWT.Selection, this);
		trayItem.addListener(SWT.DefaultSelection, this);
		trayItem.addListener(SWT.MenuDetect, this);
	}

	private void showShell() {
		setVisible(true);
		setMinimized(false);
		forceActive();
	}

	private void hideShell() {
		setMinimized(true);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
		}
		setVisible(false);
	}

	public void handleEvent(Event e) {
		if (e.widget == mtmTrayExit) {
			doExit();
		} else if (e.widget == trayItem) {
			if (e.type == SWT.Selection) {// 当单击系统托盘时
				// trayMenu.setVisible(true);// 设置菜单为显示状态
			} else if (e.type == SWT.DefaultSelection) {// 当双击系统托盘时
				if (isVisible()) {
					hideShell();
				} else
					showShell();
			} else if (e.type == SWT.MenuDetect) {// 当右击系统托盘时
				trayMenu.setVisible(true);// 设置菜单为显示状态
			}
		} else if (e.widget == mtmTrayOpenShell) {
			showShell();
		} else if (e.widget == this) {
			if (e.type == SWT.Close) {
				hideShell();
				e.doit = false;
			}
		} else if (e.widget == itemRefresh) {
			userList.removeAll();
			try {
				BroadcastHelper.broadcastState(Type.USER_ONLINE);
			} catch (IOException e1) {
				log.error("refresh user list error", e1);
			}
		} else if (e.widget == itmPreferences) {
			for (Thread t : Thread.getAllStackTraces().keySet()) {
				System.out.println(StringUtils.leftPad(t.getName(), 25, ' ')
						+ " : " + t.getState());
			}
		}

	}

	public void doExit() {
		Application.get(Config.class).setMainShellRec(getBounds());
		try {
			if (!Application.getInstance().fireEvent1(
					Application.EVENT_SYSTEM_EXIT)) {
				MessageDialog.openError(this, "error",
						"unknown error, do exit.");
			}
			dispose();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	@Override
	public void dispose() {
		tray.dispose();
		super.dispose();
	}

	protected void checkSubclass() {
	}

}
