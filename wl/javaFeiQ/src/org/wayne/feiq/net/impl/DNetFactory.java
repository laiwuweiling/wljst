package org.wayne.feiq.net.impl;

import org.wayne.feiq.net.NetFactory;
import org.wayne.feiq.net.RequestDispatcher;

public class DNetFactory implements NetFactory {

	private RequestDispatcher requestDispatcher;

	public RequestDispatcher getRequestDispatcher() {
		if (requestDispatcher == null)
			requestDispatcher = new DRequestDispatcher();
		return requestDispatcher;
	}

}
