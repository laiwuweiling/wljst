package org.wayne.feiq.ui.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.wayne.feiq.Application;
import org.wayne.feiq.data.User;
import org.wayne.feiq.ui.CommandShell;
import org.wayne.feiq.ui.UserShell;

public class ShellHelper {

	private static final Log log = LogFactory.getLog(ShellHelper.class);

	private ShellHelper() {
	}

	public static void showDrawMenu(ToolItem item, Menu menu) {
		Rectangle rect = item.getBounds();
		Point pt = new Point(rect.x + rect.width - 15, rect.y + rect.height);
		pt = item.getParent().toDisplay(pt);
		menu.setLocation(pt.x, pt.y);
		menu.setVisible(true);
	}

	public static void openCommandShell(final User user,
			OpenShellTask<CommandShell> task) {
		openShell(CommandShell.class, user, task);
	}

	public static void openUserShell(final User user,
			OpenShellTask<UserShell> task) {
		openShell(UserShell.class, user, task);
	}

	@SuppressWarnings("unchecked")
	public synchronized static void openShell(
			final Class<? extends Shell> clazz, final User user,
			final OpenShellTask task) {
		final ShellKey key = new ShellKey(clazz, user);

		final Shell existShell = Application.get(key);
		if (existShell != null) {
			existShell.getDisplay().syncExec(new Runnable() {
				public void run() {
					if (task != null) {
						task.doTask(existShell);
					}
					existShell.setMinimized(false);
					existShell.forceActive();
				}
			});
			return;
		}

		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				try {
					Shell shell = Application.set(key, clazz.getConstructor(
							Display.class).newInstance(display));
					shell.addDisposeListener(new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							Application.remove(key);
						}
					});
					shell.layout();
					int width = shell.getMonitor().getClientArea().width;
					int height = shell.getMonitor().getClientArea().height;
					int x = shell.getSize().x;
					int y = shell.getSize().y;
					shell.setLocation((width - x) / 2, (height - y) / 2);
					shell.open();
					clazz.getDeclaredMethod("setUser", User.class).invoke(
							shell, user);
					if (task != null) {
						task.doTask(shell);
					}
				} catch (Exception e) {
					log.error("shell error: " + clazz + ", " + user, e);
				} finally {
					log.debug("shell disposed: " + clazz + ", " + user);
				}
			}
		});
	}

	private static class ShellKey {
		Class<? extends Shell> shellClass;
		Object obj;

		public ShellKey(Class<? extends Shell> shellClass, Object obj) {
			this.shellClass = shellClass;
			this.obj = obj;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((obj == null) ? 0 : obj.hashCode());
			result = prime * result
					+ ((shellClass == null) ? 0 : shellClass.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ShellKey other = (ShellKey) obj;
			if (this.obj == null) {
				if (other.obj != null)
					return false;
			} else if (!this.obj.equals(other.obj))
				return false;
			if (shellClass == null) {
				if (other.shellClass != null)
					return false;
			} else if (!shellClass.equals(other.shellClass))
				return false;
			return true;
		}

	}

}
