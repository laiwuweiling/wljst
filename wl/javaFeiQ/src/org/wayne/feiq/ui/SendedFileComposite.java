package org.wayne.feiq.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.wayne.feiq.Application;
import org.wayne.feiq.cfg.Config;
import org.wayne.feiq.data.SFile;
import org.wayne.feiq.data.Type;
import org.wayne.feiq.net.UDPSender;
import org.wayne.feiq.util.Util;

import swing2swt.layout.BorderLayout;

import com.swtdesigner.SWTResourceManager;

public class SendedFileComposite extends Composite implements Listener {

	private static final Log log = LogFactory.getLog(SendedFileComposite.class);

	private ToolItem itmAcceptAll;
	private ToolItem itmRejectAll;
	private Table table;
	private MenuItem mtmAccept;
	private MenuItem mtmReject;

	public SendedFileComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		{
			ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
			toolBar.setLayoutData(BorderLayout.NORTH);
			{
				itmAcceptAll = new ToolItem(toolBar, SWT.NONE);
				itmAcceptAll.setEnabled(false);
				itmAcceptAll.setImage(SWTResourceManager.getImage(
						SendedFileComposite.class,
						"/org/wayne/feiq/ui/image/saveall_edit.gif"));
				itmAcceptAll.setText("\u5168\u90E8\u63A5\u6536");
				itmAcceptAll.addListener(SWT.Selection, this);
			}
			{
				itmRejectAll = new ToolItem(toolBar, SWT.NONE);
				itmRejectAll.setEnabled(false);
				itmRejectAll.setImage(SWTResourceManager.getImage(
						SendedFileComposite.class,
						"/org/wayne/feiq/ui/image/trash.gif"));
				itmRejectAll.setText("\u5168\u90E8\u53D6\u6D88");
				itmRejectAll.addListener(SWT.Selection, this);
			}
		}
		{
			table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
			table.addListener(SWT.MenuDetect, this);
			table.setLayoutData(BorderLayout.CENTER);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			{
				TableColumn tableColumn = new TableColumn(table, SWT.NONE);
				tableColumn.setWidth(84);
				tableColumn.setText("\u6587\u4EF6\u540D");
			}
			{
				TableColumn tableColumn = new TableColumn(table, SWT.NONE);
				tableColumn.setWidth(75);
				tableColumn.setText("\u5927\u5C0F");
			}
			{
				TableColumn tableColumn = new TableColumn(table, SWT.NONE);
				tableColumn.setWidth(119);
				tableColumn.setText("\u7C7B\u578B");
			}
			{
				Menu menu = new Menu(table);
				table.setMenu(menu);
				{
					mtmAccept = new MenuItem(menu, SWT.NONE);
					mtmAccept.setImage(SWTResourceManager.getImage(
							SendedFileComposite.class,
							"/org/wayne/feiq/ui/image/save_edit.gif"));
					mtmAccept.setText("\u63A5\u6536(&A)");
					mtmAccept.addListener(SWT.Selection, this);
				}
				{
					mtmReject = new MenuItem(menu, SWT.NONE);
					mtmReject.setImage(SWTResourceManager.getImage(
							SendedFileComposite.class,
							"/org/wayne/feiq/ui/image/delete_obj.gif"));
					mtmReject.setText("\u53D6\u6D88(&R)");
					mtmReject.addListener(SWT.Selection, this);
				}
			}
		}

	}

	private void resetOperatable() {
		boolean enabled = itmAcceptAll.isEnabled();
		if (table.getItemCount() > 0) {
			if (!enabled) {
				itmAcceptAll.setEnabled(true);
				itmRejectAll.setEnabled(true);
			}
		} else {
			if (enabled) {
				itmAcceptAll.setEnabled(false);
				itmRejectAll.setEnabled(false);
			}
		}
	}

	public SendedFileComposite addFile(SFile[] files) {
		TableItem[] items = table.getItems();

		for (SFile f : files) {
			for (TableItem m : items) {
				if (m.getData().equals(f))
					continue;
			}
			addItem(f);
		}
		resetOperatable();
		return this;
	}

	protected void addItem(SFile sfile) {
		TableItem item = new TableItem(table, SWT.BORDER);
		item.setData(sfile);
		Image image = null;
		String descripiton = null;
		if (sfile.isDirectory()) {
			image = SWTResourceManager.getImage(SendedFileComposite.class,
					"/org/wayne/feiq/ui/image/folder.gif");
			descripiton = "文件夹";
		} else {
			Object[] o = Util.findFileProgram(sfile.getPath(), getDisplay());
			image = (Image) o[1];
			descripiton = (String) o[2];
		}
		item.setImage(image);
		String size = "";
		if (sfile.isFile()) {
			size = Util.byteCountToDisplaySize(sfile.length());
		}
		item.setText(new String[] { sfile.getName(), size, descripiton });
	}

	public void handleEvent(Event e) {
		if (e.widget == table) {
			handleTableEvent(e);
		} else if (e.widget == itmAcceptAll) {
			if (handleAccept(table.getItems()))
				table.removeAll();
		} else if (e.widget == mtmAccept) {
			if (handleAccept(table.getSelection()))
				table.remove(table.getSelectionIndices());
		} else if (e.widget == itmRejectAll) {
			handleRejectAll(e);
		} else if (e.widget == mtmReject) {
			handleReject(e);
		}
		resetOperatable();
	}

	private boolean handleAccept(TableItem[] items) {
		if (items == null || items.length == 0)
			return false;

		Config config = Application.get(Config.class);
		DirectoryDialog dlg = new DirectoryDialog(getShell(), SWT.SAVE);
		dlg.setText("保存到目录..");
		dlg.setMessage("将要下载的文件或文件夹保存到指定的目录");
		String filterPath = config.getDefaultSavePath();
		if (filterPath != null)
			dlg.setFilterPath(filterPath);
		String base = dlg.open();
		if (base == null)
			return false;
		config.setDefaultSavePath(base);
		List<String> files = new ArrayList<String>(items.length);
		for (TableItem item : items) {
			files.add(((SFile) item.getData()).getPath());
		}
		try {
			Application.get(UDPSender.class)
					.send(Type.FILE_DOWNLOAD, new Object[] { files, base },
							((UserShell) getShell()).user);
		} catch (Exception e) {
			log.error("udp send file download reuqest error", e);
			MessageDialog.openError(getShell(), "错误", e.getMessage());
			return false;
		}
		return true;
	}

	private void handleReject(Event e) {
		if (MessageDialog.openConfirm(getShell(), "确认信息", "确定要取消所选文件吗？")) {
			int[] indices = table.getSelectionIndices();
			table.remove(indices);
		}
	}

	private void handleRejectAll(Event e) {
		if (MessageDialog.openConfirm(getShell(), "确认信息", "确定要取消全部吗？")) {
			table.removeAll();
		}
	}

	private void handleTableEvent(Event e) {
		if (e.type == SWT.MenuDetect) {
			if (table.getSelectionCount() == 0) {
				e.doit = false;
			}
		}
	}

	@Override
	protected void checkSubclass() {
	}

}
