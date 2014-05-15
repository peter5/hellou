package com.zlq.util;

import java.util.ArrayList;
import java.util.HashMap;

public class SystemConfig {

	// 2013-06-14 start
	public static  String IP 						 = "mps.konruns.cn";	//默认IP   192.168.1.103					
//	public static  String IP 						 = "192.168.1.103";	//默认IP   192.168.1.103					
	public static  String PORT 						 = "80";           //默认端口
//	public static  String PORT 						 = "8088";           //默认端口
	
//	public  static String serversionUrl 			 = "http://"+IP+":"+PORT+"/pf/skin/c/version2.txt";//获取版本号
//	public  static String serversionLogUrl 			 = "http://"+IP+":"+PORT+"/pf/skin/c/versionLog2.txt";//获取版本更新日志
	public static  final String DOWNLOAD_FOLDER_NAME = "Renmaitong";
	public static  final String DOWNLOAD_FILE_NAME 	 = "mps_setup_aphone.apk";
//	public static  String APK_URL 				     = "http://"+IP+":"+PORT+"/pf/skin/c/mps_setup_aphone.apk";
	//2013-06-14 end
	
	
	
	public static String URL = "http://"+IP+":"+PORT+"/pf/services/AndroidPhoneService.pt";
	public static String Location_URL = "http://"+IP+":"+PORT+"/pf/LocationServices/AndroidPhoneService.pt";
	
//	http://192.168.1.103:8088/pf/LocationServices.AndroidPhoneService.pt
	
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
}
