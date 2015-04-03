package com.gl.softphone;

public class EnvConfig extends Object {
	public boolean status;    //open or close Speaker,0:closed the speaker, 1:open the speaker; default:0
    public int networktype;	  //network type: 0:2G; 1:3G; 2:wifi; 3:4G; the other value is invalid   
	public int dialogScene;   //dialog scene: 0: general mode; else other: meeting mode

}