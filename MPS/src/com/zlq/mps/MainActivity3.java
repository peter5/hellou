package com.zlq.mps;

import io.vov.vitamio.R;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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

public class MainActivity3 extends Activity{
	private long                   downloadId           = 0;
	private DownloadManager        downloadManager;
	private DownloadManagerPro     downloadManagerPro;
	private ProgressBar            downloadProgress;
	private MyHandler              _handler;
	private DownloadChangeObserver downloadObserver;
	private CompleteReceiver       completeReceiver;

	private TextView               downloadSize;
	private TextView               downloadPrecent;
	

	private Button btnOk, btnSetting;
	private EditText userNameET, passwordET;
	
	private boolean isShow = false;
	private ImageView userNameSpinner;
	private PopupWindow pop;
    private myAdapter adapter;
    private ListView listView;
    private ArrayList<ArrayList<String>> nameList = new ArrayList<ArrayList<String>>();
    private ImageView passwordClear;
    private CheckBox remeber_numbe;
    
    private CheckBox zlCheckBox;
    
	private ProgressDialog progressDialog; 
	private String message;
	private String serverVersion = "";
	private String localVersion = "";
	private String serverlog ;
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   String isCheck = remeber_numbe.isChecked()?"1":"0";
		    	   if(existUser(nameList, userNameET.getText().toString())){
		    		   MyDatabaseHelper.updatetUserTime(MainActivity3.this, userNameET.getText().toString(),
		    				   passwordET.getText().toString(), isCheck);
		    	   }else{
		    		   MyDatabaseHelper.insertUser(MainActivity3.this, userNameET.getText().toString(), 
				    			   passwordET.getText().toString(), isCheck);
		    	   }
		    	   
//		    	   Log.i("ArrayList---Size---Two", nameList.size()+"");
		    	 
		    	   
		    	   SystemConfig.token = message;
		    	   SystemConfig.userName = userNameET.getText().toString().trim();
		    	   SystemConfig.password = passwordET.getText().toString().trim();
		    	   
		    	   //登陆成功 保存用户名，密码，token
		    	   MyUtil.saveJson2preference(MainActivity3.this, MyUtil.KEY_USER_NAME, SystemConfig.userName);
		    	   MyUtil.saveJson2preference(MainActivity3.this, MyUtil.KEY_USER_PWD, SystemConfig.password);
		    	   MyUtil.saveJson2preference(MainActivity3.this, MyUtil.KEY_TOKEN, SystemConfig.token);
		    	   log("save 保存用户名，密码，token ");
		    	   
				   new Thread(){  
			            @Override  
			        public void run(){     
			           try{  
			        	   getServerSet();
			           }catch(Exception e) {  
			               Log.d("zlq", "***************************"+e.toString());  
			          }  
			         }  
			       }.start();  
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(MainActivity3.this, message, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(MainActivity3.this, "登录超时", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 5){
//		    	   Toast.makeText(MainActivity.this, SystemConfig.URL+"  "+msgStr, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 6){
		    	   progressDialog.dismiss();
		    	   Intent i = new Intent(MainActivity3.this, BrowsePage.class); 
		    	   i.putExtra("zlisChecked", zlCheckBox.isChecked());
		    	   startActivity(i);
		    	   finish();
		       }else if(msg.what == 7){
//		    	   System.out.println("serverVersion:--"+serverVersion);
//		    	   float localVer = Float.parseFloat(localVersion);
//		    	   float serverVer = Float.parseFloat(serverVersion);	   
		    	   log("msg.what == 7 localVer:"+localVersion);
		    	   log("msg.what == 7 serverVer:"+serverVersion);
		    	   if(!localVersion.equals(serverVersion)){
					new Thread() {
						@Override
						public void run() {
							try {
								serverlog = MyUtil.getServerVersionLog("http://"+SystemConfig.IP+":"+SystemConfig.PORT+"/pf/skin/c/versionLog.txt");
								if (!serverlog.equals("")) {
									handler.sendEmptyMessage(8);
								}
							} catch (Exception e) {
								Log.d("peter",
										"***************************"+ e.toString());
							}
						}
					}.start();
		    	   }
		       }else if(msg.what == 8){	//dialog   
		    	   showUpdateDialog(serverlog);
		       }
		}   
	};  
	
	
	@Override
	protected void onPause() {
		super.onPause();
//		getContentResolver().unregisterContentObserver(downloadObserver);
	}
	@Override
	protected void onDestroy() {
		 super.onDestroy();
//		 getContentResolver().unregisterContentObserver(downloadObserver);
//		 unregisterReceiver(completeReceiver);
	}
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
		
		final SharedPreferences settings = getSharedPreferences("setting", 0);
		final String ip = settings.getString("ip", SystemConfig.IP);
		final String port = settings.getString("port", SystemConfig.PORT);
		
		SystemConfig.IP = ip;
		SystemConfig.PORT = port;
		
		SystemConfig.URL = "http://"+ip+":"+port+"/pf/services/IPadService.pt";
		SystemConfig.DataUrl = "http://"+ip+":"+port+"/pf/";
		String folderPath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath()).append(File.separator).append(SystemConfig.DOWNLOAD_FOLDER_NAME).toString();
        
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			folder.mkdirs();
		}
		if (MyUtil.isNetworkAvailable(MainActivity3.this)) {
		
			String packgeName = this.getPackageName();
			localVersion = MyUtil.getVerName(this,packgeName);
			
			final String serversionUrl = "http://"+SystemConfig.IP+":"+SystemConfig.PORT+"/pf/skin/c/version.txt";
			
			new Thread() {
				@Override
				public void run() {
					try {
						serverVersion = MyUtil.getServerVersionName(serversionUrl);
						if (!serverVersion.equals("")) {
							log("************************************************"+ serverVersion);
							handler.sendEmptyMessage(7);
						}
					} catch (Exception e) {
						Log.d("zlq","***************************" + e.toString());
					}
				}
			}.start();
		}
		
		initLayout();
		zlCheckBox = (CheckBox)findViewById(R.id.zlCheckBox);
		zlCheckBox.setChecked(true);
		remeber_numbe.setChecked(true);
		if (!MyUtil.isNetworkAvailable(MainActivity3.this)) {
			zlCheckBox.setEnabled(false);//网络不可用，不能补传
			remeber_numbe.setEnabled(false);
		}

		final long avalableSD = FileUtils.getSDAvailaleSize();
		if (avalableSD<50) {
			Toast.makeText(MainActivity3.this, "可用容量小于50MB，请清除不需要的文件", Toast.LENGTH_LONG).show();
		}
		
		
		FileService.copyAssetsDatabaseToData(this, "aaa.db");
		nameList = MyUtil.arrayToList(MyDatabaseHelper.getUserList(this));
		Log.i("ArrayList---Size", nameList.size()+"");
		
		
		
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
				final String userName = userNameET.getText().toString().trim();
				final String password = passwordET.getText().toString().trim();
				if(userName.equals("") || password.equals("")){
					Toast.makeText(MainActivity3.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
				}else{
					progressDialog = ProgressDialog.show(MainActivity3.this, "登录中...", "请稍候...", true, true);  
					
					new Thread(){  
			            @Override  
			        public void run(){     
			           try{  
			        	   getLogin(userName, password);
			           }catch(Exception e) {  
			        	   e.printStackTrace();
			               Log.d("zlq", "***************************"+e.toString());  
			          }  
			         }  
			       }.start();  
				}
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
            	serverIP.setText(settings.getString("ip", SystemConfig.IP));
            	serverPort.setText(settings.getString("port", SystemConfig.PORT));
				new AlertDialog.Builder(MainActivity3.this).setTitle("设置IP地址和端口号").setView(layout)
				.setNegativeButton("关闭", new DialogInterface.OnClickListener(){
		            public void onClick(DialogInterface dialog, int which) {
		            	
		            }})
		        .setPositiveButton("保存", new DialogInterface.OnClickListener(){
		                public void onClick(DialogInterface dialog, int which) {
		                SharedPreferences.Editor editor = settings.edit();
		                editor.putString("ip", serverIP.getText().toString().trim());
		                editor.putString("port", serverPort.getText().toString().trim());	
		                editor.commit();
		                
		                SystemConfig.IP = serverIP.getText().toString().trim();
		        		SystemConfig.PORT = serverPort.getText().toString().trim();
		        		SystemConfig.URL = "http://"+ SystemConfig.IP+":"+SystemConfig.PORT+"/pf/services/IPadService.pt";
		        		SystemConfig.DataUrl = "http://"+ SystemConfig.IP+":"+SystemConfig.PORT+"/pf/";
		        		
		                //public static String URL = "http://219.141.190.156:8080/pf/services/AndroidPhoneService.pt";
		         }})
		        .show();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MyUtil.leave(MainActivity3.this);
		}
		return true;
	}
	
	void initLayout(){
		btnOk = (Button) findViewById(R.id.btnOK);
		btnSetting = (Button) findViewById(R.id.btnSetting);
		userNameET = (EditText) findViewById(R.id.userName);
		passwordET = (EditText) findViewById(R.id.userPassword);
		userNameSpinner = (ImageView) findViewById(R.id.userNameSpinner);
		passwordClear = (ImageView) findViewById(R.id.userPassword_clear);
		remeber_numbe = (CheckBox) findViewById(R.id.remeber_number);
		passwordClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				passwordET.setText("");
			}
		});
	}
	void getLogin(String userName, String password){
		if (!MyUtil.isNetworkAvailable(MainActivity3.this)) {
			log("login");
			//断网
			String user = MyUtil.getFromPreference(MainActivity3.this, MyUtil.KEY_USER_NAME);
			String pwd =  MyUtil.getFromPreference(MainActivity3.this, MyUtil.KEY_USER_PWD);
			String token =  MyUtil.getFromPreference(MainActivity3.this, MyUtil.KEY_TOKEN);
			log(user+pwd);
			log(userName+password);
			if (user.equals(userName)&&pwd.equals(password)) {
				message = token;
				handler.sendEmptyMessage(1);
			}else{
				message = "用户名或密码错误";
				handler.sendEmptyMessage(2);
			}
		}else {// 有网
			String resultStr = "";
			
			JSONObject resultJSON;
			String result = "";
			try {
				String msg2 = "{'login':{'userName':'"+userName+"','passWord':'"+password+"'}}";
				handler.sendEmptyMessage(5);
				resultStr = JsonParser.getResponse(SystemConfig.URL, msg2);
				if(resultStr.equals("")){
					handler.sendEmptyMessage(4);//
				}else{
					resultJSON = new JSONObject(resultStr);
					result = resultJSON.getString("result");
					message = resultJSON.getString("message");
					
					if(result.equals("ok")){
						SystemConfig.token = message;
						SystemConfig.userName = userName;
						handler.sendEmptyMessage(1);
					}else if(result.equals("error")){
						handler.sendEmptyMessage(2);
					}
				}
			} catch (JSONException e) {
				handler.sendEmptyMessage(2);
				e.printStackTrace();
			}
			
		}	
	}
	
    private void getServerSet(){
    	
		String resultStr = "";
		JSONObject resultJSON;
		
		String result = "";
		String msgStr = "";
		try {
			if (!MyUtil.isNetworkAvailable(MainActivity3.this)) {
	    		//断网
				resultStr = MyUtil.getFromPreference(MainActivity3.this,MyUtil.KEY_SERVERSET);
				log("duan getServerSet");
			}else {
				//有网
				msgStr = "{'getServerSet':'shootSet', 'token':'"+SystemConfig.token+"'}";
				resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
				if (!resultStr.equals("")) {
					MyUtil.saveJson2preference(MainActivity3.this, MyUtil.KEY_SERVERSET, resultStr);
					log("save getServerSet");
				}
			}
			if(resultStr.equals("")){
				handler.sendEmptyMessage(4);//
			}else{
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if(result.equals("ok")){
					JSONObject set = resultJSON.getJSONObject("shootSet");
					SystemConfig.videoRecordTime = set.getString("videoRecordTime");
					SystemConfig.shootingMode = set.getString("shootingMode");
					SystemConfig.interval = set.getString("interval");
										
					handler.sendEmptyMessage(6);
				}else if(result.equals("error")){
					handler.sendEmptyMessage(2);
				}
			}
		} catch (JSONException e) {
			handler.sendEmptyMessage(2);
			e.printStackTrace();
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
	                            listView=new ListView(MainActivity3.this);
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
		            mInflater=LayoutInflater.from(MainActivity3.this);
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
		            final String passwordStr = nameList.get(position).get(2);
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
		                    log("apkFilePath is "+ apkFilePath);
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
		 /**
		     * MyHandler
		     * 
		     * @author Trinea 2012-12-18
		     */
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
//		                        downloadButton.setVisibility(View.GONE);
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
//		                        downloadButton.setVisibility(View.VISIBLE);
		                        downloadSize.setVisibility(View.GONE);

		                        if (status == DownloadManager.STATUS_FAILED) {
//		                            downloadButton.setText("下载失败，点击重新下载");
//		                            downloadButton.setText("下载更新");
		                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
//		                            downloadButton.setText("已下载过，点击重新下载");
//		                            downloadButton.setText("下载更新");
		                        } else {
//		                            downloadButton.setText("下载更新");
		                        }
		                    }
		                    break;
		            }
		        }
		    }
		    
		    @TargetApi(11)
			void showUpdateDialog(String _log) {
				String size = _log.substring(0,_log.indexOf("#"));
				_log = _log.replace(size+"#", "");
				String[] sslog = _log.split(";");
				log(sslog.length+"");
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.updatelog, null);
				final LinearLayout linearLayout = (LinearLayout)layout.findViewById(R.id.updatelog_layout);
				
				TextView tv1 = new TextView(MainActivity3.this);
				tv1.setText("版本号："+serverVersion+" ，大小："+size);
				TextView tv2 = new TextView(MainActivity3.this);
				tv2.setText("新版功能说明：");
				linearLayout.addView(tv1);
				linearLayout.addView(tv2);
				for (int i = 0; i < sslog.length; i++) {
					TextView textView = new TextView(MainActivity3.this);
					textView.setText(sslog[i]);
					linearLayout.addView(textView);
				}
//				Button cancelButton = (Button)layout.findViewById(R.id.download_canel);
//				downloadButton = (Button)layout.findViewById(R.id.download_button);
				downloadProgress = (ProgressBar)layout.findViewById(R.id.download_progress);
				downloadSize = (TextView)layout.findViewById(R.id.download_size);
				final TextView tip = (TextView)layout.findViewById(R.id.tv_tip);
				downloadPrecent = (TextView)layout.findViewById(R.id.download_precent);
				_handler = new MyHandler();
				downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		        downloadManagerPro = new DownloadManagerPro(downloadManager);
//		        downloadButton.setText("下载更新");
//		        cancelButton.setText("稍后再说");
		        initData();
		      
				
				downloadObserver = new DownloadChangeObserver();
		        completeReceiver = new CompleteReceiver();
		        /** register download success broadcast **/
		        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		        getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, downloadObserver);
		        updateView();
		        
		      
		         new AlertDialog.Builder(this).setTitle("程序有新版本").setView(layout)
		         .setPositiveButton("下载更新",
						new DialogInterface.OnClickListener() {

		        	 	private boolean tipedNet;
		        	 	int startint =0;//0未开启;1已开启
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 不关闭
								setClosable(dialog, false);
								if (!MyUtil.isNetworkAvailable(MainActivity3.this)) {
									return;
								}
								if (MyUtil.isWifiActive(MainActivity3.this)) {
//									Toast.makeText(MainActivity3.this, "", Toast.LENGTH_SHORT).show();
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
										}
									}else {
										tip.setVisibility(View.VISIBLE);
										tip.setText("检测到您当前为非wifi网络环境，将会产生数据流量，继续请再次点击“下载更新”，取消请点击 “稍后再说”");
										tipedNet = true;
									}
								}
							}
						})
					.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
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
//				downloadButton.setOnClickListener(new OnClickListener() {
					
//					@Override
//					public void onClick(View view) {
//						    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(APK_URL));
//					        request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME,DOWNLOAD_FILE_NAME);
//					        request.setTitle("医学信息在线系统");// download_notification_title
//					        request.setDescription("yixuezaixian desc");
//					        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//					        request.setVisibleInDownloadsUi(false);
//					        // request.allowScanningByMediaScanner();
//					        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
//					        // request.setShowRunningNotification(false);
//					        // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//					        request.setMimeType("application/com.trinea.download.file");
//					        downloadId = downloadManager.enqueue(request);
//					        /** save download id to preferences **/
//					        PreferencesUtils.putLongPreferences(MainActivity3.this,
//					        		PreferencesUtils.KEY_NAME_DOWNLOAD_ID, downloadId);
//					        updateView();
//						if (!MyUtil.isNetworkAvailable(MainActivity3.this)) {
//							return;
//						}
//						if (MyUtil.isWifiActive(MainActivity3.this)) {
////							Toast.makeText(MainActivity3.this, "", Toast.LENGTH_SHORT).show();
//							startDown();
//						}else {
//							
//						}
//					}
//				});
				
			}
			@TargetApi(11)
			void startDown(){
				DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://"+SystemConfig.IP+":"+SystemConfig.PORT+"/pf/skin/c/mps_setup_apad.apk"));
		        request.setDestinationInExternalPublicDir(SystemConfig.DOWNLOAD_FOLDER_NAME,SystemConfig.DOWNLOAD_FILE_NAME);
		        request.setTitle("医学信息在线系统");// download_notification_title
		        request.setDescription("yixuezaixian desc");
		        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		        request.setVisibleInDownloadsUi(false);
		        // request.allowScanningByMediaScanner();
		        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		        // request.setShowRunningNotification(false);
		        // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
		        request.setMimeType("application/com.trinea.download.file");
		        downloadId = downloadManager.enqueue(request);
		        /** save download id to preferences **/
		        PreferencesUtils.putLongPreferences(MainActivity3.this,
		        		PreferencesUtils.KEY_NAME_DOWNLOAD_ID, downloadId);
		        updateView();
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
			private void initData() {
		        downloadId = PreferencesUtils.getLongPreferences(this, PreferencesUtils.KEY_NAME_DOWNLOAD_ID);
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
			/**
			void tip(){
				new AlertDialog.Builder(this).setTitle("当前为非wifi网络环境").setMessage("点击继续将会使用您的流量。是否继续？")
				.setPositiveButton("继续", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
//						startDownloadApk();
					}
				}).setNegativeButton("取消", null).show();
			} 
			 */
			void log(String str){
				System.out.println("peter--"+str);
			}
}