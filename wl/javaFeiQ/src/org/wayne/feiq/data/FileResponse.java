package org.wayne.feiq.data;

import java.util.List;

public class FileResponse extends FileBaseInfo {

	private static final long serialVersionUID = 6628584075768451269L;

	protected List<SFile> files;
	protected FileProperties properties;

	public List<SFile> getFiles() {
		return files;
	}

	public void setFiles(List<SFile> files) {
		this.files = files;
	}

	public FileProperties getProperties() {
		return properties;
	}

	public void setProperties(FileProperties properties) {
		this.properties = properties;
	}

}
