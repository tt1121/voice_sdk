package com.yzx.http.net;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileItem {
	private String fileName;
	private String mimeType;
	private byte[] content;
	private File file;    

	public FileItem(File file) {
		this.file = file;
	}

	public FileItem(String filePath) {
		this(new File(filePath));
	}

	public FileItem(String fileName, byte[] content) {
		this.fileName = fileName;
		this.content = content;
	}

	public FileItem(String fileName, byte[] content, String mimeType) {
		this(fileName, content);
		this.mimeType = mimeType;
	}

	public String getFileName() {
		if (this.fileName == null && this.file != null && this.file.exists()) {
			this.fileName = file.getName();
		}
		return this.fileName;
	}

	public String getMimeType() throws IOException {
		if (this.mimeType == null) {
			this.mimeType = HouseHoldUtile.getMimeType(getContent());
		}
		return this.mimeType;
	}

	public byte[] getContent() throws IOException {
		if (this.content == null && this.file != null && this.file.exists()) {
			InputStream in = null;
			BufferedInputStream bis = null;
			ByteArrayOutputStream out = null;

			try {
				in = new FileInputStream(this.file);
				bis = new BufferedInputStream(in);
				out = new ByteArrayOutputStream();
				int len=bis.available();
				byte[] isBuffer = new byte[len];
				bis.read(isBuffer);
				this.content=isBuffer;
			} finally {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (bis != null) {
					bis.close();
				}
			}
		}
		return this.content;
	}

}
