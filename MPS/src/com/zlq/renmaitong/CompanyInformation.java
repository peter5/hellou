package com.zlq.renmaitong;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zlq.json.JsonParser;
import io.vov.vitamio.R;
import com.zlq.util.RegExpValidator;
import com.zlq.util.SystemConfig;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
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
	private String checkErrorMessage = ""; //У����ݴ�����Ϣ
	
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   	companyName.setText(intentCompName);
			   		cityName.setText(intentCityName);
			   		areaName.setText(company.get("address"));
			   		postCode.setText(company.get("mailCode"));
			   		officeNumber.setText(company.get("officeNum"));
			   		foxNumber.setText(company.get("faxNum"));
			   		note.setText(company.get("note"));
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CompanyInformation.this, "��ȡ��λ��Ϣ�ɹ�", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CompanyInformation.this, message, Toast.LENGTH_LONG).show();
		       }else if(msg.what == 3){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CompanyInformation.this, "����������ֵ����", Toast.LENGTH_LONG).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CompanyInformation.this, "��������ʱ", Toast.LENGTH_LONG).show();
		       }else if(msg.what == 5){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(CompanyInformation.this, "��ĵ�λ��Ϣ�ɹ�", Toast.LENGTH_SHORT).show();
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
		
		progressDialog = ProgressDialog.show(CompanyInformation.this, "���ص�λ��Ϣ��...", "���Ե�...", true, false);  
		
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
			checkErrorMessage = "��λ��ͳ��в���Ϊ��"+"\n";
		}else if(!postCodeStr.equals("")){
			if(!RegExpValidator.IsPostalcode(postCodeStr)){
				isOK = false;
				checkErrorMessage = checkErrorMessage+"�������� ��ʽ����ȷ"+"\n";
			}
		}else if(!officeNumberStr.equals("")){
			if(!(RegExpValidator.IsTelephone(officeNumberStr) || RegExpValidator.isMobileNO(officeNumberStr))){
				isOK = false;
				checkErrorMessage = checkErrorMessage+"�칫�绰 ��ʽ����ȷ"+"\n";
			}
		} else if(!foxNumberStr.equals("")){
			if(!(RegExpValidator.IsTelephone(foxNumberStr) || RegExpValidator.isMobileNO(foxNumberStr))){
				isOK = false;
				checkErrorMessage = checkErrorMessage+"������� ��ʽ����ȷ"+"\n";
			}
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
		//{"getCompInfo":{"compId":compId},"token":����}
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
		//{"changeCompInfo":{"compId":��λ����,"address":��ϸ��ַ,"mailCode":�ʱ�, "officeNum":����绰, 
		//"faxNum":�����, "note":��ע},"token":����}
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
