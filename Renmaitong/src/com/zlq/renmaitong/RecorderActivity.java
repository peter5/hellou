package com.zlq.renmaitong;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zlq.util.SystemConfig;

public class RecorderActivity extends Activity {
	private static final String LOG_TAG = RecorderActivity.class.getSimpleName();
	Timer  mTimer;
	Timer timer;
	int timeSize = 0;
	MovieRecorder mRecorder;

	Button btnRecoder;

	ListView mListView1;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	TextView tv;

	 final Handler handler = new Handler(){     
	        @Override  
		    public void handleMessage(Message msg) {     
		        if(msg.what == 1){  
		        	mRecorder.stopRecording();
				    setResult(RESULT_OK, (new Intent()).setAction("Corky!"));
			        finish();
		        }else if(msg.what == 2){
		        	mRecorder.startRecording(surfaceView);
		    		mRecorder.isRecording = true;
		    		btnRecoder.setText("录制中，单击停止");
		    		timeSize = 0;
		    		timer = new Timer();
		    		timer.schedule(new TimerTask() {

		    			@Override
		    			public void run() {
		    				// TODO Auto-generated method stub
		    				timeSize++;
		    				handler.sendEmptyMessage(3);
		    				if(timeSize > Integer.parseInt(SystemConfig.videosecond)){
		    					mRecorder.stopRecording();
		    					timer.cancel();
		    				    setResult(RESULT_OK, (new Intent()).setAction("Corky!"));
		    			        finish();
		    				}
		    			}
		    		}, 0, 1000);
		        }else if(msg.what == 3){
		        	tv.setText("视频 "+SystemConfig.videosecond+" 秒已经录制 "+timeSize+"秒 ");
		        }
		    }   
		};  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		// 设置横屏显示
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 选择支持半透明模式,在有surfaceview的activity中使用。
		getWindow().setFormat(PixelFormat.TRANSLUCENT);

		setContentView(R.layout.recorder);

		btnRecoder = (Button) findViewById(R.id.btnRecoder);
		btnRecoder.setOnClickListener(mRecordingClick);
		tv = (TextView) findViewById(R.id.videoTimeTV);

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceHolderCallback); // holder加入回调接口
		// setType必须设置，要不出错.
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mRecorder = new MovieRecorder();
		//start
		
		TimerTask task = new TimerTask() {
	        public void run() {
	            handler.sendEmptyMessage(2);
	       }
	    };
	    Timer timer = new Timer(true);
	    timer.schedule(task, 300);//延迟1秒后执行
	
		
		//stop
//        mTimer = new Timer(true);
//        TimerTask mTimerTask;
//        mTimerTask = new TimerTask() {
//			@Override
//			public void run() {
//				if(mRecorder.isRecording == false){
//					 handler.sendEmptyMessage(1);
//					 System.out.println(mRecorder.isRecording +">>false");
//				}else{
//					 System.out.println(mRecorder.isRecording +">>true");
//				}
//			}
//		};
//		mTimer.schedule(mTimerTask, 3000, 100);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {// 拦截menu键事件
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 拦截返回按钮事件
			timer.cancel();
			finish();
		}
		return true;
	}

	protected void onDestroy() {
		if (mRecorder != null) {
			mRecorder.release();
		}
		super.onDestroy();
	};
	

	Callback surfaceHolderCallback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			surfaceView = null;
			surfaceHolder = null;
			mRecorder = null;
		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			surfaceHolder = arg0;
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			surfaceHolder = arg0;
		}
	};

	OnClickListener mRecordingClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (!mRecorder.isRecording) {
				mRecorder.startRecording(surfaceView);

				mRecorder.isRecording = true;
				btnRecoder.setText("录制中，单击停止");
			} else {
				mRecorder.stopRecording();
				mRecorder.isRecording = false;
				btnRecoder.setText("录制录音，单击开始");
			    setResult(RESULT_OK, (new Intent()).setAction("Corky!"));
		        finish();
			}

		}
	};
	
	   @Override 
	   public void onConfigurationChanged(Configuration newConfig){ 
	       super.onConfigurationChanged(newConfig); 
	    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
	    }
	    else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
	    }
	   }
}
