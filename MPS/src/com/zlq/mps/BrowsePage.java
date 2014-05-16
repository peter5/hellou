package com.zlq.mps;

import io.vov.vitamio.R;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import javax.security.auth.PrivateCredentialPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityGroup;
import android.app.Application;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdf.MuPDFActivity;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zlq.adapter.NameAdapter;
import com.zlq.json.JsonParser;
import com.zlq.renmaitong.InformationReport;
import com.zlq.util.MyDatabaseHelper;
import com.zlq.util.MyUtil;
import com.zlq.util.PictuerCompress;
import com.zlq.util.SystemConfig;


public class BrowsePage extends ActivityGroup {

	//2013-06-16 start
//	public LocationClient mLocationClient = null;
//	public BDLocationListener myListener = new MyLocationListenner();
	
//	private final String TAG = "BrowsePage";
//	private BDLocation mLocation ;
	//2013-06-16 end
	
	
	private boolean flag_resume = false;
	private boolean scheduleAlready = false;
	int v_height;
	int v_width;
	static int flag = 0;
	private boolean isFirst = true;
	public static ProgressDialog progressDialog;
	private String message;
	
	private boolean ZLisChecked = false;
	NetworkStateReceiver networkStateReceiver;
	
	
	ArrayAdapter<String> adapter;
	ArrayAdapter<String> nameAdapter;
	NameAdapter videoAdapter, documentAdapter;

	private String fileName;

	TextView reUpTv;
	public Spinner projectSpinner;
	private LinearLayout AddLayout;
	private ListView addVideoListview, addDocumentListview;
	LayoutInflater inflater;

	// 视频播放
	LinearLayout addView;

	SurfaceView mVideoView;

	// index
	private int namePosition = 0;
	public static int currentPhotoIndex;
	public static int currentVideoIndex;
	public static String videoName = "";

	// 照相
	private SurfaceView photoSurfaceView;
	private SurfaceHolder photoSurfaceHolder;
	private Camera photoCamera;
	private boolean photoPreviewRunning;
	TimerTask photoTask;
	Timer photoTimer;
	final Handler photoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				log("take picture");
				photoCamera.takePicture(null, null, photoCallback);
			}
		}
	};
	
	/**
	 * 拍照的回调接口
	 */
	PictureCallback photoCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.i("test peter", "...onPictureTaken...");
			if (data != null) {
//				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
//						data.length);
				Options opts = new Options();
				opts.inSampleSize = 4;
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length, opts);
				try {
					final String fileName = "photo_"
							+ SystemConfig.projectArrayList.get(namePosition)
									.get("number") + "_"
							+ String.format("%03d", currentPhotoIndex) + ".jpg";
					FileUtils.saveMyBitmap(fileName, bitmap);
					FileUtils.doBackup(Environment
							.getExternalStorageDirectory().getPath()
							+ "/mps/"
							+ fileName);
					if (MyUtil.isNetworkAvailable(BrowsePage.this)) {
					// 上传
					new Thread() {
						public void run() {
							try {
//								uploadPhoto(fileName,SystemConfig.projectArrayList.get(namePosition)
//										.get("number"));
								uploadPhoto(fileName,getProjectNum(fileName));
							} catch (Exception e) {
							}
						}
					}.start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				photoCamera.startPreview();
				photoPreviewRunning = true;
				currentPhotoIndex++;
				
				//start 2013-05-12 上午
//				project.put("photoIndex",
//						temp.getString("photoIndex"));
				SystemConfig.projectArrayList.get(namePosition).put("photoIndex",
						currentPhotoIndex+"");
				// end
				save2Preferences_photo(SystemConfig.projectArrayList
						.get(namePosition)
						.get("number"), currentPhotoIndex+"");
				
//				FileUtils fff = new FileUtils();
//				FileUtils.get_photoFile(photoList, new File(Environment.getExternalStorageDirectory().getPath()+ "/mps"));
				log("currentPhotoIndex is "+currentPhotoIndex);
			}

		}

	};
	/**
	 * 在相机快门关闭时候的回调接口，通过这个接口来通知用户快门关闭的事件，
	 * 普通相机在快门关闭的时候都会发出响声，根据需要可以在该回调接口中定义各种动作， 例如：使设备震动
	 */
	ShutterCallback mShutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.i("test peter", "...onShutter...");
		}
	};
	Callback photoSurfaceHolderCallback = new Callback() {
		// 照相
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (photoPreviewRunning) {
				photoCamera.stopPreview();
			}
			try {
				photoCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			photoCamera.startPreview();
			photoPreviewRunning = true;

			Log.i("test peter", "..--------------------.surfaceChanged...");
		}

		public void surfaceCreated(SurfaceHolder holder) {
			photoCamera = Camera.open();
			Log.i("test peter", "--------------------surfaceCreated is called");
		}

		public void surfaceDestroyed(SurfaceHolder holder) {

			log("photo surfaceDestroyed is called");
			if (photoCamera != null) {
				photoPreviewRunning = false;
				photoCamera.stopPreview();
				photoCamera.setPreviewCallback(null);
				photoCamera.release();
				photoCamera = null;
			}
		}
	};

	LinearLayout recordLayout;
	LocalActivityManager m;

	private Timer mvideoTimer;
	private Timer videoTimer;
	private TimerTask videoTask;
	private int timeSize = 0;
	public MovieRecorder2 mRecorder;

	private SurfaceView videoSurfaceView;
	private SurfaceHolder videoSurfaceHolder;

	String getProjectNum(String fName){
		 String[] ss = fName.split("_");
		 log("ss[1] is "+ss[1]);
		return ss[1];
	}
	final Handler videoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				mRecorder.stopRecording();
				setResult(RESULT_OK, (new Intent()).setAction("Corky!"));
				finish();
			} else if (msg.what == 2) {

				final String fileName = "video_"
						+ SystemConfig.projectArrayList.get(namePosition).get(
								"number") + "_"
						+ String.format("%03d", currentVideoIndex) + ".mp4";

//				System.out.println("videoSurfaceView.getHeight()---"+videoSurfaceView.getHeight());
				mRecorder.startRecording(videoSurfaceView, fileName);
				mRecorder.isRecording = true;
				timeSize = 0;
				videoTimer = new Timer();
				videoTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						timeSize++;

						log("==================videoRecordTime===*****======="
								+ Integer
										.parseInt(SystemConfig.videoRecordTime));
						int videoRecordTime = Integer
								.parseInt(SystemConfig.videoRecordTime);
						if (SystemConfig.videoRecordTime.equals("")
								|| SystemConfig.videoRecordTime == null) {
							videoRecordTime = 9;
						}
						log("..videoHandler.--------------------timeSize"+ timeSize);
						if (timeSize > videoRecordTime + 1) {
						//	mRecorder.stopRecording();
							videoTimer.cancel();
							if (mRecorder != null) {
								if (mRecorder.isRecording == true) {
									mRecorder.stopRecording();
								}
								mRecorder.release();
							}
							FileUtils.doBackup(Environment
									.getExternalStorageDirectory().getPath()
									+ "/mps/"
									+ fileName);
							if (MyUtil.isNetworkAvailable(BrowsePage.this)) {
							// 上传视频
							new Thread() {
								public void run() {
									try {
										uploadVideo(fileName,getProjectNum(fileName));
									} catch (Exception e) {
									}
								}
							}.start();
							}
							currentVideoIndex++;
							//start 2013-05-12 上午
//							project.put("photoIndex",
//									temp.getString("photoIndex"));
							SystemConfig.projectArrayList.get(namePosition).put("videoIndex",
									currentVideoIndex+"");
							// end
							save2Preferences_video(SystemConfig.projectArrayList
									.get(namePosition)
									.get("number")+"v", currentVideoIndex+"");
							log("currentVideoIndex1 is "+currentVideoIndex);
							
						}
					}
				}, 0, 1000);
			} else if (msg.what == 3) {
				// tv.setText("视频 "+5+" 秒已经录制 "+timeSize+"秒 ");
			}
		}
	};
	Callback videoSurfaceHolderCallback = new Callback() {
		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			videoSurfaceView = null;
			videoSurfaceHolder = null;
			mRecorder = null;
		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			videoSurfaceHolder = arg0;
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			videoSurfaceHolder = arg0;
		}
	};
	/////////////////////////////////////////////////////////////////////////

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				progressDialog.dismiss();
			} else if (msg.what == 2) {
				progressDialog.dismiss();
				Toast.makeText(BrowsePage.this, message, Toast.LENGTH_SHORT)
						.show();
			} else if (msg.what == 4) {
				progressDialog.dismiss();
				Toast.makeText(BrowsePage.this, "服务器超时", Toast.LENGTH_SHORT)
						.show();
			} else if (msg.what == 5) {
				progressDialog.dismiss();
				LocalActivityManager m = getLocalActivityManager();
				Intent intent = new Intent().setClass(BrowsePage.this,
						MuPDFActivity.class);
				Uri uri = Uri.parse(Environment.getExternalStorageDirectory()
						.getPath() + "/mps/" + fileName);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Window w = m.startActivity("pdf", intent);
				View v = w.getDecorView();
				AddLayout.addView(v);
			} else if (msg.what == 6) {
				// Toast.makeText(BrowsePage.this, "项目列表获取完成",
				// Toast.LENGTH_SHORT).show();
				flag_resume = true;
				nameAdapter = new ArrayAdapter<String>(BrowsePage.this,
						R.layout.simple_spinner_item);
				for (int i = 0; i < SystemConfig.projectArrayList.size(); i++) {
					nameAdapter.add(SystemConfig.projectArrayList.get(i).get(
							"name"));
				}

				nameAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				projectSpinner.setAdapter(nameAdapter);
				
				
				projectSpinner
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, final int position, long arg3) {
								
								
								if (isFirst == false) {
									progressDialog = ProgressDialog.show(
											BrowsePage.this, null, "获取资料数据..",
											true, true);
								}
								namePosition = position;
								
								currentPhotoIndex = Integer
										.parseInt(SystemConfig.projectArrayList
												.get(position)
												.get("photoIndex"));
//								currentPhotoIndex1 = Integer.parseInt(settings.getString(SystemConfig.projectArrayList
//										.get(position)
//										.get("number"), "1"));
								
								currentVideoIndex = Integer
										.parseInt(SystemConfig.projectArrayList
												.get(position)
												.get("videoIndex"));
								
//								currentVideoIndex1 = Integer.parseInt(settings.getString(SystemConfig.projectArrayList
//										.get(position)
//										.get("number")+"v", "1"));
					
								
								isFirst = false;
								AddLayout.removeAllViews();
								new Thread() {
									@Override
									public void run() {
										try {
											getProjectDataList(SystemConfig.projectArrayList
													.get(position)
													.get("number"));
										} catch (Exception e) {
											Log.d("zlq",
													"***************************"
															+ e.toString());
										}
									}
								}.start();
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
							}
						});
			} else if (msg.what == 7) {
				progressDialog.dismiss();
				// Toast.makeText(BrowsePage.this, "资料信息下载完成",
				// Toast.LENGTH_SHORT).show();
				updateView();
				// 录制视频开始
				if(ZLisChecked){
					handler.sendEmptyMessage(9);    //默认勾选
				}
			} else if (msg.what == 8) {

			} else if (msg.what == 9) {
				// 录制设置
				if (!SystemConfig.projectArrayList.isEmpty()) {
					log("++++++++++interval++"
							+ Integer.parseInt(SystemConfig.interval) * 60
							* 1000);
					int interval = Integer.parseInt(SystemConfig.interval) * 60 * 1000;
					if (SystemConfig.interval.equals("")
							|| SystemConfig.interval == null) {
						interval = 5 * 60 * 1000; // 间隔五分钟
					}

					if (SystemConfig.shootingMode.equals("photo")) {
						log("shootingMode is photo");
						 	   if (!scheduleAlready) {
//						 		   photoTimer.schedule(photoTask, 1 * 1000, 1000*30);
						 		   photoTimer.schedule(photoTask, 1 * 1000, interval);
						 		   scheduleAlready= true;
						 	   }

					} else if (SystemConfig.shootingMode.equals("video")) {
						log("shootingMode is video");
						if (!scheduleAlready) {
			    			   mvideoTimer.schedule(videoTask, 1 * 1000, interval);
			    			   scheduleAlready= true;
					 	   }  

					}
				}
			} else if (msg.what == 10) {
				progressDialog.dismiss();
				Toast.makeText(BrowsePage.this, "资料信息获取失败", Toast.LENGTH_SHORT)
						.show();
				if (!MyUtil.isNetworkAvailable(BrowsePage.this)) {
					if (addVideoListview != null) {
						addVideoListview.setAdapter(null);
					}
					if (addDocumentListview != null) {
						addDocumentListview.setAdapter(null);
					}
				}
				
			} else if (msg.what == 11) {
				progressDialog.dismiss();
				Toast.makeText(BrowsePage.this, "项目列表获取失败", Toast.LENGTH_SHORT)
						.show();
			} else if (msg.what == 12) {
				progressDialog.dismiss();
				Toast.makeText(BrowsePage.this, "项目列表为空", Toast.LENGTH_SHORT)
						.show();
			} else if (msg.what == 13) {
				progressDialog.dismiss();
				Toast.makeText(BrowsePage.this, "项目列表为空", Toast.LENGTH_SHORT)
						.show();
			} else if (msg.what == 14) {
				progressDialog.dismiss();
				Toast.makeText(BrowsePage.this, "pdf打开失败，请重试",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == 15) {
				progressDialog.dismiss();
				videoPlayer2(mVideoView, Environment
						.getExternalStorageDirectory().getPath()
						+ "/mps/"
						+ videoName);
			} else if (msg.what == 16) {
			} else if (msg.what == 19) {  //补传成功
				reUPnum++;
				threadEnd++;
				reUpTv.setText("补传现场拍摄的影像文件"+reUPnum+"个");
				log("------end---------start----------------"+threadEnd + "--"+threadStart);
				if(threadEnd == threadMax){
				threadEnd = 0;
				threadStart = 0;
				rupload();
				}
//				if (weiUPList.size()!=0 && weiUPList.size()==reUPnum) {
//					reUpTv.setText("补传完毕，本次补传"+reUPnum+"个");
//					reUpTv.setVisibility(View.INVISIBLE);
//					Toast.makeText(BrowsePage.this, "补传完毕，"+reUPnum+"个~", Toast.LENGTH_SHORT).show();
//				}
			}else if (msg.what == 20) { //补传失败
				threadEnd++;
				log("--------end--------------start------"+threadEnd + "--"+threadStart);
				if(threadEnd == threadMax){
					threadEnd = 0;
					threadStart = 0;
					rupload();
				}
//				if (weiUPList.size()!=0 && weiUPList.size()==reUPnum) {
//					reUpTv.setText("补传完毕，本次补传"+reUPnum+"个");
//					reUpTv.setVisibility(View.INVISIBLE);
//					Toast.makeText(BrowsePage.this, "补传完毕，"+reUPnum+"个~", Toast.LENGTH_SHORT).show();
//				}
			}
			
		}
	};
	final LinkedList<FileInfo> weiUPList = new LinkedList<BrowsePage.FileInfo>();
	 private void getVideo_photoFile(final LinkedList<FileInfo> list,File file){//获得文件
	    	
	    	file.listFiles(new FileFilter(){

				@Override
				public boolean accept(File file) {
					String name = file.getName();
					System.out.println("name----accept--------------------"+name);
					if(name.startsWith("video_")||name.startsWith("photo_"))
					{
					int i = name.indexOf('.');
					if(i != -1){
						name = name.substring(i);
						if(name.equalsIgnoreCase(".mp4")||name.equalsIgnoreCase(".jpg")){
							FileInfo mi = new FileInfo();
							mi.displayName = file.getName();
							mi.path = file.getAbsolutePath();
							list.add(mi);
							return true;
						}
					}
					}
					return false;
				}
	    	});
	    }
	public class FileInfo{
		String displayName;//名称  
		String path;//路径
	}
	protected void onResume() {
		log("onResume");
		super.onResume();
		
		
		
		
		if (ZLisChecked) {   //需要拍摄
			log("ZLisChecked --  onResume --"+	ZLisChecked);
			// 录像
			if (SystemConfig.shootingMode.equals("video")) {
				videoSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
	
				videoSurfaceView.setVisibility(View.VISIBLE);
	
				videoSurfaceHolder = videoSurfaceView.getHolder();
				videoSurfaceHolder.addCallback(videoSurfaceHolderCallback); // holder加入回调接口
	
				videoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				if (mRecorder != null) {
					if (mRecorder.isRecording == true) {
						mRecorder.stopRecording();
					}
					mRecorder.release();
				}
	//			log("new before");
				mRecorder = new MovieRecorder2();
	//			log("new after");
				
				videoTask = new TimerTask() {
					public void run() {
						videoHandler.sendEmptyMessage(2);
					}
				};
				mvideoTimer = new Timer(true);
			} else if (SystemConfig.shootingMode.equals("photo")) {
			    //照相
				photoSurfaceView = (SurfaceView) findViewById(R.id.camera);
				photoSurfaceView.setVisibility(View.VISIBLE);
	
				photoSurfaceHolder = photoSurfaceView.getHolder();
				photoSurfaceHolder.addCallback(photoSurfaceHolderCallback);
				photoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				photoTask = new TimerTask() {
					public void run() {
						log("photoTask ------------------------------------");
						photoHandler.sendEmptyMessage(1);
					}
				};
				photoTimer = new Timer(true);
			}
			log(flag_resume+"1");
			if(flag_resume){
				log(flag_resume+"2");
				handler.sendEmptyMessage(9);
			}
		}
	};
	LocationClientOption option = new LocationClientOption();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_page_black);
		
		settings = getSharedPreferences("setting", 0);
		Intent intent  = getIntent();
		ZLisChecked = intent.getBooleanExtra("zlisChecked", true);
		log("ZLisChecked---------------------------------"+ZLisChecked);
		
	    
	    
//		 监听 network
//		IntentFilter smsfilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");  
//		networkStateReceiver = new NetworkStateReceiver();
//		this.registerReceiver(networkStateReceiver, smsfilter); 
		 
		reUpTv = (TextView)findViewById(R.id.tv_reUp);
		if (ZLisChecked) {
			reUpTv.setVisibility(View.GONE);
		}else {
			reUpTv.setText("");
			reUpTv.setTextColor(Color.WHITE);
		}
		projectSpinner = (Spinner) findViewById(R.id.projectListSpinner);
		addVideoListview = (ListView) findViewById(R.id.addVideoLayout);
		addDocumentListview = (ListView) findViewById(R.id.addDocumentLayout);
		AddLayout = (LinearLayout) findViewById(R.id.AddLayout);

		inflater = LayoutInflater.from(this);
		recordLayout = (LinearLayout) findViewById(R.id.recordLayout);

		m = getLocalActivityManager();

		// loading数据
		progressDialog = ProgressDialog.show(BrowsePage.this, null, "获取项目中..",
				true, true);
		new Thread() {
			@Override
			public void run() {
				try {
					loadReportData();
				} catch (Exception e) {
				}
			}
		}.start();
		//补传
		if(!ZLisChecked){
			rupload();
		}
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyUtil.leave(BrowsePage.this,mRecorder);
			}
		});
	}
	
	int reUPnum= 0;
	int threadStart = 0;
	int threadEnd = 0;
	final int threadMax = 3;
	protected void rupload(){
		//上传未上传的
		log("weiUPList.size is ---before--------"+weiUPList.size());
		if (!weiUPList.isEmpty()) {
			weiUPList.clear();
		}
		getVideo_photoFile(weiUPList,new File(Environment.getExternalStorageDirectory().getPath()+ "/mps"));
		log("weiUPList.size is -----after------"+weiUPList.size());
		String nameString = "";
		
		for ( FileInfo f : weiUPList) {
			nameString = f.displayName;
			if (nameString.startsWith("video_")) {
				String[] ss = nameString.split("_");
				String proNum = ss[1];
				String fName = nameString;
				String fType = "1";
				log("********************"+ proNum + fName + fType);
				uploadVideo2(proNum, fName, fType);
				threadStart++;
//				log(threadStart+" is threadStart");
				log("循环：threadStart is "+threadStart);
				if (threadStart == threadMax) {
//					threadStart=0;
					return;
				}
			}else if (nameString.startsWith("photo_")) {
				String[] ss = nameString.split("_");
				String proNum = ss[1];
				String fName = nameString;
				String fType = "0";
				log("********************"+ proNum + fName + fType);
				uploadPhoto2(proNum, fName, fType);
				threadStart++;
//				log(threadStart+" is threadStart");
				log("循环：threadStart is "+threadStart);
				if (threadStart  == threadMax) {
//					threadStart = 0;
					return;
				}
			}
		}
		
		
		
	}


	private void uploadVideo2(final String projNumber, final String name,
			final String fileType) {
		new Thread() {
			@Override
			public void run() {
				try {
					String resultStr = "";
					JSONObject resultJSON;

					File video = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ "/mps/"
							+ name);
					if (video.exists()) {
						log(">>>>>>>>>>>>>>>>>>>"
								+ video.getName() + " " + video.exists()
								+ "  video.length() " + video.length());
						resultStr = JsonParser.UpLoadRes(SystemConfig.URL,
								"{'uploadfile':{'userName':'"
										+ SystemConfig.userName + "'}}", video);
						log("resultJSON.toString()" + resultStr);

						if (resultStr.equals("")) {
							handler.sendEmptyMessage(16);//
							handler.sendEmptyMessage(20);
						} else {
							resultJSON = new JSONObject(resultStr);
							String result = resultJSON.getString("result");
							message = resultJSON.getString("message");
							if (result.equals("ok")) {
								uploadSuccess(projNumber,
										video.getAbsolutePath(), fileType);
							} else if (result.equals("error")) {
								handler.sendEmptyMessage(17);
								handler.sendEmptyMessage(20);
							}
						}
					} else {
						handler.sendEmptyMessage(9);
						handler.sendEmptyMessage(20);
					}
				} catch (Exception e) {
					log( "***************************" + e.toString());
					e.printStackTrace();
					handler.sendEmptyMessage(20);
				}
			}
		}.start();

	}

	private void uploadPhoto2(final String projNumber, final String Name,
			final String fileType) {

		final String path = Environment.getExternalStorageDirectory().getPath()
				+ "/mps/" + Name;
		final File test = new File(path);
		log(">>>>>>>>>>>>>>>>>>>>>>>>>>>test.exists  "
				+ test.exists() + "   test.length()" + test.length());
		log("start upload fileName" + path);
		if (test.exists()) {
			// progressDialog = ProgressDialog.show(InformationReport.this,
			// "正在上传照片...", "请稍等...", true, true);
			new Thread() {
				@Override
				public void run() {
					try {
						String resultStr = "";
						JSONObject resultJSON;

						//File picture = new File(path);
						
						File picture = PictuerCompress.compFile(path);
						log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
								+ "picture.length()" + picture.length());
						resultStr = JsonParser.UpLoadRes(SystemConfig.URL,
								"{'uploadfile':{'userName':'"
										+ SystemConfig.userName + "'}}",
								picture);
						log("resultJSON.toString()" + resultStr);

						if (resultStr.equals("")) {
							handler.sendEmptyMessage(16);//
							handler.sendEmptyMessage(20);
						} else {
							resultJSON = new JSONObject(resultStr);
							String result = resultJSON.getString("result");
							message = resultJSON.getString("message");
							if (result.equals("ok")) {
								uploadSuccess(projNumber, test.getAbsolutePath(), fileType);
							} else if (result.equals("error")) {
								handler.sendEmptyMessage(20);
							}
						}
					} catch (Exception e) {
						log(
								"***************************" + e.toString());
						e.printStackTrace();
						handler.sendEmptyMessage(20);
					}
				}
			}.start();
		} else {
			handler.sendEmptyMessage(13);
			handler.sendEmptyMessage(20);
		}

	}

	private void updateView() {
		final ArrayList<HashMap<String, String>> videoList = new ArrayList<HashMap<String, String>>();
		final ArrayList<HashMap<String, String>> documentList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < SystemConfig.projectDataList.size(); i++) {
			if (SystemConfig.projectDataList.get(i).get("dataType")
					.equals("视频")) {
				videoList.add(SystemConfig.projectDataList.get(i));
			}
		}
		for (int i = 0; i < SystemConfig.projectDataList.size(); i++) {
			if (SystemConfig.projectDataList.get(i).get("dataType")
					.equals("文档")) {
				documentList.add(SystemConfig.projectDataList.get(i));
			}
		}

//		System.out.println("SystemConfig.projectDataList"
//				+ SystemConfig.projectDataList.size());
		videoAdapter = new NameAdapter(BrowsePage.this, videoList);
		addVideoListview.setAdapter(videoAdapter);
		documentAdapter = new NameAdapter(BrowsePage.this, documentList);
		addDocumentListview.setAdapter(documentAdapter);
		addVideoListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {

				final String videoUrl = SystemConfig.DataUrl
						+ videoList.get(position).get("path") + "/"
						+ videoList.get(position).get("dataId") + ".flv";

				AddLayout.setBackgroundColor(Color.BLACK);
				AddLayout.removeAllViews();
			
				AddLayout.refreshDrawableState();

//				System.out.println("videoUrl"
//						+ ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + videoUrl);
				videoName = videoUrl.substring(videoUrl.lastIndexOf('/') + 1,
						videoUrl.length());
				final File videoFile = new File(Environment
						.getExternalStorageDirectory().getPath()
						+ "/mps/"
						+ videoName);

				if (videoFile.exists() && videoFile.length() == 0) {
					videoFile.delete();
				}
				FileUtils fileUtils = new FileUtils();

				if (fileUtils.isFileExist("mps/" + videoName)) {
//					System.out.println(videoName+" 文件已经存在");
					videoPlayer2(mVideoView, Environment
							.getExternalStorageDirectory().getPath()
							+ "/mps/"
							+ videoName);
					
//					final long avalableSD = fileUtils.getSDAvailaleSize();
//					final long allSDsize = fileUtils.getSDAllSize();
//					System.out.println("avalableSD--"+avalableSD+"MB***allSDsize--"+allSDsize+"MB");
					
				} else {
					
					final long avalableSD = FileUtils.getSDAvailaleSize();
//					final long allSDsize = fileUtils.getSDAllSize();
//					System.out.println("avalableSD--"+avalableSD+"MB***allSDsize--"+"MB");
					if (avalableSD<1024) {//SDcard 可用容量小于1024MB
						Toast.makeText(BrowsePage.this, "机器存储空间不足，请清除不需要的文件", Toast.LENGTH_LONG).show();
					}else {
						if (!MyUtil.isNetworkAvailable(BrowsePage.this)) {
							Toast.makeText(BrowsePage.this,"请接通网络下载", Toast.LENGTH_SHORT).show();
							return;
						}
						progressDialog = ProgressDialog.show(BrowsePage.this, null,
								"正在下载请稍后..", true, true);
						new Thread() {
							@Override
							public void run() {
								try {
									boolean flag = Downloader.DownloadFile(
											videoUrl, new File(Environment
													.getExternalStorageDirectory()
													.getPath()
													+ "/mps/" + videoName));
									// Toast.makeText(BrowsePage.this, "flag"+flag,
									// Toast.LENGTH_SHORT).show();
	//								System.out
	//										.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>flag"
	//												+ flag);
									if (flag == true) {
	//									System.out
	//											.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>flag"
	//													+ flag);
										handler.sendEmptyMessage(15);
										
									} else {
									}
	
								} catch (Exception e) {
								}
							}
						}.start();
					}
				}
				if (MyUtil.isNetworkAvailable(BrowsePage.this)) {
					new Thread() {
						@Override
						public void run() {
							try {
								updateRecord(
										SystemConfig.projectArrayList.get(
												namePosition).get("number"),
										videoList.get(position).get("dataId"),
										SystemConfig.userName, SystemConfig.token);
							} catch (Exception e) {
								Log.d("zlq",
										"***************************"
												+ e.toString());
							}
						}
					}.start();
				}
			}
		});
		addDocumentListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final String url = SystemConfig.DataUrl
						+ documentList.get(position).get("path") + "/"
						+ documentList.get(position).get("dataId") + ".pdf";
				fileName = documentList.get(position).get("dataId") + ".pdf";
//				System.out.println(url);
				AddLayout.removeAllViews();
				AddLayout.refreshDrawableState();
				final File pdfFile = new File(Environment
						.getExternalStorageDirectory().getPath()
						+ "/mps/"
						+ fileName);
				if (pdfFile.exists() && pdfFile.length() == 0) {
					pdfFile.delete();
				}
				FileUtils fileUtils = new FileUtils();
				if (fileUtils.isFileExist("mps/" + fileName)) {
//					System.out.println("文件已经存在");
					LocalActivityManager m = getLocalActivityManager();
					Intent intent = new Intent().setClass(BrowsePage.this,
							MuPDFActivity.class);
					Uri uri = Uri.parse(Environment
							.getExternalStorageDirectory().getPath()
							+ "/mps/"
							+ fileName);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(uri);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Window w = m.startActivity("pdf", intent);
					View v = w.getDecorView();
					AddLayout.addView(v);
				} else {
					
					log("not exist document");
					
					if (!MyUtil.isNetworkAvailable(BrowsePage.this)) {
						Toast.makeText(BrowsePage.this,"请接通网络下载", Toast.LENGTH_SHORT).show();
					    return;
					}
					progressDialog = ProgressDialog.show(BrowsePage.this, null,
							"获取数据..", true, true);
					new Thread() {
						@Override
						public void run() {
							try {
								boolean flag = Downloader.DownloadFile(url,
										new File(Environment
												.getExternalStorageDirectory()
												.getPath()
												+ "/mps/" + fileName));
								if (flag == true) {
									handler.sendEmptyMessage(5);
								} else {
									if (pdfFile.exists()) {
										pdfFile.delete();
									}
									handler.sendEmptyMessage(5);
								}

							} catch (Exception e) {
								Log.d("zlq",
										"***************************"
												+ e.toString());
								handler.sendEmptyMessage(5);
							}
						}
					}.start();
				}
				if (MyUtil.isNetworkAvailable(BrowsePage.this)) {
					new Thread() {
						@Override
						public void run() {
							try {
								updateRecord(
										SystemConfig.projectArrayList.get(
												namePosition).get("number"),
										documentList.get(position).get("dataId"),
										SystemConfig.userName, SystemConfig.token);
							} catch (Exception e) {
								Log.d("zlq",
										"***************************"
												+ e.toString());
							}
						}
					}.start();
				}
			}
		});
	}
	
	
	
	
	private SharedPreferences settings ;
	void save2Preferences_photo(String projectNum ,String photoIndex){
		SharedPreferences.Editor editor = settings.edit();
//        editor.putString("projectNum", projectNum);
        editor.putString(projectNum, photoIndex);//
        editor.commit();
	}
	void save2Preferences_video(String projectNum ,String videoIndex){
		SharedPreferences.Editor editor = settings.edit();
//        editor.putString("projectNum", projectNum);
        editor.putString(projectNum, videoIndex);//
        editor.commit();
	}
	
	private void loadReportData() {
		
		String resultStr = "";
		JSONObject resultJSON;

		String result = "";
		int number;
		String msgStr = "";
		try {
			if (!MyUtil.isNetworkAvailable(BrowsePage.this)) {
				//断网
				resultStr = MyUtil.getFromPreference(BrowsePage.this,MyUtil.KEY_PROJECT_LIST);
			}else {
				//有网
				// {"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
				msgStr = "{'getMyProjectList':{'userName':'"
						+ SystemConfig.userName + "'}, 'token':'"
						+ SystemConfig.token + "'}";
				resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
				if (!resultStr.equals("")) {
					MyUtil.saveJson2preference(BrowsePage.this, MyUtil.KEY_PROJECT_LIST, resultStr);
				}
			}
			log("getMyProjectList is "+resultStr);
			if (resultStr.equals("")) {
				handler.sendEmptyMessage(11);
			} else {
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if (result.equals("ok")) {
					number = resultJSON.getInt("number");
					if (number != 0) {
						JSONObject projectList = resultJSON
								.getJSONObject("myProjectList");
						JSONArray projectArray = projectList.names();
						SystemConfig.projectArrayList = new ArrayList<HashMap<String, String>>();
						for (int i = 0; i < projectArray.length(); i++) {
							JSONObject temp = projectList
									.getJSONObject(projectArray.getString(i));
							HashMap<String, String> project = new HashMap<String, String>();
							project.put("type", temp.getString("projType"));
							project.put("number", temp.getString("projNum"));
							project.put("name", temp.getString("projName"));
							project.put("photoIndex",
									temp.getString("photoIndex"));
							project.put("videoIndex",
									temp.getString("videoIndex"));
							
//							String viIndex = settings.getString("videoIndex", "-1");
//							int videx = Integer.parseInt(viIndex);
							
							int localPhdex = Integer.parseInt(settings.getString(temp.getString("projNum"), "-1"));
							
							int serverPhdex = Integer.parseInt(temp.getString("photoIndex"));
							
							log("localPhdex is *********  "+localPhdex);
							log("serverPhdex is *********  "+serverPhdex);
							if (localPhdex < serverPhdex) {
								save2Preferences_photo(temp.getString("projNum"),temp.getString("photoIndex"));
							}else{
								project.put("photoIndex",""+localPhdex);
							}
							
							int localVidex = Integer.parseInt(settings.getString(temp.getString("projNum")+"v", "-1"));
							
							int serverVidex = Integer.parseInt(temp.getString("videoIndex"));
							
							log("localVidex is ********* "+localVidex);
							log("serverVidex is ********* *********  "+serverVidex);
							if (localVidex < serverVidex) {
								save2Preferences_video(temp.getString("projNum")+"v",temp.getString("videoIndex"));
							}else{
								project.put("videoIndex",""+localVidex);
							}
							
							SystemConfig.projectArrayList.add(project);
						}
						handler.sendEmptyMessage(6);
					} else {
						handler.sendEmptyMessage(12);
					}
				} else if (result.equals("error")) {
					handler.sendEmptyMessage(11);
				}
			}
		} catch (JSONException e) {
			handler.sendEmptyMessage(11);
			e.printStackTrace();
		}
	
	}

	public static Intent muPdf(Context context, String param) {
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ param);
		if (file.exists()) {
//			System.out.println("文件存在");
		} else {
//			System.out.println("文件不存在");
		}
//		System.out.println(context.getFilesDir().toString());

		Uri uri = Uri.parse(param);
		Intent intent = new Intent(context, MuPDFActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(uri);
		return intent;
	}

	public static Intent viewIntenertPdf(Context context, String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(context, MuPDFActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(uri);
		return intent;
	}

	private void getProjectDataList(final String projNum) {
		String resultStr = "";
		JSONObject resultJSON;

		String result = "";
		int number;
		String msgStr = "";
		try {
			
			if (!MyUtil.isNetworkAvailable(BrowsePage.this)) {
				//断网
				resultStr = MyUtil.getFromPreference(BrowsePage.this,projNum);
			}else {
					//有网
				// {"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
				// {"getProjectDataList":{"projNum":项目编号},"token":令牌}
				msgStr = "{'getProjectDataList':{'projNum':'" + projNum
						+ "'}, 'token':'" + SystemConfig.token + "'}";
	
				resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
				if (!resultStr.equals("")) {//保存
					MyUtil.saveJson2preference(BrowsePage.this, projNum, resultStr);
				}
			}
//			System.out.println("数据返回值" + resultStr);
			if (resultStr.equals("")) {
				handler.sendEmptyMessage(10);
			} else {
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if (result.equals("ok")) {
					number = resultJSON.getInt("number");
					if (number != 0) {
						JSONObject projectList = resultJSON
								.getJSONObject("projectDataList");
						JSONArray projectArray = projectList.names();
						SystemConfig.projectDataList = new ArrayList<HashMap<String, String>>();
						for (int i = 0; i < projectArray.length(); i++) {
							JSONObject temp = projectList
									.getJSONObject(projectArray.getString(i));
							HashMap<String, String> project = new HashMap<String, String>();
							project.put("dataType", temp.getString("dataType"));
							project.put("dataName", temp.getString("dataName"));
							project.put("dataLength",
									temp.getString("dataLength"));
							project.put("dataId", temp.getString("dataId"));
							project.put("path", temp.getString("path"));
							project.put("userId", temp.getString("userId"));
							SystemConfig.projectDataList.add(project);
						}
					}
					handler.sendEmptyMessage(7);
				} else if (result.equals("error")) {
					handler.sendEmptyMessage(10);
				}
			}
		} catch (JSONException e) {
			handler.sendEmptyMessage(10);
			e.printStackTrace();
		}
	}

	private void updateRecord(String projNum, String dataId, String userName,
			String token) {
		// {"useData":{"projNum":项目编号,"dataId":资料主键值,"userName":用户名},"token":令牌}
		String resultStr = "";
		JSONObject resultJSON;

		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'useData':{'projNum':'" + projNum + "','dataId':'"
					+ dataId + "','userName':'" + userName + "'}, 'token':'"
					+ token + "'}";

			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
//			System.out.println("数据返回值" + resultStr);
			if (resultStr.equals("")) {
				
			} else {
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if (result.equals("ok")) {
				} else if (result.equals("error")) {
				} else {
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//
	private void uploadPhoto(final String ff,final String projectNum) {
//		final String fileName = Environment.getExternalStorageDirectory()
//				.getPath()
//				+ "/mps/photo_"
//				+ SystemConfig.projectArrayList.get(namePosition).get("number")
//				+ "_" + String.format("%03d", currentPhotoIndex1) + ".jpg";
		final String fileName = Environment.getExternalStorageDirectory()
				.getPath()
				+ "/mps/"+ff;
		
		File test = new File(fileName);
		log(">>>>>>>>>>>>>>>>>>>>>>>>>>>test.exists  "
				+ test.exists() + "   test.length()" + test.length());
		log("start upload fileName" + fileName);
		if (test.exists()) {
			// progressDialog = ProgressDialog.show(InformationReport.this,
			// "正在上传照片...", "请稍等...", true, true);
			new Thread() {
				@Override
				public void run() {
					try {
						String resultStr = "";
						JSONObject resultJSON;

						File picture = PictuerCompress.compFile(fileName);
						log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
								+ "picture.length()" + picture.length());
						resultStr = JsonParser.UpLoadRes(SystemConfig.URL,
								"{'uploadfile':{'userName':'"
										+ SystemConfig.userName + "'}}",
								picture);
						log("resultJSON.toString()" + resultStr);

						if (resultStr.equals("")) {
							handler.sendEmptyMessage(16);//
						} else {
							resultJSON = new JSONObject(resultStr);
							String result = resultJSON.getString("result");
							message = resultJSON.getString("message");
							if (result.equals("ok")) {
								uploadSuccess(projectNum,
										fileName, "0");
//								getGps();
							} else if (result.equals("error")) {
								// handler.sendEmptyMessage(15);
							}
						}
					} catch (Exception e) {
						Log.d("zlq",
								"***************************" + e.toString());
					}
				}
			}.start();
		} else {
			handler.sendEmptyMessage(13);
		}
	}

	private void uploadVideo(final String fileName,final String projectNum) {
		new Thread() {
			@Override
			public void run() {
				log("start upload video "+fileName);
				try {
					String resultStr = "";
					JSONObject resultJSON;

					File video = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ "/mps/"
							+ fileName);
					if (video.exists()) {
						log(">>>>>>>>>>>>>>>>>>>"
								+ video.getName() + " " + video.exists()
								+ "  video.length() " + video.length());
						resultStr = JsonParser.UpLoadRes(SystemConfig.URL,
								"{'uploadfile':{'userName':'"
										+ SystemConfig.userName + "'}}", video);
						log("resultJSON.toString()" + resultStr);

						if (resultStr.equals("")) {
							handler.sendEmptyMessage(16);//
						} else {
							resultJSON = new JSONObject(resultStr);
							String result = resultJSON.getString("result");
							message = resultJSON.getString("message");
							if (result.equals("ok")) {
								uploadSuccess(projectNum,video.getAbsolutePath(), "1");
								//2013-06-16 start
//								getGps();
								//2013-06-16 end
							} else if (result.equals("error")) {
								handler.sendEmptyMessage(17);
							}
						}
					} else {
						handler.sendEmptyMessage(9);
					}
				} catch (Exception e) {
					Log.d("zlq", "***************************" + e.toString());
				}
			}
		}.start();
	}
	// {"saveAttach":{"userName":用户名,"projNum":项目编号,"fileName":文件名,"attachType"文件类型},"token":令牌}
	private void uploadSuccess(String projNumber, String fileName,
			String fileType) {
		String resultStr = "";
		JSONObject resultJSON;
		File file = new File(fileName);

		String result = "";
		String msgStr = "";
		try {
			// {"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
			msgStr = "{'saveAttach':{'userName':'" + SystemConfig.userName
					+ "','projNum':'" + projNumber + "','fileName':'"
					+ file.getName() + "','attachType':'" + fileType
					+ "'},'token':'" + SystemConfig.token + "'}";

			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
			log("uploadSuccess------------"+resultStr);
			if (resultStr.equals("")) {
				handler.sendEmptyMessage(20);
			} else {
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if (result.equals("ok")) {
//					if (fileType.equals("0")) {
//						currentPhotoIndex++;
//					} else if (fileType.equals("1")) {
//						currentVideoIndex++;
//					}
					log(">>>>>>>>>>>>>>>file.exists()  "
							+ file.exists()+"fileName-"+file.getName()+"filePath"+file.getAbsolutePath());
					if (file.exists()) {
						if(file.delete()){
							if (!ZLisChecked) {
								handler.sendEmptyMessage(19);
							}
						}else {
							handler.sendEmptyMessage(20);
						}
					}else {
						handler.sendEmptyMessage(20);
					}
				}else {
					handler.sendEmptyMessage(20);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
				handler.sendEmptyMessage(20);
		}
	}

	
	void videoPlayer2(final SurfaceView videoView, String url) {
		AddLayout.removeAllViews();
		
		AddLayout.refreshDrawableState();
		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
			return;

		LocalActivityManager m = getLocalActivityManager();
		Intent intent = new Intent().setClass(BrowsePage.this,
				PMediaPlayer3.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra("url", url);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		Window w = m.startActivity("video", intent);
		View v = w.getDecorView();
		AddLayout.addView(v);

	}
	void log(String str){
		System.out.println("peter--"+str);
	}

	
	protected void onPause() {
		log("onPause");
		
		
		if (mRecorder != null) {
			if (mRecorder.isRecording == true) {
				mRecorder.stopRecording();
			}
			mRecorder.release();
		}
		if (photoTimer != null) {
			photoTimer.cancel();
		}
		if (videoTask != null) {
			videoTask.cancel();
		}
		if (videoTimer != null) {
			videoTimer.cancel();
		}
		if (mvideoTimer != null) {
			mvideoTimer.cancel();
		}
		scheduleAlready= false;
		super.onPause();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MyUtil.leave(BrowsePage.this,mRecorder);
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDestroy() {
//		System.out.println("onDestroy is called");
		super.onDestroy();
		m.destroyActivity("pdf", true);
		m.destroyActivity("video", true);
		m.removeAllActivities();
		if (photoTimer != null) {
			photoTimer.cancel();
		}
		
		if (videoTask != null) {
			videoTask.cancel();
		}
		if (videoTimer != null) {
			videoTimer.cancel();
		}
		if (mvideoTimer != null) {
			mvideoTimer.cancel();
		}
		if (mRecorder != null) {
			if (mRecorder.isRecording == true) {
				mRecorder.stopRecording();
			}
			mRecorder.release();
		}
		if (networkStateReceiver!=null) {
			this.unregisterReceiver(networkStateReceiver); 
		}
		
		
	}
	
	private class NetworkStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent arg1) {
//			Log.i(TAG, "network state changed.");
			if (!MyUtil.isNetworkAvailable(context)) {
				Toast.makeText(context, "network disconnected!", 0).show();
				log("Network is not Available ");
				if (projectSpinner!=null) {
					projectSpinner.setEnabled(false);
					log("setEnabled(false)");
				}
			}
		}
	}
	// 2013-06-16 start  上传视频 ，照片成功时，获取GPS并且上传
		void upGps() {
			if (MyUtil.isNetworkAvailable(BrowsePage.this)) {
				new Thread() {
					@Override
					public void run() {
						try {
							getGps();
						} catch (Exception e) {
							Log.d("zlq","***************************" + e.toString());
						}
					}
				}.start();
			}
		}// 2013-06-16 end
		 private void getGps(){
		    	LocationApp mApplication = (LocationApp) getApplication();
		    	BDLocation location = mApplication.getLocation();
		    	if(location != null){
		    		if(!SystemConfig.projectArrayList.isEmpty()){
		    			log("正在上传GPS"+location.getLatitude()+"  "+location.getLongitude());
		        		//上传信息{"sendGPS":{"projNum":"MT201212002","userName":"1001","xxx":"114.4546","yyy":"32.451245"},
		        		//"token":"402881ed3c376d5c013c38323cd1002c"}
		        		String resultStr = "";
		        		JSONObject resultJSON;
		        				
		        		String result = "";
		        		String msgStr = "";
		        		try {
		        			log(SystemConfig.projectArrayList.get(namePosition).get("number"));
		        			msgStr = "{'sendGPS':{'projNum':'"+SystemConfig.projectArrayList.get(namePosition).get("number")+
		        					"','userName':'"+SystemConfig.userName+"','xxx':'"+location.getLongitude()+
		        					"','yyy':'"+location.getLatitude()+"'},'token':'"+SystemConfig.token+"'}";
		        					
//		        			resultStr = JsonParser.getResponse(SystemConfig.GPS_URL, msgStr);
		        			log("------0521-----@peter--********************"+resultStr);
		        			if(resultStr.equals("")){
//		        				handler.sendEmptyMessage(4);//
		        			}else{
		        				resultJSON = new JSONObject(resultStr);
		        				result = resultJSON.getString("result");
		        				log("------0521-------********************"+result);
		        				message = resultJSON.getString("message");
		        				if(result.equals("ok")){
//		        					handler.sendEmptyMessage(5);
		        				}else if(result.equals("error")){
//		        					handler.sendEmptyMessage(2);
		        				}else{
//		        					handler.sendEmptyMessage(3);
		        				}
		        			}
		        		} catch (JSONException e) {
//		        			handler.sendEmptyMessage(4);
		        			e.printStackTrace();
		        		}
		    		}else{
//		    			handler.sendEmptyMessage(6);
		    		}
		    	}else{
//		    		handler.sendEmptyMessage(7);
		    	}
		    	
		    }
}
