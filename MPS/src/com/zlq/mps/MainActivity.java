package com.zlq.mps;

import io.vov.vitamio.R;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zlq.json.JsonParser;
import com.zlq.util.MyDatabaseHelper;
import com.zlq.util.MyUtil;
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
    private ImageView passwordClear;
    private CheckBox remeber_numbe;
    
    
	private ProgressDialog progressDialog; 
	private String message;
	private String msgStr = "";
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
		    	   
		    	   Log.i("ArrayList---Size---Two", nameList.size()+"");
		    	 
		    	   SystemConfig.token = message;
		    	   SystemConfig.userName = userNameET.getText().toString();
		    	   SystemConfig.password = passwordET.getText().toString();
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
		    	   Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(MainActivity.this, "登录超时", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 5){
//		    	   Toast.makeText(MainActivity.this, SystemConfig.URL+"  "+msgStr, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 6){
		    	   progressDialog.dismiss();
		    	   startActivity(new Intent(MainActivity.this, BrowsePage2.class));
		    	   finish();
		       }
		}   
	};  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_activie_account);
		initLayout();
		
		
		
//		startActivity(new Intent(MainActivity.this, BrowsePage.class));
		
//		PictuerCompress.compFile(Environment.getExternalStorageDirectory().getPath()+"/myImage/aaaa.jpg");
//		String aaa = null;
//		System.out.println(aaa.indexOf(2));
		
		
//		FileService.copyAssetsDatabaseToData(this, "aaa.db");
//		nameList = MyUtil.arrayToList(MyDatabaseHelper.getUserList(this));
//		Log.i("ArrayList---Size", nameList.size()+"");
//		
//		//��ʼ��URL
//		final SharedPreferences settings = getSharedPreferences("setting", 0);
//		String ip = settings.getString("url", "219.141.190.156");
//		String port = settings.getString("port", "8080");
//		SystemConfig.URL = "http://"+ip+":"+port+"/pf/services/IPadService.pt";
//		System.out.println("SystemConfig.URL>>>>>>>>>>>"+SystemConfig.URL);
//		
//		if(!nameList.isEmpty()){
//			userNameET.setText(nameList.get(0).get(1));
//			if(nameList.get(0).get(3).equals("1")){
//				passwordET.setText(nameList.get(0).get(2));
//				remeber_numbe.setChecked(true);
//			}else{
//				passwordET.setText("");
//				remeber_numbe.setChecked(false);
//			}
//		}
		
//		startActivity(new Intent(this, InformationReport.class));
//		ArrayList<String> list = new ArrayList<String>();
//		list.add("name1");
//		list.add("password");
//		nameList.add(list);
//		list = new ArrayList<String>();
//		list.add("name2");
//		list.add("password2");
//		nameList.add(list);
	
//		setUpListeners();
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(MainActivity.this, BrowsePage2.class));
				finish();
				
//				final String userName = userNameET.getText().toString();
//				final String password = passwordET.getText().toString();
//				if(userName.equals("") || password.equals("")){
//					Toast.makeText(MainActivity.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
//				}else{
//					progressDialog = ProgressDialog.show(MainActivity.this, "登录中...", "请稍候...", true, true);  
////					final TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//					
//					new Thread(){  
//			            @Override  
//			        public void run(){     
//			           try{  
////							System.out.println("mTelephonyMgr.getSubscriberId()"+mTelephonyMgr.getSubscriberId());
////							String ismiStr = mTelephonyMgr.getSubscriberId()==null?"13800000000":mTelephonyMgr.getSubscriberId();
////							getLogin(userName, password, ismiStr);
//							getLogin2(userName, password);
////							getLogin(userName, password, "13813813800");
//			           }catch(Exception e) {  
//			               Log.d("zlq", "***************************"+e.toString());  
//			          }  
//			         }  
//			       }.start();  
//				}
////				startActivity(new Intent(MainActivity.this, InformationReport.class));
			}
		});
//		btnSetting.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				LayoutInflater inflater = getLayoutInflater();
//				final View layout = inflater.inflate(R.layout.setting_dialog, (ViewGroup) findViewById(R.id.dialog));
//				final EditText serverIP = (EditText)layout.findViewById(R.id.serverIP);
//            	final EditText serverPort = (EditText)layout.findViewById(R.id.serverPort);
//            	final SharedPreferences settings = getSharedPreferences("setting", 0);
//            	serverIP.setText(settings.getString("url", "219.141.190.156"));
//            	serverPort.setText(settings.getString("port", "8080"));
//				new AlertDialog.Builder(MainActivity.this).setTitle("设置IP地址和端口号").setView(layout)
//				.setNegativeButton("关闭", new DialogInterface.OnClickListener(){
//		            public void onClick(DialogInterface dialog, int which) {
//		            	
//		            }})
//		        .setPositiveButton("保存", new DialogInterface.OnClickListener(){
//		                public void onClick(DialogInterface dialog, int which) {
//		                SharedPreferences.Editor editor = settings.edit();
//		                editor.putString("url", serverIP.getText().toString());
//		                editor.putString("port", serverPort.getText().toString());	
//		                editor.commit();
//		                //����public static String URL = "http://219.141.190.156:8080/pf/services/AndroidPhoneService.pt";
//		                SystemConfig.URL = "http://"+serverIP.getText().toString()+":"+serverPort.getText().toString()+
//		                		"/pf/services/AndroidPhoneService.pt";
//		                System.out.println("SystemConfig.URL>>>>>>>>>>>"+SystemConfig.URL);
//		         }})
//		        .show();
//
//			}
//		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
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
		passwordClear = (ImageView) findViewById(R.id.userPassword_clear);
		remeber_numbe = (CheckBox) findViewById(R.id.remeber_number);
		passwordClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				passwordET.setText("");
			}
		});
	}
	
	void getLogin(String userName, String password, String IMSI){
		String resultStr = "";
		
		JSONObject resultJSON;
		String result = "";
		try {
			String msg2 = "{'login':{'userName':'"+userName+"','passWord':'"+password+"'}}";
			
			handler.sendEmptyMessage(5);
			resultStr = JsonParser.getResponse(SystemConfig.URL, msg2);
			System.out.println("result:>>>>>>>>>>>>>>>>>>"+resultStr+"SystemConfig.URL"+SystemConfig.URL);
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
	
	void getLogin2(String userName, String password){
		String resultStr = "";
		
		JSONObject resultJSON;
		String result = "";
		try {
			String msg2 = "{'login':{'userName':'"+userName+"','passWord':'"+password+"'}}";
			
			handler.sendEmptyMessage(5);
			resultStr = JsonParser.getResponse(SystemConfig.URL, msg2);
			System.out.println("result:>>>>>>>>>>>>>>>>>>"+resultStr+"SystemConfig.URL"+SystemConfig.URL);
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
	
	
	
	
    private void getServerSet(){
		String resultStr = "";
		JSONObject resultJSON;
		
		String result = "";
		String msgStr = "";
		try {
			//{"getMyProjectList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc641ada0003"}
			//{"getServerSet":"shootSet","token":令牌}
			msgStr = "{'getServerSet':'shootSet', 'token':'"+SystemConfig.token+"'}";
			
			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
			System.out.println("数据返回值"+resultStr);
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
					System.out.println("set=============="+set.toString());
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}