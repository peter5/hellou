package com.cn.rtmp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.shouyanwang.h264encoder;
import com.smaxe.io.ByteArray;
import com.smaxe.uv.client.INetStream;
import com.smaxe.uv.client.NetStream;
import com.smaxe.uv.client.camera.AbstractCamera;
import com.smaxe.uv.stream.support.MediaDataByteArray;
import com.zlq.json.JsonParser;
import com.zlq.renmaitong.InformationReport;
import com.zlq.renmaitong.R;
import com.zlq.util.SystemConfig;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class CameraVideoActivity extends Activity {
	private TextView hour; // 小时
	private TextView minute; // 分钟
	private TextView second; // 秒
	private Button mStart; // 开始按钮
	private Button mStop; // 结束按钮
	private Button mReturn; // 返回按钮
	private static AndroidCamera aCamera;
	private boolean streaming;
	private boolean isTiming = false;
	
	private boolean isStart = false;
	private ProgressDialog progressDialog; 
	private String message ;
	
	h264encoder encoder;
	long handle;
	
	private 
	final Handler handler2 = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CameraVideoActivity.this, message, Toast.LENGTH_SHORT).show();
		    	   myReturn();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CameraVideoActivity.this, "服务器超时", Toast.LENGTH_SHORT).show();
		    	   myReturn();
		       }else if(msg.what == 8){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CameraVideoActivity.this, "上传视频成功", Toast.LENGTH_SHORT).show();
		    	   myReturn();
		       }else if(msg.what == 9){
		    	   start();
		       }else if(msg.what == 10){
		    	   stop();
		       }
		}   
	};  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 设置屏幕为全屏
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video);

		init();

		encoder = new h264encoder ();
		
		TimerTask task = new TimerTask() {
	        public void run() {
	                Message message = new Message();
	                message.what = 9;
	                handler2.sendMessage(message);
	       }
	   };
	                                        
	    Timer timer = new Timer(true);
	    timer.schedule(task, 2000);//延迟5秒后执行
	}

	private void start(){
		if (streaming == false) {
			aCamera.start();
		}
		aCamera.startVideo();
		isTiming = true;
		handler.postDelayed(task, 1000);
		// 设置按钮状态
		mStart.setEnabled(false);
		mReturn.setEnabled(false);
		mStop.setEnabled(true);
		
		isStart = true;
	}
	
	private void stop(){
		aCamera.stop();
		// 设置按钮状态
		mStart.setEnabled(true);
		mReturn.setEnabled(true);
		mStop.setEnabled(false);
		isTiming = false;
		
		if(isStart == true){
			progressDialog = ProgressDialog.show(CameraVideoActivity.this, "正在上传视频中...", "请稍等...", true, false);  
			new Thread(){  
	            @Override  
	        public void run(){     
	           try{  
	        	   uploadSuccess(Main.projNumber, Main.fileName+".flv", "1");
	           }catch(Exception e) {  
	               Log.d("zlq", "***************************"+e.toString());  
	          }  
	         }  
	       }.start();  
		}
		isStart = false;
	}
	
	private void myReturn(){
		if (RTMPConnectionUtil.netStream != null) {
			RTMPConnectionUtil.netStream.close();
			RTMPConnectionUtil.netStream = null;
		}
		if (aCamera != null) {
			aCamera = null;
		}
		finish();
	}
	
	
	private void init() {
		aCamera = new AndroidCamera(CameraVideoActivity.this);
		hour = (TextView) findViewById(R.id.mediarecorder_TextView01);
		minute = (TextView) findViewById(R.id.mediarecorder_TextView03);
		second = (TextView) findViewById(R.id.mediarecorder_TextView05);
		mStart = (Button) findViewById(R.id.mediarecorder_VideoStartBtn);
		mStop = (Button) findViewById(R.id.mediarecorder_VideoStopBtn);
		mReturn = (Button) findViewById(R.id.mediarecorder_VideoReturnBtn);
		// 开始录像
		mStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (streaming == false) {
					aCamera.start();
				}
				aCamera.startVideo();
				isTiming = true;
				handler.postDelayed(task, 1000);
				// 设置按钮状态
				mStart.setEnabled(false);
				mReturn.setEnabled(false);
				mStop.setEnabled(true);
				
				isStart = true;
			}
		});
		mReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (RTMPConnectionUtil.netStream != null) {
					RTMPConnectionUtil.netStream.close();
					RTMPConnectionUtil.netStream = null;
				}
				if (aCamera != null) {
					aCamera = null;

				}
				finish();
			}
		});
		mStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				aCamera.stop();
				// 设置按钮状态
				mStart.setEnabled(true);
				mReturn.setEnabled(true);
				mStop.setEnabled(false);
				isTiming = false;
				
				if(isStart == true){
					progressDialog = ProgressDialog.show(CameraVideoActivity.this, "正在上传视频中...", "请稍等...", true, false);  
					new Thread(){  
			            @Override  
			        public void run(){     
			           try{  
			        	   uploadSuccess(Main.projNumber, Main.fileName, "1");
			           }catch(Exception e) {  
			               Log.d("zlq", "***************************"+e.toString());  
			          }  
			         }  
			       }.start();  
				}
				
				isStart = false;
			}
		});
	}

	   //{"saveAttach":{"userName":用户名,"projNum":项目编号,"fileName":文件名,"attachType"文件类型},"token":令牌}
	   private void uploadSuccess(String projNumber, String fileName, String fileType){
			String resultStr = "";
			JSONObject resultJSON;
			
			String result = "";
			String msgStr = "";
			try {
				//{"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
				msgStr = "{'saveAttach':{'userName':'"+SystemConfig.userName+"','projNum':'"+projNumber+
						"','fileName':'"+fileName+"','attachType':'"+fileType+"'},'token':'"+SystemConfig.token+"'}";
				
				resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
				System.out.println(resultStr);
				if(resultStr.equals("")){
					handler2.sendEmptyMessage(4);//
				}else{
					resultJSON = new JSONObject(resultStr);
					result = resultJSON.getString("result");
					message = resultJSON.getString("message");
					if(result.equals("ok")){
						handler2.sendEmptyMessage(8);//UI线程外想更新UI线程  
					}else if(result.equals("error")){
						handler2.sendEmptyMessage(2);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	   }
	
	
	public class AndroidCamera extends AbstractCamera implements
			SurfaceHolder.Callback, Camera.PreviewCallback {

		private SurfaceView surfaceView;
		private SurfaceHolder surfaceHolder;
		private Camera camera;

		private int width;
		private int height;

		private boolean init;

		int blockWidth;
		int blockHeight;
		int timeBetweenFrames; // 1000 / frameRate
		int frameCounter;
		byte[] previous;

		public AndroidCamera(Context context) {

			surfaceView = (SurfaceView) findViewById(R.id.mediarecorder_Surfaceview);
			surfaceView.setVisibility(View.VISIBLE);
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(AndroidCamera.this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			width = 352 / 2;
			height = 288 / 2;

			init = false;
			Log.d("DEBUG", "AndroidCamera()");
		}

		private void startVideo() {
			Log.d("DEBUG", "startVideo()");

			RTMPConnectionUtil.ConnectRed5(CameraVideoActivity.this);
			RTMPConnectionUtil.netStream = new UltraNetStream(
					RTMPConnectionUtil.connection);
			RTMPConnectionUtil.netStream
					.addEventListener(new NetStream.ListenerAdapter() {

						@Override
						public void onNetStatus(final INetStream source,
								final Map<String, Object> info) {
							Log.d("DEBUG", "Publisher#NetStream#onNetStatus: "
									+ info);

							final Object code = info.get("code");

							if (NetStream.PUBLISH_START.equals(code)) {
								if (CameraVideoActivity.aCamera != null) {
									RTMPConnectionUtil.netStream
											.attachCamera(aCamera, -1 /* snapshotMilliseconds */);
									Log.d("DEBUG", "aCamera.start()");
									aCamera.start();
								} else {
									Log.d("DEBUG", "camera == null");
								}
							}else{
								//RTMP服务器不通
//								setResult(RESULT_OK, (new Intent()).setAction("netError"));
								Toast.makeText(CameraVideoActivity.this, "连接RTMP服务器失败", Toast.LENGTH_SHORT).show();
							}
						}
					});

			RTMPConnectionUtil.netStream.publish(Main.fileName, NetStream.RECORD);
		}

		public void start() {
			camera.startPreview();
			streaming = true;
		}

		public void stop() {
			camera.stopPreview();
			streaming = false;
		}

		public void printHexString(byte[] b) {
			for (int i = 0; i < b.length; i++) {
				String hex = Integer.toHexString(b[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}

			}
		}

		@Override
		public void onPreviewFrame(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub
			// if (!active) return;
			if (!init) {
				blockWidth = 32;
				blockHeight = 32;
				timeBetweenFrames = 100; // 1000 / frameRate
				frameCounter = 0;
				previous = null;
				init = true;
			}
			final long ctime = System.currentTimeMillis();

//			byte[] current = new byte[16*1024];
//			int result = encoder.encodeframe(handle, -1, arg0, arg0.length, current);
			
			/** 将采集的YUV420SP数据转换为RGB格式 */
			byte[] current = RemoteUtil.decodeYUV420SP2RGB(arg0, width, height);
			
			try {
				final byte[] packet = RemoteUtil.encode(current, previous,
						blockWidth, blockHeight, width, height);
				fireOnVideoData(new MediaDataByteArray(timeBetweenFrames,
						new ByteArray(packet)));
				previous = current;
				if (++frameCounter % 10 == 0)
					previous = null;

			} catch (Exception e) {
				e.printStackTrace();
			}
			final int spent = (int) (System.currentTimeMillis() - ctime);
			try {

				Thread.sleep(Math.max(0, timeBetweenFrames - spent));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// TODO Auto-generated method stub
			// camera.startPreview();
			// camera.unlock();

			// Log.d("DEBUG", "surfaceChanged()");

			// startVideo();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			camera = Camera.open();
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.setPreviewCallback(this);
				Camera.Parameters params = camera.getParameters();
				params.setPreviewSize(width, height);
				camera.setParameters(params);
				handle=encoder.initEncoder(width, height);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				camera.release();
				camera = null;
			}

			Log.d("DEBUG", "surfaceCreated()");
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if (camera != null) {
				encoder.destory(handle);
				camera.stopPreview();
				camera.release();
				camera = null;
				
			}

		}

	}

	/* 定时器设置，实现计时 */
	private Handler handler = new Handler();
	int s, h, m, s1, m1, h1;
	private Runnable task = new Runnable() {
		public void run() {
			if (isTiming) {
				handler.postDelayed(this, 1000);
				s++;
				
				if(s == Integer.parseInt(SystemConfig.videosecond)){
					handler2.sendEmptyMessage(10);
					System.out.println(">>>>>>>>>>>>>>>>>>time out"+s);
				}else{
					System.out.println(">>>>>>>>>>>>>>>>>>time"+s);
				}
				if (s < 60) {
					second.setText(format(s));
				} else if (s < 3600) {
					m = s / 60;
					s1 = s % 60;
					minute.setText(format(m));
					second.setText(format(s1));
				} else {
					h = s / 3600;
					m1 = (s % 3600) / 60;
					s1 = (s % 3600) % 60;
					hour.setText(format(h));
					minute.setText(format(m1));
					second.setText(format(s1));
				}
			}
		}
	};

	/* 格式化时间 */
	public String format(int i) {
		String s = i + "";
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;
	}
}
