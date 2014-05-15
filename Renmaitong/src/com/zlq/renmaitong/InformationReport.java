package com.zlq.renmaitong;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.LocationListener;
import com.cn.rtmp.Main;
import com.zlq.json.JsonParser;
import com.zlq.util.MyDatabaseHelper;
import com.zlq.util.MyUtil;
import com.zlq.util.PictuerCompress;
import com.zlq.util.SystemConfig;

public class InformationReport extends Activity{

	public static int TAKEPHOTO = 1;
	public static int RECORDERVIDEO = 2;
	public static int AUDIORECORDER = 3;
	
	BMapManager mBMapMan = null;
	LocationListener mLocationListener = null;//createʱע���listener��Destroyʱ��ҪRemove
	Location myLocation;
	
	private ImageButton informationReport, ContactsManage, SystemSetting, exitSystem;
	private InformationListener informationListener = new InformationListener();
	private ButtonClickListener buttonClickListener = new ButtonClickListener();
	private Spinner projectTypeSpinner, projectNameSpinner;
	private Button pictureBtn, recordBtn, GspBtn, audioBtn;
	private Button btnLocation; // ����λ���ϴ�
	private int namePosition = 0;
	
	private TextView tv_count;
	
	public static int currentPhotoIndex;
	public static int currentVideoIndex;
	public static int currentAudioIndex;
	
	private boolean uploadPhotoFail = false;
	private boolean uploadVideoFail = false;
	private boolean uploadAudioFail = false;
	
	public  ProgressDialog progressDialog; 
	private String message ;
	
	ArrayAdapter<String> adapter;
	ArrayAdapter<String> nameAdapter;
	private 
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   progressDialog.dismiss();
		    	   if(SystemConfig.projectArrayList.isEmpty()){
		    		   Toast.makeText(InformationReport.this, "��Ŀ�б�Ϊ��", Toast.LENGTH_LONG).show();
		    	   }else{
		    		   pictureBtn.setEnabled(true);
		    		   recordBtn.setEnabled(true);
		    		   if (MyUtil.isNetworkAvailable(InformationReport.this)) {
		    			   GspBtn.setEnabled(true); // ���� ���ܴ�gps
		    		   }
		    		   audioBtn.setEnabled(true);
		    		   Toast.makeText(InformationReport.this, "������Ŀ���ɹ�", Toast.LENGTH_LONG).show();
			    	   adapter = new ArrayAdapter<String>(InformationReport.this, R.layout.simple_spinner_item);
				   		for(int i=0; i<SystemConfig.projectArrayList.size(); i++){
				   			adapter.add(SystemConfig.projectArrayList.get(i).get("type"));
				   		}
				   		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				   		projectTypeSpinner.setAdapter(adapter);
				   		projectTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				   			@Override
				   			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				   				String projectType = adapter.getItem(position);
				   			}
				   			@Override
				   			public void onNothingSelected(AdapterView<?> arg0) {}
				   		});
				   		
				   		
				   		nameAdapter = new ArrayAdapter<String>(InformationReport.this, R.layout.simple_spinner_item);
				   		for(int i=0; i<SystemConfig.projectArrayList.size(); i++){
				   			nameAdapter.add(SystemConfig.projectArrayList.get(i).get("name"));
				   		}
				   		nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				   		projectNameSpinner.setAdapter(nameAdapter);
				   		projectNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				   			@Override
				   			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				   				namePosition = position;
				   				currentPhotoIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("photoIndex"));
				   				currentVideoIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("videoIndex"));
				   				currentAudioIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("audioIndex"));
				   				log("position"+position+"  currentPhotoIndex"+currentPhotoIndex+
				   						"   currentVideoIndex"+currentVideoIndex+"  currentAudioIndex:"+currentAudioIndex);
				   				projectTypeSpinner.setSelection(position);
				   			}
		
				   			@Override
				   			public void onNothingSelected(AdapterView<?> arg0) {
				   			}
				   		});
		    	   }
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, message, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 3){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "����������ֵ����", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 4){
		    	   if (progressDialog!=null) {
		    		   progressDialog.dismiss();
		    	   }
		    	   Toast.makeText(InformationReport.this, "��������ʱ", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 5){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "�ϴ�GPS�ɹ�", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 6){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "��Ŀ�б�Ϊ�գ������¼�������", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 7){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "��ȡGPSʧ�ܣ���ȷ��������GPS�����Ժ�����", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 8){
		    	   Toast.makeText(InformationReport.this, "�ϴ���Ƭ�ɹ�", Toast.LENGTH_SHORT).show();
		    	   deletePhoto(photoPath);
		    	   if(uploadPhotoFail == true){
		    		   uploadPhotoFail = false;
		    	   }
//		    	   upGps();
//		    	   currentPhotoIndex++;
		       }else if(msg.what == 9){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "�ļ�������", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 10){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "�ϴ���Ƶ�ɹ�", Toast.LENGTH_SHORT).show();
		    	   deleteVideo(videoPath);
		    	   if(uploadVideoFail == true){
		    		   uploadVideoFail = false;
		    	   }
//		    	   upGps();
//		    	   currentVideoIndex++;
		       }else if(msg.what == 11){
		    	   Toast.makeText(InformationReport.this, "��Ƭ������", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 12){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "�ϴ�¼���ɹ�", Toast.LENGTH_SHORT).show();
		    	   if(uploadAudioFail == true){
		    		   uploadAudioFail = false;
		    	   }
		    	   deleteAudio(audioPath);
//		    	   upGps();
//		    	   currentAudioIndex++;
		       }else if(msg.what == 13){
		    	   Toast.makeText(InformationReport.this, "��Ƭ������", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 14){
		    	   Toast.makeText(InformationReport.this, "�ϴ���Ƭʧ��", Toast.LENGTH_SHORT).show();
		    	   uploadPictureFailed();
		       }else if(msg.what == 15){
		    	   Toast.makeText(InformationReport.this, message, Toast.LENGTH_SHORT).show();
		    	   uploadPictureFailed();
		       }else if(msg.what == 16){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "�ϴ���Ƶ��������ʱ", Toast.LENGTH_SHORT).show();
		    	   uploadVideoFailed();
		       }else if(msg.what == 17){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "�ϴ���Ƶ����"+message, Toast.LENGTH_SHORT).show();
		    	   uploadVideoFailed();
		       }else if(msg.what == 18){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "�ϴ�¼����������ʱ", Toast.LENGTH_SHORT).show();
		    	   uploadAudioFailed();
		       }else if(msg.what == 19){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(InformationReport.this, "�ϴ�¼������"+message, Toast.LENGTH_SHORT).show();
		    	   uploadAudioFailed();
		       }
		       else if(msg.what == 20){  //�����ɹ�
		    	    reUPnum++;
					threadEnd++;
					reUp_tv.setText("δ�����ļ�"+(total-reUPnum) +"��");
					yiUpTv.setText("�Ѳ����ļ�"+reUPnum +"��");
					log("�����ֳ������Ӱ���ļ�"+reUPnum+"��");
					log("------end--�����ɹ�-------start----------------"+threadEnd + "--"+threadStart);
					if(threadEnd == threadMax){
					threadEnd = 0;
					threadStart = 0;
					rupload();
					}
		       }
		       else if(msg.what == 21){  //����ʧ��
		    	   threadEnd++;
					log("--------end----����ʧ��----------start------"+threadEnd + "--"+threadStart);
					if(threadEnd == threadMax){
						threadEnd = 0;
						threadStart = 0;
						rupload();
					}
		       }
		       else if(msg.what == 22){  //
		    	   
		       }
		       
		}   
	};  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_informationreport);
		intiView();
		

		log("======================================onCreate");
		
//		YuvImage myYuvImage = new YuvImage(yuv, format, width, height, strides);
		
		mBMapMan = new BMapManager(this);
		mBMapMan.init("B2CC3DD73EAA572CAD1B4B81C84798F67C98C1C3", null);
		
        // ע�ᶨλ�¼�
        mLocationListener = new LocationListener(){
			@Override
			public void onLocationChanged(Location location) {
				if(location != null){
					String strLog = String.format("����ǰ��λ��:\r\n" +
							"����:%f\r\n" +
							"γ��:%f",
							location.getLongitude(), location.getLatitude());
					myLocation = location;
			        log(strLog+"==========================");
				}
			}
        };
		
        if(SystemConfig.projectArrayList.isEmpty()){
        	progressDialog = ProgressDialog.show(InformationReport.this, "����������...", "���Ե�...", true, true);  
    		new Thread(){  
                @Override  
            public void run(){     
               try{  
            	   loadReportData();
           			log(SystemConfig.projectArrayList.size()+"loadReportData()");
               }catch(Exception e) { 
            	   e.printStackTrace();
                   Log.d("zlq", "***************************"+e.toString());  
              }  
             }  
           }.start();  
        }else{
           log("��Ŀ�б��Ѿ�����");

 		   pictureBtn.setEnabled(true);
 		   recordBtn.setEnabled(true);
 		   if (MyUtil.isNetworkAvailable(InformationReport.this)) {
 		   GspBtn.setEnabled(true);
 		   }
 		   audioBtn.setEnabled(true);
	    	   adapter = new ArrayAdapter<String>(InformationReport.this, R.layout.simple_spinner_item);
		   		for(int i=0; i<SystemConfig.projectArrayList.size(); i++){
		   			adapter.add(SystemConfig.projectArrayList.get(i).get("type"));
		   		}
		   		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		   		projectTypeSpinner.setAdapter(adapter);
		   		projectTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		   			@Override
		   			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		   				String projectType = adapter.getItem(position);
		   			}
		   			@Override
		   			public void onNothingSelected(AdapterView<?> arg0) {}
		   		});
		   		
		   		nameAdapter = new ArrayAdapter<String>(InformationReport.this, R.layout.simple_spinner_item);
		   		for(int i=0; i<SystemConfig.projectArrayList.size(); i++){
		   			nameAdapter.add(SystemConfig.projectArrayList.get(i).get("name"));
		   		}
		   		nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		   		projectNameSpinner.setAdapter(nameAdapter);
		   		projectNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		   			@Override
		   			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		   				namePosition = position;
		   				currentPhotoIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("photoIndex"));
		   				currentVideoIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("videoIndex"));
		   				currentAudioIndex = Integer.parseInt(SystemConfig.projectArrayList.get(position).get("audioIndex"));
		   				log("position"+position+"  currentPhotoIndex"+currentPhotoIndex+
		   						"   currentVideoIndex"+currentVideoIndex+"  currentAudioIndex:"+currentAudioIndex);
		   				projectTypeSpinner.setSelection(position);
		   			}

		   			@Override
		   			public void onNothingSelected(AdapterView<?> arg0) {
		   			}
		   		});
 	   
        }
	}
	
	TextView reUp_tv ;
	TextView yiUpTv ;
	Button btn_reUp;
	private int total;
	
	void intiView(){
		
		btnLocation = (Button)findViewById(R.id.btnLocation);
		
		yiUpTv = (TextView)findViewById(R.id.yiUpTv);
		reUp_tv = (TextView)findViewById(R.id.reUpTv);
		btn_reUp = (Button)findViewById(R.id.btnReUp);
		btn_reUp.setVisibility(View.INVISIBLE);
		reUp_tv.setVisibility(View.INVISIBLE);
		yiUpTv.setVisibility(View.INVISIBLE);
		
		informationReport = (ImageButton) findViewById(R.id.tabbar1);
		informationReport.setPressed(true);
		ContactsManage = (ImageButton) findViewById(R.id.tabbar2);
		SystemSetting = (ImageButton) findViewById(R.id.tabbar4);
		exitSystem = (ImageButton) findViewById(R.id.tabbar5);
		projectTypeSpinner = (Spinner) findViewById(R.id.projectTypeSpinner);
		projectNameSpinner = (Spinner) findViewById(R.id.projectnameSpinner);
		
		pictureBtn = (Button) findViewById(R.id.btnPicture);
		recordBtn = (Button) findViewById(R.id.btnRecord);
		GspBtn = (Button) findViewById(R.id.btnGps);
		audioBtn = (Button) findViewById(R.id.btnAudio);
		pictureBtn.setEnabled(false);
		recordBtn.setEnabled(false);
		GspBtn.setEnabled(false);
		audioBtn.setEnabled(false);
		btn_reUp.setEnabled(false);
		
		pictureBtn.setOnClickListener(buttonClickListener);
		GspBtn.setOnClickListener(buttonClickListener);
		recordBtn.setOnClickListener(buttonClickListener);
		audioBtn.setOnClickListener(buttonClickListener);
		btn_reUp.setOnClickListener(buttonClickListener);
		btnLocation.setOnClickListener(buttonClickListener);
		
		
		ContactsManage.setOnClickListener(informationListener);
		SystemSetting.setOnClickListener(informationListener);
		exitSystem.setOnClickListener(informationListener);
		
		if (!MyUtil.isNetworkAvailable(InformationReport.this)) {//����
			ContactsManage.setEnabled(false);
			SystemSetting.setEnabled(false);
		}
		dir = new File(Environment.getExternalStorageDirectory().getPath()+ "/myImage");
		getVideo_photoFile(weiUPList,dir);
		if (MyUtil.isNetworkAvailable(InformationReport.this)) {//����
			if (weiUPList.size()>0) {
				total = weiUPList.size();
				yiUpTv.setVisibility(View.VISIBLE);
				yiUpTv.setText("�Ѳ����ļ�0��");
				reUp_tv.setVisibility(View.VISIBLE);
				reUp_tv.setText("δ�����ļ�"+weiUPList.size()+"��");
				btn_reUp.setVisibility(View.VISIBLE);
				btn_reUp.setEnabled(true);
			}	
		}
		  
		tv_count = (TextView) findViewById(R.id.tv_count);
	}
	File dir;
	int reUPnum= 0;
	int threadStart = 0;
	int threadEnd = 0;
	final int threadMax = 2;
	boolean isUpLoading = false;
	final LinkedList<FileInfo> weiUPList = new LinkedList<InformationReport.FileInfo>();
	protected void rupload(){
		//�ϴ�δ�ϴ���
		log("weiUPList.size is ---before--------"+weiUPList.size());
		if (!weiUPList.isEmpty()) {
			weiUPList.clear();
		}
		getVideo_photoFile(weiUPList,dir);
		if (weiUPList.size()==0) {
			isUpLoading = false;//�ϴ�����
			btn_reUp.setEnabled(true);
			Toast.makeText(InformationReport.this, "��������", Toast.LENGTH_SHORT).show();
			return;
		}
		log("weiUPList.size is -----after------"+weiUPList.size());
		String nameString = "";
		String absPath = "";
		for ( FileInfo f : weiUPList) {
			nameString = f.displayName;
			absPath = f.path;
			if (nameString.startsWith("mvideo_")) {
				String[] ss = nameString.split("_");
				String proNum = ss[1];
				String fName = nameString;
				String fType = "1";
				log("********************"+ proNum + fName + fType);
				uploadVideo2(nameString);
				threadStart++;
//				log(threadStart+" is threadStart");
				log("ѭ����threadStart is "+threadStart);
				if (threadStart == threadMax) {
					return;
				}
			}else if (nameString.startsWith("mphoto_")) {
				String[] ss = nameString.split("_");
				String proNum = ss[1];
				String fName = nameString;
				String fType = "0";
				log("********************"+ proNum + fName + fType);
				uploadPhoto2(proNum, absPath);
				threadStart++;
//				log(threadStart+" is threadStart");
				log("ѭ����threadStart is "+threadStart);
				if (threadStart  == threadMax) {
					return;
				}
			}else if (nameString.startsWith("maudio_")) {
				String[] ss = nameString.split("_");
				String proNum = ss[1];
				String fName = nameString;
				String fType = "2";
				log("********************"+ proNum + fName + fType);
				uploadAudio2(nameString);
				threadStart++;
//				log(threadStart+" is threadStart");
				log("ѭ����threadStart is "+threadStart);
				if (threadStart  == threadMax) {
					return;
				}
			}
		}
	}
	public class FileInfo{
		String displayName;//����  
		String path;//·��
	}
	 private void getVideo_photoFile(final LinkedList<FileInfo> list,File file){//����ļ�
	    	
	    	file.listFiles(new FileFilter(){

				@Override
				public boolean accept(File file) {
					String name = file.getName();
					System.out.println("name----accept--------------------"+name);
					if(name.startsWith("mvideo_")||name.startsWith("mphoto_")||name.startsWith("maudio_"))
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {// ����menu���¼�
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {// ���ط��ذ�ť�¼�
			leave();
		}
		return true;
	}
	
    /**�뿪����**/
    private void leave(){
        new AlertDialog.Builder(InformationReport.this)
        .setMessage("��ȷ��Ҫ�ر���")
        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
            	informationReport.setPressed(true);
            }})
        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }})
        .show();
    }
    
   class ButtonClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String sdStatus = Environment.getExternalStorageState();
			File photoDir = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/");
			switch (v.getId()) {
			case R.id.btnPicture:
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
                    Log.v("TestFile", "SD card is not avaiable/writeable right now.");
                    Toast.makeText(InformationReport.this, "SD��������", Toast.LENGTH_SHORT).show();
                    return;
                }
				 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				 
				 if(!photoDir.exists()){
					 photoDir.mkdir();
				 }
//				 File test = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/photo_"+
				 File test = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/mphoto_"+
				 SystemConfig.projectArrayList.get(namePosition).get("number")+"_"+SystemConfig.userName+"_"+String.format("%03d", currentPhotoIndex)+".jpg");
				 intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(test));
				 photoPath = test.getAbsolutePath();
	             startActivityForResult(intent, TAKEPHOTO);
				 break;
			case R.id.btnRecord:
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
                    Log.v("TestFile", "SD card is not avaiable/writeable right now.");
                    Toast.makeText(InformationReport.this, "SD��������", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!photoDir.exists()){
					 photoDir.mkdir();
				 }
				new Thread(){  
		            @Override  
		        public void run(){     
		           try{  
		        	   record();
		           }catch(Exception e) {  
		               Log.d("zlq", "***************************"+e.toString());  
		          }  
		         }  
		       }.start();  
				break;
			case R.id.btnGps:
//				MyUtil.initGPS(InformationReport.this);
				if (!MyUtil.isNetworkAvailable(InformationReport.this)) {
					Toast.makeText(InformationReport.this, "���粻���ã�ȡ���ϴ�", Toast.LENGTH_SHORT).show();
					return;
				}
			    progressDialog = ProgressDialog.show(InformationReport.this, "�����ϴ�GPS...", "���Ե�...", true, true);  
				new Thread(){  
		            @Override  
		        public void run(){     
		           try{  
		        	   getGps();
		           }catch(Exception e) {  
		               Log.d("zlq", "***************************"+e.toString());  
		          }  
		         }  
		       }.start();  
				break;
			case R.id.btnAudio:
			    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
	                Log.v("TestFile", "SD card is not avaiable/writeable right now.");
	                Toast.makeText(InformationReport.this, "SD��������", Toast.LENGTH_SHORT).show();
	                return;
	            }		
			    if(!photoDir.exists()){
					 photoDir.mkdir();
				 }
				Intent audioIntent = new Intent(InformationReport.this, AudioActivity.class);
				final String audioName = "maudio_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
	            		"_"+SystemConfig.userName+"_"+String.format("%03d", currentAudioIndex)+".mp4";
				audioPath = audioName;
				audioIntent.putExtra("audioName", audioName);
	    		startActivityForResult(audioIntent, AUDIORECORDER);
				break;
			case R.id.projectnameSpinner:
				break;
			case R.id.btnReUp:
				if (!isUpLoading) {
					if (weiUPList!=null) {
						weiUPList.clear();
					}
					getVideo_photoFile(weiUPList,dir);
					if (weiUPList.size()==0){
						Toast.makeText(InformationReport.this, "û����Ҫ�������ļ�", Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(InformationReport.this, "��ʼ����", Toast.LENGTH_SHORT).show();
					rupload();
					isUpLoading = true;
					btn_reUp.setEnabled(false);
				}
				break;
			case R.id.btnLocation:
				Intent intent2 = new Intent(InformationReport.this,SubMitLocation.class);
				startActivity(intent2);
				break;
			default:
				break;
			}
		}
    }
    
    class InformationListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tabbar2:
				startActivity(new Intent(InformationReport.this, ContactsManage.class));
				finish();
				break;
			case R.id.tabbar4:
				startActivity(new Intent(InformationReport.this, SystemSetting.class));
				finish();
				break;
			case R.id.tabbar5:
				leave();
				break;
			}
		}
    }
   
    private void record(){
    	log(">>>>>>>>>>>>>>>>>>>>>>>��Ƶ");
//    	if(MyUtil.isNetworkAvailable(InformationReport.this)){
    		Main.url = SystemConfig.RTMPUrl;
//    		final String fileName = "video_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//            		"_"+String.format("%03d", currentVideoIndex)+".mp4";
    		
    		final String fileName = "mvideo_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
            		"_"+SystemConfig.userName+"_"+String.format("%03d", currentVideoIndex)+".mp4";
    		videoPath = fileName;
    		Main.fileName = fileName;
    		Main.projNumber = SystemConfig.projectArrayList.get(namePosition).get("number");
    		log(Main.url+ Main.fileName + Main.projNumber);
    		Intent intent = new Intent(InformationReport.this, RecorderActivity.class);
    		startActivityForResult(intent, RECORDERVIDEO);
//    	}else{
//    		 new AlertDialog.Builder(InformationReport.this)
//    	        .setMessage("��ǰ���粻���ã��Ƿ������磿")
//    	        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
//    	            public void onClick(DialogInterface dialog, int which) {
//    	            	//���뱾��
//    	            }})
//    	        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
//    	                public void onClick(DialogInterface dialog, int which) {
//    	                    
//    	                }})
//    	        .show();
//    	}
    }
    
    private void getGps(){
    	
    	Location location1 = MyDatabaseHelper.getCurrentLocation(InformationReport.this);
    	Location location2 = myLocation;
    	Location location = null;
    	if(location1 != null){
    		location = location1;
    		log("location1 is not null");
    	}
    	if(location2 != null){
    		location = location2;
    		log("baidu  location2 is not null");
    	}
    	if(location != null){
    		if(!SystemConfig.projectArrayList.isEmpty()){
    			log("�����ϴ�GPS"+location.getLatitude()+"  "+location.getLongitude());
        		//�ϴ���Ϣ{"sendGPS":{"projNum":"MT201212002","userName":"1001","xxx":"114.4546","yyy":"32.451245"},
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
        					
        			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
        			log("------0521-------********************"+resultStr);
        			if(resultStr.equals("")){
        				handler.sendEmptyMessage(4);//
        			}else{
        				resultJSON = new JSONObject(resultStr);
        				result = resultJSON.getString("result");
        				log("------0521-------********************"+result);
        				message = resultJSON.getString("message");
        				if(result.equals("ok")){
        					handler.sendEmptyMessage(5);
        				}else if(result.equals("error")){
        					handler.sendEmptyMessage(2);
        				}else{
        					handler.sendEmptyMessage(3);
        				}
        			}
        		} catch (JSONException e) {
        			handler.sendEmptyMessage(4);
        			e.printStackTrace();
        		}
    		}else{
    			handler.sendEmptyMessage(6);
    		}
    	}else{
    		handler.sendEmptyMessage(7);
    	}
    	
    }
    
private void getGps2(){
    	
    	Location location1 = MyDatabaseHelper.getCurrentLocation(InformationReport.this);
    	Location location2 = myLocation;
    	Location location = null;
    	if(location1 != null){
    		location = location1;
    		log("location1 is not null");
    	}
    	if(location2 != null){
    		location = location2;
    		log("baidu  location2 is not null");
    	}
    	if(location != null){
    		if(!SystemConfig.projectArrayList.isEmpty()){
    			log("�����ϴ�GPS"+location.getLatitude()+"  "+location.getLongitude());
        		//�ϴ���Ϣ{"sendGPS":{"projNum":"MT201212002","userName":"1001","xxx":"114.4546","yyy":"32.451245"},
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
        					
        			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
        			log("------0521-------********************"+resultStr);
        			if(resultStr.equals("")){
//        				handler.sendEmptyMessage(4);//
        			}else{
        				resultJSON = new JSONObject(resultStr);
        				result = resultJSON.getString("result");
        				log("------0521-------********************"+result);
//        				message = resultJSON.getString("message");
        				if(result.equals("ok")){
//        					handler.sendEmptyMessage(5);
        				}else if(result.equals("error")){
//        					handler.sendEmptyMessage(2);
        				}else{
//        					handler.sendEmptyMessage(3);
        				}
        			}
        		} catch (JSONException e) {
//        			handler.sendEmptyMessage(4);
        			e.printStackTrace();
        		}
    		}else{
//    			handler.sendEmptyMessage(6);
    		}
    	}else{
//    		handler.sendEmptyMessage(7);
    	}
    	
    }
    
    
    String videoPath = "";
    String audioPath = "";
    String photoPath ="";

   
    /**
     *  backup ���ݵ� "konruns-backup" �ļ�����
     * @param srcFileAbspath Դ�ļ�·��
     */
	public void doBackup(String srcFileAbspath) {
		
		File file = new File(srcFileAbspath);
		File file_backup = new File(new StringBuilder(Environment
				.getExternalStorageDirectory().getAbsolutePath())
				.append(File.separator).append("konruns-backup").toString()
				+ "/" + file.getName());
		if (file.exists()) {
			try {
				copyFile(file, file_backup);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAKEPHOTO){
        	  if (resultCode == Activity.RESULT_OK) {
        		  deleteLatestPhoto(InformationReport.this);
        		  if (!photoPath.equals("")) {
        			  doBackup(photoPath);
        			  if (MyUtil.isNetworkAvailable(this)) {
        				  uploadPhoto(photoPath);
        			  }
        			currentPhotoIndex++;
      				
//      			project.put("photoIndex",
//      						temp.getString("photoIndex"));
      				SystemConfig.projectArrayList.get(namePosition).put("photoIndex",
      						currentPhotoIndex+"");
      				MyUtil.saveProjMaxIndex2preference(this, SystemConfig.projectArrayList
  						.get(namePosition)
  						.get("number")+"p", currentPhotoIndex+"");
//      				save2Preferences_photo(SystemConfig.projectArrayList
//      						.get(namePosition)
//      						.get("number"), currentPhotoIndex+"");
      				log("photoPath is "+photoPath);
      				log("currentPhotoIndex is "+currentPhotoIndex);
        			  
        		  }
              }
        }else if(requestCode == RECORDERVIDEO){
        	if(resultCode == Activity.RESULT_OK){
        		if (videoPath.equals(""))
        			return;
        		doBackup(Environment.getExternalStorageDirectory().getPath()+
	            		"/myImage/"+videoPath);
                if (MyUtil.isNetworkAvailable(this)) {
                	progressDialog = ProgressDialog.show(InformationReport.this, "�����ϴ���Ƶ...", "���Ե�...", true, true);  
                	uploadVideo(videoPath);
				}
                currentVideoIndex++;
                
//  			project.put("photoIndex",
//  						temp.getString("photoIndex"));
  				SystemConfig.projectArrayList.get(namePosition).put("videoIndex",
  						currentVideoIndex+"");
  				MyUtil.saveProjMaxIndex2preference(this, SystemConfig.projectArrayList
						.get(namePosition)
						.get("number")+"v", currentVideoIndex+"");
//  				save2Preferences_photo(SystemConfig.projectArrayList
//  						.get(namePosition)
//  						.get("number"), currentPhotoIndex+"");
  				
  				log("currentVideoIndex is "+currentVideoIndex);
        	}
        }else if(requestCode == AUDIORECORDER){
         	if(resultCode == Activity.RESULT_OK){
         		if (audioPath.equals(""))
        			return;
         		doBackup(Environment.getExternalStorageDirectory().getPath()+"/myImage/"+audioPath);
                if (MyUtil.isNetworkAvailable(this)) {
                	progressDialog = ProgressDialog.show(InformationReport.this, "�����ϴ���Ƶ...", "���Ե�...", true, true);  
                	 uploadAudio(audioPath);
				}
                currentAudioIndex++;
//  			project.put("photoIndex",
//  						temp.getString("photoIndex"));
  				SystemConfig.projectArrayList.get(namePosition).put("audioIndex",
  						currentAudioIndex+"");
  				MyUtil.saveProjMaxIndex2preference(this, SystemConfig.projectArrayList
						.get(namePosition)
						.get("number")+"a", currentAudioIndex+"");
//  				save2Preferences_photo(SystemConfig.projectArrayList
//  						.get(namePosition)
//  						.get("number"), currentPhotoIndex+"");
  				log("currentAudioIndex is "+currentAudioIndex);
        	}
        }
      
   }
    
   private void loadReportData(){
		String resultStr = "";
		JSONObject resultJSON;
		
		String result = "";
		int number;
		String msgStr = "";
		try {
			
			//{"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
//			msgStr = "{'getMyProjectList':{'userName':'"+SystemConfig.userName+
//					"'}, 'token':'"+SystemConfig.token+"'}";
			if (!MyUtil.isNetworkAvailable(InformationReport.this)) {
				//����
				resultStr = MyUtil.getFromPreference(InformationReport.this,MyUtil.KEY_PROJECT_LIST);
			}else {
				//����
				// {"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
				msgStr = "{'getMyProjectList2':{'userName':'"+SystemConfig.userName+
						"'},'token':'"+SystemConfig.token+"'}";
				resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
				Log.d("InformationReport", "result is "+resultStr);
				if (!resultStr.equals("")) {
					MyUtil.saveJson2preference(InformationReport.this, MyUtil.KEY_PROJECT_LIST, resultStr);
				}
			}
			log("peter"+resultStr);
			if(resultStr.equals("")){
				handler.sendEmptyMessage(4);//
			}else{
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if(result.equals("ok")){
					number = resultJSON.getInt("number");
					if(number != 0){
						SystemConfig.RTMPUrl = resultJSON.getString("rtmp");
						SystemConfig.videosecond = resultJSON.getString("videosecond");
						SystemConfig.audiosecond = resultJSON.getString("audiosecond");
						log(">>>>>>>>>>>>>>>>>"+SystemConfig.videosecond+" SystemConfig.audiosecond"+SystemConfig.audiosecond);
						JSONObject projectList = resultJSON.getJSONObject("myProjectList");
						JSONArray projectArray = projectList.names();
						for(int i=0; i<projectArray.length(); i++){
							JSONObject temp = projectList.getJSONObject(projectArray.getString(i));
							HashMap<String, String> project = new HashMap<String, String>();
							project.put("type",temp.getString("projType"));
							project.put("number", temp.getString("projNum"));
							project.put("name", temp.getString("projName"));
							project.put("photoIndex", temp.getString("photoIndex"));
							project.put("videoIndex", temp.getString("videoIndex"));
							project.put("audioIndex", temp.getString("audioIndex"));
							
							
//							int localPhdex = Integer.parseInt(settings.getString(temp.getString("projNum"), "-1"));
							int localPhdex = Integer.parseInt(MyUtil.getProjMaxIndexFromPreference(InformationReport.this, temp.getString("projNum")+"p"));
							int serverPhdex = Integer.parseInt(temp.getString("photoIndex"));
							
							log("localPhdex is *********  "+localPhdex);
							log("serverPhdex is *********  "+serverPhdex);
							if (localPhdex < serverPhdex) {
//								save2Preferences_photo(temp.getString("projNum"),temp.getString("photoIndex"));
								MyUtil.saveProjMaxIndex2preference(InformationReport.this, temp.getString("projNum")+"p", serverPhdex+"");
							}else{
								project.put("photoIndex",""+localPhdex);
							}
							
//							int localVidex = Integer.parseInt(settings.getString(temp.getString("projNum")+"v", "-1"));
							int localVidex = Integer.parseInt(MyUtil.getProjMaxIndexFromPreference(InformationReport.this, temp.getString("projNum")+"v"));
							
							int serverVidex = Integer.parseInt(temp.getString("videoIndex"));
							
							log("localVidex is ********* "+localVidex);
							log("serverVidex is ********* *********  "+serverVidex);
							if (localVidex < serverVidex) {
//								save2Preferences_video(temp.getString("projNum")+"v",temp.getString("videoIndex"));
								MyUtil.saveProjMaxIndex2preference(InformationReport.this, temp.getString("projNum")+"v", serverVidex+"");
							}else{
								project.put("videoIndex",""+localVidex);
							}
							
							int localAudex = Integer.parseInt(MyUtil.getProjMaxIndexFromPreference(InformationReport.this, temp.getString("projNum")+"a"));
							
							int serverAudex = Integer.parseInt(temp.getString("audioIndex"));
							
							log("localAudex is ********* "+localAudex);
							log("serverAudex is ********* *********  "+serverAudex);
							if (localAudex < serverAudex) {
//								save2Preferences_video(temp.getString("projNum")+"v",temp.getString("videoIndex"));
								MyUtil.saveProjMaxIndex2preference(InformationReport.this, temp.getString("projNum")+"a", serverAudex+"");
							}else{
								project.put("audioIndex",""+localAudex);
							}
							
							SystemConfig.projectArrayList.add(project);
						}
					}
					handler.sendEmptyMessage(1);
				}else if(result.equals("error")){
					handler.sendEmptyMessage(2);
				}else{
					handler.sendEmptyMessage(3);
				}
			}
		} catch (JSONException e) {
//			handler.sendEmptyMessage(4);
			e.printStackTrace();
		}
   }
   private void uploadSuccess2(String projNumber, String fileName, String fileType){
	   log("uploadSuccess2 ");
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
			log("uploadSuccess resultStr "+resultStr);
			if(resultStr.equals("")){
				handler.sendEmptyMessage(21);//��ʾ��ʱ  
			}else{
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if(result.equals("ok")){
					if (file.exists()) {
						if(file.delete()){
								handler.sendEmptyMessage(20);
								log(">>>>>>>>>>>>>>>deleted ");
						}else {
							handler.sendEmptyMessage(21);
						}
					}else {
						handler.sendEmptyMessage(21);
					}
					log(">>>>>>>>>>>>>>>file.exists()  "+file.exists());
				}else if(result.equals("error")){
					handler.sendEmptyMessage(21);
				}
			}
		} catch (JSONException e) {
			handler.sendEmptyMessage(21);
			e.printStackTrace();
		}
   }
   //{"saveAttach":{"userName":�û���,"projNum":��Ŀ���,"fileName":�ļ���,"attachType"�ļ�����},"token":����}
   private void uploadSuccess(String projNumber, String fileName, String fileType){
	   log("uploadSuccess ");
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
			log("uploadSuccess resultStr "+resultStr);
			if(resultStr.equals("")){
				if(fileType.equals("0")){
					handler.sendEmptyMessage(8);//UI�߳��������UI�߳�  
				}else if(fileType.equals("1")){
					handler.sendEmptyMessage(16);//UI�߳��������UI�߳�  
				}else if(fileType.equals("2")){
					handler.sendEmptyMessage(18);//UI�߳��������UI�߳�  
				}
			}else{
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if(result.equals("ok")){
					if(fileType.equals("0")){
						handler.sendEmptyMessage(8);//UI�߳��������UI�߳�  
					}else if(fileType.equals("1")){
						handler.sendEmptyMessage(10);//UI�߳��������UI�߳�  
					}else if(fileType.equals("2")){
						handler.sendEmptyMessage(12);//UI�߳��������UI�߳�  
					}
					log(">>>>>>>>>>>>>>>file.exists()  "+file.exists());
					
				}else if(result.equals("error")){
					if(fileType.equals("0")){
						handler.sendEmptyMessage(14);//UI�߳��������UI�߳�  
					}else if(fileType.equals("1")){
						handler.sendEmptyMessage(17);//UI�߳��������UI�߳�  
					}else if(fileType.equals("2")){
						handler.sendEmptyMessage(19);//UI�߳��������UI�߳�  
					}
				}
			}
		} catch (JSONException e) {
			handler.sendEmptyMessage(4);
			e.printStackTrace();
		}
   }
   
   private void deleteLatestPhoto(Context mContext) {
       String[] projection = new String[] { MediaStore.Images.ImageColumns._ID,
               MediaStore.Images.ImageColumns.DATE_TAKEN };
       Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
               null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
       if (cursor != null) {
           cursor.moveToFirst();
           ContentResolver cr = mContext.getContentResolver();
           cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                   BaseColumns._ID + "=" + cursor.getString(0), null);
       }
   }

   /**�ϴ���Ƭʧ��**/
   private void uploadPictureFailed(){
	   if(uploadPhotoFail == true){
		   new AlertDialog.Builder(InformationReport.this)
	       .setMessage("�ϴ���Ƭʧ�ܣ��Ƿ����ԣ�")
	       .setNegativeButton("����", new DialogInterface.OnClickListener(){
	           public void onClick(DialogInterface dialog, int which) {
//	           	 deletePhoto();
	           	 uploadPhotoFail = false;
	           }})
	       .show();
	   }else{
		   uploadPhotoFail = true;
		   new AlertDialog.Builder(InformationReport.this)
	       .setMessage("�ϴ���Ƭʧ�ܣ��Ƿ����ԣ�")
	       .setNegativeButton("����", new DialogInterface.OnClickListener(){
	           public void onClick(DialogInterface dialog, int which) {
//	           	 deletePhoto();
	           	 uploadPhotoFail = false;
	           }})
	       .setPositiveButton("����", new DialogInterface.OnClickListener(){
	           public void onClick(DialogInterface dialog, int which) {
//	           	 uploadPhoto();
	        	   if (!photoPath.equals("")) {
	        			  uploadPhoto(photoPath);
	        		  }
	           	 dialog.dismiss();
	          }})
	       .show();
	   }
   }
   String getProjectNum(String fName){
		 String[] ss = fName.split("_");
//		 log("ss[1] is "+ss[1]);
		return ss[1];
	}
   private void uploadPhoto(final String photoPath){
//	   final String fileName =  Environment.getExternalStorageDirectory().getPath()+"/myImage/photo_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//         		"_"+String.format("%03d", currentPhotoIndex)+".jpg";
         final File test = new File(photoPath);
         log(">>>>>>>>>>>>>>>>>>>>>>>>>>>test.exists  "+ test.exists() +"   test.length()"+test.length());
         log("fileName"+photoPath);
         if(test.exists()){
//             progressDialog = ProgressDialog.show(InformationReport.this, "�����ϴ���Ƭ...", "���Ե�...", true, true);  
              new Thread(){  
       	            @Override  
       	        public void run(){     
       	           try{  
       	        	   String resultStr = "";
       	       			JSONObject resultJSON;
       	        	   
       		            File picture = new File(photoPath);
       		            picture = PictuerCompress.compFile(photoPath);
       		           	log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+"picture.length()"+picture.length());
       		            resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", picture);
            		        log("resultJSON.toString()"+resultStr);
            		            
            		        if(resultStr.equals("")){
                    			handler.sendEmptyMessage(14);//
                    		}else{
                    			resultJSON = new JSONObject(resultStr);
                    			String result = resultJSON.getString("result");
                  				message = resultJSON.getString("message");
                  				if(result.equals("ok")){
                  					uploadSuccess(getProjectNum(test.getName()), photoPath, "0");
                  				}else if(result.equals("error")){
                  					handler.sendEmptyMessage(15);
                  				}
                  			}
       	           }catch(Exception e) {  
       	               Log.d("zlq", "***************************"+e.toString());  
       	          }  
       	         }  
       	       }.start(); 
         }else{
       	  handler.sendEmptyMessage(13);
         }
   }
   private void uploadPhoto2(final String proNum,final String absPath){
//	   final String fileName =  Environment.getExternalStorageDirectory().getPath()+"/myImage/photo_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//         		"_"+String.format("%03d", currentPhotoIndex)+".jpg";
         final File test = new File(absPath);
         log(">>>>>>>>>>>>>>>>>>>>>>>>>>>test.exists  "+ test.exists() +"   test.length()"+test.length());
         log("absPath  "+absPath);
         if(test.exists()){
//             progressDialog = ProgressDialog.show(InformationReport.this, "�����ϴ���Ƭ...", "���Ե�...", true, true);  
              new Thread(){  
       	            @Override  
       	        public void run(){     
       	           try{  
       	        	    String resultStr = "";
       	       			JSONObject resultJSON;
       	        	   
       		            File picture = new File(absPath);
       		            picture = PictuerCompress.compFile(absPath);
       		           	log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+"picture.length()"+picture.length());
       		            resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", picture);
            		    log("resultJSON.toString()"+resultStr);
            		            
            		        if(resultStr.equals("")){
                    			handler.sendEmptyMessage(21);//
                    		}else{
                    			resultJSON = new JSONObject(resultStr);
                    			String result = resultJSON.getString("result");
                  				message = resultJSON.getString("message");
                  				if(result.equals("ok")){
                  					log("uploadPhoto2 ok");
                  					uploadSuccess2(proNum, absPath, "0");
                  				}else if(result.equals("error")){
                  					handler.sendEmptyMessage(21);
                  				}
                  			}
       	           }catch(Exception e) {  
       	        	   e.printStackTrace();
       	               Log.d("zlq", "***************************"+e.toString());
       	            handler.sendEmptyMessage(21);
       	          }  
       	         }  
       	       }.start(); 
         }else{
        	 handler.sendEmptyMessage(21);
         }
   }
   private void deletePhoto(final String pPath){
//	   final String fileName =  Environment.getExternalStorageDirectory().getPath()+"/myImage/photo_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//         		"_"+String.format("%03d", currentPhotoIndex)+".jpg";
      File file = new File(pPath);
		if (file.exists()) {
			file.delete();
		}
   }
   
   /**�ϴ���Ƶʧ��**/
   private void uploadVideoFailed(){
	   if(uploadVideoFail == true){
		   new AlertDialog.Builder(InformationReport.this)
	       .setMessage("�ϴ���Ƶʧ�ܣ��Ƿ����ԣ�")
	       .setNegativeButton("����", new DialogInterface.OnClickListener(){
	           public void onClick(DialogInterface dialog, int which) {
//	        	   deleteVideo();
	        	   uploadVideoFail = false;
	           }})
	       .show();
	   }else{
		   uploadVideoFail = true;
		   progressDialog = ProgressDialog.show(InformationReport.this, "�����ϴ���Ƶ...", "���Ե�...", true, true);  
		   new AlertDialog.Builder(InformationReport.this)
	       .setMessage("�ϴ���Ƶʧ�ܣ��Ƿ����ԣ�")
	       .setNegativeButton("����", new DialogInterface.OnClickListener(){
	           public void onClick(DialogInterface dialog, int which) {
//	        	   deleteVideo();
	        	   uploadVideoFail = false;
	           }})
	       .setPositiveButton("����", new DialogInterface.OnClickListener(){
	           public void onClick(DialogInterface dialog, int which) {
	        	   uploadVideo(videoPath);
	        	   dialog.dismiss();
	          }})
	       .show();
	   }
   }
    
	private void deleteVideo(final String vPath) {
		File video = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/myImage/" + vPath);
//		File video_backup = new File(new StringBuilder(Environment
//				.getExternalStorageDirectory().getAbsolutePath())
//				.append(File.separator).append("konruns-backup").toString()
//				+ "/" + vPath);
		if (video.exists()) {
//			try {
//				copyFile(video, video_backup);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			video.delete();
		}
	}
   
// �����ļ�
   public  void copyFile(File sourceFile, File targetFile) throws IOException {
       BufferedInputStream inBuff = null;
       BufferedOutputStream outBuff = null;
       try {
           // �½��ļ����������������л���
           inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
           // �½��ļ���������������л���
           outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
           // ��������
           byte[] b = new byte[1024 * 5];
           int len;
           while ((len = inBuff.read(b)) != -1) {
               outBuff.write(b, 0, len);
           }
           // ˢ�´˻���������
           outBuff.flush();
       } finally {
           // �ر���
           if (inBuff != null)
               inBuff.close();
           if (outBuff != null)
               outBuff.close();
       }
   }
   
   private void uploadVideo2(final String vPath){
	   	new Thread(){  
	            @Override  
	        public void run(){     
	           try{  
	        	   String resultStr = "";
	       			JSONObject resultJSON;
	        	   
//		            File video = new File(Environment.getExternalStorageDirectory().getPath()+
//		            		"/myImage/"+Main.fileName);
		            File video = new File(Environment.getExternalStorageDirectory().getPath()+
		            		"/myImage/"+vPath);
		            log("uploadVideo vPath is "+vPath );
		            if(video.exists()){
		            	log(">>>>>>>>>>>>>>>>>>>"+video.getName()+" "+video.exists()+"  video.length() "+video.length());
		            	resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", video);
 		            log("resultJSON.toString()"+resultStr);
 		            
 		            if(resultStr.equals("")){
         				handler.sendEmptyMessage(21);//
         			}else{
         				resultJSON = new JSONObject(resultStr);
         				String result = resultJSON.getString("result");
         				message = resultJSON.getString("message");
         				if(result.equals("ok")){
         					uploadSuccess2(getProjectNum(video.getName()), video.getAbsolutePath(), "1");
         				}else if(result.equals("error")){
         					handler.sendEmptyMessage(21);
         				}
         			}
		            }else{
		            	handler.sendEmptyMessage(21);
		            	log(">>>>>>>>>>>>>>>>>>>");
		            }
	           }catch(Exception e) { 
	        	   e.printStackTrace();
	        	   handler.sendEmptyMessage(21);
	               Log.d("zlq", "***************************"+e.toString());  
	          }  
	         }  
	       }.start(); 
  }
   private void uploadVideo(final String vPath){
	   	new Thread(){  
	            @Override  
	        public void run(){     
	           try{  
	        	   String resultStr = "";
	       			JSONObject resultJSON;
	        	   
//		            File video = new File(Environment.getExternalStorageDirectory().getPath()+
//		            		"/myImage/"+Main.fileName);
		            File video = new File(Environment.getExternalStorageDirectory().getPath()+
		            		"/myImage/"+vPath);
		            log("uploadVideo vPath is "+vPath );
		            if(video.exists()){
		            	log(">>>>>>>>>>>>>>>>>>>"+video.getName()+" "+video.exists()+"  video.length() "+video.length());
		            	resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", video);
  		            log("resultJSON.toString()"+resultStr);
  		            
  		            if(resultStr.equals("")){
          				handler.sendEmptyMessage(16);//
          			}else{
          				resultJSON = new JSONObject(resultStr);
          				String result = resultJSON.getString("result");
          				message = resultJSON.getString("message");
          				if(result.equals("ok")){
          					uploadSuccess(getProjectNum(video.getName()), video.getAbsolutePath(), "1");
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
   
   /**�ϴ�¼��ʧ��**/
   private void uploadAudioFailed(){
	   if(uploadAudioFail == true){
		   new AlertDialog.Builder(InformationReport.this)
	       .setMessage("�ϴ�¼��ʧ�ܣ��Ƿ����ԣ�")
	       .setNegativeButton("����", new DialogInterface.OnClickListener(){
	           public void onClick(DialogInterface dialog, int which) {
//	        	   deleteAudio();
	        	   uploadAudioFail = false;
	           }})
	       .show();
	   }else{
		   uploadAudioFail = true;
		   progressDialog = ProgressDialog.show(InformationReport.this, "�����ϴ���Ƶ...", "���Ե�...", true, true);  
		   new AlertDialog.Builder(InformationReport.this)
	       .setMessage("�ϴ�¼��ʧ�ܣ��Ƿ����ԣ�")
	       .setNegativeButton("����", new DialogInterface.OnClickListener(){
	           public void onClick(DialogInterface dialog, int which) {
//	        	   deleteAudio();
	        	   uploadAudioFail = false;
	           }})
	       .setPositiveButton("����", new DialogInterface.OnClickListener(){
	           public void onClick(DialogInterface dialog, int which) {
	        	   uploadAudio(audioPath);
	        	   dialog.dismiss();
	          }})
	       .show();
	   }
   }
   
   private void deleteAudio(final String aPath){
//	   final String audioName = "maudio_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//       		"_"+String.format("%03d", currentAudioIndex)+".mp4";
	   File audio = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/"+aPath);
//	   File audio_backup = new File(new StringBuilder(Environment
//				.getExternalStorageDirectory().getAbsolutePath())
//				.append(File.separator).append("konruns-backup").toString()
//				+ "/" + audio.getName());
		if (audio.exists()) {
//			try {
//				copyFile(audio, audio_backup);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			audio.delete();
		}
   }
   private void uploadAudio2(final String aPath){
	 	new Thread(){  
	            @Override  
	        public void run(){     
	           try{  
	        	    String resultStr = "";
	       			JSONObject resultJSON;
//	       			final String audioName = "audio_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//           		"_"+String.format("%03d", currentAudioIndex)+".mp4";
		            File audio = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/"+aPath);
		            if(audio.exists()){
		            	log(">>>>>>>>>>>>>>>>>>>"+audio.getAbsolutePath()+" "+audio.exists()+"  video.length() "+audio.length());
		            	resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", audio);
 		            log("resultJSON.toString()"+resultStr);
 		            
 		            if(resultStr.equals("")){
         				handler.sendEmptyMessage(21);//
         			}else{
         				resultJSON = new JSONObject(resultStr);
         				String result = resultJSON.getString("result");
         				message = resultJSON.getString("message");
         				if(result.equals("ok")){
         					uploadSuccess2(getProjectNum(audio.getName()), audio.getAbsolutePath(), "2");
         				}else if(result.equals("error")){
         					handler.sendEmptyMessage(21);
         				}
         			}
		            }else{
		            	handler.sendEmptyMessage(21);
		            }
	           }catch(Exception e) { 
	        	   e.printStackTrace();
	        	   handler.sendEmptyMessage(21);
	               Log.d("zlq", "***************************"+e.toString());  
	          }  
	         }  
	       }.start(); 
 }
   private void uploadAudio(final String aPath){
	 	new Thread(){  
	            @Override  
	        public void run(){     
	           try{  
	        	   String resultStr = "";
	       			JSONObject resultJSON;
//	       			final String audioName = "audio_"+SystemConfig.projectArrayList.get(namePosition).get("number")+
//            		"_"+String.format("%03d", currentAudioIndex)+".mp4";
		            File audio = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/"+aPath);
		            if(audio.exists()){
		            	log(">>>>>>>>>>>>>>>>>>>"+audio.getAbsolutePath()+" "+audio.exists()+"  video.length() "+audio.length());
		            	resultStr = JsonParser.UpLoadRes(SystemConfig.URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'}}", audio);
  		            log("resultJSON.toString()"+resultStr);
  		            
  		            if(resultStr.equals("")){
          				handler.sendEmptyMessage(18);//
          			}else{
          				resultJSON = new JSONObject(resultStr);
          				String result = resultJSON.getString("result");
          				message = resultJSON.getString("message");
          				if(result.equals("ok")){
          					uploadSuccess(getProjectNum(audio.getName()), audio.getAbsolutePath(), "2");
          				}else if(result.equals("error")){
          					handler.sendEmptyMessage(19);
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
   
//   @Override 
//   public void onConfigurationChanged(Configuration newConfig){ 
//	   super.onConfigurationChanged(newConfig); 
//	   if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
//	   }
//	   else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//	   }
//   }
   
   @Override
	protected void onDestroy() {
	   log("======================================onDestroy");
	    if (mBMapMan != null) {
	    	mBMapMan.getLocationManager().removeUpdates(mLocationListener);
	        mBMapMan.destroy();
	        mBMapMan = null;
	    }
	    super.onDestroy();
	}
	@Override
	protected void onPause() {
		log("======================================onPause");
	    if (mBMapMan != null) {
	    	mBMapMan.getLocationManager().removeUpdates(mLocationListener);
	        mBMapMan.stop();
	    }
	    super.onPause();
	}
	@Override
	protected void onResume() {
		log("======================================onResume");
	    if (mBMapMan != null) {
	    	mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
	        mBMapMan.start();
	    }else{
	    	log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>MBmapMan is null");
	    }
	    String count = MyUtil.getFromPreference(this, "count");
		if (count.equals("")) {
			count = "0";
		}
		if (!count.equals("-1")) {// -1��ʾ���ߵ�¼
			tv_count.setText("�������ϱ�" + count + "��");
		}
//	    int i = 1;
//		if (i>=1){
////			tv_count.setText(Html.fromHtml("�������ϱ�<font color=\"#ff0000\">"+i+"</font>��"));
//			tv_count.setText("�������ϱ�"+i+"��");
//		}
	    super.onResume();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		log("======================================onStarty");
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		log("======================================onStop");
		super.onStop();
	}
	
	@Override
	protected void onRestart() {
		log("======================================onRestart");
		super.onRestart();
	}
	void log(String msg){
		System.out.println("peter--"+msg);
	}
	// 2013-06-15 start  �ϴ���Ƶ ����Ƭ����Ƶ�ɹ�ʱ����ȡgps�����ϴ�
	void upGps() {
		if (MyUtil.isNetworkAvailable(InformationReport.this)) {
			new Thread() {
				@Override
				public void run() {
					try {
						getGps2();
					} catch (Exception e) {
						Log.d("zlq","***************************" + e.toString());
					}
				}
			}.start();
		}
	}// 2013-06-15 end
}
