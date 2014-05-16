package com.zlq.mps;

import io.vov.vitamio.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
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
import android.widget.Toast;

import com.artifex.mupdf.MuPDFActivity;
import com.zlq.adapter.NameAdapter;
import com.zlq.json.JsonParser;
import com.zlq.util.MyUtil;
import com.zlq.util.PictuerCompress;
import com.zlq.util.SystemConfig;

public class BrowsePage2 extends ActivityGroup{

	private boolean scheduleAlready = false;
	int v_height ;
	int v_width;
	static int flag=0;
	private boolean isFirst = true;
	public static ProgressDialog progressDialog; 
	private String message ;
	
	ArrayAdapter<String> adapter;
	ArrayAdapter<String> nameAdapter;
	NameAdapter videoAdapter, documentAdapter;
	
	private String fileName;
	
	private Spinner projectSpinner;
	private LinearLayout  AddLayout;
	private ListView addVideoListview, addDocumentListview;
	LayoutInflater inflater;
//	private ImageButton exitBtn;
	
	//视频播放
	LinearLayout addView;
//	VideoView mVideoView;
	SurfaceView mVideoView;
	
	//index
	private int namePosition = 0;
	public static int currentPhotoIndex;
	public static int currentVideoIndex;
	public static String videoName = "";
	
	private boolean pupOK = false;
	private boolean vupOK = false;
	
	//照相
    private SurfaceView photoSurfaceView;  
    private SurfaceHolder photoSurfaceHolder;  
    private Camera photoCamera;  
    private boolean photoPreviewRunning;  
    TimerTask photoTask;
    Timer photoTimer;
    final Handler photoHandler = new Handler(){     
        @Override  
	    public void handleMessage(Message msg) {     
	        if(msg.what == 1){
//	        	System.out.println((photoCamera==null)+"------photoCamera--------test----------");
//	        	System.out.println((photoSurfaceHolderCallback==null)+"--photoSurfaceHolderCallback------------test----------");
//	        	System.out.println((photoCallback==null)+"----photoCallback----------test----------");
//	        	if (photoCamera==null) {
////	        		photoCamera = Camera.open();  
//	        		
//	        		 photoSurfaceHolderCallback = new SurfaceHolder.Callback() {
//	        				//照相
//	        				public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {  
//	        			        if (photoPreviewRunning) {  
//	        			        	photoCamera.stopPreview();  
//	        			        }  
//	        			        try {  
//	        			        	photoCamera.setPreviewDisplay(holder);  
//	        			        } catch (IOException e) {  
//	        			            e.printStackTrace();  
//	        			        }  
//	        			        photoCamera.startPreview();  
//	        			        photoPreviewRunning = true;  
//	        			        
//	        			        Log.i("test peter", "..--------------------.surfaceChanged...");  
//	        			    }  
//	        			  
//	        			    public void surfaceCreated(SurfaceHolder holder) {  
//	        			    	photoCamera = Camera.open();  
//	        			    	Log.i("test peter","--------------------surfaceCreated is called");
//	        			    }  
//	        			    
//	        			    public void surfaceDestroyed(SurfaceHolder holder) { 
//	        			    	
//	        			    	Log.i("test peter","surfaceDestroyed is called");
//	        			    	if (photoCamera!=null) {
//	        			    		photoPreviewRunning = false;  
//	        			    		photoCamera.stopPreview();  
//	        			    		photoCamera.setPreviewCallback(null) ;
//	        			    		photoCamera.release();  
//	        			    		photoCamera = null;  
//	        					}
//	        			    }
//	        			};
//	        			
//	        		photoSurfaceView = (SurfaceView) findViewById(R.id.camera);  
//	     		    photoSurfaceHolder = photoSurfaceView.getHolder();
//	     		    photoSurfaceHolder.addCallback(photoSurfaceHolderCallback);  
//	     		    photoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
//	        		
//				}
////	        	photoCamera.get
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
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  
                try {
         		  String fileName = "photo_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
       	         		"_"+String.format("%03d", currentPhotoIndex)+".jpg";
					FileUtils.saveMyBitmap(fileName, bitmap);
					//上传
					new Thread(){  
			            @Override  
			        public void run(){     
			           try{  
			        	   uploadPhoto();
			           }catch(Exception e) {  
			               Log.d("zlq", "***************************"+e.toString());  
			          }  
			         }  
			       }.start(); 
				} catch (IOException e) {
					e.printStackTrace();
				}
                photoCamera.startPreview();
                photoPreviewRunning = true;
            } 
            
            Log.i("test peter", "photoPreviewRunning is ..."+photoPreviewRunning);  
            Log.i("test peter", "camera.release is called...-----------...");
//            camera.release();
            
//            if (photoCamera!=null) {
//	    		photoPreviewRunning = false;  
//	    		photoCamera.stopPreview();  
//	    		photoCamera.setPreviewCallback(null) ;
//	    		photoCamera.release();  
//	    		photoCamera = null;  
//	    		photoSurfaceHolder.removeCallback(photoSurfaceHolderCallback);
//			}
//            if (photoSurfaceView!=null) {
//            	photoSurfaceView.getHolder().removeCallback(photoSurfaceHolderCallback);
//			}
            
        }  
  
    };  
    /** 
     * 在相机快门关闭时候的回调接口，通过这个接口来通知用户快门关闭的事件， 
     * 普通相机在快门关闭的时候都会发出响声，根据需要可以在该回调接口中定义各种动作， 
     * 例如：使设备震动 
     */  
    ShutterCallback mShutterCallback = new ShutterCallback() {  
        public void onShutter() {  
            Log.i("test peter", "...onShutter...");  
        }  
    };  
	Callback photoSurfaceHolderCallback = new Callback() {
		//照相
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {  
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
	    	Log.i("test peter","--------------------surfaceCreated is called");
	    }  
	    
	    public void surfaceDestroyed(SurfaceHolder holder) { 
	    	
	    	Log.i("test peter","surfaceDestroyed is called");
	    	if (photoCamera!=null) {
	    		photoPreviewRunning = false;  
	    		photoCamera.stopPreview();  
	    		photoCamera.setPreviewCallback(null) ;
	    		photoCamera.release();  
	    		photoCamera = null;  
			}
	    }
	};
	
	//录像
//	Timer  mTimer;
	LinearLayout recordLayout ;
	LocalActivityManager m;
//	Timer timer;
//	 final Handler handler2 = new Handler(){     
//	        @Override  
//		    public void handleMessage(Message msg) {     
//		        if(msg.what == 1){  
//		        	System.out.println("刷新一次");
//		        	recordLayout.removeAllViews();
//		        	m.dispatchDestroy(true);
//		            Intent intent = new Intent().setClass(BrowsePage.this, RecorderActivity2.class); 
//		            Window w = m.startActivity("pdf", intent); 
//		            View v = w.getDecorView(); 
//		            recordLayout.addView(v); 
//		        }
//		    }   
//		};  
		private Timer  paiTimer;
		private TimerTask paiTask;
		
		private Timer  mvideoTimer;
		private Timer videoTimer;
		private TimerTask videoTask;
		private int timeSize = 0;
		private MovieRecorder2 mRecorder;

		private SurfaceView videoSurfaceView;
		private SurfaceHolder videoSurfaceHolder;
//		private TextView tv;

		final Handler videoHandler = new Handler(){     
		        @Override  
			    public void handleMessage(Message msg) {     
			        if(msg.what == 1){  
			        	mRecorder.stopRecording();
					    setResult(RESULT_OK, (new Intent()).setAction("Corky!"));
				        finish();
			        }else if(msg.what == 2){
			        	
			        	Log.i("test peter", "..videoHandler.msg.what == 2..called"); 
			        	
			        	
			        	final String fileName = "video_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
			            		"_"+String.format("%03d", currentVideoIndex)+".mp4";
//			        	Log.i("test peter", "videoHandler msg.what == 2  ..videoSurfaceView"+videoSurfaceView.toString()); 
			        	Log.i("test peter", "..videoHandler.msg.what == 2..called   mRecorder"+mRecorder.toString()); 
			        	
			        	mRecorder.startRecording(videoSurfaceView, fileName);
			    		mRecorder.isRecording = true;
			    		timeSize = 0;
			    		videoTimer = new Timer();
			    		videoTimer.schedule(new TimerTask() {
			    			@Override
			    			public void run() {
			    				timeSize++;
			    				videoHandler.sendEmptyMessage(3);
			    				System.out.println("=====================*****======="+Integer.parseInt(SystemConfig.videoRecordTime));
			    				int videoRecordTime = Integer.parseInt(SystemConfig.videoRecordTime);
			    				if (SystemConfig.videoRecordTime.equals("")||SystemConfig.videoRecordTime==null) {
			    					videoRecordTime = 9;
								}
			    				Log.i("test peter", "..videoHandler.--------------------timeSize"  +timeSize); 
			    				if(timeSize > videoRecordTime+1){
			    					mRecorder.stopRecording();
			    					videoTimer.cancel();
			    					if (mRecorder != null) {
			    						if(mRecorder.isRecording == true){
			    							mRecorder.stopRecording();
			    						}
			    						Log.i("test peter", "..mRecorder.release()   is called..."); 
			    						mRecorder.release();
			    					}
			    					//上传视频
			    					new Thread(){  
			    			            @Override  
			    			        public void run(){     
			    			           try{  
			    			        	   uploadVideo(fileName);
			    			           }catch(Exception e) {  
			    			               Log.d("zlq", "***************************"+e.toString());  
			    			          }  
			    			         }  
			    			       }.start(); 
			    				}
			    			}
			    		}, 0, 1000);
			        }else if(msg.what == 3){
//			        	tv.setText("视频 "+5+" 秒已经录制 "+timeSize+"秒 ");
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
				public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
					videoSurfaceHolder = arg0;
				}
			};
			/////////////////////////////////////////////////////////////////////////
	
			
	private final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   progressDialog.dismiss();
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BrowsePage2.this, message, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BrowsePage2.this, "服务器超时", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 5){
		    	   progressDialog.dismiss();
		    	     LocalActivityManager m = getLocalActivityManager(); 
			            Intent intent = new Intent().setClass(BrowsePage2.this, MuPDFActivity.class); 
			        	Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/mps/"+fileName);
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(uri);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
			            Window w = m.startActivity("pdf", intent); 
			            View v = w.getDecorView(); 
			            AddLayout.addView(v); 
		       }else if(msg.what == 6){
//		    	   Toast.makeText(BrowsePage.this, "项目列表获取完成", Toast.LENGTH_SHORT).show();
		    	   nameAdapter = new ArrayAdapter<String>(BrowsePage2.this, R.layout.simple_spinner_item);
			   		for(int i=0; i<SystemConfig.projectArrayList.size(); i++){
			   			nameAdapter.add(SystemConfig.projectArrayList.get(i).get("name"));
			   		}
			   		
			   		nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			   		projectSpinner.setAdapter(nameAdapter);
			   		projectSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			   			@Override
			   			public void onItemSelected(AdapterView<?> arg0, View arg1, final int position, long arg3) {
			   				if(isFirst == false){
			   					progressDialog = ProgressDialog.show(BrowsePage2.this, null, "获取资料数据..", true, true); 
			   				}
			   				namePosition = position;
			   				currentPhotoIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("photoIndex"));
			   				currentVideoIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("videoIndex"));
			   				isFirst = false;
			   				AddLayout.removeAllViews();
			   				new Thread(){  
					               @Override  
					           public void run(){     
					              try{  
					           	   getProjectDataList(SystemConfig.projectArrayList.get(position).get("number"));
					              }catch(Exception e) {  
					                  Log.d("zlq", "***************************"+e.toString());  
					             }  
					            }  
					          }.start();  
			   			}
			   			@Override
			   			public void onNothingSelected(AdapterView<?> arg0) {
			   			}
			   		});
		       }else if(msg.what == 7){
		    	   progressDialog.dismiss();
//		    	   Toast.makeText(BrowsePage.this, "资料信息下载完成", Toast.LENGTH_SHORT).show();
		    	   updateView();
		    	   //录制视频开始
		    	   handler.sendEmptyMessage(9);
		       }else if(msg.what == 8){
		    	   
		       }else if(msg.what == 9){
		    	   //录制设置
		    	   if(!SystemConfig.projectArrayList.isEmpty()){
		    		   System.out.println("++++++++++++"+Integer.parseInt(SystemConfig.interval)*60*1000);
		    		   int interval = Integer.parseInt(SystemConfig.interval)*60*1000;
//		    		   int interval = 30*1000;
		    		    if(SystemConfig.interval.equals("")||SystemConfig.interval==null){
		    		     interval =5*60*1000; // 间隔五分钟
//		    		     interval =2*1000;
		    		    }
//		    		    paiTimer.schedule(paiTask, 10*1000, interval );
		    		    
		    		    
			    	   if(SystemConfig.shootingMode.equals("photo")){
//			    			//照相
//			    		     photoSurfaceView = (SurfaceView) findViewById(R.id.camera);  
//			    		     photoSurfaceHolder = photoSurfaceView.getHolder();
//			    		     photoSurfaceHolder.addCallback(photoSurfaceHolderCallback);  
//			    		     photoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
//			    			 photoTask = new TimerTask() {
//			    		        public void run() {
//			    		        	photoHandler.sendEmptyMessage(1);
//			    		     }
//			    		};
//			    		photoTimer = new Timer(true);
			    		   if (!scheduleAlready) {
//			    			   photoTimer.cancel();
			    			   photoTimer.schedule(photoTask, 10*1000, interval);
			    			   scheduleAlready = true;
						   }
							   
			    		   
			    	   }else if(SystemConfig.shootingMode.equals("video")){
//				    		videoSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
//				    		videoSurfaceHolder = videoSurfaceView.getHolder();
//				    		videoSurfaceHolder.addCallback(videoSurfaceHolderCallback); // holder加入回调接口
//				    		videoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//				    		mRecorder = new MovieRecorder2();
//				    		videoTask = new TimerTask() {
//				    	        public void run() {
//				    	        	videoHandler.sendEmptyMessage(2);
//				    	       }
//				    	    };
//				    	    mvideoTimer = new Timer(true);
			    		   if (!scheduleAlready) {
//			    			   mvideoTimer.cancel();
			    			   mvideoTimer.schedule(videoTask, 10*1000, interval);//延迟1秒后执行
			    			   scheduleAlready = true;
						   }
			    	   }
		    	   }
		       }else if(msg.what == 10){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BrowsePage2.this, "资料信息获取失败", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 11){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BrowsePage2.this, "项目列表获取失败", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 12){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BrowsePage2.this, "项目列表为空", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 13){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BrowsePage2.this, "项目列表为空", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 14){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BrowsePage2.this, "pdf打开失败，请重试", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 15){
		    	   progressDialog.dismiss();
//		    	   Toast.makeText(BrowsePage.this, "pdf打开失败，请重试", Toast.LENGTH_SHORT).show();
		    	   
		    	   videoPlayer2(mVideoView,
		            		Environment.getExternalStorageDirectory().getPath()+"/mps/"+videoName);
		       }
		       
		}   
	};  
	
//	protected void onResume() {
//		super.onResume();
//		//录像
//				if(SystemConfig.shootingMode.equals("video")){
//					videoSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
//					videoSurfaceHolder = videoSurfaceView.getHolder();
//					videoSurfaceHolder.addCallback(videoSurfaceHolderCallback); // holder加入回调接口
//					
//					videoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//					mRecorder = new MovieRecorder2();
//					videoTask = new TimerTask() {
//				        public void run() {
////				        	videoHandler.sendEmptyMessage(2);
////				        	Log.i("test peter","----------tttttttttttttttttttttttttttttttttttttttt");
//				       }
//				    };
//				    mvideoTimer = new Timer(true);
////				    mvideoTimer.schedule(videoTask, 3000, 10*1000);//延迟1秒后执行
//				}else if(SystemConfig.shootingMode.equals("photo")){
////					 //照相
//				     photoSurfaceView = (SurfaceView) findViewById(R.id.camera);  
//				     photoSurfaceHolder = photoSurfaceView.getHolder();
//				     photoSurfaceHolder.addCallback(photoSurfaceHolderCallback);  
//				     photoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
//					 photoTask = new TimerTask() {
//				        public void run() {
////				        	photoHandler.sendEmptyMessage(1);
//				       }
//				     };
//				     photoTimer = new Timer(true);
////				     photoTimer.schedule(photoTask, 2000, 10*1000);//延迟1秒后执行
//				}
//	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_page2);
		
//		projectSpinner = (Spinner) findViewById(R.id.projectListSpinner);
//		addVideoListview = (ListView) findViewById(R.id.addVideoLayout);
//		addDocumentListview = (ListView) findViewById(R.id.addDocumentLayout);
//		AddLayout = (LinearLayout) findViewById(R.id.AddLayout);
		
//		inflater = LayoutInflater.from(this);  
//		recordLayout = (LinearLayout) findViewById(R.id.recordLayout);
		
//		addView = (LinearLayout)inflater.inflate(R.layout.my_videoview, null);
//		mVideoView = (SurfaceView)addView.findViewById(R.id.myVideoView);
		
//		m = getLocalActivityManager();    

//		 DisplayMetrics dm = new DisplayMetrics();
//		 getWindowManager().getDefaultDisplay().getMetrics(dm);
//		 v_width = dm.widthPixels-150;
//		 v_height  = dm.heightPixels-44;
		
//		//录像
//		if(SystemConfig.shootingMode.equals("video")){
//			videoSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
//			videoSurfaceHolder = videoSurfaceView.getHolder();
//			videoSurfaceHolder.addCallback(videoSurfaceHolderCallback); // holder加入回调接口
//			
//			videoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//			mRecorder = new MovieRecorder2();
//			videoTask = new TimerTask() {
//		        public void run() {
//		        	videoHandler.sendEmptyMessage(2);
//		        	Log.i("test peter","----------tttttttttttttttttttttttttttttttttttttttt");
//		       }
//		    };
//		    mvideoTimer = new Timer(true);
////		    mvideoTimer.schedule(videoTask, 3000, 10*1000);//延迟1秒后执行
//		}else if(SystemConfig.shootingMode.equals("photo")){
////			 //照相
//		     photoSurfaceView = (SurfaceView) findViewById(R.id.camera);  
//		     photoSurfaceHolder = photoSurfaceView.getHolder();
//		     photoSurfaceHolder.addCallback(photoSurfaceHolderCallback);  
//		     photoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
//			 photoTask = new TimerTask() {
//		        public void run() {
//		        	photoHandler.sendEmptyMessage(1);
//		       }
//		     };
//		     photoTimer = new Timer(true);
//////		     photoTimer.schedule(photoTask, 2000, 10*1000);//延迟1秒后执行
//		}
		     
		     
//		    paiTask = new TimerTask() {
//				@Override
//				public void run() {
//					if (flag==0) {
////						 photoSurfaceView = (SurfaceView) findViewById(R.id.camera);
////						 photoSurfaceHolder = photoSurfaceView.getHolder();
////						 photoSurfaceHolder.addCallback(photoSurfaceHolderCallback);  
////					     photoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
////					     
//						videoHandler.sendEmptyMessage(2);
//						Log.i("test peter","----------video"+flag);
//						
//						flag =1;
//					}else{
////						videoSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
////						videoSurfaceHolder = videoSurfaceView.getHolder();
////						videoSurfaceHolderCallback = new Callback() {
////								@Override
////								public void surfaceDestroyed(SurfaceHolder arg0) {
////									videoSurfaceView = null;
////									videoSurfaceHolder = null;
////									mRecorder = null;
////								}
////
////								@Override
////								public void surfaceCreated(SurfaceHolder arg0) {
////									videoSurfaceHolder = arg0;
////								}
////
////								@Override
////								public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
////									videoSurfaceHolder = arg0;
////								}
////							};
////						videoSurfaceHolder.addCallback(videoSurfaceHolderCallback); // holder加入回调接口
////						videoSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
////						mRecorder = new MovieRecorder2();
//					
//						photoHandler.sendEmptyMessage(1);
//						Log.i("test peter","----------photo"+flag);
//						flag = 0;
//					}
////					videoHandler.sendEmptyMessage(2);
////					Log.i("test peter","----------tttttttttttttttttttttttttttttttttttttttt");
//					
//				}
//			};
//			 paiTimer = new Timer(true);
//			
		
		
	    //loading数据
//		progressDialog = ProgressDialog.show(BrowsePage2.this, null, "获取项目中..", true, true);  
//		new Thread(){  
//            @Override  
//        public void run(){     
//           try{  
//        	   loadReportData();
//           }catch(Exception e) {  
//               Log.d("zlq", "***************************"+e.toString());  
//          }  
//         }  
//       }.start();  
	   	findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(BrowsePage2.this, "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh", Toast.LENGTH_SHORT).show();
				MyUtil.leave(BrowsePage2.this,mRecorder);
			}
		});
//	   	findViewById(R.id.label_title).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				Toast.makeText(BrowsePage.this, "hhh", Toast.LENGTH_SHORT).show();
//				  
//				String strs =  FileUtils.ReadTxtFile();
//				strs = strs.substring(0,strs.length()-2);
//				System.out.println("strs---"+strs);
//				String[] nameList = strs.split("\\*");
//				for (int i = 0; i < nameList.length; i++) {
//					System.out.println(nameList[i]+"--");
//					if (!nameList[i].equals("")) {
//						if (nameList[i].endsWith(".jpg")) {
//							String[] ss = nameList[i].split("_");
//							String projNumber = ss[1];
//							String Name =nameList[i];
//							String fileType ="0";
//							System.out.println("********************"+projNumber+Name+fileType);
//							uploadPhoto2(projNumber,Name,fileType);
////							if (pupOK) {
////								strs = strs.replace(Name, "");
////							}
//						}
//						if (nameList[i].endsWith(".mp4")) {
//							
//							String[] ss = nameList[i].split("_");
//							String projNumber = ss[1];
//							String Name =nameList[i];
//							String fileType ="1";
//							System.out.println("********************"+projNumber+Name+fileType);
//							uploadVideo2(projNumber,Name,fileType);
////							if (vupOK) {
////								strs = strs.replace(Name, "");
////							}
//						}
//					}
//				}
////				if (strs.equals("")) {
////					FileUtils.deleteUPFailedTxt();
////				}else {
////					FileUtils.deleteUPFailedTxt();
////					FileUtils.write2Txt(strs+"*");
////				}
//			}
//		});
	}
	private void uploadVideo2(final String projNumber, final String name,
			final String fileType) {
	 	new Thread(){  
            @Override  
        public void run(){     
           try{  
        	   String resultStr = "";
       			JSONObject resultJSON;
        	   
	            File video = new File(Environment.getExternalStorageDirectory().getPath()+
	            		"/mps/"+name);
	            if(video.exists()){
	            	System.out.println(">>>>>>>>>>>>>>>>>>>"+video.getName()+" "+video.exists()+"  video.length() "+video.length());
	            	resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", video);
		            System.out.println("resultJSON.toString()"+resultStr);
		            
		            if(resultStr.equals("")){
      				handler.sendEmptyMessage(16);//
      			}else{
      				resultJSON = new JSONObject(resultStr);
      				String result = resultJSON.getString("result");
      				message = resultJSON.getString("message");
      				if(result.equals("ok")){
      					uploadSuccess(projNumber, video.getAbsolutePath(), fileType);
      				}else if(result.equals("error")){
      					handler.sendEmptyMessage(17);
      				}
      			}
	            }else{
	            	handler.sendEmptyMessage(9);
	            }
           }catch(Exception e) {  
               Log.d("zlq", "***************************"+e.toString());  
          }  
         }  
       }.start(); 
		
		
		
	}
	private void uploadPhoto2(final String projNumber, final String Name,
			final String fileType) {

		final String path = Environment.getExternalStorageDirectory()
				.getPath()
				+ "/mps/"+Name;
		File test = new File(path);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>test.exists  "
				+ test.exists() + "   test.length()" + test.length());
		System.out.println("start upload fileName" + path);
		if (test.exists()) {
			// progressDialog = ProgressDialog.show(InformationReport.this,
			// "正在上传照片...", "请稍等...", true, true);
			new Thread() {
				@Override
				public void run() {
					try {
						String resultStr = "";
						JSONObject resultJSON;

						File picture = new File(path);
						picture = PictuerCompress.compFile(path);
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
								+ "picture.length()" + picture.length());
						resultStr = JsonParser.UpLoadRes(SystemConfig.URL,
								"{'uploadfile':{'userName':'"
										+ SystemConfig.userName + "'}}",
								picture);
						System.out.println("resultJSON.toString()" + resultStr);

						if (resultStr.equals("")) {
							handler.sendEmptyMessage(14);//
						} else {
							resultJSON = new JSONObject(resultStr);
							String result = resultJSON.getString("result");
							message = resultJSON.getString("message");
							if (result.equals("ok")) {
								uploadSuccess(projNumber,Name,fileType);
							} else if (result.equals("error")) {
//								handler.sendEmptyMessage(15);
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
	
	private void updateView(){
		final ArrayList<HashMap<String, String>> videoList = new ArrayList<HashMap<String, String>>();
		final ArrayList<HashMap<String, String>> documentList = new ArrayList<HashMap<String, String>>();
	        for(int i=0; i<SystemConfig.projectDataList.size(); i++){
	        	if(SystemConfig.projectDataList.get(i).get("dataType").equals("视频")){
	        		videoList.add(SystemConfig.projectDataList.get(i));
	        	}
	        }
	        for(int i=0; i<SystemConfig.projectDataList.size(); i++){
	        	if(SystemConfig.projectDataList.get(i).get("dataType").equals("文档")){
	        		documentList.add(SystemConfig.projectDataList.get(i));
	        	}
	        }
	        
	   		System.out.println("SystemConfig.projectDataList"+SystemConfig.projectDataList.size());
	   		videoAdapter = new NameAdapter(BrowsePage2.this, videoList);
	   		addVideoListview.setAdapter(videoAdapter);
	   		documentAdapter = new NameAdapter(BrowsePage2.this, documentList);
	   		addDocumentListview.setAdapter(documentAdapter);
	   		addVideoListview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
//					LinearLayout addView = (LinearLayout)inflater.inflate(R.layout.my_videoview, null);
//					VideoView mVideoView = (VideoView)addView.findViewById(R.id.myVideoView);
					
					final String videoUrl = SystemConfig.DataUrl+videoList.get(position).get("path")+
							"/"+videoList.get(position).get("dataId")+".flv";
					
					AddLayout.setBackgroundColor(Color.BLACK);
					AddLayout.removeAllViews();
//					AddLayout.addView(addView);
					AddLayout.refreshDrawableState();
					
					
					System.out.println("videoUrl"+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+videoUrl);
					 videoName = videoUrl.substring(videoUrl.lastIndexOf('/')+1, videoUrl.length());
					final File videoFile = new File(Environment.getExternalStorageDirectory().getPath()+"/mps/"+videoName);
					
					if(videoFile.exists() && videoFile.length() == 0){
						videoFile.delete();
					}
					FileUtils fileUtils = new FileUtils();  
					
					if(fileUtils.isFileExist("mps/" + videoName)){  
			            System.out.println("文件已经存在");
			            videoPlayer2(mVideoView,
			            		Environment.getExternalStorageDirectory().getPath()+"/mps/"+videoName);
			        }else{
			        	progressDialog = ProgressDialog.show(BrowsePage2.this, null, "正在下载请稍后..", true, true);  
			            new Thread(){  
			                @Override  
			            public void run(){     
			               try{  
			                	boolean flag =  Downloader.DownloadFile(videoUrl, new File(Environment.getExternalStorageDirectory().getPath()+
			                			"/mps/"+videoName));
//			                Toast.makeText(BrowsePage.this, "flag"+flag, Toast.LENGTH_SHORT).show();
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>flag"+ flag);
			                	if(flag == true){
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>flag"+ flag);                		
			                		handler.sendEmptyMessage(15);
//			                		AddLayout.removeAllViews();
////			    					AddLayout.addView(addView);
//			    					AddLayout.refreshDrawableState();
//			                		videoPlayer2(mVideoView,
//						            		Environment.getExternalStorageDirectory().getPath()+"/mps/"+videoName);
			                	}else{
//			                		if(pdfFile.exists()){
//			                			pdfFile.delete();
//			                		}
//			                		handler.sendEmptyMessage(5);
			                	}
			                   
			               }catch(Exception e) {  
			                   Log.d("zlq", "***************************"+e.toString());  
//			                   handler.sendEmptyMessage(5);
			              }
			             }  
			           }.start();  
			        } 
					
					
//					videoPlayer2(mVideoView, videoUrl);
//					updateRecord(String projNum, String dataId, String  userName, String token)
					new Thread(){  
			            @Override  
			        public void run(){     
			           try{  
			        	   updateRecord(SystemConfig.projectArrayList.get(namePosition).get("number"),
			        			   videoList.get(position).get("dataId"), SystemConfig.userName,
			        			   SystemConfig.token);
			           }catch(Exception e) {  
			               Log.d("zlq", "***************************"+e.toString());  
			          }  
			         }  
			       }.start(); 
				}
			});
	   		addDocumentListview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
					final String url = SystemConfig.DataUrl+documentList.get(position).get("path")+"/"+
							documentList.get(position).get("dataId")+".pdf";
					fileName = documentList.get(position).get("dataId")+".pdf";
					System.out.println(url);
					AddLayout.removeAllViews();
					AddLayout.refreshDrawableState();
					final File pdfFile = new File(Environment.getExternalStorageDirectory().getPath()+"/mps/"+fileName);
					if(pdfFile.exists() && pdfFile.length() == 0){
						pdfFile.delete();
					}
					FileUtils fileUtils = new FileUtils();  
					if(fileUtils.isFileExist("mps/" + fileName)){  
			            System.out.println("文件已经存在");
			            LocalActivityManager m = getLocalActivityManager(); 
			            Intent intent = new Intent().setClass(BrowsePage2.this, MuPDFActivity.class); 
			        	Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/mps/"+fileName);
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(uri);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
			            Window w = m.startActivity("pdf", intent); 
			            View v = w.getDecorView(); 
			            AddLayout.addView(v); 
			        }else{
			        	progressDialog = ProgressDialog.show(BrowsePage2.this, null, "获取数据..", true, true);  
			            new Thread(){  
			                @Override  
			            public void run(){     
			               try{  
			                	boolean flag =  Downloader.DownloadFile(url, new File(Environment.getExternalStorageDirectory().getPath()+
			                			"/mps/"+fileName));
			                	if(flag == true){
			                		 handler.sendEmptyMessage(5);
			                	}else{
			                		if(pdfFile.exists()){
			                			pdfFile.delete();
			                		}
			                		 handler.sendEmptyMessage(5);
			                	}
			                   
			               }catch(Exception e) {  
			                   Log.d("zlq", "***************************"+e.toString());  
			                   handler.sendEmptyMessage(5);
			              }
			             }  
			           }.start();  
			        } 
//					updateRecord(String projNum, String dataId, String  userName, String token)
					new Thread(){  
			            @Override  
			        public void run(){     
			           try{  
			        	   updateRecord(SystemConfig.projectArrayList.get(namePosition).get("number"),
			        			   documentList.get(position).get("dataId"), SystemConfig.userName,
			        			   SystemConfig.token);
			           }catch(Exception e) {  
			               Log.d("zlq", "***************************"+e.toString());  
			          }  
			         }  
			       }.start(); 
				}
			});
	}
	
	 private void loadReportData(){
			String resultStr = "";
			JSONObject resultJSON;
			
			String result = "";
			int number;
			String msgStr = "";
			try {
				//{"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
				msgStr = "{'getMyProjectList':{'userName':'"+SystemConfig.userName+
						"'}, 'token':'"+SystemConfig.token+"'}";
				
				resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
				System.out.println(resultStr);
				if(resultStr.equals("")){
					handler.sendEmptyMessage(11);
				}else{
					resultJSON = new JSONObject(resultStr);
					result = resultJSON.getString("result");
					message = resultJSON.getString("message");
					if(result.equals("ok")){
						number = resultJSON.getInt("number");
						if(number != 0){
							JSONObject projectList = resultJSON.getJSONObject("myProjectList");
							JSONArray projectArray = projectList.names();
							SystemConfig.projectArrayList = new ArrayList<HashMap<String,String>>();
							for(int i=0; i<projectArray.length(); i++){
								JSONObject temp = projectList.getJSONObject(projectArray.getString(i));
								HashMap<String, String> project = new HashMap<String, String>();
								project.put("type",temp.getString("projType"));
								project.put("number", temp.getString("projNum"));
								project.put("name", temp.getString("projName"));
								project.put("photoIndex", temp.getString("photoIndex"));
								project.put("videoIndex", temp.getString("videoIndex"));
								SystemConfig.projectArrayList.add(project);
							}
							handler.sendEmptyMessage(6);
						}else{
							handler.sendEmptyMessage(12);
						}
					}else if(result.equals("error")){
						handler.sendEmptyMessage(11);
					}
				}
			} catch (JSONException e) {
				handler.sendEmptyMessage(11);
				e.printStackTrace();
			}
	   }
	    
	    public static Intent muPdf(Context context, String param ){
	    	File file = new File(Environment.getExternalStorageDirectory()+"/"+param);
	    	if(file.exists()){
	    		System.out.println("文件存在");
	    	}else{
	    		System.out.println("文件不存在");
	    	}
	    	System.out.println(context.getFilesDir().toString());
	    	
			Uri uri = Uri.parse(param);
			Intent intent = new Intent(context, MuPDFActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(uri);
	        return intent;
	    }
	 
	    public static Intent viewIntenertPdf(Context context, String url){
	    	Uri uri = Uri.parse(url);
			Intent intent = new Intent(context, MuPDFActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(uri);
	        return intent;
	    }
	    
	    private void getProjectDataList(String projNum){
			String resultStr = "";
			JSONObject resultJSON;
			
			String result = "";
			int number;
			String msgStr = "";
			try {
				//{"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
				//{"getProjectDataList":{"projNum":项目编号},"token":令牌}
				msgStr = "{'getProjectDataList':{'projNum':'"+projNum+
						"'}, 'token':'"+SystemConfig.token+"'}";
				
				resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
				System.out.println("数据返回值"+resultStr);
				if(resultStr.equals("")){
					handler.sendEmptyMessage(10);
				}else{
					resultJSON = new JSONObject(resultStr);
					result = resultJSON.getString("result");
					message = resultJSON.getString("message");
					if(result.equals("ok")){
						number = resultJSON.getInt("number");
						if(number != 0){
							JSONObject projectList = resultJSON.getJSONObject("projectDataList");
							JSONArray projectArray = projectList.names();
							SystemConfig.projectDataList = new ArrayList<HashMap<String,String>>();
							for(int i=0; i<projectArray.length(); i++){
								JSONObject temp = projectList.getJSONObject(projectArray.getString(i));
								HashMap<String, String> project = new HashMap<String, String>();
								project.put("dataType",temp.getString("dataType"));
								project.put("dataName", temp.getString("dataName"));
								project.put("dataLength", temp.getString("dataLength"));
								project.put("dataId", temp.getString("dataId"));
								project.put("path", temp.getString("path"));
								project.put("userId", temp.getString("userId"));
								SystemConfig.projectDataList.add(project);
							}
						}
						handler.sendEmptyMessage(7);
					}else if(result.equals("error")){
						handler.sendEmptyMessage(10);
					}
				}
			} catch (JSONException e) {
				handler.sendEmptyMessage(10);
				e.printStackTrace();
			}
	   }
	    
	   private void updateRecord(String projNum, String dataId, String  userName, String token){
		   //{"useData":{"projNum":项目编号,"dataId":资料主键值,"userName":用户名},"token":令牌}
			String resultStr = "";
			JSONObject resultJSON;
			
			String result = "";
			String msgStr = "";
			try {
				msgStr = "{'useData':{'projNum':'"+projNum+"','dataId':'"+dataId+"','userName':'"+
						userName+"'}, 'token':'"+token+"'}";
				
				resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
				System.out.println("数据返回值"+resultStr);
				if(resultStr.equals("")){
//					handler.sendEmptyMessage(4);//
				}else{
					resultJSON = new JSONObject(resultStr);
					result = resultJSON.getString("result");
					message = resultJSON.getString("message");
					if(result.equals("ok")){
//						handler.sendEmptyMessage(1);
					}else if(result.equals("error")){
//						handler.sendEmptyMessage(2);
					}else{
//						handler.sendEmptyMessage(3);
					}
				}
			} catch (JSONException e) {
//				handler.sendEmptyMessage(4);
				e.printStackTrace();
			}
	   } 
	    
	   
	   //
	private void uploadPhoto() {
		final String fileName = Environment.getExternalStorageDirectory()
				.getPath()
				+ "/mps/photo_"
				+ SystemConfig.projectArrayList.get(namePosition).get("number")
				+ "_" + String.format("%03d", currentPhotoIndex) + ".jpg";
		File test = new File(fileName);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>test.exists  "
				+ test.exists() + "   test.length()" + test.length());
		System.out.println("start upload fileName" + fileName);
		if (test.exists()) {
			// progressDialog = ProgressDialog.show(InformationReport.this,
			// "正在上传照片...", "请稍等...", true, true);
			new Thread() {
				@Override
				public void run() {
					try {
						String resultStr = "";
						JSONObject resultJSON;

						File picture = new File(fileName);
						picture = PictuerCompress.compFile(fileName);
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
								+ "picture.length()" + picture.length());
						resultStr = JsonParser.UpLoadRes(SystemConfig.URL,
								"{'uploadfile':{'userName':'"
										+ SystemConfig.userName + "'}}",
								picture);
						System.out.println("resultJSON.toString()" + resultStr);

						if (resultStr.equals("")) {
							handler.sendEmptyMessage(14);//
						} else {
							resultJSON = new JSONObject(resultStr);
							String result = resultJSON.getString("result");
							message = resultJSON.getString("message");
							if (result.equals("ok")) {
								uploadSuccess(SystemConfig.projectArrayList
										.get(namePosition).get("number"),
										fileName, "0");
							} else if (result.equals("error")) {
								//handler.sendEmptyMessage(15);
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
	   
	   private void uploadVideo(final String fileName){
		   	new Thread(){  
		            @Override  
		        public void run(){     
		           try{  
		        	   String resultStr = "";
		       			JSONObject resultJSON;
		        	   
			            File video = new File(Environment.getExternalStorageDirectory().getPath()+
			            		"/mps/"+fileName);
			            if(video.exists()){
			            	System.out.println(">>>>>>>>>>>>>>>>>>>"+video.getName()+" "+video.exists()+"  video.length() "+video.length());
			            	resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", video);
	  		            System.out.println("resultJSON.toString()"+resultStr);
	  		            
	  		            if(resultStr.equals("")){
	          				handler.sendEmptyMessage(16);//
	          			}else{
	          				resultJSON = new JSONObject(resultStr);
	          				String result = resultJSON.getString("result");
	          				message = resultJSON.getString("message");
	          				if(result.equals("ok")){
	          					uploadSuccess(SystemConfig.projectArrayList.get(namePosition).get("number"), video.getAbsolutePath(), "1");
	          				}else if(result.equals("error")){
	          					handler.sendEmptyMessage(17);
	          				}
	          			}
			            }else{
			            	handler.sendEmptyMessage(9);
			            }
		           }catch(Exception e) {  
		               Log.d("zlq", "***************************"+e.toString());  
		          }  
		         }  
		       }.start(); 
	   }
	   
	   //{"saveAttach":{"userName":用户名,"projNum":项目编号,"fileName":文件名,"attachType"文件类型},"token":令牌}
	   private void uploadSuccess(String projNumber, String fileName, String fileType){
			String resultStr = "";
			JSONObject resultJSON;
			File file = new File(fileName);
			
			String result = "";
			String msgStr = "";
			try {
				//{"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
				msgStr = "{'saveAttach':{'userName':'"+SystemConfig.userName+"','projNum':'"+projNumber+
						"','fileName':'"+file.getName()+"','attachType':'"+fileType+"'},'token':'"+SystemConfig.token+"'}";
				
				resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
				System.out.println(resultStr);
				if(resultStr.equals("")){
					
				}else{
					resultJSON = new JSONObject(resultStr);
					result = resultJSON.getString("result");
					message = resultJSON.getString("message");
					if(result.equals("ok")){
						if(fileType.equals("0")){
							currentPhotoIndex++;
							pupOK = true;  
						}else if(fileType.equals("1")){
							currentVideoIndex++;
							vupOK = true;
						}
						System.out.println(">>>>>>>>>>>>>>>file.exists()  "+file.exists());
						if (file.exists()) {
//							FileUtils.write2Txt(file.getName()+"*");
							file.delete();  // delete file
						}
						
					}else if(result.equals("error")){
						FileUtils.write2Txt(file.getName()+"*");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	   }
	   
//	   SurfaceHolder hholder;
	   	void videoPlayer2(final SurfaceView videoView, String url){
	   		AddLayout.removeAllViews();
//			AddLayout.addView(addView);
			AddLayout.refreshDrawableState();
		   if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
				return;

//		   videoView.setVideoPath(url);
//		   videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
//		   hholder = videoView.getHolder();
//		   hholder.setFixedSize(v_width, v_height);
		   
           LocalActivityManager m = getLocalActivityManager(); 
           Intent intent = new Intent().setClass(BrowsePage2.this, PMediaPlayer3.class); 
			intent.setAction(Intent.ACTION_VIEW);
			intent.putExtra("url", url);
			
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
            @SuppressWarnings("deprecation")
			Window w = m.startActivity("video", intent); 
            View v = w.getDecorView(); 
            AddLayout.addView(v); 
		   
//		   Toast.makeText(BrowsePage.this, "v_height"+v_height, Toast.LENGTH_SHORT).show();
//		   hholder.setSizeFromLayout();
		   
//		   videoView.setMediaController(new MediaController(this));
		   
//		   videoView.setVideoLayout(videoView.VIDEO_LAYOUT_ZOOM, 0);
		   
//		    DisplayMetrics dm = new DisplayMetrics();
//			getWindowManager().getDefaultDisplay().getMetrics(dm);
//			int  width = dm.widthPixels-150;
//			int height = dm.heightPixels-44;
//		    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(width, height );
//			
//		   videoView.setLayoutParams(p);
		   
//		   Log.i("width ---", videoView.getHeight() +"" );
//		   Log.i("width2 ---", videoView.getWidth() +"" );
//		   Log.i("getVideoHeight ---", videoView.getVideoHeight() +"" );
//		   Log.i("getVideoWidth ---", videoView.getVideoWidth() +"" );
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int width = dm.widthPixels;
//		int height = dm.heightPixels;

		   
		}
	   	
//	   void videoPlayer(final VideoView videoView, String url){
//			 progressDialog = ProgressDialog.show(BrowsePage.this, null, "缓冲视频中..", true, true);  
////		   	   Uri uri = Uri.parse("http://content.bitsontherun.com/videos/3XnJSIm4-kNspJqnJ.mp4"); 
//			 videoView.setOnPreparedListener(new OnPreparedListener() {
//				@Override
//				public void onPrepared(MediaPlayer mp) {
//					videoView.setBackgroundDrawable(null);
//					progressDialog.dismiss();
//				}
//			  });
//			   Uri uri = Uri.parse(url); 
//		       videoView.setMediaController(new MediaController(this)); 
//		       videoView.setVideoURI(uri);  
//		       videoView.start();   
//		       videoView.requestFocus(); 
//		       videoView.start();
//		}
	   
	   
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MyUtil.leave(BrowsePage2.this,mRecorder);
		}
		return true;
	}
		 
	@Override
	protected void onDestroy() {
		 m.removeAllActivities();
		 if(photoTimer!=null){
			 photoTimer.cancel();
		 }
			if(videoTask != null){
				videoTask.cancel();
			}
			if(videoTimer != null){
				videoTimer.cancel();
			}
			if(mvideoTimer != null){
				mvideoTimer.cancel();
			}
			if (mRecorder != null) {
				if(mRecorder.isRecording == true){
					mRecorder.stopRecording();
				}
				mRecorder.release();
			}
		 
		super.onDestroy();
	}
}

