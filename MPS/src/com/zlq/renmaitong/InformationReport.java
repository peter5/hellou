package com.zlq.renmaitong;
//
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.baidu.mapapi.BMapManager;
//import com.baidu.mapapi.LocationListener;
//import com.cn.rtmp.CameraVideoActivity;
//import com.cn.rtmp.Main;
//import com.zlq.json.JsonParser;
//import io.vov.vitamio.R;
//import com.zlq.util.MyDatabaseHelper;
//import com.zlq.util.MyUtil;
//import com.zlq.util.PictuerCompress;
//import com.zlq.util.SystemConfig;
//
import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.YuvImage;
//import android.location.Location;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.BaseColumns;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
public class InformationReport extends Activity{
//
//	public static int TAKEPHOTO = 1;
//	public static int RECORDERVIDEO = 2;
//	public static int AUDIORECORDER = 3;
//	
//	BMapManager mBMapMan = null;
//	LocationListener mLocationListener = null;//create时注册此listener，Destroy时需要Remove
//	Location myLocation;
//	
//	private ImageButton informationReport, ContactsManage, SystemSetting, exitSystem;
//	private InformationListener informationListener = new InformationListener();
//	private ButtonClickListener buttonClickListener = new ButtonClickListener();
//	private Spinner projectTypeSpinner, projectNameSpinner;
//	private Button pictureBtn, recordBtn, GspBtn, audioBtn;
//	
//	private int namePosition = 0;
//	private int photoNumber = 0, videoNumber = 0;
//	
//	public static int currentPhotoIndex;
//	public static int currentVideoIndex;
//	public static int currentAudioIndex;
//	
//	private boolean uploadPhotoFail = false;
//	private boolean uploadVideoFail = false;
//	private boolean uploadAudioFail = false;
//	
//	public static ProgressDialog progressDialog; 
//	private String message ;
//	
//	ArrayAdapter<String> adapter;
//	ArrayAdapter<String> nameAdapter;
//	private 
//	final Handler handler = new Handler(){     
//		@Override  
//	 	public void handleMessage(Message msg) {     
//		       if(msg.what == 1){
//		    	   progressDialog.dismiss();
//		    	   if(SystemConfig.projectArrayList.isEmpty()){
//		    		   Toast.makeText(InformationReport.this, "项目列表为空", Toast.LENGTH_LONG).show();
//		    	   }else{
//		    		   pictureBtn.setEnabled(true);
//		    		   recordBtn.setEnabled(true);
//		    		   GspBtn.setEnabled(true);
//		    		   audioBtn.setEnabled(true);
//		    		   Toast.makeText(InformationReport.this, "加载项目类表成功", Toast.LENGTH_LONG).show();
//			    	   adapter = new ArrayAdapter<String>(InformationReport.this, R.layout.simple_spinner_item);
//				   		for(int i=0; i<SystemConfig.projectArrayList.size(); i++){
//				   			adapter.add(SystemConfig.projectArrayList.get(i).get("type"));
//				   		}
//				   		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//				   		projectTypeSpinner.setAdapter(adapter);
//				   		projectTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//				   			@Override
//				   			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//				   				String projectType = adapter.getItem(position);
//				   			}
//				   			@Override
//				   			public void onNothingSelected(AdapterView<?> arg0) {}
//				   		});
//				   		
//				   		
//				   		nameAdapter = new ArrayAdapter<String>(InformationReport.this, R.layout.simple_spinner_item);
//				   		for(int i=0; i<SystemConfig.projectArrayList.size(); i++){
//				   			nameAdapter.add(SystemConfig.projectArrayList.get(i).get("name"));
//				   		}
//				   		nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//				   		projectNameSpinner.setAdapter(nameAdapter);
//				   		projectNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//				   			@Override
//				   			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//				   				namePosition = position;
//				   				currentPhotoIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("photoIndex"));
//				   				currentVideoIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("videoIndex"));
//				   				currentAudioIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("audioIndex"));
//				   				System.out.println("position"+position+"  currentPhotoIndex"+currentPhotoIndex+
//				   						"   currentVideoIndex"+currentVideoIndex+"  currentAudioIndex:"+currentAudioIndex);
//				   				projectTypeSpinner.setSelection(position);
//				   			}
//		
//				   			@Override
//				   			public void onNothingSelected(AdapterView<?> arg0) {
//				   			}
//				   		});
//		    	   }
//		       }else if(msg.what == 2){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, message, Toast.LENGTH_SHORT).show();
//		       }else if(msg.what == 3){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "服务器返回值错误", Toast.LENGTH_SHORT).show();
//		       }else if(msg.what == 4){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "服务器超时", Toast.LENGTH_SHORT).show();
//		       }else if(msg.what == 5){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "上传GPS成功", Toast.LENGTH_SHORT).show();
//		       }else if(msg.what == 6){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "项目列表为空，请重新加载数据", Toast.LENGTH_SHORT).show();
//		       }else if(msg.what == 7){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "获取GPS失败，请确定开启了GPS或者稍候重试", Toast.LENGTH_SHORT).show();
//		       }else if(msg.what == 8){
//		    	   Toast.makeText(InformationReport.this, "上传照片成功", Toast.LENGTH_SHORT).show();
//		    	   deletePhoto();
//		    	   if(uploadPhotoFail == true){
//		    		   uploadPhotoFail = false;
//		    	   }
//		    	   currentPhotoIndex++;
//		       }else if(msg.what == 9){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "文件不存在", Toast.LENGTH_SHORT).show();
//		       }else if(msg.what == 10){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "上传视频成功", Toast.LENGTH_SHORT).show();
//		    	   deleteVideo();
//		    	   if(uploadVideoFail == true){
//		    		   uploadVideoFail = false;
//		    	   }
//		    	   currentVideoIndex++;
//		       }else if(msg.what == 11){
//		    	   Toast.makeText(InformationReport.this, "照片不存在", Toast.LENGTH_SHORT).show();
//		       }else if(msg.what == 12){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "上传录音成功", Toast.LENGTH_SHORT).show();
//		    	   if(uploadAudioFail == true){
//		    		   uploadAudioFail = false;
//		    	   }
//		    	   deleteAudio();
//		    	   currentAudioIndex++;
//		       }else if(msg.what == 13){
//		    	   Toast.makeText(InformationReport.this, "照片不存在", Toast.LENGTH_SHORT).show();
//		       }else if(msg.what == 14){
//		    	   Toast.makeText(InformationReport.this, "上传照片失败", Toast.LENGTH_SHORT).show();
//		    	   uploadPictureFailed();
//		       }else if(msg.what == 15){
//		    	   Toast.makeText(InformationReport.this, message, Toast.LENGTH_SHORT).show();
//		    	   uploadPictureFailed();
//		       }else if(msg.what == 16){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "上传视频服务器超时", Toast.LENGTH_SHORT).show();
//		    	   uploadVideoFailed();
//		       }else if(msg.what == 17){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "上传视频出错，"+message, Toast.LENGTH_SHORT).show();
//		    	   uploadVideoFailed();
//		       }else if(msg.what == 18){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "上传录音服务器超时", Toast.LENGTH_SHORT).show();
//		    	   uploadAudioFailed();
//		       }else if(msg.what == 19){
//		    	   progressDialog.dismiss();
//		    	   Toast.makeText(InformationReport.this, "上传录音出错，"+message, Toast.LENGTH_SHORT).show();
//		    	   uploadAudioFailed();
//		       }
//		}   
//	};  
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.my_informationreport);
//		intiView();
//		System.out.println("======================================onCreate");
//		
////		YuvImage myYuvImage = new YuvImage(yuv, format, width, height, strides);
//		
//		mBMapMan = new BMapManager(this);
//		mBMapMan.init("B2CC3DD73EAA572CAD1B4B81C84798F67C98C1C3", null);
//		
//        // 注册定位事件
//        mLocationListener = new LocationListener(){
//			@Override
//			public void onLocationChanged(Location location) {
//				if(location != null){
//					String strLog = String.format("您当前的位置:\r\n" +
//							"经度:%f\r\n" +
//							"纬度:%f",
//							location.getLongitude(), location.getLatitude());
//					myLocation = location;
//			        System.out.println(strLog+"==========================");
//				}
//			}
//        };
//		
//        if(SystemConfig.projectArrayList.isEmpty()){
//        	progressDialog = ProgressDialog.show(InformationReport.this, "加载数据中...", "请稍等...", true, false);  
//    		new Thread(){  
//                @Override  
//            public void run(){     
//               try{  
//            	   loadReportData();
//           			System.out.println(SystemConfig.projectArrayList.size()+"loadReportData()");
//               }catch(Exception e) {  
//                   Log.d("zlq", "***************************"+e.toString());  
//              }  
//             }  
//           }.start();  
//        }else{
//        	System.out.println("项目列表已经加载");
//
// 		   pictureBtn.setEnabled(true);
// 		   recordBtn.setEnabled(true);
// 		   GspBtn.setEnabled(true);
// 		  audioBtn.setEnabled(true);
//	    	   adapter = new ArrayAdapter<String>(InformationReport.this, R.layout.simple_spinner_item);
//		   		for(int i=0; i<SystemConfig.projectArrayList.size(); i++){
//		   			adapter.add(SystemConfig.projectArrayList.get(i).get("type"));
//		   		}
//		   		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		   		projectTypeSpinner.setAdapter(adapter);
//		   		projectTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//		   			@Override
//		   			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//		   				String projectType = adapter.getItem(position);
//		   			}
//		   			@Override
//		   			public void onNothingSelected(AdapterView<?> arg0) {}
//		   		});
//		   		
//		   		
//		   		nameAdapter = new ArrayAdapter<String>(InformationReport.this, R.layout.simple_spinner_item);
//		   		for(int i=0; i<SystemConfig.projectArrayList.size(); i++){
//		   			nameAdapter.add(SystemConfig.projectArrayList.get(i).get("name"));
//		   		}
//		   		nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		   		projectNameSpinner.setAdapter(nameAdapter);
//		   		projectNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//		   			@Override
//		   			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//		   				namePosition = position;
//		   				currentPhotoIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("photoIndex"));
//		   				currentVideoIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("videoIndex"));
//		   				currentAudioIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("audioIndex"));
//		   				System.out.println("position"+position+"  currentPhotoIndex"+currentPhotoIndex+
//		   						"   currentVideoIndex"+currentVideoIndex+"  currentAudioIndex:"+currentAudioIndex);
//		   				projectTypeSpinner.setSelection(position);
//		   			}
//
//		   			@Override
//		   			public void onNothingSelected(AdapterView<?> arg0) {
//		   			}
//		   		});
// 	   
//        }
//	}
//	
//	
//	void intiView(){
//		informationReport = (ImageButton) findViewById(R.id.tabbar1);
//		informationReport.setPressed(true);
//		ContactsManage = (ImageButton) findViewById(R.id.tabbar2);
//		SystemSetting = (ImageButton) findViewById(R.id.tabbar4);
//		exitSystem = (ImageButton) findViewById(R.id.tabbar5);
//		projectTypeSpinner = (Spinner) findViewById(R.id.projectTypeSpinner);
//		projectNameSpinner = (Spinner) findViewById(R.id.projectnameSpinner);
//		
//		pictureBtn = (Button) findViewById(R.id.btnPicture);
//		recordBtn = (Button) findViewById(R.id.btnRecord);
//		GspBtn = (Button) findViewById(R.id.btnGps);
//		audioBtn = (Button) findViewById(R.id.btnAudio);
//		pictureBtn.setEnabled(false);
//		recordBtn.setEnabled(false);
//		GspBtn.setEnabled(false);
//		audioBtn.setEnabled(false);
//		
//		pictureBtn.setOnClickListener(buttonClickListener);
//		GspBtn.setOnClickListener(buttonClickListener);
//		recordBtn.setOnClickListener(buttonClickListener);
//		audioBtn.setOnClickListener(buttonClickListener);
//		
//		ContactsManage.setOnClickListener(informationListener);
//		SystemSetting.setOnClickListener(informationListener);
//		exitSystem.setOnClickListener(informationListener);
//		
//
//	}
//	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_MENU) {// 拦截menu键事件
//		}
//		if (keyCode == KeyEvent.KEYCODE_BACK) {// 拦截返回按钮事件
//			leave();
//		}
//		return true;
//	}
//	
//    /**离开程序**/
//    private void leave(){
//        new AlertDialog.Builder(InformationReport.this)
//        .setMessage("你确定要关闭吗？")
//        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
//            public void onClick(DialogInterface dialog, int which) {
//            	informationReport.setPressed(true);
//            }})
//        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
//                public void onClick(DialogInterface dialog, int which) {
//                    System.exit(0);
//                }})
//        .show();
//    }
//    
//   class ButtonClickListener implements OnClickListener{
//		@Override
//		public void onClick(View v) {
//			String sdStatus = Environment.getExternalStorageState();
//			File photoDir = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/");
//			switch (v.getId()) {
//			case R.id.btnPicture:
//                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
//                    Log.v("TestFile", "SD card is not avaiable/writeable right now.");
//                    Toast.makeText(InformationReport.this, "SD卡不存在", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//				 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				 if(!photoDir.exists()){
//					 photoDir.mkdir();
//				 }
//				 File test = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/photo_"+
//				 SystemConfig.projectArrayList.get(namePosition).get("number")+"_"+String.format("%03d", currentPhotoIndex)+".jpg");
//				 intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(test));
//			
//	             startActivityForResult(intent, TAKEPHOTO);
//				 break;
//			case R.id.btnRecord:
//                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
//                    Log.v("TestFile", "SD card is not avaiable/writeable right now.");
//                    Toast.makeText(InformationReport.this, "SD卡不存在", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//				 if(!photoDir.exists()){
//					 photoDir.mkdir();
//				 }
//				new Thread(){  
//		            @Override  
//		        public void run(){     
//		           try{  
//		        	   record();
//		           }catch(Exception e) {  
//		               Log.d("zlq", "***************************"+e.toString());  
//		          }  
//		         }  
//		       }.start();  
//				break;
//			case R.id.btnGps:
////				MyUtil.initGPS(InformationReport.this);
//			    progressDialog = ProgressDialog.show(InformationReport.this, "正在上传GPS...", "请稍等...", true, true);  
//				new Thread(){  
//		            @Override  
//		        public void run(){     
//		           try{  
//		        	   getGps();
//		           }catch(Exception e) {  
//		               Log.d("zlq", "***************************"+e.toString());  
//		          }  
//		         }  
//		       }.start();  
//				break;
//			case R.id.btnAudio:
//			    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
//	                Log.v("TestFile", "SD card is not avaiable/writeable right now.");
//	                Toast.makeText(InformationReport.this, "SD卡不存在", Toast.LENGTH_SHORT).show();
//	                return;
//	            }	
//				 if(!photoDir.exists()){
//					 photoDir.mkdir();
//				 }
//				Intent audioIntent = new Intent(InformationReport.this, AudioActivity.class);
//				final String audioName = "audio_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//	            		"_"+String.format("%03d", currentAudioIndex)+".mp4";
//				audioIntent.putExtra("audioName", audioName);
//	    		startActivityForResult(audioIntent, AUDIORECORDER);
//				break;
//			case R.id.projectnameSpinner:
//				break;
//			default:
//				break;
//			}
//		}
//    }
//    
//    class InformationListener implements OnClickListener{
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.tabbar2:
//				startActivity(new Intent(InformationReport.this, ContactsManage.class));
//				finish();
//				break;
//			case R.id.tabbar4:
//				startActivity(new Intent(InformationReport.this, SystemSetting.class));
//				finish();
//				break;
//			case R.id.tabbar5:
//				leave();
//				break;
//			}
//		}
//    }
//    
//    private void record(){
//    	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>视频");
//    	if(MyUtil.isNetworkAvailable(InformationReport.this)){
//    		Main.url = SystemConfig.RTMPUrl;
//    		final String fileName = "video_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//            		"_"+String.format("%03d", currentVideoIndex)+".mp4";
//    		Main.fileName = fileName;
//    		Main.projNumber = SystemConfig.projectArrayList.get(namePosition).get("number");
//    		System.out.println(Main.url+ Main.fileName + Main.projNumber);
//    		Intent intent = new Intent(InformationReport.this, RecorderActivity.class);
//    		startActivityForResult(intent, RECORDERVIDEO);
//    	}else{
//    		 new AlertDialog.Builder(InformationReport.this)
//    	        .setMessage("当前网络不可用，是否开启网络？")
//    	        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
//    	            public void onClick(DialogInterface dialog, int which) {
//    	            	//存入本地
//    	            }})
//    	        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
//    	                public void onClick(DialogInterface dialog, int which) {
//    	                    
//    	                }})
//    	        .show();
//    	}
//    }
//    
//    private void getGps(){
//    	
//    	Location location1 = MyDatabaseHelper.getCurrentLocation(InformationReport.this);
//    	Location location2 = myLocation;
//    	Location location = null;
//    	if(location1 != null){
//    		location = location1;
//    		System.out.println("location1 is not null");
//    	}
//    	if(location2 != null){
//    		location = location2;
//    		System.out.println("baidu  location2 is not null");
//    	}
//    	if(location != null){
//    		if(!SystemConfig.projectArrayList.isEmpty()){
//    			System.out.println("正在上传GPS"+location.getLatitude()+"  "+location.getLongitude());
//        		//上传信息{"sendGPS":{"projNum":"MT201212002","userName":"1001","xxx":"114.4546","yyy":"32.451245"},
//        		//"token":"402881ed3c376d5c013c38323cd1002c"}
//        		String resultStr = "";
//        		JSONObject resultJSON;
//        				
//        		String result = "";
//        		String msgStr = "";
//        		try {
//        			System.out.println(SystemConfig.projectArrayList.get(namePosition).get("number"));
//        			msgStr = "{'sendGPS':{'projNum':'"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//        					"','userName':'"+SystemConfig.userName+"','xxx':'"+location.getLongitude()+
//        					"','yyy':'"+location.getLatitude()+"'},'token':'"+SystemConfig.token+"'}";
//        					
//        			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
//        			System.out.println(resultStr);
//        			if(resultStr.equals("")){
//        				handler.sendEmptyMessage(4);//
//        			}else{
//        				resultJSON = new JSONObject(resultStr);
//        				result = resultJSON.getString("result");
//        				message = resultJSON.getString("message");
//        				if(result.equals("ok")){
//        					handler.sendEmptyMessage(5);
//        				}else if(result.equals("error")){
//        					handler.sendEmptyMessage(2);
//        				}else{
//        					handler.sendEmptyMessage(3);
//        				}
//        			}
//        		} catch (JSONException e) {
//        			handler.sendEmptyMessage(4);
//        			e.printStackTrace();
//        		}
//    		}else{
//    			handler.sendEmptyMessage(6);
//    		}
//    	}else{
//    		handler.sendEmptyMessage(7);
//    	}
//    	
//    }
//    
//    
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == TAKEPHOTO){
//        	  if (resultCode == Activity.RESULT_OK) {
//        		  deleteLatestPhoto(InformationReport.this);
//        		  uploadPhoto();
//              }
//        }else if(requestCode == RECORDERVIDEO){
//        	if(resultCode == Activity.RESULT_OK){
//                progressDialog = ProgressDialog.show(InformationReport.this, "正在上传视频...", "请稍等...", true, true);  
//                uploadVideo();
//        	}
//        }else if(requestCode == AUDIORECORDER){
//         	if(resultCode == Activity.RESULT_OK){
//                progressDialog = ProgressDialog.show(InformationReport.this, "正在上传音频...", "请稍等...", true, true);  
//                uploadAudio();
//        	}
//        }
//      
//   }
//    
//   private void loadReportData(){
//		String resultStr = "";
//		JSONObject resultJSON;
//		
//		String result = "";
//		int number;
//		String msgStr = "";
//		try {
//			//{"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
//			msgStr = "{'getMyProjectList':{'userName':'"+SystemConfig.userName+
//					"'}, 'token':'"+SystemConfig.token+"'}";
//			
//			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
//			System.out.println(resultStr);
//			if(resultStr.equals("")){
//				handler.sendEmptyMessage(4);//
//			}else{
//				resultJSON = new JSONObject(resultStr);
//				result = resultJSON.getString("result");
//				message = resultJSON.getString("message");
//				if(result.equals("ok")){
//					number = resultJSON.getInt("number");
//					if(number != 0){
//						SystemConfig.RTMPUrl = resultJSON.getString("rtmp");
//						SystemConfig.videosecond = resultJSON.getString("videosecond");
//						SystemConfig.audiosecond = resultJSON.getString("audiosecond");
//						System.out.println(">>>>>>>>>>>>>>>>>"+SystemConfig.videosecond+" SystemConfig.audiosecond"+SystemConfig.audiosecond);
//						JSONObject projectList = resultJSON.getJSONObject("myProjectList");
//						JSONArray projectArray = projectList.names();
//						for(int i=0; i<projectArray.length(); i++){
//							JSONObject temp = projectList.getJSONObject(projectArray.getString(i));
//							HashMap<String, String> project = new HashMap<String, String>();
//							project.put("type",temp.getString("projType"));
//							project.put("number", temp.getString("projNum"));
//							project.put("name", temp.getString("projName"));
//							project.put("photoIndex", temp.getString("photoIndex"));
//							project.put("videoIndex", temp.getString("videoIndex"));
//							project.put("audioIndex", temp.getString("audioIndex"));
//							SystemConfig.projectArrayList.add(project);
//						}
//					}
//					handler.sendEmptyMessage(1);
//				}else if(result.equals("error")){
//					handler.sendEmptyMessage(2);
//				}else{
//					handler.sendEmptyMessage(3);
//				}
//			}
//		} catch (JSONException e) {
//			handler.sendEmptyMessage(4);
//			e.printStackTrace();
//		}
//   }
//   
//   //{"saveAttach":{"userName":用户名,"projNum":项目编号,"fileName":文件名,"attachType"文件类型},"token":令牌}
//   private void uploadSuccess(String projNumber, String fileName, String fileType){
//		String resultStr = "";
//		JSONObject resultJSON;
//		File file = new File(fileName);
//		
//		String result = "";
//		String msgStr = "";
//		try {
//			//{"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
//			msgStr = "{'saveAttach':{'userName':'"+SystemConfig.userName+"','projNum':'"+projNumber+
//					"','fileName':'"+file.getName()+"','attachType':'"+fileType+"'},'token':'"+SystemConfig.token+"'}";
//			
//			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
//			System.out.println(resultStr);
//			if(resultStr.equals("")){
//				if(fileType.equals("0")){
//					handler.sendEmptyMessage(8);//UI线程外想更新UI线程  
//				}else if(fileType.equals("1")){
//					handler.sendEmptyMessage(16);//UI线程外想更新UI线程  
//				}else if(fileType.equals("2")){
//					handler.sendEmptyMessage(18);//UI线程外想更新UI线程  
//				}
//			}else{
//				resultJSON = new JSONObject(resultStr);
//				result = resultJSON.getString("result");
//				message = resultJSON.getString("message");
//				if(result.equals("ok")){
//					if(fileType.equals("0")){
//						handler.sendEmptyMessage(8);//UI线程外想更新UI线程  
//					}else if(fileType.equals("1")){
//						handler.sendEmptyMessage(10);//UI线程外想更新UI线程  
//					}else if(fileType.equals("2")){
//						handler.sendEmptyMessage(12);//UI线程外想更新UI线程  
//					}
//					System.out.println(">>>>>>>>>>>>>>>file.exists()  "+file.exists());
//					
//				}else if(result.equals("error")){
//					if(fileType.equals("0")){
//						handler.sendEmptyMessage(14);//UI线程外想更新UI线程  
//					}else if(fileType.equals("1")){
//						handler.sendEmptyMessage(17);//UI线程外想更新UI线程  
//					}else if(fileType.equals("2")){
//						handler.sendEmptyMessage(19);//UI线程外想更新UI线程  
//					}
//				}
//			}
//		} catch (JSONException e) {
//			handler.sendEmptyMessage(4);
//			e.printStackTrace();
//		}
//   }
//   
//   private void deleteLatestPhoto(Context mContext) {
//       String[] projection = new String[] { MediaStore.Images.ImageColumns._ID,
//               MediaStore.Images.ImageColumns.DATE_TAKEN };
//       Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
//               null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
//       if (cursor != null) {
//           cursor.moveToFirst();
//           ContentResolver cr = mContext.getContentResolver();
//           cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                   BaseColumns._ID + "=" + cursor.getString(0), null);
//       }
//   }
//
//   /**上传照片失败**/
//   private void uploadPictureFailed(){
//	   if(uploadPhotoFail == true){
//		   new AlertDialog.Builder(InformationReport.this)
//	       .setMessage("上传照片失败，是否重试？")
//	       .setNegativeButton("放弃", new DialogInterface.OnClickListener(){
//	           public void onClick(DialogInterface dialog, int which) {
//	           	 deletePhoto();
//	           	 uploadPhotoFail = false;
//	           }})
//	       .show();
//	   }else{
//		   uploadPhotoFail = true;
//		   new AlertDialog.Builder(InformationReport.this)
//	       .setMessage("上传照片失败，是否重试？")
//	       .setNegativeButton("放弃", new DialogInterface.OnClickListener(){
//	           public void onClick(DialogInterface dialog, int which) {
//	           	 deletePhoto();
//	           	 uploadPhotoFail = false;
//	           }})
//	       .setPositiveButton("重试", new DialogInterface.OnClickListener(){
//	           public void onClick(DialogInterface dialog, int which) {
//	           	 uploadPhoto();
//	           	 dialog.dismiss();
//	          }})
//	       .show();
//	   }
//   }
//   
//   private void uploadPhoto(){
//	   final String fileName =  Environment.getExternalStorageDirectory().getPath()+"/myImage/photo_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//         		"_"+String.format("%03d", currentPhotoIndex)+".jpg";
//         File test = new File(fileName);
//         System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>test.exists  "+ test.exists() +"   test.length()"+test.length());
//         System.out.println("fileName"+fileName);
//         if(test.exists()){
////             progressDialog = ProgressDialog.show(InformationReport.this, "正在上传照片...", "请稍等...", true, true);  
//              new Thread(){  
//       	            @Override  
//       	        public void run(){     
//       	           try{  
//       	        	   String resultStr = "";
//       	       			JSONObject resultJSON;
//       	        	   
//       		            File picture = new File(fileName);
//       		            picture = PictuerCompress.compFile(fileName);
//       		           	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+"picture.length()"+picture.length());
//       		            resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", picture);
//            		        System.out.println("resultJSON.toString()"+resultStr);
//            		            
//            		        if(resultStr.equals("")){
//                    			handler.sendEmptyMessage(14);//
//                    		}else{
//                    			resultJSON = new JSONObject(resultStr);
//                    			String result = resultJSON.getString("result");
//                  				message = resultJSON.getString("message");
//                  				if(result.equals("ok")){
//                  					uploadSuccess(SystemConfig.projectArrayList.get(namePosition).get("number"), fileName, "0");
//                  				}else if(result.equals("error")){
//                  					handler.sendEmptyMessage(15);
//                  				}
//                  			}
//       	           }catch(Exception e) {  
//       	               Log.d("zlq", "***************************"+e.toString());  
//       	          }  
//       	         }  
//       	       }.start(); 
//         }else{
//       	  handler.sendEmptyMessage(13);
//         }
//   }
//   
//   private void deletePhoto(){
//	   final String fileName =  Environment.getExternalStorageDirectory().getPath()+"/myImage/photo_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//         		"_"+String.format("%03d", currentPhotoIndex)+".jpg";
//      File file = new File(fileName);
//	   if(file.exists()){
//		   file.delete();
//	   }
//   }
//   
//   /**上传视频失败**/
//   private void uploadVideoFailed(){
//	   if(uploadVideoFail == true){
//		   new AlertDialog.Builder(InformationReport.this)
//	       .setMessage("上传视频失败，是否重试？")
//	       .setNegativeButton("放弃", new DialogInterface.OnClickListener(){
//	           public void onClick(DialogInterface dialog, int which) {
//	        	   deleteVideo();
//	        	   uploadVideoFail = false;
//	           }})
//	       .show();
//	   }else{
//		   uploadVideoFail = true;
//		   progressDialog = ProgressDialog.show(InformationReport.this, "正在上传视频...", "请稍等...", true, true);  
//		   new AlertDialog.Builder(InformationReport.this)
//	       .setMessage("上传视频失败，是否重试？")
//	       .setNegativeButton("放弃", new DialogInterface.OnClickListener(){
//	           public void onClick(DialogInterface dialog, int which) {
//	        	   deleteVideo();
//	        	   uploadVideoFail = false;
//	           }})
//	       .setPositiveButton("重试", new DialogInterface.OnClickListener(){
//	           public void onClick(DialogInterface dialog, int which) {
//	        	   uploadVideo();
//	        	   dialog.dismiss();
//	          }})
//	       .show();
//	   }
//   }
//   
//   private void deleteVideo(){
//	   File video = new File(Environment.getExternalStorageDirectory().getPath()+
//         		"/myImage/"+Main.fileName);
//	   if(video.exists()){
//		   video.delete();
//	   }
//   }
//   
//   private void uploadVideo(){
//	   	new Thread(){  
//	            @Override  
//	        public void run(){     
//	           try{  
//	        	   String resultStr = "";
//	       			JSONObject resultJSON;
//	        	   
//		            File video = new File(Environment.getExternalStorageDirectory().getPath()+
//		            		"/myImage/"+Main.fileName);
//		            if(video.exists()){
//		            	System.out.println(">>>>>>>>>>>>>>>>>>>"+video.getName()+" "+video.exists()+"  video.length() "+video.length());
//		            	resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", video);
//  		            System.out.println("resultJSON.toString()"+resultStr);
//  		            
//  		            if(resultStr.equals("")){
//          				handler.sendEmptyMessage(16);//
//          			}else{
//          				resultJSON = new JSONObject(resultStr);
//          				String result = resultJSON.getString("result");
//          				message = resultJSON.getString("message");
//          				if(result.equals("ok")){
//          					uploadSuccess(SystemConfig.projectArrayList.get(namePosition).get("number"), video.getAbsolutePath(), "1");
//          				}else if(result.equals("error")){
//          					handler.sendEmptyMessage(17);
//          				}
//          			}
//		            }else{
//		            	handler.sendEmptyMessage(9);
//		            }
//	           }catch(Exception e) {  
//	               Log.d("zlq", "***************************"+e.toString());  
//	          }  
//	         }  
//	       }.start(); 
//   }
//   
//   /**上传录音失败**/
//   private void uploadAudioFailed(){
//	   if(uploadAudioFail == true){
//		   new AlertDialog.Builder(InformationReport.this)
//	       .setMessage("上传录音失败，是否重试？")
//	       .setNegativeButton("放弃", new DialogInterface.OnClickListener(){
//	           public void onClick(DialogInterface dialog, int which) {
//	        	   deleteAudio();
//	        	   uploadAudioFail = false;
//	           }})
//	       .show();
//	   }else{
//		   uploadAudioFail = true;
//		   progressDialog = ProgressDialog.show(InformationReport.this, "正在上传音频...", "请稍等...", true, true);  
//		   new AlertDialog.Builder(InformationReport.this)
//	       .setMessage("上传录音失败，是否重试？")
//	       .setNegativeButton("放弃", new DialogInterface.OnClickListener(){
//	           public void onClick(DialogInterface dialog, int which) {
//	        	   deleteAudio();
//	        	   uploadAudioFail = false;
//	           }})
//	       .setPositiveButton("重试", new DialogInterface.OnClickListener(){
//	           public void onClick(DialogInterface dialog, int which) {
//	        	   uploadAudio();
//	        	   dialog.dismiss();
//	          }})
//	       .show();
//	   }
//   }
//   
//   private void deleteAudio(){
//	   final String audioName = "audio_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//       		"_"+String.format("%03d", currentAudioIndex)+".mp4";
//	   File audio = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/"+audioName);
//	   if(audio.exists()){
//		   audio.delete();
//	   }
//   }
//   
//   private void uploadAudio(){
//	 	new Thread(){  
//	            @Override  
//	        public void run(){     
//	           try{  
//	        	   String resultStr = "";
//	       			JSONObject resultJSON;
//	       			final String audioName = "audio_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//            		"_"+String.format("%03d", currentAudioIndex)+".mp4";
//		            File audio = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/"+audioName);
//		            if(audio.exists()){
//		            	System.out.println(">>>>>>>>>>>>>>>>>>>"+audio.getAbsolutePath()+" "+audio.exists()+"  video.length() "+audio.length());
//		            	resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", audio);
//  		            System.out.println("resultJSON.toString()"+resultStr);
//  		            
//  		            if(resultStr.equals("")){
//          				handler.sendEmptyMessage(18);//
//          			}else{
//          				resultJSON = new JSONObject(resultStr);
//          				String result = resultJSON.getString("result");
//          				message = resultJSON.getString("message");
//          				if(result.equals("ok")){
//          					uploadSuccess(SystemConfig.projectArrayList.get(namePosition).get("number"), audio.getAbsolutePath(), "2");
//          				}else if(result.equals("error")){
//          					handler.sendEmptyMessage(19);
//          				}
//          			}
//		            }else{
//		            	handler.sendEmptyMessage(9);
//		            }
//	           }catch(Exception e) {  
//	               Log.d("zlq", "***************************"+e.toString());  
//	          }  
//	         }  
//	       }.start(); 
//  }
//   
////   @Override 
////   public void onConfigurationChanged(Configuration newConfig){ 
////	   super.onConfigurationChanged(newConfig); 
////	   if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
////	   }
////	   else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
////	   }
////   }
//   
//   @Override
//	protected void onDestroy() {
//	   System.out.println("======================================onDestroy");
//	    if (mBMapMan != null) {
//	    	mBMapMan.getLocationManager().removeUpdates(mLocationListener);
//	        mBMapMan.destroy();
//	        mBMapMan = null;
//	    }
//	    super.onDestroy();
//	}
//	@Override
//	protected void onPause() {
//		System.out.println("======================================onPause");
//	    if (mBMapMan != null) {
//	    	mBMapMan.getLocationManager().removeUpdates(mLocationListener);
//	        mBMapMan.stop();
//	    }
//	    super.onPause();
//	}
//	@Override
//	protected void onResume() {
//		System.out.println("======================================onResume");
//	    if (mBMapMan != null) {
//	    	mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
//	        mBMapMan.start();
//	    }else{
//	    	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>MBmapMan is null");
//	    }
//	    super.onResume();
//	}
//	
//	@Override
//	protected void onStart() {
//		// TODO Auto-generated method stub
//		System.out.println("======================================onStarty");
//		super.onStart();
//	}
//	
//	@Override
//	protected void onStop() {
//		System.out.println("======================================onStop");
//		super.onStop();
//	}
//	
//	@Override
//	protected void onRestart() {
//		System.out.println("======================================onRestart");
//		super.onRestart();
//	}
}
