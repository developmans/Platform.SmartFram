package com.boxlab.ndk;

/* Usage: 
 * Build project once
 * cd /d D:\Workspaces\App\485demo
 * javah -classpath bin/classes -d jni com.boxlab.ndk.RS485Ctrl
 */
public class RS485Ctrl {
	public native int open();
	public native int setMode(int mode);
	public native int close();
	
	//调用刚才生成的库的函数,否则会出现“unfortunately，xxx has stopped!”的错误
    static {
        System.loadLibrary("jni-rs485");
    }
}
