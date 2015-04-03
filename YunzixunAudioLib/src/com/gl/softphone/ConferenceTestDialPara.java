package com.gl.softphone;

public class ConferenceTestDialPara {
	public int isConfCall; /* normal call or conference call */
	public int testFileIdx; /* auto test play file index */
	public int refFileIdx; /* auto test reference file index, for caller */
	public int callRole; /* call role, caller or called */
	public int callCnt; /* number of calls */
	public int callTime; /* the duration time per call */
	public int callInterval; /* interval time in every call */
	public int user_num;
	public String user_attr;
	public String roomname;
	public String roompwd;
	public String remark;
}
