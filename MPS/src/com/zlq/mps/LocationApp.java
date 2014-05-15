package com.zlq.mps;

import android.app.Application;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationApp extends Application{

	public LocationClient mLocationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	public static String TAG = "LocationApp";
	private BDLocation bdLocation ;
	
	@Override//2013-06-16 start
	public void onCreate() {
//		mLocationClient = new LocationClient( this );
//		mLocationClient.registerLocationListener( myListener );
//		
//			    setLocationOption();
//			    mLocationClient.start();
//			    int re =  mLocationClient.requestLocation();
//			    Log.d(TAG,"re is oncreate "+re);
//			    
//			    Log.d(TAG,"mLocationClient.isStarted() is "+mLocationClient.isStarted());
		super.onCreate(); 
//		Log.d(TAG, "... LocationApp onCreate... pid=" + Process.myPid());
	}
	
	public BDLocation getLocation (){
		return bdLocation;
	}
	@Override
	public void onTerminate() {
		mLocationClient.stop();
		super.onTerminate();
	}
	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
//		private int ffff = 0;
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			bdLocation = location;
			Log.d(TAG,"mLocationClient.isStarted() is "+mLocationClient.isStarted());
			Log.d(TAG,"===============error code is "+location.getLocType());
			Log.d(TAG,"===============lat is "+location.getLatitude());
			Log.d(TAG,"===============lon is "+location.getLongitude());
//			Toast.makeText(getApplicationContext(), location.getLatitude()+"--"+mLocationClient.isStarted()+location.getLongitude()+"--"+location.getAltitude(), Toast.LENGTH_SHORT).show();
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ; 
			}
		}
	}
	//设置相关参数
		private void setLocationOption(){
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);				//打开GPS
			option.setCoorType("bd09ll");		//设置坐标类型
//			option.setServiceName("com.baidu.location.service_v2.9");
			option.setPoiExtraInfo(false);	
			option.setAddrType("");
			option.setScanSpan(8000);	//设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
			
//			option.setPriority(LocationClientOption.NetWorkFirst);      //设置网络优先
			option.setPriority(LocationClientOption.GpsFirst);        //设置GPS优先(不设置，默认是GPS优先)

			option.setPoiNumber(0);
			option.disableCache(true);		
			mLocationClient.setLocOption(option);
		}
}
