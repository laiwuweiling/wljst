package org.wayne.feiq.ui;

import java.net.InetAddress;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.wayne.feiq.data.User;

import com.swtdesigner.SWTResourceManager;

public class UserItem extends TableItem {

	public UserItem(Table parent, int style) {
		super(parent, style);
	}

	protected User user;

	public UserItem setUser(User user) {
		this.user = user;
		InetAddress address = this.user.getAddress();
		setImage(SWTResourceManager.getImage(UserItem.class,
				"/org/wayne/feiq/ui/image/userlog.png"));
		setText(user.getUserName() + '(' + address.getHostName() + ')'
				+ address.getHostAddress());
		return this;
	}

	public void setChecked(boolean b) {
		if (b) {
			setImage(SWTResourceManager.getImage(UserItem.class,
					"/org/wayne/feiq/ui/image/userBig.png")		
					);
		} else {
			setImage(SWTResourceManager.getImage(UserItem.class,
					"/org/wayne/feiq/ui/image/userlog.png"));
		}
		super.setChecked(b);
	}

	public boolean isSameAs(User user) {
		return user.equals(this.user);
	}

	public boolean isSameAs(InetAddress address) {
		return address.equals(user.getAddress());
	}

	protected void checkSubclass() {
	}

}
