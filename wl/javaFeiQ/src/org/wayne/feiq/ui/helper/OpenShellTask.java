package org.wayne.feiq.ui.helper;

import org.eclipse.swt.widgets.Shell;

public interface OpenShellTask<S extends Shell> {

	void doTask(S shell);
}