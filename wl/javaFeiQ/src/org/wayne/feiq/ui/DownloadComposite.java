package org.wayne.feiq.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.swtdesigner.SWTResourceManager;

public class DownloadComposite extends Composite {
	private Label lblImage;
	private Label lblFile;
	private ProgressBar progressBar;
	private Label lblMsg;
	private ToolItem toolItem;
	private FormData formData_1;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DownloadComposite(Composite parent, int style) {
		super(parent, SWT.BORDER);
		setSize(500, 80);
		setLayout(new FormLayout());
		{
			lblImage = new Label(this, SWT.CENTER);
			lblImage.setImage(SWTResourceManager.getImage(
					DownloadComposite.class,
					"/org/wayne/feiq/ui/image/userBig.png"));
			{
				FormData formData = new FormData();
				formData.width = 50;
				formData.bottom = new FormAttachment(100, -10);
				formData.top = new FormAttachment(0);
				formData.left = new FormAttachment(0);
				lblImage.setLayoutData(formData);
			}
		}
		{
			lblFile = new Label(this, SWT.NONE);
			{
				formData_1 = new FormData();
				formData_1.top = new FormAttachment(lblImage, 0, SWT.TOP);
				formData_1.left = new FormAttachment(lblImage, 6);
				formData_1.height = 25;
				lblFile.setLayoutData(formData_1);
			}
			lblFile.setText("\u6587\u4EF61");
		}
		{
			progressBar = new ProgressBar(this, SWT.NONE);
			formData_1.right = new FormAttachment(progressBar, 0, SWT.RIGHT);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblFile, 6);
				formData.right = new FormAttachment(100, -66);
				formData.left = new FormAttachment(lblImage, 6);
				progressBar.setLayoutData(formData);
			}
		}
		{
			lblMsg = new Label(this, SWT.NONE);
			{
				FormData formData = new FormData();
				formData.right = new FormAttachment(100, -66);
				formData.left = new FormAttachment(lblImage, 6);
				formData.top = new FormAttachment(progressBar, 6);
				formData.height = 25;
				lblMsg.setLayoutData(formData);
			}
			lblMsg.setText("New Label");
		}
		{
			ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
			{
				FormData formData = new FormData();
				formData.bottom = new FormAttachment(progressBar, 0, SWT.BOTTOM);
				formData.right = new FormAttachment(100, -10);
				formData.width = 50;
				toolBar.setLayoutData(formData);
			}
			{
				toolItem = new ToolItem(toolBar, SWT.NONE);
				toolItem.setToolTipText("Cancel");
				toolItem.setImage(SWTResourceManager.getImage(
						DownloadComposite.class,
						"/org/wayne/feiq/ui/image/progress_stop.gif"));
			}
		}

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
