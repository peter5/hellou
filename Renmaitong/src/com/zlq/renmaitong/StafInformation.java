package com.zlq.renmaitong;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.zlq.json.JsonParser;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class StafInformation extends Activity{

	private EditText staffName,  staffbumen, staffgangwei, staffPhone,
		staffOfficePhone, staffHomePhone, staffHobby, staffNote;
	private Spinner staffCompany;
	private ImageButton companyUpdate, companyUpdateBack;
	
	private String memberId = "";
	private String intentCompName = "";
	private String intentMemberName = "";
	private String intentPost = "";
	private HashMap<String, String> staff = new HashMap<String, String>();
	
	private int companyListPosition = 0;
	
	private ProgressDialog progressDialog; 
	private String message ;
	private String checkErrorMessage = "";
	
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   staffName.setText(intentMemberName);
			   		staffbumen.setText(staff.get("department"));
			   		staffgangwei.setText(intentPost);
			   		staffPhone.setText(staff.get("cellNum"));
			   		staffOfficePhone.setText(staff.get("officialNum"));
			   		staffHomePhone.setText(staff.get("homeNum"));
			   		staffHobby.setText(staff.get("hobby"));
			   		staffNote.setText(staff.get("remark"));
		    	   progressDialog.dismiss();
		    	   Toast.makeText(StafInformation.this, "获取员工信息成功", Toast.LENGTH_SHORT).show();
		    	   for(int i=0; i<ContactsManage.companyArrayList.size(); i++){
		    		   if(ContactsManage.companyArrayList.get(i).get("compName").equals(staff.get("companyName"))){
		    				staffCompany.setSelection(i);
		    		   }
		    	   }
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(StafInformation.this, message, Toast.LENGTH_LONG).show();
		       }else if(msg.what == 3){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(StafInformation.this, "服务器返回值错误", Toast.LENGTH_LONG).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(StafInformation.this, "服务器超时", Toast.LENGTH_LONG).show();
		       }else if(msg.what == 5){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(StafInformation.this, "更改员工信息成功", Toast.LENGTH_SHORT).show();
		       }
		}   
	};  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_staff_update);
		
		initView();
		
		memberId = getIntent().getStringExtra("memberId");
		intentCompName = getIntent().getStringExtra("compName");
		intentMemberName = getIntent().getStringExtra("memberName");
		intentPost = getIntent().getStringExtra("post");
		
		progressDialog = ProgressDialog.show(StafInformation.this, "加载联系人详细信息", "加载联系人中...", true, false);  
		new Thread(){  
            @Override  
        public void run(){     
           try{  
        	   getStaffData();
           }catch(Exception e) {  
               Log.d("zlq", "***************************"+e.toString());  
          }  
         }  
       }.start(); 
	}
	void log(String msg){
		System.out.println("msg:--"+msg);
	}
	
	private boolean Check(){
		boolean isOK = true;
		String staffNameStr = staffName.getText().toString();
		String staffbumenStr = staffbumen.getText().toString();
		String staffgangweiStr = staffgangwei.getText().toString();
		String staffPhoneStr = staffPhone.getText().toString().trim();
		String staffOfficePhoneStr = staffOfficePhone.getText().toString().trim();
		String staffHomePhoneStr = staffHomePhone.getText().toString().trim();
		
		
		log(staffOfficePhoneStr);
		log("staffPhoneStr.length() is "+staffPhoneStr.length());
		
		if(staffNameStr.equals("") || staffbumenStr.equals("") || staffgangweiStr.equals("") ){
			isOK = false;
			checkErrorMessage = "人员姓名.所在部门.职务岗位不能为空"+"\n";
		}else if(staffPhoneStr.equals("") && staffOfficePhoneStr.equals("")){
			isOK = false;
			checkErrorMessage = checkErrorMessage+"手机号码和办公电话至少一项必填"+"\n";
		}else if ((staffPhoneStr.length() != 11)&&(staffPhoneStr.length() != 0)) {
				isOK = false;
				checkErrorMessage = checkErrorMessage+"手机号码 长度不正确"+"\n";
		}else if(
				(!staffPhoneStr.equals(""))&&
				(!(RegExpValidator.IsTelephone(staffPhoneStr) || RegExpValidator.isMobileNO(staffPhoneStr)))
				){
				
				isOK = false;
				System.out.println("staffPhoneStr--"+staffPhoneStr);
				checkErrorMessage = checkErrorMessage+"手机号码 格式不正确"+"\n";
				
		}else if ((staffOfficePhoneStr.length() >= 1)&&(staffOfficePhoneStr.length() <= 6)) {
			isOK = false;
			checkErrorMessage = checkErrorMessage+"办公电话 长度不正确"+"\n";
		}else if(
				(!staffOfficePhoneStr.equals(""))&&
				(!(RegExpValidator.IsTelephone(staffOfficePhoneStr)))
				){
			System.out.println("staffOfficePhoneStr-********************-"+staffOfficePhoneStr);
				isOK = false;
				checkErrorMessage = checkErrorMessage+"办公电话 格式不正确"+"\n";
		} else if ((staffHomePhoneStr.length() >= 1)&&(staffHomePhoneStr.length() <= 6)) {
			isOK = false;
			checkErrorMessage = checkErrorMessage+"家庭电话 长度不正确"+"\n";
		}else if(
				(!staffHomePhoneStr.equals(""))&&
				(!(RegExpValidator.IsTelephone(staffHomePhoneStr)))
				){
				isOK = false;
				checkErrorMessage = checkErrorMessage+"家庭号码 格式不正确"+"\n";
		} 
		log(staffOfficePhoneStr+"--end");
		return isOK;
	}
	
	private void initView(){
		staffName = (EditText) findViewById(R.id.staffName);
		staffbumen = (EditText) findViewById(R.id.staffbumen);
		staffgangwei = (EditText) findViewById(R.id.staffgangwei);
		staffPhone = (EditText) findViewById(R.id.staffPhone);
		staffOfficePhone = (EditText) findViewById(R.id.update_staffOfficePhone);
		staffHomePhone = (EditText) findViewById(R.id.staffHomePhone);
		staffHobby = (EditText) findViewById(R.id.staffHobby);
		staffNote = (EditText) findViewById(R.id.staffNote);
		
		companyUpdate = (ImageButton) findViewById(R.id.staffUpdate);
		companyUpdateBack = (ImageButton) findViewById(R.id.staffUpdateBack);
		companyUpdateBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(StafInformation.this, R.layout.simple_spinner_item);
		for(int i=0; i<ContactsManage.companyArrayList.size(); i++){
			nameAdapter.add(ContactsManage.companyArrayList.get(i).get("compName"));
		}
		
		System.out.println(nameAdapter.getCount()+"adapter size");
		nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		staffCompany = (Spinner) findViewById(R.id.staffCompany);
		staffCompany.setAdapter(nameAdapter);
		staffCompany.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				System.out.println("position"+position);
				companyListPosition = position;
				staffCompany.setSelection(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		
		
		companyUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkErrorMessage = "";
				System.out.println("staffUpdate is clicked");
				if(Check() == true){
					new Thread(){  
			            @Override  
			        public void run(){     
			           try{  
			        	   updateStaffData();
			           }catch(Exception e) {  
			               Log.d("zlq", "***************************"+e.toString());  
			          }  
			         }  
			       }.start(); 
				}else{
					Toast.makeText(StafInformation.this, checkErrorMessage, Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	
	private void getStaffData(){
		//{"getMemberInfo":{"memberId":memberId},"token":令牌}
	  	String resultStr = "";
		JSONObject resultJSON;
			
		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'getMemberInfo':{'memberId':'"+memberId+"'}, 'token':'"+SystemConfig.token+"'}";
				
			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
			System.out.println(resultStr);
			if(resultStr.equals("")){
				handler.sendEmptyMessage(4);//
			}else{
					resultJSON = new JSONObject(resultStr);
					result = resultJSON.getString("result");
					message = resultJSON.getString("message");
					if(result.equals("ok")){
						JSONObject companyObject = resultJSON.getJSONObject("memberInfo");
						staff.put("memberId", companyObject.getString("memberId"));
						staff.put("memberName", companyObject.getString("memberName"));
						staff.put("companyId", companyObject.getString("companyId"));
						staff.put("companyName", companyObject.getString("companyName"));
						staff.put("department", companyObject.getString("department"));
						staff.put("cellNum", companyObject.getString("cellNum"));
						staff.put("officialNum", companyObject.getString("officialNum"));
						staff.put("homeNum", companyObject.getString("homeNum"));
						staff.put("hobby", companyObject.getString("hobby"));
						staff.put("remark", companyObject.getString("remark"));
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
	
	private void updateStaffData(){
		//{"changeMemberInfo":{"memberId":成员主键,"companyId":单位主键,"department":部门,"post": 职位,
		//"cellNum": 手机号, "officialNum":办公电话, "homeNum":家庭电话, "hobby":兴趣爱好, "remark":备注},"token":令牌}
	  	String resultStr = "";
		JSONObject resultJSON;
			
		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'changeMemberInfo':{'memberId':'"+memberId+"', 'companyId':'"+ContactsManage.companyArrayList.get(companyListPosition).get("compId")+
					"', 'department':'"+staffbumen.getText().toString()+"', 'post':'"+intentPost+
					"', 'cellNum':'"+staffPhone.getText().toString()+"', 'officialNum':'"+staffOfficePhone.getText().toString()+
					"', 'homeNum':'"+staffHomePhone.getText().toString()+"', 'hobby':'"+staffHobby.getText().toString()+
					"', 'remark':'"+staffNote.getText().toString()+"'},'token':'"+SystemConfig.token+"'}";
				
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
