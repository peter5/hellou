package com.zlq.util;

import java.util.ArrayList;
import java.util.HashMap;

public class SystemConfig {

	// 2013-06-14 start
	public static  String IP 						= "mps.konruns.cn";	//默认ip		192.168.1.103			
	public static  String PORT 						= "80";           //默认端口
//	public final static String serversionUrl 		= "http://"+IP+":"+PORT+"/pf/skin/c/version.txt";//获取版本号
//	public final static String serversionLogUrl 	= "http://"+IP+":"+PORT+"/pf/skin/c/versionLog.txt";//获取版本更新日志
	public static final String DOWNLOAD_FOLDER_NAME = "mps";
	public static final String DOWNLOAD_FILE_NAME 	= "mps_setup_apad.apk";
//	public static final String APK_URL 				= "http://"+IP+":"+PORT+"/pf/skin/c/mps_setup_apad.apk";
//	public static final String GPS_URL 				= "http://"+IP+":"+PORT+"/mps/services/AndroidPhoneService.pt";
	//2013-06-14 end
	
	
	//114.221.40.4:9072
//	public static String URL = "http://114.221.40.4:9072/pf/services/IPadService.pt";
	public static String URL = "http://"+IP+":"+PORT+"/pf/services/IPadService.pt";
//	public static String URL = "http://222.94.249.193:9072/pf/services/AndroidPhoneService.pt";
	
//	public static String DataUrl = "http://114.221.40.4:9072/pf/";
	public static String DataUrl = "http://"+IP+":"+PORT+"/pf/";
	
	public static String videoRecordTime = "";
	
	public static String shootingMode = "";
	
	public static String interval = "";
	
	
	
	
	public static String token = "";
	
	public static String userName = "";
	
	public static String password = "";
	
	public static String RTMPUrl = "";
	
	public static String videosecond = "8";
	
	public static String audiosecond = "30";
	
	public static int videoWidth;
	
	public static int videoHeight;
	
	public static boolean isAddContacts = false;
	
	public static  ArrayList<HashMap<String, String>> projectArrayList = new ArrayList<HashMap<String, String>>() ;

	public static  ArrayList<HashMap<String, String>> projectDataList = new ArrayList<HashMap<String, String>>() ;

	 
}
