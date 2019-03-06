package org.wayne.feiq.data;

import java.util.ArrayList;
import java.util.List;

public class FileRequest extends FileBaseInfo {
	private static final long serialVersionUID = 3640804539528546634L;

	protected List<String> files;

	public FileRequest() {
	}

	public String getFile() {
		return files.get(0);
	}

	public void setFile(String file) {
		if (files == null)
			files = new ArrayList<String>(1);
		if (files.size() == 0)
			files.add(file);
		else
			files.set(0, file);
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

}
