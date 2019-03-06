package org.wayne.feiq.data;

import java.io.File;
import java.io.Serializable;

/**
 * 用于传输的文件标识
 * 
 * @author 5v
 * 
 */
public class SFile implements Serializable {

	private static final long serialVersionUID = -1282573233441848179L;

	protected transient File file;

	protected String name;
	protected String path;
	protected boolean isFile;
	protected long lastModified;
	protected long length;

	public SFile(File f) {
		name = f.getName();
		path = f.getPath();
		if (name.length() == 0)
			name = path;
		isFile = f.isFile();
		lastModified = f.lastModified();
		if (isFile)
			length = f.length();
	}

	public long lastModified() {
		return lastModified;
	}

	public long length() {
		return length;
	}

	public boolean isFile() {
		return isFile;
	}

	public boolean isDirectory() {
		return !isFile;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SFile other = (SFile) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

}
