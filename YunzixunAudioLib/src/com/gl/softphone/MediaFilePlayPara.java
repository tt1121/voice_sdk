package com.gl.softphone;

public class MediaFilePlayPara {
	public int mode; // 0:unused, 1:file stream mode, 2:file name mode
	public String filepath;
	public int iFileFormat;
	public int iDirect;
	public int iLoop;
	public int data_size;
	public byte[] audioData;
}
