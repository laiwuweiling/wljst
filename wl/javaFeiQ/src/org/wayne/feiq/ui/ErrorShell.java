package org.wayne.feiq.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ErrorShell extends Shell {

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public ErrorShell(Display display) {
		super(display, SWT.DIALOG_TRIM);
		setImage(SWTResourceManager.getImage(ErrorShell.class,
				"/org/eclipse/jface/dialogs/images/message_error.gif"));
		{
			Button btnc = new Button(this, SWT.NONE);
			btnc.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					dispose();
				}
			});
			btnc.setBounds(365, 60, 75, 25);
			btnc.setText("\u5173\u95ED(&C)");
		}
		{
			Label lblInitReceiversFailed = new Label(this, SWT.NONE);
			lblInitReceiversFailed.setBounds(23, 20, 394, 34);
			lblInitReceiversFailed.setText("init receivers failed");
		}
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("ERROR");
		setSize(456, 128);

	}

	/**
	 * Open the window.
	 */
	public void show() {
		Display display = getDisplay();
		createContents();
		open();
		layout();
		while (!isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
