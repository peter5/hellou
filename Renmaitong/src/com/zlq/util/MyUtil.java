package com.zlq.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;


public class MyUtil {
	
	/*
	 * 此些字段 与断网情况下有关
	 */
	public final static String KEY_USER_NAME="uName"; // 登陆 用户名 key
	public final static String KEY_USER_PWD="pwd";		  
	public final static String KEY_TOKEN="token";
	public final static String KEY_PROJECT_LIST = "ProjectList";
	
	public static void saveJson2preference(Context ctx,String key,String value){
		  SharedPreferences jsonPreference = ctx.getSharedPreferences("duanwangjson", 0);
		  SharedPreferences.Editor editor = jsonPreference.edit();
	      editor.putString(key, value);//
	      editor.commit();
		}
	public static String getFromPreference(Context ctx,String key){
		  SharedPreferences jsonPreference = ctx.getSharedPreferences("duanwangjson", 0);
		 return jsonPreference.getString(key, "");
	}
	
	public static void saveProjMaxIndex2preference(Context ctx,String key,String value){
		  //key 的规则为：projectNum+“p”或者“v”或者“a”分别示意 photo,video,audio
		  SharedPreferences jsonPreference = ctx.getSharedPreferences("duanwangjson", 0);
		  SharedPreferences.Editor editor = jsonPreference.edit();
	      editor.putString(key, value);//
	      editor.commit();
		}
	public static String getProjMaxIndexFromPreference(Context ctx,String key){
		  SharedPreferences jsonPreference = ctx.getSharedPreferences("duanwangjson", 0);
		 return jsonPreference.getString(key, "-1");
	}
	
	
	
	

	public static  String  getServerVersionName(final String URL ) {
		System.out.println("URL--"+URL);
		String versionName = "";
		HttpClient client = new DefaultHttpClient();  
//	       final  String url = "http://219.141.190.156:8080/pf/skin/c/version2.sn";
	        HttpGet get = new HttpGet(URL);  
	        try {
	        HttpResponse response = client.execute(get);  
	        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
	            InputStream is = response.getEntity().getContent();  
	            versionName = inStream2String(is);  
	            System.out.println("result-- peter1:"+versionName);
//	            System.out.println("result-- peter2:"+serverVersion);
	        }  
	        System.out.println("result-- peter:"+versionName);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception-----");
		}
			System.out.println("versionName:--"+versionName);
			return versionName;
		}
	      //将输入流转换成字符串  
	        private static String inStream2String(InputStream is) throws Exception {  
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	            byte[] buf = new byte[1024];  
	            int len = -1;  
	            while ((len = is.read(buf)) != -1) {  
	                baos.write(buf, 0, len);  
	            }  
	            return new String(baos.toByteArray(),"gbk");  
	        }  
	
	        public static  String  getVerName(Context context) {
	    		String versionName = "";
	    		try {
	    			PackageManager  pm = context.getPackageManager();
	    			versionName = pm.getPackageInfo(context.getPackageName(), 0).versionName;
	    		} catch (NameNotFoundException e) {
	    			e.printStackTrace();
	    		}
	    		return versionName;
	    	}
	        
	     // 判断当前是否使用的是 WIFI网络  
	        public static boolean isWifiActive(Context icontext){
	            Context context = icontext.getApplicationContext();    
	            ConnectivityManager connectivity = (ConnectivityManager) context    
	                    .getSystemService(Context.CONNECTIVITY_SERVICE);
	            NetworkInfo[] info;
	            if (connectivity != null) {    
	                info = connectivity.getAllNetworkInfo();    
	                if (info != null) {    
	                    for (int i = 0; i < info.length; i++) {    
	                        if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {    
	                            return true;    
	                        }    
	                    }    
	                }    
	            }    
	            return false;   
	        }
	        
	        
	        
	public static ArrayList<ArrayList<String>> arrayToList(String[][] data){
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		if(data != null){
			for(int i=0; i<data.length; i++){
				ArrayList<String> tem = new ArrayList<String>();
				for(int j=0; j<data[i].length; j++){
					tem.add(data[i][j]);
				}
				list.add(tem);
			}
			return list;
		}else{
			return list;
		}
	}
	
	public static boolean isNetworkAvailable(Context context) { 
		ConnectivityManager cm = (ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) { } else {
		//如果仅仅是用来判断网络连接　　　　　　 
		//则可以使用 cm.getActiveNetworkInfo().isAvailable(); 
		NetworkInfo[] info = cm.getAllNetworkInfo(); 
		if (info != null) { 
			for (int i = 0; i < info.length; i++) {
			     if (info[i].getState() == NetworkInfo.State.CONNECTED){ 
			    	 return true;
			     } 
			}
		} 
		} 
		return false;
	}
	
	
	// 判断网络连接是否已开
    public static boolean isConn(Context context){
        boolean bisConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }
    
    
     //打开设置网络界面
    public static void setNetworkMethod(final Context context){
        //提示对话框
        AlertDialog.Builder builder=new Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent=null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本 
                if(android.os.Build.VERSION.SDK_INT>10){
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                }else{
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }).show();
    }
	
	
    public static boolean isGpsEnabled(Context context) { 
    	LocationManager lm = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
    	List<String> accessibleProviders = lm.getProviders(true); 
    	return accessibleProviders != null && accessibleProviders.size() > 0; 
    }

    //下边这小段代码是用来判断手机的GPS服务是否为开启状态.如果是就提示用户GPS已经打开.
   	//如果现在GPS 处于关闭状态,那么给用户一个提示, 然后打开GPS设置界面,让用户更改GPS为启动状态.
   	private void openGPSSettings(Context context){
    	LocationManager alm =(LocationManager)context.getSystemService( Context.LOCATION_SERVICE );
    	if( alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) ){
    		Toast.makeText(context, "GPS is already on", Toast.LENGTH_SHORT ).show();
    	}else{
	    	Toast.makeText(context, "Please turn on GPS", Toast.LENGTH_SHORT ).show();
	    	Intent myIntent = new Intent( Settings.ACTION_SECURITY_SETTINGS );
	    	context.startActivity(myIntent);
    	}
    }

    //初始化GPS设置
    public static void initGPS(Context context){
    	LocationManager locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    	//判断GPS模块是否开启，如果没有则开启
    	if(!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
	    	Toast.makeText(context, "GPS is not open,Please open it!", Toast.LENGTH_SHORT).show();
	    	Intent intent=new Intent(Settings.ACTION_SECURITY_SETTINGS);
//	    	startActivityForResult(intent, 0);
    	}else {
    		Toast.makeText(context, "GPS is ready", Toast.LENGTH_SHORT);
    	}
    	}

	public static boolean isWifiEnabled(Context context) { 
		ConnectivityManager mgrConn = (ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE); 
		TelephonyManager mgrTel = (TelephonyManager) context .getSystemService(Context.TELEPHONY_SERVICE); 
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS); 
	}
	
	public static boolean is3rd(Context context) { 
		ConnectivityManager cm = (ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo(); 
		if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) { 
			return true; 
		}
		return false; 
	}

	public static boolean isWifi(Context context) { 
		ConnectivityManager cm = (ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo(); 
		if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) { 
			return true; 
		} 
		return false; 
	}

	public static Location getCurrentLocation(Context context){
		String serviceName = Context.LOCATION_SERVICE;
		LocationManager locationManager = (LocationManager) context.getSystemService(serviceName);
		String provider = LocationManager.GPS_PROVIDER;

		Location location = locationManager.getLastKnownLocation(provider);// 通过GPS获取位置

		if (location == null){
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}

    /**离开程序**/
    public static void leave(Context context){
        new AlertDialog.Builder(context)
        .setMessage("你确定要关闭吗？")
        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
            }})
        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }})
        .show();
    }
	public static String getServerVersionLog(String _url) {
		log("1");
		String resultString = "";
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(_url);
		try {
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = response.getEntity().getContent();
				resultString = inStream2String(is);
			}
			log("result is" + resultString);
		} catch (Exception e) {
			e.printStackTrace();
			log("exception");
		} 
		return resultString;
	
	}
	/**
	 * 改变图片尺寸
	 * 
	 * @param makeWidth
	 * @param makeHeight
	 * @param makeBit
	 * @return
	 */
	public static Bitmap makeImg(int makeWidth, int makeHeight, Bitmap makeBit) {
		if (makeBit != null) {
			float scaleWidth = ((float) makeWidth) / makeBit.getWidth();
			float scaleHeight = ((float) makeHeight) / makeBit.getHeight();
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(makeBit, 0, 0,
					makeBit.getWidth(), makeBit.getHeight(), matrix, true);
			return resizedBitmap;
		} else {
			return null;
		}
	}
	private static void log(String string) {
		System.out.println("peter--"+string);
	}
}
