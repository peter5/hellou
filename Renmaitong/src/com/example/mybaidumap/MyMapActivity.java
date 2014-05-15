package com.example.mybaidumap;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.zlq.renmaitong.R;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MyMapActivity extends Activity {
	
	BMapManager mBMapMan = null;
	LocationListener mLocationListener = null;//createʱע���listener��Destroyʱ��ҪRemove
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBMapMan = new BMapManager(this);
		
		mBMapMan.init("C42D273D82F95D53DC10510D6F7A4B28061616B1", null);
//		mBMapMan.init("A00B1419B02BE3BA4F8F759165A66D83FCCCD9EE", null);
		
        // ע�ᶨλ�¼�
        mLocationListener = new LocationListener(){

//			@Override
//			public void onLocationChanged(Location arg0) {
//				if(arg0 == null){
//					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//				}else{
//					System.out.println(arg0.getLongitude()+">>>>>>>>>>>>>>>>>>>>>");
//				}
//				
//			}
			@Override
			public void onLocationChanged(Location location) {
				if(location != null){
					String strLog = String.format("����ǰ��λ��:\r\n" +
							"����:%f\r\n" +
							"γ��:%f",
							location.getLongitude(), location.getLatitude());

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
	    }
	    super.onResume();
	}
}
