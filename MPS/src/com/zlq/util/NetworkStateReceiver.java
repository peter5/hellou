package com.zlq.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.zlq.mps.BrowsePage;

public class NetworkStateReceiver extends BroadcastReceiver {
	void log(String msg){
		System.out.println("peter--"+msg);
	}
	@Override
	public void onReceive(Context context, Intent arg1) {
//		Log.i(TAG, "network state changed.");
		if (!isNetworkAvailable(context)) {
			Toast.makeText(context, "network disconnected!", 0).show();
			BrowsePage browsePage = new BrowsePage();
			log("Network is not Available ");
			if (browsePage.projectSpinner!=null) {
				browsePage.projectSpinner.setEnabled(false);
				log("setEnabled(false)");
			}
		}
	}

	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}
}
