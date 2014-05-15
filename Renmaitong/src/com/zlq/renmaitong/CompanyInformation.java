package com.zlq.renmaitong;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zlq.json.JsonParser;
import com.zlq.util.RegExpValidator;
import com.zlq.util.SystemConfig;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CompanyInformation extends Activity{

	private EditText companyName, cityName, customType, areaName, postCode, officeNumber, foxNumber, note;
	private ImageButton companyUpdate, companyUpdateBack;
	
	private String compId = "";
	private String intentCityName = "";
	private String intentCompName = "";
	private HashMap<String, String> company = new HashMap<String, String>();
	
	private ProgressDialog progressDialog; 
	private String message ;
	private String checkErrorMessage = ""; //校验数据错误信息
	
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   	companyName.setText(intentCompName);
		    	   	cityName.setText(intentCityName);
		    	   	// add 2013-04-26 23:40
		    	   	companyName.setCursorVisible(false);      
		    	   	companyName.setFocusable(false);         
		    	   	companyName.setFocusableInTouchMode(false);
		    	   	companyName.setTextColor(Color.GRAY);
		    	   	cityName.setCursorVisible(false);      
		    	   	cityName.setFocusable(false);         
		    	   	cityName.setFocusableInTouchMode(false);
		    	   	cityName.setTextColor(Color.GRAY);
		    	   	//add by peter
			   		
			   		
			   		areaName.setText(company.get("address"));
			   		postCode.setText(company.get("mailCode"));
			   		officeNumber.setText(company.get("officeNum"));
			   		foxNumber.setText(company.get("faxNum"));
			   		note.setText(company.get("note"));
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CompanyInformation.this, "获取单位信息成功", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CompanyInformation.this, message, Toast.LENGTH_LONG).show();
		       }else if(msg.what == 3){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CompanyInformation.this, "服务器返回值错误", Toast.LENGTH_LONG).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CompanyInformation.this, "服务器超时", Toast.LENGTH_LONG).show();
		       }else if(msg.what == 5){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CompanyInformation.this, "更改单位信息成功", Toast.LENGTH_SHORT).show();
		       }
		}   
	};  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_company_update);
		
		initView();
		
		compId = getIntent().getStringExtra("companyId");
		intentCityName = getIntent().getStringExtra("cityName");
		intentCompName = getIntent().getStringExtra("compName");
		
		progressDialog = ProgressDialog.show(CompanyInformation.this, "加载单位信息中...", "请稍等...", true, false);  
		
		new Thread(){  
            @Override  
        public void run(){     
           try{  
        	   getCompanyData();
           }catch(Exception e) {  
               Log.d("zlq", "***************************"+e.toString());  
          }  
         }  
       }.start();  
	}
	
	private boolean Check(){
		boolean isOK = true;
		String companyNameStr = companyName.getText().toString();
		String cityNameStr = cityName.getText().toString();
		String postCodeStr = postCode.getText().toString();
		String officeNumberStr = officeNumber.getText().toString();
		String foxNumberStr = foxNumber.getText().toString();
		
		if(companyNameStr.equals("") || cityNameStr.equals("")){
			isOK = false;
			checkErrorMessage = "单位名和城市不能为空"+"\n";
		}else if(
				(!postCodeStr.equals(""))&&
				(!RegExpValidator.IsPostalcode(postCodeStr))
				){
				isOK = false;
				checkErrorMessage = checkErrorMessage+"邮政编码 格式不正确"+"\n";
		}
		else if ((officeNumberStr.length() >= 1)&&(officeNumberStr.length() <= 6)) {
			isOK = false;
			checkErrorMessage = checkErrorMessage+"办公电话 长度不正确"+"\n";
		}
		else if(
				(!officeNumberStr.equals(""))&&
				(!(RegExpValidator.IsTelephone(officeNumberStr) ))
				){
				isOK = false;
				checkErrorMessage = checkErrorMessage+"办公电话 格式不正确"+"\n";
		}else if ((foxNumberStr.length() >= 1)&&(foxNumberStr.length() <= 6)) {
			isOK = false;
			checkErrorMessage = checkErrorMessage+"传真号码 长度不正确"+"\n";
		} else if(
				(!foxNumberStr.equals(""))&&
				(!(RegExpValidator.IsTelephone(foxNumberStr) ))
				){
				isOK = false;
				checkErrorMessage = checkErrorMessage+"传真号码 格式不正确"+"\n";
		} 
		return isOK;
	}
	
	private void initView(){
		companyName = (EditText) findViewById(R.id.update_companyname);
		cityName = (EditText) findViewById(R.id.update_cityname);
		customType = (EditText) findViewById(R.id.update_customType);
		areaName = (EditText) findViewById(R.id.update_areaname);
		postCode = (EditText) findViewById(R.id.update_postCode);
		officeNumber = (EditText) findViewById(R.id.update_officeName);
		foxNumber = (EditText) findViewById(R.id.update_phoneNumber);
		note = (EditText) findViewById(R.id.update_companyNOte);
		
		companyUpdate = (ImageButton) findViewById(R.id.companyUpdate);
		companyUpdateBack = (ImageButton) findViewById(R.id.companyUpdateBack);
		companyUpdateBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		companyUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkErrorMessage = "";
				if(Check() == true){
					new Thread(){  
			            @Override  
			        public void run(){     
			           try{  
			        	   updateCompanyData();
			           }catch(Exception e) {  
			               Log.d("zlq", "***************************"+e.toString());  
			          }  
			         }  
			       }.start(); 
				}else{
					Toast.makeText(CompanyInformation.this, checkErrorMessage, Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	
	private void getCompanyData(){
		//{"getCompInfo":{"compId":compId},"token":令牌}
	  	String resultStr = "";
		JSONObject resultJSON;
			
		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'getCompInfo':{'compId':'"+compId+"'}, 'token':'"+SystemConfig.token+"'}";
				
			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
			System.out.println(resultStr);
			if(resultStr.equals("")){
				handler.sendEmptyMessage(4);//
			}else{
					resultJSON = new JSONObject(resultStr);
					result = resultJSON.getString("result");
					message = resultJSON.getString("message");
					if(result.equals("ok")){
						JSONObject companyObject = resultJSON.getJSONObject("compInfo");
						company.put("compId", companyObject.getString("compId"));
						company.put("compName", companyObject.getString("compName"));
						company.put("cityName", companyObject.getString("cityName"));
						company.put("address", companyObject.getString("address"));
						company.put("mailCode", companyObject.getString("mailCode"));
						company.put("officeNum", companyObject.getString("officeNum"));
						company.put("faxNum", companyObject.getString("faxNum"));
						company.put("note", companyObject.getString("note"));
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
	
	private void updateCompanyData(){
		//{"changeCompInfo":{"compId":单位主键,"address":详细地址,"mailCode":邮编, "officeNum":公办电话, 
		//"faxNum":传真号, "note":备注},"token":令牌}
	  	String resultStr = "";
		JSONObject resultJSON;
			
		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'changeCompInfo':{'compId':'"+compId+"', 'address':'"+areaName.getText().toString()+
					"', 'mailCode':'"+postCode.getText().toString()+"', 'officeNum':'"+officeNumber.getText().toString()+
					"', 'faxNum':'"+foxNumber.getText().toString()+"', 'note':'"+note.getText().toString()+
					"'},'token':'"+SystemConfig.token+"'}";
				
			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
			System.out.println(resultStr);
			if(resultStr.equals("")){
				handler.sendEmptyMessage(4);//
			}else{
					resultJSON = new JSONObject(resultStr);
					result = resultJSON.getString("result");
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
	}
	
	 @Override 
	   public void onConfigurationChanged(Configuration newConfig)
	   { 
	       super.onConfigurationChanged(newConfig); 
	    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
	    	
	    }
	    else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
	    }
	   }
}
