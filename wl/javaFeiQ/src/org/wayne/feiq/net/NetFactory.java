package org.wayne.feiq.net;

public interface NetFactory {

	/**
	 * get RequestDispatcher <br>
	 * only one of this class's instance is enough
	 * 
	 * @return
	 */
	RequestDispatcher getRequestDispatcher();

}
