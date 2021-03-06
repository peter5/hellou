package com.zlq.renmaitong;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class CopyOfMainActivity extends Activity {
	
	BMapManager mBMapMan = null;
	LocationListener mLocationListener = null;//create时注册此listener，Destroy时需要Remove
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baidumap);
		
		mBMapMan = new BMapManager(this);
		mBMapMan.init("A131B2C6BB55949031C6351E6A516A74268EF216", null);
		
		 System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> onCreate");
        // 注册定位事件
        mLocationListener = new LocationListener(){
			@Override
			public void onLocationChanged(Location location) {
				if(location != null){
					String strLog = String.format("您当前的位置:\r\n" +
							"经度:%f\r\n" +
							"纬度:%f",
							location.getLongitude(), location.getLatitude());

					System.out.println("");
					TextView mainText = (TextView)findViewById(R.id.textview);
			        mainText.setText(strLog);
			        System.out.println(strLog+"==========================");
				}
			}
        };
		
	}
 
	@Override
	protected void onDestroy() {
	    if (mBMapMan != null) {
	    	mBMapMan.getLocationManager().removeUpdates(mLocationListener);
	        mBMapMan.destroy();
	        mBMapMan = null;
	    }
	    super.onDestroy();
	}
	@Override
	protected void onPause() {
	    if (mBMapMan != null) {
	    	mBMapMan.getLocationManager().removeUpdates(mLocationListener);
	        mBMapMan.stop();
	    }
	    super.onPause();
	}
	@Override
	protected void onResume() {
	    if (mBMapMan != null) {
	    	mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
	        mBMapMan.start();
	        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> is  not null");
	    }else {
	    	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> is null");
	    }
	    super.onResume();
	}
}
