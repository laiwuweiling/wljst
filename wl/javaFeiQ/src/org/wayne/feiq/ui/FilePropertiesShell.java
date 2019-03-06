package org.wayne.feiq.ui;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.wayne.feiq.data.FileProperties;
import org.wayne.feiq.data.User;
import org.wayne.feiq.util.Util;

import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.custom.CLabel;

public class FilePropertiesShell extends Shell implements Listener {
	private static final Log log = LogFactory.getLog(FilePropertiesShell.class);

	private Text txtName;
	private Text txtType;
	private Text txtLocation;
	private Text txtSize;
	private Text txtCreated;
	private Text txtModified;
	private Button btnClose;

	/**
	 * Launch the application.
	 * 
	 * @param
	 */
	public static Runnable getNewShellRunnable(final User user,
			final FileProperties properties) {
		return new Runnable() {
			public void run() {
				try {
					final Display display = Display.getDefault();
					display.asyncExec(new Runnable() {
						public void run() {
							log.debug("open properties shell");
							FilePropertiesShell shell = new FilePropertiesShell(
									display);
							shell.setText("Remote File Properties: "
									+ user.getUserName() + "_"
									+ user.getAddress().getHostAddress());
							shell.setProperties(properties);
							shell.open();
							shell.layout();
						}
					});
				} catch (Exception e) {
					log.error("getNewShellRunnable", e);
				}
			}
		};

	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public FilePropertiesShell(Display display) {
		super(display, SWT.CLOSE | SWT.MIN | SWT.TITLE);
		setText("Properties");
		setSize(442, 392);
		setImage(SWTResourceManager.getImage(FilePropertiesShell.class,
				"/org/wayne/feiq/ui/image/prop_ps.gif"));
		setLayout(null);
		{
			Label lblFileName = new Label(this, SWT.NONE);
			lblFileName.setBounds(10, 33, 105, 21);
			lblFileName.setText("File Name:");
		}
		{
			txtName = new Text(this, SWT.READ_ONLY);
			txtName.setBounds(121, 33, 295, 21);
		}
		{
			Label lblTypeOfFile = new Label(this, SWT.NONE);
			lblTypeOfFile.setBounds(10, 60, 105, 21);
			lblTypeOfFile.setText("Type of File:");
		}
		{
			txtType = new Text(this, SWT.READ_ONLY);
			txtType.setBounds(121, 60, 295, 21);
		}
		{
			Label lblLocation = new Label(this, SWT.NONE);
			lblLocation.setBounds(10, 131, 105, 21);
			lblLocation.setText("Location:");
		}
		{
			txtLocation = new Text(this, SWT.READ_ONLY);
			txtLocation.setBounds(121, 131, 295, 21);
		}
		{
			Label lblSize = new Label(this, SWT.NONE);
			lblSize.setBounds(10, 158, 105, 21);
			lblSize.setText("Size:");
		}
		{
			txtSize = new Text(this, SWT.READ_ONLY);
			txtSize.setBounds(121, 158, 295, 21);
		}
		{
			Label lblCreated = new Label(this, SWT.NONE);
			lblCreated.setBounds(10, 197, 105, 21);
			lblCreated.setText("Created:");
		}
		{
			txtCreated = new Text(this, SWT.READ_ONLY);
			txtCreated.setBounds(121, 197, 295, 21);
		}
		{
			Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setBounds(10, 114, 406, 11);
		}
		{
			Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setBounds(10, 180, 406, 11);
		}
		{
			Label lblModitifd = new Label(this, SWT.NONE);
			lblModitifd.setBounds(10, 224, 105, 21);
			lblModitifd.setText("Modified:");
		}
		{
			txtModified = new Text(this, SWT.READ_ONLY);
			txtModified.setBounds(121, 224, 295, 21);
		}
		{
			Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setBounds(10, 251, 406, 11);
		}
		{
			Label lblAttributes = new Label(this, SWT.NONE);
			lblAttributes.setBounds(10, 268, 105, 21);
			lblAttributes.setText("Attributes:");
		}
		{
			Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setBounds(10, 295, 406, 11);
		}
		{
			Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setBounds(10, 16, 406, 11);
		}
		{
			btnClose = new Button(this, SWT.NONE);
			btnClose.addListener(SWT.Selection, this);
			btnClose.setBounds(338, 312, 75, 25);
			btnClose.setText("&Close");
		}
		{
			Label lblContains = new Label(this, SWT.NONE);
			lblContains.setBounds(10, 87, 105, 21);
			lblContains.setText("Contains:");
		}
		{
			txtContains = new Text(this, SWT.READ_ONLY);
			txtContains.setBounds(121, 87, 295, 21);
		}
		{
			lblReadonly = new CLabel(this, SWT.NONE);
			lblReadonly.setImage(SWTResourceManager.getImage(
					FilePropertiesShell.class,
					"/org/wayne/feiq/ui/image/incomplete_tsk.gif"));
			lblReadonly.setBounds(121, 268, 90, 21);
			lblReadonly.setText("Read-only");
		}
		{
			lblHidden = new CLabel(this, SWT.NONE);
			lblHidden.setBounds(244, 268, 90, 21);
			lblHidden.setText("Hidden");
			lblHidden.setImage(SWTResourceManager.getImage(
					FilePropertiesShell.class,
					"/org/wayne/feiq/ui/image/incomplete_tsk.gif"));
		}
	}

	public void setProperties(FileProperties properties) {
		txtName.setText(properties.getName());
		if (properties.isDirectory()) {
			txtType.setText("file folder");
			txtContains.setText(properties.getContainFiles() + " Files, "
					+ properties.getContainFolders() + " Folders");
		} else {
			txtType.setText("file");
			txtContains.setText("1 File");
		}
		txtLocation.setText(properties.getPath());
		txtModified.setText(format.format(new Date(properties.lastModified())));
		txtSize.setText(Util.byteCountToDisplaySize(properties.length()) + " ("
				+ NumberFormat.getInstance().format(properties.length())
				+ " bytes)");
		if (properties.isHidden())
			lblHidden.setImage(SWTResourceManager.getImage(
					FilePropertiesShell.class,
					"/org/wayne/feiq/ui/image/complete_tsk.gif"));
		if (!properties.isWriteable()) {
			lblReadonly.setImage(SWTResourceManager.getImage(
					FilePropertiesShell.class,
					"/org/wayne/feiq/ui/image/complete_tsk.gif"));
		}
	}

	private static final SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private Text txtContains;
	private CLabel lblReadonly;
	private CLabel lblHidden;

	public void handleEvent(Event e) {
		if (e.widget == btnClose) {
			dispose();
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
