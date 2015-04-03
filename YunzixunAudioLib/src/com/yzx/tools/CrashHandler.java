package com.yzx.tools;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;  

/** 
 * 
 * @author xiongjijian
 * @data:2014-10-29 下午4:37:41
 */  
public class CrashHandler implements UncaughtExceptionHandler {
	
	private Thread.UncaughtExceptionHandler mDefaultHandler; // 系统默认ERROR处理器
    private static CrashHandler sHandler;  
  
    public static CrashHandler getInstance() {
    	if(sHandler == null){
    		sHandler = new CrashHandler();
    	}
        return sHandler;  
    }
    
    
    public CrashHandler(){}
    
    public void init(){
    	mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
    } 
    
    
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) { 
    	if(!printlnException(ex) && mDefaultHandler != null){
    		mDefaultHandler.uncaughtException(thread, ex);
    	}else{
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
    	}
    } 
    
    public boolean printlnException(Throwable ex){
    	StringWriter sw = new StringWriter();  
    	ex.printStackTrace(new PrintWriter(sw, true));  
    	String str = sw.toString();
        if(str.contains("com.yzx")){
        	FileTools.saveExLog(str,"YZX_log_");
        }
        return false;
    }
}  