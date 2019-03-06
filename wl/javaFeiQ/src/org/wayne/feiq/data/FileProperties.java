package org.wayne.feiq.data;

import java.io.File;

public class FileProperties extends SFile {

	private static final long serialVersionUID = 7632336092891016540L;

	protected boolean writeable;
	protected boolean readable;
	protected boolean isHidden;
	protected int containFolders;
	protected int containFiles;

	public FileProperties(File f) {
		super(f);
		writeable = f.canWrite();
		readable = f.canRead();
		isHidden = f.isHidden();
		if (!isFile) {
			long[] size = sizeOfDirectory(f);
			length = size[0];
			containFolders = (int) size[1];
			containFiles = (int) size[2];
		}
	}

	private long[] sizeOfDirectory(File directory) {
		long[] size = new long[3];// [length, folders, files]

		File[] files = directory.listFiles();
		if (files != null) { // null if security restricted
			for (File file : files) {
				if (file.isDirectory()) {
					long[] csz = sizeOfDirectory(file);
					size[0] += csz[0];
					size[1] += csz[1] + 1;
					size[2] += csz[2];
				} else {
					size[0] += file.length();
					size[2] += 1;
				}
			}
		}
		return size;
	}

	public boolean isWriteable() {
		return writeable;
	}

	public boolean isReadable() {
		return readable;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public int getContainFiles() {
		return containFiles;
	}
	
	public int getContainFolders() {
		return containFolders;
	}
}
