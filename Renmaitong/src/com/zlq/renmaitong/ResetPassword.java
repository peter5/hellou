package com.zlq.renmaitong;


import org.json.JSONException;
import org.json.JSONObject;

import com.zlq.json.JsonParser;
import com.zlq.util.SystemConfig;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ResetPassword extends Activity{

	private ImageButton backImageBtn;
	private EditText userNameEt, passwordEt, newPasswordEt, repeatPasswordEt;
	private ImageButton resetImageView, backImageView;
	
	ResetPasswordListener mResetPasswordListener = new ResetPasswordListener();
	
	private ProgressDialog progressDialog; 
	private String message ;
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(ResetPassword.this, "更改密码成功", Toast.LENGTH_SHORT).show();
		    	   startActivity(new Intent(ResetPassword.this, RestPasswordSuccess.class));
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(ResetPassword.this, message, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 3){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(ResetPassword.this, "服务器返回值错误", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(ResetPassword.this, "服务器超时", Toast.LENGTH_SHORT).show();
		       }
		}   
	};  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_reset_password);
		initView();
		
	}
	
	private void initView(){
		backImageBtn = (ImageButton) findViewById(R.id.back);
		userNameEt = (EditText) findViewById(R.id.userName);
		userNameEt.setText(SystemConfig.userName);
		passwordEt = (EditText) findViewById(R.id.resetPassword);
		newPasswordEt = (EditText) findViewById(R.id.newPassword);
		repeatPasswordEt = (EditText) findViewById(R.id.repeatPassword);
		resetImageView = (ImageButton) findViewById(R.id.resetImageview);
		backImageView = (ImageButton) findViewById(R.id.backImageView);
		
		backImageView.setOnClickListener(mResetPasswordListener);
		backImageBtn.setOnClickListener(mResetPasswordListener);
		resetImageView.setOnClickListener(mResetPasswordListener);
	}
	
	private void bingPassword(){
		//{"changePassword":{"userName":"1001", "oldPassword":"1234", "newPassword":"12345"},
		//"token":"402881ea34bc62fa0134bc8976d1000c"}
    	String resultStr = "";
		JSONObject resultJSON;
		
		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'changePassword':{'userName':'"+SystemConfig.userName+"', 'oldPassword':'"+passwordEt.getText().toString()+
					"', 'newPassword':'"+newPasswordEt.getText().toString()+"'},'token':'"+SystemConfig.token+"'}";
			
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
	
	class ResetPasswordListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.backImageView:
				finish();
				System.out.println("backImageView");
				break;
			case R.id.resetImageview:
				if(passwordEt.getText().toString().equals("") || newPasswordEt.getText().toString().equals("")){
					Toast.makeText(ResetPassword.this, "原密码和新密码不能为空", Toast.LENGTH_SHORT).show();
				}else{
					if(!newPasswordEt.getText().toString().equals(repeatPasswordEt.getText().toString())){
						Toast.makeText(ResetPassword.this, "两次新密码不相同", Toast.LENGTH_SHORT).show();
					}else{
						progressDialog = ProgressDialog.show(ResetPassword.this, "修改密码...", "请稍等...", true, false);  
						new Thread(){  
				            @Override  
				        public void run(){     
				           try{  
				        	   bingPassword();
				           }catch(Exception e) {  
				               Log.d("zlq", "***************************"+e.toString());  
				          }  
				         }  
				       }.start(); 
					}
				}
				break;
			default:
				break;
			}
		}
		
	}
}
