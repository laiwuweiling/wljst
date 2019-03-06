package org.wayne.feiq.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.wayne.feiq.data.FileResponse;
import org.wayne.feiq.data.SFile;
import org.wayne.feiq.data.User;

import swing2swt.layout.BorderLayout;

import com.swtdesigner.SWTResourceManager;

public class FileShareShell extends Shell implements Listener {

	private static final Log log = LogFactory.getLog(FileShareShell.class);

	private MenuItem tblmtmRefresh;
	private Menu mnuTableSpace;
	private MenuItem tblmtmSelectAll;
	private MenuItem tblmtmBack;
	private Menu mnuTableSelect;
	private MenuItem tblmtmProp;
	private MenuItem tblmtmOpen;
	private MenuItem tblmtmSave;
	private MenuItem tblmtmPropCur;

	private Label lblStatus;
	private Table table;
	private Text txtAddress;
	private Composite composite;
	private ToolBar toolBar;
	private ToolItem titmGoUp;
	private ToolItem titmHome;
	private ToolBar toolBar_1;
	private ToolItem itemGoto;

	/**
	 * Create the composite
	 * 
	 *
	 **/
	public FileShareShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		setSize(new Point(760, 500));
		addListener(SWT.Close, this);
		setSize(680, 499);
		setImage(SWTResourceManager.getImage(FileShareShell.class,
				"/org/wayne/feiq/ui/image/vlog.jpg"));
		setLayout(new BorderLayout(0, 0));

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLayoutData(BorderLayout.CENTER);
		table.addListener(SWT.MouseDoubleClick, this);
		table.addListener(SWT.MenuDetect, this);
		table.addListener(SWT.KeyDown, this);

		{
			mnuTableSelect = new Menu(table);
			table.setMenu(mnuTableSelect);

			tblmtmOpen = new MenuItem(mnuTableSelect, SWT.NONE);
			tblmtmOpen.setImage(SWTResourceManager.getImage(
					FileShareShell.class,
					"/org/wayne/feiq/ui/image/fldr_obj.gif"));
			tblmtmOpen.addListener(SWT.Selection, this);
			tblmtmOpen.setText("打开(&O)");

			tblmtmSave = new MenuItem(mnuTableSelect, SWT.NONE);
			tblmtmSave.setImage(SWTResourceManager.getImage(
					FileShareShell.class,
					"/org/wayne/feiq/ui/image/save_edit.gif"));
			tblmtmSave.addListener(SWT.Selection, this);
			tblmtmSave.setText("保存(&S)");

			new MenuItem(mnuTableSelect, SWT.SEPARATOR);

			tblmtmProp = new MenuItem(mnuTableSelect, SWT.NONE);
			tblmtmProp.setImage(SWTResourceManager.getImage(
					FileShareShell.class,
					"/org/wayne/feiq/ui/image/prop_ps.gif"));
			tblmtmProp.addListener(SWT.Selection, this);
			tblmtmProp.setText("\u5C5E\u6027(&R)");

		}
		{
			mnuTableSpace = new Menu(table);

			tblmtmBack = new MenuItem(mnuTableSpace, SWT.NONE);
			tblmtmBack.setImage(SWTResourceManager
					.getImage(FileShareShell.class,
							"/org/wayne/feiq/ui/image/up_nav.gif"));
			tblmtmBack.addListener(SWT.Selection, this);
			tblmtmBack.setText("\u5411\u4E0A(&B)");

			new MenuItem(mnuTableSpace, SWT.SEPARATOR);

			tblmtmRefresh = new MenuItem(mnuTableSpace, SWT.NONE);
			tblmtmRefresh.setImage(SWTResourceManager.getImage(
					FileShareShell.class,
					"/org/wayne/feiq/ui/image/refresh_nav.gif"));
			tblmtmRefresh.addListener(SWT.Selection, this);
			tblmtmRefresh.setText("刷锟斤拷(&E)");

			tblmtmSelectAll = new MenuItem(mnuTableSpace, SWT.NONE);
			tblmtmSelectAll.setImage(SWTResourceManager.getImage(
					FileShareShell.class,
					"/org/wayne/feiq/ui/image/tasks_tsk.gif"));
			tblmtmSelectAll.addListener(SWT.Selection, this);
			tblmtmSelectAll.setText("全选(&A)");

			new MenuItem(mnuTableSpace, SWT.SEPARATOR);

			tblmtmPropCur = new MenuItem(mnuTableSpace, SWT.NONE);
			tblmtmPropCur.setImage(SWTResourceManager.getImage(
					FileShareShell.class,
					"/org/wayne/feiq/ui/image/prop_ps.gif"));
			tblmtmPropCur.addListener(SWT.Selection, this);
			tblmtmPropCur.setText("\u5C5E\u6027(&R)");
		}

		{
			final TableColumn newColumnTableColumn = new TableColumn(table,
					SWT.NONE);
			newColumnTableColumn.setWidth(180);
			newColumnTableColumn.setText("文件名");

			final TableColumn newColumnTableColumn_1 = new TableColumn(table,
					SWT.NONE);
			newColumnTableColumn_1.setWidth(140);
			newColumnTableColumn_1.setText("文件类型");

			final TableColumn newColumnTableColumn_2 = new TableColumn(table,
					SWT.NONE);
			newColumnTableColumn_2.setWidth(90);
			newColumnTableColumn_2.setText("文件大小");

			final TableColumn newColumnTableColumn_3 = new TableColumn(table,
					SWT.NONE);
			newColumnTableColumn_3.setWidth(145);
			newColumnTableColumn_3.setText("最后修改时间");
		}

		lblStatus = new Label(this, SWT.NONE);
		lblStatus.setText("\u72B6\u6001\uFF1A");
		lblStatus.setLayoutData(BorderLayout.SOUTH);

		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FormLayout());
		composite.setLayoutData(BorderLayout.NORTH);

		toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		FormData formData = null;
		{
			formData = new FormData();
			formData.top = new FormAttachment(0);
			formData.width = 94;
			formData.left = new FormAttachment(0, 10);
			toolBar.setLayoutData(formData);
		}

		titmHome = new ToolItem(toolBar, SWT.NONE);
		titmHome.setImage(SWTResourceManager.getImage(FileShareShell.class,
				"/org/wayne/feiq/ui/image/home_nav.gif"));
		titmHome.addListener(SWT.Selection, this);
		titmHome.setText("\u5BB6(&H)");

		titmGoUp = new ToolItem(toolBar, SWT.NONE);
		titmGoUp.setImage(SWTResourceManager.getImage(FileShareShell.class,
				"/org/wayne/feiq/ui/image/up_nav.gif"));
		titmGoUp.addListener(SWT.Selection, this);
		titmGoUp.setText("\u5411\u4E0A(&B)");
		FormData formData_2 = null;
		{
			txtAddress = new Text(composite, SWT.BORDER);
			formData.right = new FormAttachment(txtAddress, -6);
			txtAddress.addListener(SWT.KeyDown, this);
			{
				formData_2 = new FormData();
				formData_2.left = new FormAttachment(0, 176);
				formData_2.top = new FormAttachment(0, 2);
				formData_2.width = 289;
				txtAddress.setLayoutData(formData_2);
			}
		}

		toolBar_1 = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		formData_2.right = new FormAttachment(toolBar_1, -6);
		{
			FormData formData_1 = new FormData();
			formData_1.right = new FormAttachment(100, -21);
			formData_1.top = new FormAttachment(0, 1);
			formData_1.width = 88;
			toolBar_1.setLayoutData(formData_1);
		}

		itemGoto = new ToolItem(toolBar_1, SWT.NONE);
		itemGoto.setImage(SWTResourceManager.getImage(FileShareShell.class,
				"/org/wayne/feiq/ui/image/forward_nav.gif"));
		itemGoto.addListener(SWT.Selection, this);
		itemGoto.setText("转锟斤拷(&G)");

	}

	protected User user;
	private FileShareShellControler controler;

	public void setUser(User user) {
		this.user = user;
		try {
			setText("Remote File Share: " + user.getUserName() + "_"
					+ user.getAddress().getHostAddress());
			controler = new FileShareShellControler(this);
			controler.doConnect();
			txtAddress.setText("");
			controler.doListRequest("");
		} catch (Exception e) {
			log.error("File share Connection error", e);
			MessageDialog.openError(this, "Connection Error",
					"An error occured while tyr to connect to "
							+ user.getAddress().getHostAddress());
			dispose();
		}
	}

	public void handleEvent(Event e) {
		if (e.widget == table) {
			handlTableEvent(e);
		} else if (e.widget == titmGoUp) {
			doGoBack();
		} else if (e.widget == titmHome) {
			txtAddress.setText("");
			controler.doListRequest("");
		} else if (e.widget == itemGoto) {
			controler.doListRequest(txtAddress.getText());
		} else if (e.widget == tblmtmOpen) {
			doGoInto();
		} else if (e.widget == tblmtmBack) {
			doGoBack();
			table.forceFocus();
		} else if (e.widget == tblmtmRefresh) {
			txtAddress.setText(controler.getCurrentDir());
			controler.doListRequest(txtAddress.getText());
		} else if (e.widget == tblmtmSave) {
			doSave();
		} else if (e.widget == tblmtmProp) {
			controler.doProperties(table.getSelection()[0].getText());
		} else if (e.widget == tblmtmPropCur) {
			controler.doProperties(null);
		} else if (e.widget == tblmtmSelectAll) {
			table.selectAll();
		} else if (e.widget == this) {
			if (e.type == SWT.Close) {
				e.doit = false;
				controler.doClose();
			}
		} else if (e.widget == txtAddress) {
			if (e.keyCode == '\r') {
				controler.doListRequest(txtAddress.getText());
				e.doit = false;
			}
		}
	}

	private void handlTableEvent(Event e) {
		if (e.type == SWT.MenuDetect) {
			Rectangle area = new Rectangle(0, table.getHeaderHeight(), 0, 0);
			area.height = table.getItemHeight() * table.getItemCount();
			for (int j = table.getColumnCount() - 1; j > -1; --j) {
				area.width += table.getColumn(j).getWidth();
			}
			if (!area.contains(e.x, e.y) && table.getSelectionCount() == 0) {
				e.doit = false;
				mnuTableSpace.setVisible(true);
			}
		} else if (e.type == SWT.MouseDoubleClick) {
			doGoInto();
		} else if (e.type == SWT.KeyDown) {
			if (e.keyCode == '\b') {
				doGoBack();
				table.forceFocus();
			} else if (e.keyCode == '\r') {
				doGoInto();
				table.forceFocus();
			} else if (e.keyCode == SWT.F6) {
				txtAddress.setSelection(txtAddress.getCharCount());
				txtAddress.forceFocus();
			}
		}
	}

	private void doGoInto() {
		TableItem[] items = table.getSelection();
		if (items != null && items.length > 0) {
			String path = null;
			String currentDir = controler.getCurrentDir();
			if (currentDir.length() == 0) {
				path = items[0].getText();
			} else {
				path = currentDir + user.separatorChar + items[0].getText();
				if (!items[0].getText(1).equals("锟侥硷拷锟斤拷")) {
					controler.doFileDownload(path);
					return;
				}
			}
			txtAddress.setText(path);
			controler.doListRequest(txtAddress.getText());
		}
	}

	private void doGoBack() {
		String currentDir = controler.getCurrentDir();
		if (currentDir != null) {
			if (currentDir.length() <= 3) {
				txtAddress.setText("");
			}
			if (currentDir.length() >= 3) {
				String s = currentDir.substring(0, currentDir
						.lastIndexOf(user.separatorChar));
				if (s.length() == 2)
					s += user.separatorChar;
				txtAddress.setText(s);
			}
			controler.doListRequest(txtAddress.getText());
		}
	}

	private void doSave() {
		String currentDir = controler.getCurrentDir();
		TableItem[] items = table.getSelection();
		if (items != null && items.length > 0) {
			List<String> files = new ArrayList<String>(items.length);
			for (TableItem it : items) {
				files.add(currentDir + user.separatorChar + it.getText());
			}
			controler.doFileDownload(files);
		}
	}

	void handleListFiles(FileResponse res) {
		String path = txtAddress.getText();
		controler.setCurrentDir(path);
		List<SFile> sfiles = res.getFiles();
		table.removeAll();
		if (sfiles == null) {
			MessageDialog.openWarning(this, "锟斤拷息", res.getMessage());
			return;
		}
		lblStatus.setText(sfiles.size() + "个文件对象");
		for (SFile sfile : sfiles) {
			controler.setProgramImageAndName(sfile, new TableItem(table,
					SWT.BORDER));
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
