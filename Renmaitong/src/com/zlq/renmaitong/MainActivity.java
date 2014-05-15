package com.zlq.renmaitong;


import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zlq.json.JsonParser;
import com.zlq.util.DownloadManagerPro;
import com.zlq.util.FileService;
import com.zlq.util.MyDatabaseHelper;
import com.zlq.util.MyUtil;
import com.zlq.util.PreferencesUtils;
import com.zlq.util.SystemConfig;

public class MainActivity extends Activity{

	private Button btnOk, btnSetting;
	private EditText userNameET, passwordET;
	
	private boolean isShow = false;
	private ImageView userNameSpinner;
	private PopupWindow pop;
    private myAdapter adapter;
    private ListView listView;
    private ArrayList<ArrayList<String>> nameList = new ArrayList<ArrayList<String>>();
    private ImageButton passwordClear;
    private CheckBox remeber_numbe;
    private ProgressBar            downloadProgress;
    private DownloadManager        downloadManager;
    private long                   downloadId           = 0;
	private DownloadManagerPro     downloadManagerPro;
    private MyHandler              _handler;
    private TextView               downloadSize;
	private TextView               downloadPrecent;
	private DownloadChangeObserver downloadObserver;
	private CompleteReceiver       completeReceiver;
    
    private int lixian = 0;
	private ProgressDialog progressDialog; 
	private String message;
	private String serverlog;
	
	 /**
     * MyHandler
     * 
     * @author Trinea 2012-12-18
     */
	public static boolean isDownloading(int downloadManagerStatus) {
        return downloadManagerStatus == DownloadManager.STATUS_RUNNING
               || downloadManagerStatus == DownloadManager.STATUS_PAUSED
               || downloadManagerStatus == DownloadManager.STATUS_PENDING;
    }
	 public static String getNotiPercent(long progress, long max) {
	        int rate = 0;
	        if (progress <= 0 || max <= 0) {
	            rate = 0;
	        } else if (progress > max) {
	            rate = 100;
	        } else {
	            rate = (int)((double)progress / max * 100);
	        }
	        return new StringBuilder(16).append(rate).append("%").toString();
	    }
	 /**
	     * @param size
	     * @return
	     */
	    public static CharSequence getAppSize(long size) {
	        if (size <= 0) {
	            return "0M";
	        }

	        if (size >= MB_2_BYTE) {
	            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / MB_2_BYTE)).append("M");
	        } else if (size >= KB_2_BYTE) {
	            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / KB_2_BYTE)).append("K");
	        } else {
	            return size + "B";
	        }
	    }
	    static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");

	    public static final int    MB_2_BYTE             = 1024 * 1024;
	    public static final int    KB_2_BYTE             = 1024;

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    int status = (Integer)msg.obj;
                    if (isDownloading(status)) {
                        downloadProgress.setVisibility(View.VISIBLE);
                        downloadProgress.setMax(0);
                        downloadProgress.setProgress(0);
//                        downloadButton.setVisibility(View.GONE);
                        downloadSize.setVisibility(View.VISIBLE);
                        downloadPrecent.setVisibility(View.VISIBLE);

                        if (msg.arg2 < 0) {
                            downloadProgress.setIndeterminate(true);
                            downloadPrecent.setText("0%");
                            downloadSize.setText("0M/0M");
                        } else {
                            downloadProgress.setIndeterminate(false);
                            downloadProgress.setMax(msg.arg2);
                            downloadProgress.setProgress(msg.arg1);
                            downloadPrecent.setText(getNotiPercent(msg.arg1, msg.arg2));
                            downloadSize.setText(getAppSize(msg.arg1) + "/" + getAppSize(msg.arg2));
                        }
                    } else {
                        downloadProgress.setVisibility(View.GONE);
                        downloadProgress.setMax(0);
                        downloadProgress.setProgress(0);
                        downloadPrecent.setVisibility(View.GONE);
//                        downloadButton.setVisibility(View.VISIBLE);
                        downloadSize.setVisibility(View.GONE);

                        if (status == DownloadManager.STATUS_FAILED) {
//                            downloadButton.setText("下载失败，点击重新下载");
//                            downloadButton.setText("下载更新");
                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                            downloadButton.setText("已下载过，点击重新下载");
//                            downloadButton.setText("下载更新");
                        } else {
//                            downloadButton.setText("下载更新");
                        }
                    }
                    break;
            }
        }
    }
	
	
	
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   String isCheck = remeber_numbe.isChecked()?"1":"0";
		    	   if(existUser(nameList, userNameET.getText().toString())){
		    		   MyDatabaseHelper.updatetUserTime(MainActivity.this, userNameET.getText().toString(),
		    				   passwordET.getText().toString(), isCheck);
		    	   }else{
		    		   MyDatabaseHelper.insertUser(MainActivity.this, userNameET.getText().toString(), 
				    			   passwordET.getText().toString(), isCheck);
		    	   }
		    	 
		    	   progressDialog.dismiss();
		    	   SystemConfig.token = message;
		    	   SystemConfig.userName = userNameET.getText().toString();
		    	   SystemConfig.password = passwordET.getText().toString();
		    	   //登陆成功 保存用户名，密码，token
		    	   MyUtil.saveJson2preference(MainActivity.this, MyUtil.KEY_USER_NAME, SystemConfig.userName);
		    	   MyUtil.saveJson2preference(MainActivity.this, MyUtil.KEY_USER_PWD, SystemConfig.password);
		    	   MyUtil.saveJson2preference(MainActivity.this, MyUtil.KEY_TOKEN, SystemConfig.token);
		    	   log("save 保存用户名，密码，token ");
		    	   log(lixian+"--");
//		    	        更新登录信息
		    	   if (lixian==-1) {
		    		   Toast.makeText(MainActivity.this, "离线登陆", Toast.LENGTH_SHORT).show();
		    		   count = "-1";
		    	   }
		    	   MyUtil.saveJson2preference(MainActivity.this, "count", count);
		    	   startActivity(new Intent(MainActivity.this, InformationReport.class));
				   finish();
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 3){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(MainActivity.this, "服务器返回值错误", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(MainActivity.this, "服务器超时1", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 5){
//		    	   Toast.makeText(MainActivity.this, SystemConfig.URL+"  "+msgStr, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 7){
//		    	   float localVer = Float.parseFloat(localVersion);
//		    	   float serverVer = Float.parseFloat(serverVersion);	   
		    	   log("msg.what == 7 localVer:"+localVersion);
		    	   log("msg.what == 7 serverVer:"+serverVersion);
		    	   if(!localVersion.equals(serverVersion)){
		    		   log("sdk_int is "+sdk_int);
		    		   if (sdk_int>=14) {
		    		   new Thread() {
							@Override
							public void run() {
								try {
									serverlog = MyUtil.getServerVersionLog("http://"+SystemConfig.IP+":"+SystemConfig.PORT+"/pf/skin/c/versionLog2.txt");
									log("$"+serverlog);
									if (!serverlog.equals("")) {
										handler.sendEmptyMessage(8);
									}
								} catch (Exception e) {
									Log.d("peter","***************************"+ e.toString());
								}
							}
						}.start();	
		    		   }else {
		    			   Toast.makeText(MainActivity.this, "本客户端有新版本，请登录网站下载更新！", Toast.LENGTH_LONG).show();
		    		   }
		    	   }
		       }else if(msg.what == 8){	//dialog   
		    	   if (serverlog.contains("<html>")) {
					return;
				   }
		    	   log("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		    	   showUpdateDialog(serverlog);
		       }
		}   
	};  
	
	
	private String localVersion = "";
	private String serverVersion = "";
	
	
	
	private void initData() {
        downloadId = PreferencesUtils.getLongPreferences(this, PreferencesUtils.KEY_NAME_DOWNLOAD_ID);
    }
	void log(String str){
		System.out.println("peter--"+str);
	}
	
	private void showUpdateDialog(String _log) {

		String size = _log.substring(0,_log.indexOf("#"));
		_log = _log.replace(size+"#", "");
		String[] sslog = _log.split(";");
		log(sslog.length+"");
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.updatelog, null);
		final LinearLayout linearLayout = (LinearLayout)layout.findViewById(R.id.updatelog_layout);
		TextView tv1 = new TextView(MainActivity.this);
		tv1.setText("版本号："+serverVersion+" ，大小："+size);
		TextView tv2 = new TextView(MainActivity.this);
		tv2.setText("新版功能说明：");
		linearLayout.addView(tv1);
		linearLayout.addView(tv2);
		for (int i = 0; i < sslog.length; i++) {
			TextView textView = new TextView(MainActivity.this);
			textView.setText(sslog[i]);
			linearLayout.addView(textView);
		}
		
		downloadProgress = (ProgressBar)layout.findViewById(R.id.download_progress);
		downloadSize = (TextView)layout.findViewById(R.id.download_size);
		final TextView tip = (TextView)layout.findViewById(R.id.tv_tip);
		downloadPrecent = (TextView)layout.findViewById(R.id.download_precent);
		_handler = new MyHandler();
		downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        downloadManagerPro = new DownloadManagerPro(downloadManager);
        initData();
      
		
		downloadObserver = new DownloadChangeObserver();
        completeReceiver = new CompleteReceiver();
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, downloadObserver);
        updateView();
        
      
         new AlertDialog.Builder(this).setTitle("版本更新提示").setView(layout)
         .setPositiveButton("下载更新",
				new DialogInterface.OnClickListener() {
        	 	private boolean tipedNet = false;
        	 	int startint =0;//0未开启;1已开启(正在下载)
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 不关闭
						if (!MyUtil.isNetworkAvailable(MainActivity.this)) {
							dialog.dismiss();
							return;
						}
						setClosable(dialog, false);
						if (MyUtil.isWifiActive(MainActivity.this)) {
							if (startint==0) {
								tip.setVisibility(View.GONE);
								String apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath()).append(File.separator).append(SystemConfig.DOWNLOAD_FOLDER_NAME).append(File.separator).append(SystemConfig.DOWNLOAD_FILE_NAME).toString();
			                    File apkFile = new File(apkFilePath);
								if (apkFile.isFile()&&apkFile.exists()) {
									apkFile.delete();
								}
								startDown();
								startint=1;
							}
						}else {
							if (tipedNet) {//已经提示了
								if (startint==0) {
									tip.setVisibility(View.GONE);
									String apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath()).append(File.separator).append(SystemConfig.DOWNLOAD_FOLDER_NAME).append(File.separator).append(SystemConfig.DOWNLOAD_FILE_NAME).toString();
				                    File apkFile = new File(apkFilePath);
									if (apkFile.isFile()&&apkFile.exists()) {
										apkFile.delete();
									}
									startDown();
									startint=1;
									return;
								}
							}else {
								tip.setVisibility(View.VISIBLE);
								tip.setText("检测到您当前为非wifi网络环境，将会产生数据流量，继续请再次点击“下载更新”，取消请点击 “以后再说”");
								tipedNet = true;
							}
						}
					}
				})
			.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					log("downloadId is "+downloadId);
					if (downloadId!=-1) {
						downloadManager.remove(downloadId);
						updateView();
					}
					setClosable(dialog, true);
					dialog.dismiss();
					getContentResolver().unregisterContentObserver(downloadObserver);
					unregisterReceiver(completeReceiver);
				}
			}).show();
         
	}
	
	@TargetApi(11)
	void startDown(){
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://"+SystemConfig.IP+":"+SystemConfig.PORT+"/pf/skin/c/mps_setup_aphone.apk"));
        request.setDestinationInExternalPublicDir(SystemConfig.DOWNLOAD_FOLDER_NAME,SystemConfig.DOWNLOAD_FILE_NAME);
        request.setTitle("外勤项目管理系统");// download_notification_title
        request.setDescription("外勤管理 desc");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        // request.allowScanningByMediaScanner();
        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // request.setShowRunningNotification(false);
        // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setMimeType("application/com.trinea.download.file");
        downloadId = downloadManager.enqueue(request);
        /** save download id to preferences **/
        PreferencesUtils.putLongPreferences(MainActivity.this,
        		PreferencesUtils.KEY_NAME_DOWNLOAD_ID, downloadId);
        updateView();
	}
	private int sdk_int = -1;
	
	private void initMediaBackup(){
		new Thread(){
			public void run() {
				System.out.println( "--"+"天前");
				String backupFolderPath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath()).append(
						File.separator).append("konruns-backup").toString();
				File backupFolder = new File(backupFolderPath);
				if (!backupFolder.exists() || !backupFolder.isDirectory()) {
					backupFolder.mkdirs();
				}
				delete7DaysBeforeMedium(backupFolder);
			};
		}.start();
	}
	private void delete7DaysBeforeMedium(File dir){
		if(dir == null || !dir.isDirectory())
			return;
		File [] listF = dir.listFiles();
		for (File f: listF) {
			if (!f.isDirectory()) {
				long l = f.lastModified();
				long currentTime = System.currentTimeMillis();
				System.out.println(f.getName() + "-------"+ (currentTime - l)/(24*60*60*1000)+"天前");
				System.out.println(currentTime - l + "-------"+ (7*24*60*60*1000));
				if (currentTime - l >= (7*24*60*60*1000)) {// 毫秒
					f.delete();
				}
			}
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initMediaBackup();//备份文件夹初始化
		
		setContentView(R.layout.my_activie_account);
		initLayout();

		sdk_int = android.os.Build.VERSION.SDK_INT;
		
		//复制数据库文件到程序
		FileService.copyAssetsDatabaseToData(this, "aaa.db");
		nameList = MyUtil.arrayToList(MyDatabaseHelper.getUserList(this));
		
		//初始化URL
		 SharedPreferences settings = getSharedPreferences("setting", 0);
		 String ip = settings.getString("ip", SystemConfig.IP);
		 String port = settings.getString("port", SystemConfig.PORT);
		
		SystemConfig.IP = ip; 
		SystemConfig.PORT = port;
		SystemConfig.URL = "http://"+ip+":"+port+"/pf/services/AndroidPhoneService.pt";
		SystemConfig.Location_URL = "http://"+ip+":"+port+"/pf/LocationServices/AndroidPhoneService.pt";
		log("SystemConfig.URL>>>>>>>>>>>"+SystemConfig.URL);
		log("SystemConfig.Location_URL>>>>>>>>>>>"+SystemConfig.Location_URL);
		
		String folderPath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath()).append(File.separator).append(SystemConfig.DOWNLOAD_FOLDER_NAME).toString();
        
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			folder.mkdirs();
		}
		localVersion = MyUtil.getVerName(this);
		TextView version = (TextView)findViewById(R.id.version);
		version.setText(localVersion);
		
//		final String versionUrl = SystemConfig.serversionUrl;
		final String versionUrl = "http://"+ip+":"+port+"/pf/skin/c/version2.txt";
		log("versionUrl is "+versionUrl);
		if (MyUtil.isNetworkAvailable(this)) {
		//有网
		new Thread(){  
            @Override  
        public void run(){     
           try{  
        	   serverVersion =  MyUtil.getServerVersionName(versionUrl);
        	   if (serverVersion != "") {
				handler.sendEmptyMessage(7);
        	   }
           }catch(Exception e) {  
               Log.d("zlq", "***************************"+e.toString());  
          }  
         }  
        }.start();
		}
		
		
		if(!nameList.isEmpty()){
			userNameET.setText(nameList.get(0).get(1));
			if(nameList.get(0).get(3).equals("1")){
				passwordET.setText(nameList.get(0).get(2));
				remeber_numbe.setChecked(true);
			}else{
				passwordET.setText("");
				remeber_numbe.setChecked(false);
			}
		}
		
		setUpListeners();
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String userName = userNameET.getText().toString();
				final String password = passwordET.getText().toString();
				if(userName.equals("") || password.equals("")){
					Toast.makeText(MainActivity.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
				}else{
					progressDialog = ProgressDialog.show(MainActivity.this, "登录中...", "请稍等...", true, true);  
					final TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					Camera camera = Camera.open();
					Parameters parameters = camera.getParameters();
					camera.release();
					List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes(); 
					String pictureSizeStr = "";
					for(int i=0; i<supportedPictureSizes.size(); i++){
						log(">>>>>>>>>>>>>>>>>>>>>>>"+supportedPictureSizes.get(i).height+
								"  "+supportedPictureSizes.get(i).width);
						pictureSizeStr = pictureSizeStr + supportedPictureSizes.get(i).width+"  "+supportedPictureSizes.get(i).height+"\n";
					}
					SystemConfig.videoWidth = supportedPictureSizes.get(0).width;
					SystemConfig.videoHeight = supportedPictureSizes.get(0).height;
					log("SystemConfig.videoWidth"+SystemConfig.videoWidth+"SystemConfig.videoHeight"+SystemConfig.videoHeight);
					new Thread(){  
			            @Override  
			        public void run(){     
			           try{  
							log("mTelephonyMgr.getSubscriberId()"+mTelephonyMgr.getSubscriberId());
							String ismiStr = mTelephonyMgr.getSubscriberId()==null?"13800000000":mTelephonyMgr.getSubscriberId();
							getLogin(userName, password, ismiStr);
//							getLogin(userName, password, "13813813800");
			           }catch(Exception e) {  
			               Log.d("zlq", "***************************"+e.toString());  
			          }  
			         }  
			       }.start();  
				}
//				startActivity(new Intent(MainActivity.this, InformationReport.class));
			}
		});
		btnSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = getLayoutInflater();
				final View layout = inflater.inflate(R.layout.setting_dialog, (ViewGroup) findViewById(R.id.dialog));
				final EditText serverIP = (EditText)layout.findViewById(R.id.serverIP);
            	final EditText serverPort = (EditText)layout.findViewById(R.id.serverPort);
            	final SharedPreferences settings = getSharedPreferences("setting", 0);
            	serverIP.setText(settings.getString("url", SystemConfig.IP));
            	serverPort.setText(settings.getString("port", SystemConfig.PORT));
				new AlertDialog.Builder(MainActivity.this).setTitle("设置服务器地址").setView(layout)
				.setNegativeButton("取消", new DialogInterface.OnClickListener(){
		            public void onClick(DialogInterface dialog, int which) {
		            	
		            }})
		        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
		                public void onClick(DialogInterface dialog, int which) {
		                SharedPreferences.Editor editor = settings.edit();
		                editor.putString("ip", serverIP.getText().toString().trim());
		                editor.putString("port", serverPort.getText().toString().trim());	
		                editor.commit();
		                
		                SystemConfig.IP = serverIP.getText().toString().trim(); 
		        		SystemConfig.PORT = serverPort.getText().toString().trim();
		        		
		                //更新public static String URL = "http://219.141.190.156:8080/mps/services/AndroidPhoneService.pt";
		                SystemConfig.URL = "http://"+SystemConfig.IP+":"+SystemConfig.PORT+
		                		"/pf/services/AndroidPhoneService.pt";
		                SystemConfig.Location_URL = "http://"+SystemConfig.IP+":"+SystemConfig.PORT+
		                		"/pf/LocationServices/AndroidPhoneService.pt";
		                log("SystemConfig.URL>>>>>>>>>>>"+SystemConfig.URL);
		         }})
		        .show();

			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 拦截返回按钮事件
			MyUtil.leave(MainActivity.this);
		}
		return true;
	}
	
	void initLayout(){
		btnOk = (Button) findViewById(R.id.btnOK);
		btnSetting = (Button) findViewById(R.id.btnSetting);
		userNameET = (EditText) findViewById(R.id.userName);
		passwordET = (EditText) findViewById(R.id.userPassword);
		userNameSpinner = (ImageView) findViewById(R.id.userNameSpinner);
		passwordClear = (ImageButton) findViewById(R.id.userPassword_clear);
		remeber_numbe = (CheckBox) findViewById(R.id.remeber_number);
		passwordClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				passwordET.setText("");
			}
		});
	}
	private String count = "";
	void getLogin(String userName, String password, String IMSI){
		
		if (!MyUtil.isNetworkAvailable(MainActivity.this)) {
			log("login");
			//断网
			String user = MyUtil.getFromPreference(MainActivity.this, MyUtil.KEY_USER_NAME);
			String pwd =  MyUtil.getFromPreference(MainActivity.this, MyUtil.KEY_USER_PWD);
			String token =  MyUtil.getFromPreference(MainActivity.this, MyUtil.KEY_TOKEN);
			log(user+pwd);
			log(userName+password);
			if (user.equals(userName)&&pwd.equals(password)) {
				lixian =  -1;
				message = token;
				handler.sendEmptyMessage(1);
			}else{
				message = "用户名或密码错误";
				handler.sendEmptyMessage(2);
			}
		}else {// 有网
		String resultStr = "";
		JSONObject obj = new JSONObject();
		JSONObject msg = new JSONObject();
		JSONObject resultJSON;
		
		String result = "";
		try {
			obj.put("userName", userName);
			obj.put("passWord", password);
			obj.put("imsi", IMSI);
			msg.put("androidPhoneLogin", obj);
			
			log("result:>>>>>>>>>>>>>>>>>>"+resultStr);
			handler.sendEmptyMessage(5);
			resultStr = JsonParser.getResponse(SystemConfig.URL, msg.toString());
			if(resultStr.equals("")){
				handler.sendEmptyMessage(4);//
			}else{
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if (resultJSON.has("count")) {
					count = resultJSON.getString("count");
				}
				
				if(result.equals("ok")){
					SystemConfig.token = message;
					SystemConfig.userName = userName;
					handler.sendEmptyMessage(1);
				}else if(result.equals("error")){
					handler.sendEmptyMessage(2);
				}else{
					handler.sendEmptyMessage(3);
				}
			}
		} catch (JSONException e) {
			handler.sendEmptyMessage(2);
			e.printStackTrace();
		}
		}
	}
	
	void showDialog(){
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.mm_progress_dialog, (ViewGroup) findViewById(R.id.progressDialog));
		new AlertDialog.Builder(this).setView(layout).show();
	}
	
	
	 public void setUpListeners(){
		 userNameSpinner.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                if(pop==null){
	                    if(adapter==null){
	                    	if(!nameList.isEmpty()){
	                            adapter=new myAdapter();
	                            listView=new ListView(MainActivity.this);
//	                            listView.setFastScrollEnabled(true);
//	                            listView.setFocusable(true);
	                            listView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
	                            pop=new PopupWindow(listView, userNameET.getWidth()+40, LayoutParams.WRAP_CONTENT);
	                            pop.setFocusable(true);
	                            listView.setAdapter(adapter);
	                            listView.setOnItemClickListener(new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> arg0,
											View arg1, int arg2, long arg3) {
										 	pop.dismiss();
					                        isShow = false;
					                        userNameET.setText(nameList.get(arg2).get(1));
					                        if(nameList.get(arg2).get(3).equals("1")){
					                        	 passwordET.setText(nameList.get(arg2).get(2));
					                        }
					                       
									}
								});
	                            pop.showAsDropDown(userNameET);
	                            isShow = true;
	                    	}
	                    }
	                }
	                else if(isShow){
	                    pop.dismiss();
	                    isShow = false;
	                }else if(!isShow){
	                    pop.showAsDropDown(userNameET);
	                    isShow = true;
	                }
	            }
	        });
	    }
	    
		 class myAdapter extends BaseAdapter {
		        LayoutInflater mInflater;
		        public myAdapter() {
		            mInflater=LayoutInflater.from(MainActivity.this);
		        }
		        @Override
		        public int getCount() {
		            return nameList.size();
		        }
		        @Override
		        public Object getItem(int position) {
		            return null;
		        }
		        @Override
		        public long getItemId(int position) {
		            return position;
		        }
		        @Override
		        public View getView(final int position, View convertView, ViewGroup parent) {
		            Holder holder=null;
		            final String name = nameList.get(position).get(1);
//		            final String passwordStr = nameList.get(position).get(2);
		            if(convertView==null){
		                convertView=mInflater.inflate(R.layout.popup, null);
		                holder=new Holder();
		                holder.view=(TextView)convertView.findViewById(R.id.mQQ);
//		                holder.deleteButton = (ImageButton)convertView.findViewById(R.id.userPassword_clear);
		                convertView.setTag(holder);
		            }
		            else{
		                holder=(Holder) convertView.getTag();
		            }
		            
		            if(holder!=null){
		                convertView.setId(position);
		                holder.setId(position);
		                holder.view.setText(name);
//		                holder.deleteButton.setOnClickListener(new OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								nameList.remove(position);
//								adapter.notifyDataSetChanged();
//								pop.dismiss();
//							}
//						});
		            }
		            return convertView;
		        }
		        
		        class Holder{
		            TextView view;
//		            ImageButton deleteButton;
		            
		            void setId(int position){
		                view.setId(position);
//		                deleteButton.setId(position);
		            }
		        }

		    }	
	
	
	
		 private boolean existUser(ArrayList<ArrayList<String>> userList, String userName){
				if(!userList.isEmpty()){
				   for(int i=0; i<userList.size(); i++){
			           	if(userList.get(i).get(1).equals(userName)){
			           		return true;
			           	}
		           }
				}
				return false;
			}
	
	
	
	
		 class CompleteReceiver extends BroadcastReceiver {

		        @Override
		        public void onReceive(Context context, Intent intent) {
		            /**
		             * get the id of download which have download success, if the id is my id and it's status is successful,
		             * then install it
		             **/
		            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
		            if (completeDownloadId == downloadId) {
		                initData();
		                updateView();
		                // if download successful, install apk
		                if (downloadManagerPro.getStatusById(downloadId) == DownloadManager.STATUS_SUCCESSFUL) {
		                    String apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath()).append(File.separator).append(SystemConfig.DOWNLOAD_FOLDER_NAME).append(File.separator).append(SystemConfig.DOWNLOAD_FILE_NAME).toString();
		                    install(context, apkFilePath);
		                }
		            }
		        }
		    };
		    /**
		     * install app
		     * 
		     * @param context
		     * @param filePath
		     * @return whether apk exist
		     */
		    public static boolean install(Context context, String filePath) {
		        Intent i = new Intent(Intent.ACTION_VIEW);
		        File file = new File(filePath);
		        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
		            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
		            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            context.startActivity(i);
		            return true;
		        }
		        return false;
		    }
	
	
	
		    class DownloadChangeObserver extends ContentObserver {

		        public DownloadChangeObserver(){
		            super(_handler);
		        }

		        @Override
		        public void onChange(boolean selfChange) {
		            updateView();
		        }

		    }
	
		    public void updateView() {
				int[] bytesAndStatus = downloadManagerPro.getBytesAndStatus(downloadId);
				_handler.sendMessage(_handler.obtainMessage(0, bytesAndStatus[0],bytesAndStatus[1], bytesAndStatus[2]));
			}
	
		    private void setClosable(DialogInterface dialog, boolean b) {
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, b);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	
}