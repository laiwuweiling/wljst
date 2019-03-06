package org.wayne.feiq.ui;

import java.applet.Applet;
import java.io.File;
import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.wayne.feiq.Application;
import org.wayne.feiq.cfg.Config;
import org.wayne.feiq.data.SFile;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.data.User;
import org.wayne.feiq.ui.helper.OpenShellTask;
import org.wayne.feiq.ui.helper.ShellHelper;
import org.wayne.feiq.util.Listener;

import com.swtdesigner.SWTResourceManager;

public class UserList extends Table implements Listener,
		org.eclipse.swt.widgets.Listener {

	private static final Log log = LogFactory.getLog(UserList.class);
	private MenuItem mtmFileShare;
	private MenuItem mtmCommand;
	private MenuItem mtmTalk;

	public UserList(Composite parent, int style) {
		super(parent, style);
		setHeaderVisible(false);
		setLinesVisible(false);
		addListener(SWT.Selection, this);
		addListener(SWT.MouseDoubleClick, this);
		addListener(SWT.KeyDown, this);
		addListener(SWT.MenuDetect, this);

		{
			Menu menu = new Menu(this);
			setMenu(menu);

			mtmTalk = new MenuItem(menu, SWT.NONE);
			mtmTalk.addListener(SWT.Selection, this);
			mtmTalk.setImage(SWTResourceManager.getImage(UserList.class,
					"/org/wayne/feiq/ui/image/photo.small.png"));
			mtmTalk.setText("\u8FDC\u7A0B\u804A\u5929(&T)");

			mtmFileShare = new MenuItem(menu, SWT.NONE);
			mtmFileShare.setImage(SWTResourceManager.getImage(UserList.class,
					"/org/wayne/feiq/ui/image/fldr_obj.gif"));
			mtmFileShare.addListener(SWT.Selection, this);
			mtmFileShare.setText("\u6587\u4EF6\u5171\u4EAB(&S)");

			new MenuItem(menu, SWT.SEPARATOR);

			mtmCommand = new MenuItem(menu, SWT.NONE);
			mtmCommand.setImage(SWTResourceManager.getImage(UserList.class,
					"/org/wayne/feiq/ui/image/defaultview_misc.gif"));
			mtmCommand.addListener(SWT.Selection, this);
			mtmCommand.setText("\u8FDC\u7A0B\u547D\u4EE4(&C)");

		}

		Application.getInstance().addListener(this, Application.EVENT_USER_ON,
				Application.EVENT_USER_OFF, Application.EVENT_MESSAGE_RECEIVED,
				Application.EVENT_FILE_SEND);

	}

	public boolean doListener(final String eventName, final Object... args) {
		if (log.isDebugEnabled())
			log.debug("user list listener: " + eventName);
		getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					byte type = (Byte) args[0];
					if (type == Type.USER_ONLINE) {
						User user = (User) args[1];
						if (findUserItem(user.getAddress()) == null)
							addItem(user);
					} else if (type == Type.USER_OFFLINE) {
						Object[] o = findUserItem(((User) args[1]).getAddress());
						if (o != null)
							remove((Integer) o[0]);
					} else if (type == Type.MESSAGE || type == Type.FILE_SEND) {
						Object[] o = findUserItem((InetAddress) args[1]);
						UserItem item = (o == null) ? addNewItem(args)
								: (UserItem) o[1];
						setSelection(item);
						doShowUser(type, args[2]);
						Applet.newAudioClip(
								new File(Application.get(Config.class)
										.getMsgSound()).toURI().toURL()).play();
					}
				} catch (Exception e) {
					log.error("user list dolistener error", e);
				}
			}

			private UserItem addNewItem(final Object... args) {
				UserItem item;
				User user = new User();
				user.setAddress((InetAddress) args[1]);
				user.setOsUserName(user.getAddress().getHostName());
				user.setUserName(user.getOsUserName());
				item = addItem(user);
				return item;
			}

		});

		return true;
	}

	private UserItem addItem(User user) {
		return new UserItem(UserList.this, SWT.NONE).setUser(user);
	}

	private Object[] findUserItem(InetAddress address) {
		for (int i = 0, j = getItemCount(); i < j; ++i) {
			UserItem item = (UserItem) getItem(i);
			if (item.isSameAs(address)) {
				return new Object[] { i, item };
			}
		}
		return null;
	}

	public void handleEvent(Event e) {
		if (e.type == SWT.Selection) {
			widgetSelected(e);
		} else if (e.type == SWT.MouseDoubleClick) {
			if (getSelectionCount() > 0)
				doShowUser();
		} else if (e.type == SWT.KeyDown) {
			if (e.character == '\r') {
				doShowUser();
			}
		} else if (e.type == SWT.MenuDetect) {
			if (getSelectionCount() == 0)
				e.doit = false;
		}
	}

	public void widgetSelected(Event e) {
		UserItem sitem = (UserItem) getSelection()[0];
		if (e.widget == this) {
			for (int i = 0, j = getItemCount(); i < j; ++i) {
				UserItem item = (UserItem) getItem(i);
				if (item != sitem)
					item.setChecked(false);
			}
			sitem.setChecked(true);
		} else if (e.widget == mtmTalk) {
			doShowUser();
		} else if (e.widget == mtmFileShare) {
			ShellHelper.openShell(FileShareShell.class, sitem.user, null);
		} else if (e.widget == mtmCommand) {
			ShellHelper.openShell(CommandShell.class, sitem.user, null);
		}
	}

	private void doShowUser() {
		doShowUser((byte) 0, null);
	}

	private void doShowUser(final byte type, final Object data) {
		if (getSelectionCount() == 0)
			return;
		final UserItem item = (UserItem) getSelection()[0];
		ShellHelper.openUserShell(item.user, type == 0 ? null
				: new OpenShellTask<UserShell>() {
					public void doTask(UserShell shell) {
						if (type == Type.MESSAGE) {
							shell.appendMessage((String) data);
						} else if (type == Type.FILE_SEND) {
							shell.addReceiveFile((SFile[]) data);
						}
					}
				});
	}

	public void dispose() {
		super.dispose();
		Application.getInstance().un(Application.EVENT_USER_ON, this);
		Application.getInstance().un(Application.EVENT_USER_OFF, this);
		Application.getInstance().un(Application.EVENT_MESSAGE_RECEIVED, this);
		Application.getInstance().un(Application.EVENT_FILE_SEND, this);
	}

	protected void checkSubclass() {
	}
}
