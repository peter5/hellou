package com.zlq.renmaitong;

import org.json.JSONException;
import org.json.JSONObject;

import com.zlq.json.JsonParser;
import com.zlq.util.SystemConfig;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BingPhoneNumber extends Activity{

	private EditText passwordEt, phoneEt;
	private EditText userNameTV;
	private ImageView bindNumberMV, bindNumberBackMV;
	private ImageButton title_back;
	
	private ProgressDialog progressDialog; 
	private String message ;
	private String imsi;
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BingPhoneNumber.this, "绑定成功", Toast.LENGTH_SHORT).show();
		    	   startActivity(new Intent(BingPhoneNumber.this, BingPhoneSuccess.class));
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BingPhoneNumber.this, message, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 3){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BingPhoneNumber.this, "服务器返回值错误", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(BingPhoneNumber.this, "服务器超时", Toast.LENGTH_SHORT).show();
		       }
		}   
	};  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_bing_phone);
		
		initView();
		final TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		imsi = mTelephonyMgr.getSubscriberId();
	}
	
	void initView(){
		passwordEt = (EditText) findViewById(R.id.bindPasswordET);
		phoneEt = (EditText) findViewById(R.id.bindPhoneET);
		userNameTV = (EditText) findViewById(R.id.bindUserName);
		bindNumberMV = (ImageView) findViewById(R.id.bindNumberMV);
		bindNumberBackMV = (ImageView) findViewById(R.id.bingNumberBackMV); 
		title_back = (ImageButton) findViewById(R.id.title_back);
		title_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		userNameTV.setText(SystemConfig.userName);
		bindNumberBackMV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		bindNumberMV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(passwordEt.getText().toString().equals("") ||
						phoneEt.getText().toString().equals("")){
					Toast.makeText(BingPhoneNumber.this, "密码和手机号不能为空", Toast.LENGTH_SHORT).show();
				}else{
					if(imsi == null){
						Toast.makeText(BingPhoneNumber.this, "无法获取到手机卡", Toast.LENGTH_SHORT).show();
					}else{
						progressDialog = ProgressDialog.show(BingPhoneNumber.this, "绑定手机中...", "请稍等...", true, false);  
						new Thread(){  
				            @Override  
				        public void run(){     
				           try{  
				        	   bindNumber();
				           }catch(Exception e) {  
				               Log.d("zlq", "***************************"+e.toString());  
				          }  
				         }  
				       }.start(); 
					}
				}
			}
		});
	}
	
	void bindNumber(){
		//{" bindingPhoneCard":{"userName":"1001","passWord":"1234", "phoneNumber":"12345", 
		//"imsi":"13813813800"},"token":"402881ea34bc62fa0134bc8b856d0010"}
    	String resultStr = "";
		JSONObject resultJSON;
		
		String result = "";
		String msgStr = "";
		try {
			
			msgStr = "{'bindingPhoneCard':{'userName':'"+SystemConfig.userName+"', 'passWord':'"+
					passwordEt.getText().toString()+"', 'phoneNumber':'"+phoneEt.getText().toString()+"'," +
					"'imsi':'"+imsi+"'},'token':'"+SystemConfig.token+"'}";
			
			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
			System.out.println(resultStr);
			if(resultStr.equals("")){
				handler.sendEmptyMessage(4);//
			}else{
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if(result.equals("ok")){
					handler.sendEmptyMessage(1);
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
	}
	
	
}
