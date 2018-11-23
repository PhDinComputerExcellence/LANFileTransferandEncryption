package com.files4Dayz.application;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class FileInfo extends File {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String extension;
    private long size;

    public FileInfo(String pathname) {
        super(pathname);
        extension = FilenameUtils.getExtension(pathname);
        size = this.length();
    }

    public String getExtension() {
        return extension;
    }

    public long getSize() {
        return size;
    }
}
