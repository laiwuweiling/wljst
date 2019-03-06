package org.wayne.feiq.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.wayne.feiq.Application;
import org.wayne.feiq.cfg.Config;
import org.wayne.feiq.data.SFile;
import org.wayne.feiq.data.User;
import org.wayne.feiq.ui.helper.ShellHelper;

import swing2swt.layout.BorderLayout;

import com.swtdesigner.SWTResourceManager;

public class UserShell extends Shell implements Listener, SelectionListener {

	private static final Log log = LogFactory.getLog(UserShell.class);

	protected ToolBar toolBar;
	protected CLabel lblUser;
	protected Text txtMessage;
	protected Text txtInput;
	private Label lblInfo_userName;
	private Label lblInfo_hostName;
	private Label lblInfo_osUserName;
	private Label lblInfo_groupName;
	private Label lblInfo_hostAddress;
	private Composite userInfoComposite;
	private SendedFileComposite sendedFileComposite;
	protected ToolItem itemFileShare;
	protected ToolItem itemVoiceMsg;
	protected ToolItem itemRemoteCom;
	protected ToolItem itemSend;
	protected ToolItem itemClose;

	private Menu menuSend;
	private MenuItem mntmCtrlEnter;
	private MenuItem mntmEnter;
	private DropTarget dropTarget;
	private DropTarget dtMessage;
	private DragSource dragSource;

	public UserShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		setText("\u4E0E\u804A\u5929\u4E2D");
		setSize(553, 511);
		addListener(SWT.Close, this);
		setLayout(new BorderLayout(0, 0));
		setImage(SWTResourceManager.getImage(UserShell.class,
				"/org/wayne/feiq/ui/image/vlog.png"));
		{
			toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT | SWT.SHADOW_OUT);
			toolBar.setLayoutData(BorderLayout.NORTH);

			{
				itemFileShare = new ToolItem(toolBar, SWT.DROP_DOWN);
				itemFileShare.addSelectionListener(this);
				itemFileShare.setText("\u6587\u4EF6\u5171\u4EAB");
				itemFileShare.setImage(SWTResourceManager.getImage(
						UserShell.class,
						"/org/wayne/feiq/ui/image/fldr_obj.gif"));
			}
			{
				itemRemoteCom = new ToolItem(toolBar, SWT.DROP_DOWN);
				itemRemoteCom.addSelectionListener(this);
				itemRemoteCom.setImage(SWTResourceManager.getImage(
						UserShell.class,
						"/org/wayne/feiq/ui/image/defaultview_misc.gif"));
				itemRemoteCom.setText("\u8FDC\u7A0B\u547D\u4EE4");
			}
			{
				itemVoiceMsg = new ToolItem(toolBar, SWT.NONE);
				itemVoiceMsg.addSelectionListener(this);
				itemVoiceMsg.setText("\u8BED\u97F3\u804A\u5929");
				itemVoiceMsg.setImage(SWTResourceManager.getImage(
						UserShell.class, "/org/wayne/feiq/ui/image/file.gif"));
			}
		}
		{
			SashForm sashForm = new SashForm(this, SWT.SMOOTH);
			{
				SashForm sashFormText = new SashForm(sashForm, SWT.VERTICAL);
				{
					Composite composite = new Composite(sashFormText,
							SWT.BORDER);
					composite.setLayout(new BorderLayout(0, 0));
					{
						lblUser = new CLabel(composite, SWT.NONE);
						lblUser.setLayoutData(BorderLayout.NORTH);
						lblUser.setImage(SWTResourceManager.getImage(
								UserShell.class,
								"/org/wayne/feiq/ui/image/userBig.png"));
					}
					{
						txtMessage = new Text(composite, SWT.BORDER
								| SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
						txtMessage.setBackground(SWTResourceManager
								.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
						txtMessage.setLayoutData(BorderLayout.CENTER);

						dragSource = new DragSource(txtMessage, DND.DROP_MOVE);
						dragSource.setTransfer(new Transfer[] { TextTransfer
								.getInstance() });
						dragSource.addDragListener(new DragSourceAdapter() {
							public void dragSetData(DragSourceEvent e) {
								e.data = txtMessage.getSelectionText();
							}
						});
					}
				}
				{
					Composite composite = new Composite(sashFormText,
							SWT.BORDER);
					composite.setLayout(new BorderLayout(0, 0));
					{
						ToolBar toolBar_1 = new ToolBar(composite, SWT.FLAT
								| SWT.RIGHT);
						toolBar_1.setLayoutData(BorderLayout.NORTH);
						{
							ToolItem toolItem = new ToolItem(toolBar_1,
									SWT.NONE);
							toolItem.setText("\u5B57\u4F53");
						}
						{
							ToolItem toolItem = new ToolItem(toolBar_1,
									SWT.NONE);
							toolItem.setText("\u6837\u5F0F");
						}
					}
					{
						txtInput = new Text(composite, SWT.BORDER
								| SWT.V_SCROLL | SWT.MULTI);
						txtInput.addListener(SWT.KeyDown, this);
						txtInput.setLayoutData(BorderLayout.CENTER);

						DragSource ds = new DragSource(txtInput, DND.DROP_MOVE);
						ds.setTransfer(new Transfer[] { TextTransfer
								.getInstance() });
						ds.addDragListener(new DragSourceAdapter() {
							public void dragSetData(DragSourceEvent e) {
								e.data = txtInput.getSelectionText();
							}
						});

						dtMessage = new DropTarget(txtInput, DND.DROP_MOVE);
						dtMessage.setTransfer(new Transfer[] {
								TextTransfer.getInstance(),
								HTMLTransfer.getInstance(),
//								URLTransfer.getInstance(),
								FileTransfer.getInstance() });
						dtMessage.addDropListener(dropTargetListener);
					}
					{
						ToolBar toolBar_1 = new ToolBar(composite, SWT.FLAT);
						toolBar_1.setLayoutData(BorderLayout.SOUTH);
						{
							itemClose = new ToolItem(toolBar_1, SWT.NONE);
							itemClose.addSelectionListener(this);
							itemClose.setText("\u5173\u95ED(&C)");
						}
						{
							itemSend = new ToolItem(toolBar_1, SWT.DROP_DOWN);
							itemSend.addSelectionListener(this);
							itemSend.setText("\u53D1\u9001(&S)");
						}
						{
							menuSend = new Menu(this);
							{
								mntmCtrlEnter = new MenuItem(menuSend,
										SWT.RADIO);
								mntmCtrlEnter.setText("Ctrl + Enter");
								mntmCtrlEnter.addSelectionListener(this);

								mntmEnter = new MenuItem(menuSend, SWT.RADIO);
								mntmEnter.setText("Enter");
								mntmEnter.addSelectionListener(this);
							}
						}
					}
				}
				sashFormText.setWeights(new int[] { 278, 132 });
			}
			{
				Composite composite = new Composite(sashForm, SWT.BORDER);
				composite.setLayout(new BorderLayout(0, 0));
				{
					ExpandBar expandBar = new ExpandBar(composite, SWT.NONE);
					expandBar.setLayoutData(BorderLayout.CENTER);
					{
						ExpandItem expandItem = new ExpandItem(expandBar,
								SWT.NONE);
						expandItem.setImage(SWTResourceManager.getImage(
								UserShell.class,
								"/org/wayne/feiq/ui/image/userInfo.png"));
						expandItem.setExpanded(true);
						expandItem.setText("\u5BF9\u65B9\u8D44\u6599");
						{
							userInfoComposite = new Composite(expandBar,
									SWT.NONE);
							GridLayout gridLayout = new GridLayout(2, false);
							gridLayout.verticalSpacing = 2;
							gridLayout.marginHeight = 2;
							userInfoComposite.setLayout(gridLayout);
							expandItem.setControl(userInfoComposite);
							{
								new Label(userInfoComposite, SWT.NONE)
										.setText("用户名: ");
								lblInfo_userName = new Label(userInfoComposite,
										SWT.NONE);
								{
									GridData gridData = new GridData(SWT.LEFT,
											SWT.CENTER, false, false, 1, 1);
									gridData.widthHint = 200;
									lblInfo_userName.setLayoutData(gridData);
								}
								new Label(userInfoComposite, SWT.NONE)
										.setText("主机: ");
								lblInfo_hostName = new Label(userInfoComposite,
										SWT.NONE);
								{
									GridData gridData = new GridData(SWT.LEFT,
											SWT.CENTER, false, false, 1, 1);
									gridData.widthHint = 200;
									lblInfo_hostName.setLayoutData(gridData);
								}
								new Label(userInfoComposite, SWT.NONE)
										.setText("登录名: ");
								lblInfo_osUserName = new Label(
										userInfoComposite, SWT.NONE);
								{
									GridData gridData = new GridData(SWT.LEFT,
											SWT.CENTER, false, false, 1, 1);
									gridData.widthHint = 200;
									lblInfo_osUserName.setLayoutData(gridData);
								}
								new Label(userInfoComposite, SWT.NONE)
										.setText("组名: ");
								lblInfo_groupName = new Label(
										userInfoComposite, SWT.NONE);
								{
									GridData gridData = new GridData(SWT.LEFT,
											SWT.CENTER, false, false, 1, 1);
									gridData.widthHint = 200;
									lblInfo_groupName.setLayoutData(gridData);
								}
								new Label(userInfoComposite, SWT.NONE)
										.setText("IP地址: ");
								lblInfo_hostAddress = new Label(
										userInfoComposite, SWT.NONE);
								{
									GridData gridData = new GridData(SWT.LEFT,
											SWT.CENTER, false, false, 1, 1);
									gridData.widthHint = 200;
									lblInfo_hostAddress.setLayoutData(gridData);
								}
							}
						}
						expandItem.setHeight(expandItem.getControl()
								.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + 5);
					}
					{
						ExpandItem expandItem = new ExpandItem(expandBar,
								SWT.NONE);
						expandItem.setImage(SWTResourceManager.getImage(
								UserShell.class,
								"/org/wayne/feiq/ui/image/fldr_obj.gif"));
						expandItem.setExpanded(true);
						expandItem.setText("\u63A5\u6536\u6587\u4EF6");
						{
							sendedFileComposite = new SendedFileComposite(
									expandBar,

									SWT.NONE);
							expandItem.setControl(sendedFileComposite);
						}
						expandItem.setHeight(150);
					}
					{
						ExpandItem expandItem = new ExpandItem(expandBar,
								SWT.NONE);
						expandItem.setExpanded(true);
						expandItem.setText("New Item");
						{
							Composite composite_1 = new Composite(expandBar,
									SWT.NONE);
							expandItem.setControl(composite_1);
						}
						expandItem.setHeight(expandItem.getControl()
								.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
					}
				}
			}
			sashForm.setWeights(new int[] { 349, 168 });
		}

		dropTarget = new DropTarget(this, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dropTarget.addDropListener(dropTargetListener);

	}

	protected Config config;
	protected User user;

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
		fillUserInfo();
		config = Application.get(Config.class);
		byte shortcut = config.getSendMsgShortcut();
		if (shortcut == Config.SHORTCUT_CTRL_ENTER) {
			mntmCtrlEnter.setSelection(true);
		} else if (shortcut == Config.SHORTCUT_ENTER) {
			mntmEnter.setSelection(true);
		}
	}

	protected void fillUserInfo() {
		try {
			setText("与 " + user.getUserName() + " 聊天中");
			lblUser.setText(user.getUserName());
			lblInfo_userName.setText(user.getUserName());
			lblInfo_hostName.setText(user.getAddress().getHostName());
			lblInfo_osUserName.setText(user.getOsUserName());
			lblInfo_groupName.setText(user.getGroupName());
			lblInfo_hostAddress.setText(user.getAddress().getHostAddress());
		} catch (Exception e) {
			log.error("fillUserInfo error", e);
		}
	}

	public UserShell appendMessage(String msg) {
		return appendMessage(user.getUserName(), msg);
	}

	public UserShell addReceiveFile(SFile[] files) {
		StringBuilder msg = new StringBuilder(200);
		msg.append("接收文件");
		for (SFile f : files)
			msg.append("\r\n\t").append(f.getName());
		appendMessage(msg.toString());
		sendedFileComposite.addFile(files);
		return this;
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	protected UserShell appendMessage(String userName, String msg) {
		try {
			txtMessage.append(userName);
			txtMessage.append(" ");
			txtMessage.append(dateFormat.format(new Date()));
			txtMessage.append("\n\t");
			txtMessage.append(msg);
			txtMessage.append("\n\n");
		} catch (Exception e) {
			log.error("append message error", e);
		}
		return this;
	}

	private UserShellControler controler = new UserShellControler(this);

	public void handleEvent(Event e) {
		if (e.widget == txtInput) {
			handleTxtInputEvent(e);
		} else if (e.widget == this) {
			if (e.type == SWT.Close) {
				controler.doClose();
				e.doit = false;
			}
		}
	}

	private void handleTxtInputEvent(Event e) {
		if (e.type == SWT.KeyDown) {
			if (e.keyCode == '\r') {
				byte shortcut = config.getSendMsgShortcut();
				if (shortcut == Config.SHORTCUT_CTRL_ENTER) {
					if (e.stateMask == SWT.CTRL) {
						controler.doSendMsg();
						e.doit = false;
					}
				} else if (shortcut == Config.SHORTCUT_ENTER) {
					if (e.stateMask != SWT.CTRL) {
						controler.doSendMsg();
						e.doit = false;
					}
				}
			} else if (e.keyCode == SWT.ESC) {
				controler.doClose();
			}
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == itemSend) {
			if (e.detail == SWT.ARROW) {
				ShellHelper.showDrawMenu(itemSend, menuSend);
			} else {
				if (controler.doSendMsg())
					e.doit = false;
			}
		} else if (e.widget == itemClose) {
			controler.doClose();
		} else if (e.widget == itemFileShare) {
			if (e.detail == SWT.ARROW) {
			} else {
				controler.doFileShare();
			}
		} else if (e.widget == itemRemoteCom) {
			if (e.detail == SWT.ARROW) {
			} else {
				controler.doRemoteCommand();
			}
		} else if (e.widget == itemVoiceMsg) {

		} else if (e.widget == mntmCtrlEnter) {
			if (mntmCtrlEnter.getSelection())
				config.setSendMsgShortcut(Config.SHORTCUT_CTRL_ENTER);
		} else if (e.widget == mntmEnter) {
			if (mntmEnter.getSelection())
				config.setSendMsgShortcut(Config.SHORTCUT_ENTER);
		}
	}

	private DropTargetListener dropTargetListener = new DropTargetAdapter() {

		public void drop(DropTargetEvent e) {
//			System.out.println("drop: " + e.currentDataType.type + ", "
//					+ e.data.getClass());
			if (TextTransfer.getInstance().isSupportedType(e.currentDataType)) {
				txtInput.insert((String) e.data);
			} else if (HTMLTransfer.getInstance().isSupportedType(
					e.currentDataType)) {
				txtInput.insert((String) e.data);
			} else if (FileTransfer.getInstance().isSupportedType(
					e.currentDataType)) {
				controler.doSendFile((String[]) e.data);
			} 
//			else if (URLTransfer.getInstance().isSupportedType(
//					e.currentDataType)) {
//				System.out.println(e.data);
//			}
		}

	};

	protected void checkSubclass() {
	}
}
