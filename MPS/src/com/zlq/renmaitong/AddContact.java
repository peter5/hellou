package com.zlq.renmaitong;


import java.util.ArrayList;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

public class AddContact extends Activity{

	private RadioButton company, staff;
	private LinearLayout addContentLayout;
	private LinearLayout myCompanyContent;
	private LinearLayout myStaffContent;
	private LayoutInflater flater;
	
	private int currenId = 0;
	int namePosition = 0;
	private boolean isUpdated = false;
	private boolean isCompanyUpdated = false;
	private boolean iscompanyClear = false;
	private boolean isStaffClear = false;
	
	private String companyCheckErrorMessage = "";
	private String staffCheckErrorMessage = "";
	
	
	private ArrayList<HashMap<String, String>> companyArrayList = new ArrayList<HashMap<String, String>>() ;
	
	private EditText addcompanyCompanyname, addcompanyCityname, addcompanyCustomType, addcompanyAreaname,
		addcompanyPostCode, addcompanyOfficeName, addcompanyFoxNumber, addcompanyCompanyNOte;
	
	private EditText  staffName, staffbumen, staffgangwei, staffPhone, staffOfficePhone,
		staffHomePhone, staffHobby, staffNote;
	private Spinner staffCompany ;

	private ImageButton myUpdate, myBack, myAdd, myClear;
	
	private ProgressDialog progressDialog; 
	private String message ;
	
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   SystemConfig.isAddContacts = true;
		    	   if(iscompanyClear == true){
		    		   clearData();
		    		   iscompanyClear =  false;
		    	   }
		    	   isCompanyUpdated = true;
		    	   progressDialog.dismiss();
		    	   Toast.makeText(AddContact.this, "��ӵ�λ��Ϣ�ɹ�", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(AddContact.this, message, Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 3){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(AddContact.this, "����������ֵ����", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(AddContact.this, "��������ʱ", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 5){
		    	   SystemConfig.isAddContacts = true;
		    	   if(isStaffClear == true){
		    		   clearData();
		    		   isStaffClear = false;
		    	   }
		    	   isUpdated = true;
		    	   progressDialog.dismiss();
		    	   Toast.makeText(AddContact.this, "�����Ա��Ϣ�ɹ�", Toast.LENGTH_SHORT).show();
		       }else if(msg.what == 6){
		    		ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(AddContact.this, R.layout.simple_spinner_item);
		    		nameAdapter.clear();
		    		nameAdapter.notifyDataSetChanged();
		    		nameAdapter.notifyDataSetInvalidated();
		    		if(!nameAdapter.isEmpty()){
		    			for(int i=0;i<nameAdapter.getCount(); i++){
		    				System.out.println(nameAdapter.getItem(i));
		    				nameAdapter.remove(nameAdapter.getItem(i));
		    			}
		    		}
		    		for(int i=0; i<companyArrayList.size(); i++){
						nameAdapter.add(companyArrayList.get(i).get("compName"));
					}
		    		
		    		System.out.println(nameAdapter.getCount()+"adapter size");
					nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					staffCompany = (Spinner) findViewById(R.id.staffCompany);
					staffCompany.invalidate();
					staffCompany.setAdapter(nameAdapter);
					staffCompany.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
							namePosition = position;
							System.out.println("position"+position);
							staffCompany.setSelection(position);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
						}
					});
		    	   progressDialog.dismiss();
		    	   Toast.makeText(AddContact.this, "��ȡ��λ��Ϣ�ɹ�", Toast.LENGTH_SHORT).show();
		       }
		}   
	};  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contact);
		
		initView();
	}
	
	private boolean companyCheck(){
		boolean isOK = true;
		String companyNameStr = addcompanyCompanyname.getText().toString();
		String cityNameStr = addcompanyCityname.getText().toString();
		String postCodeStr = addcompanyPostCode.getText().toString();
		String officeNumberStr = addcompanyOfficeName.getText().toString();
		String foxNumberStr = addcompanyFoxNumber.getText().toString();
		
		if(companyNameStr.equals("") || cityNameStr.equals("")){
			isOK = false;
			companyCheckErrorMessage = "��λ��ͳ��в���Ϊ��"+"\n";
		}else if(!postCodeStr.equals("")){
			if(!RegExpValidator.IsPostalcode(postCodeStr)){
				isOK = false;
				companyCheckErrorMessage = companyCheckErrorMessage+"�������� ��ʽ����ȷ"+"\n";
			}
		}else if(!officeNumberStr.equals("")){
			if(!RegExpValidator.IsTelephone(officeNumberStr) || !RegExpValidator.isMobileNO(officeNumberStr)){
				isOK = false;
				companyCheckErrorMessage = companyCheckErrorMessage+"�칫�绰 ��ʽ����ȷ"+"\n";
			}
		} else if(!foxNumberStr.equals("")){
			if(!RegExpValidator.IsTelephone(foxNumberStr) || !RegExpValidator.isMobileNO(foxNumberStr)){
				isOK = false;
				companyCheckErrorMessage = companyCheckErrorMessage+"������� ��ʽ����ȷ"+"\n";
			}
		} 
		return isOK;
	}
	
	private boolean staffCheck(){
		boolean isOK = true;
		String staffNameStr = staffName.getText().toString();
		String staffbumenStr = staffbumen.getText().toString();
		String staffgangweiStr = staffgangwei.getText().toString();
		String staffPhoneStr = staffPhone.getText().toString();
		String staffOfficePhoneStr = staffOfficePhone.getText().toString();
		String staffHomePhoneStr = staffHomePhone.getText().toString();
		
		if(staffNameStr.equals("") || staffbumenStr.equals("") || staffgangweiStr.equals("") ){
			isOK = false;
			staffCheckErrorMessage = "��Ա����.���ڲ���.ְ���λ����Ϊ��"+"\n";
		}else if(staffPhoneStr.equals("") && staffOfficePhoneStr.equals("")){
			isOK = false;
			staffCheckErrorMessage = staffCheckErrorMessage+"�ֻ����Ͱ칫�绰����һ�����"+"\n";
		}else if(!staffPhoneStr.equals("")){
			if(!RegExpValidator.isMobileNO(staffPhoneStr)){
				isOK = false;
				staffCheckErrorMessage = staffCheckErrorMessage+"�ֻ���� ��ʽ����ȷ"+"\n";
			}
		}else if(!staffOfficePhoneStr.equals("")){
			if(!RegExpValidator.IsTelephone(staffOfficePhoneStr) || !RegExpValidator.isMobileNO(staffOfficePhoneStr)){
				isOK = false;
				staffCheckErrorMessage = staffCheckErrorMessage+"�칫�绰 ��ʽ����ȷ"+"\n";
			}
		} else if(!staffHomePhoneStr.equals("")){
			if(!RegExpValidator.IsTelephone(staffHomePhoneStr) || !RegExpValidator.isMobileNO(staffHomePhoneStr)){
				isOK = false;
				staffCheckErrorMessage = staffCheckErrorMessage+"������� ��ʽ����ȷ"+"\n";
			}
		} 
		return isOK;
	}
	
	private void initView(){
		flater = LayoutInflater.from(this);
		addContentLayout = (LinearLayout) findViewById(R.id.addContentLayout);
		myCompanyContent = (LinearLayout) findViewById(R.id.myCompanyContent);
		myStaffContent = (LinearLayout) findViewById(R.id.myStaffContent);
		myStaffContent.setVisibility(View.GONE);
//		myCompanyContent.setVisibility(View.GONE);
		myUpdate = (ImageButton) findViewById(R.id.myUpdate);
		myBack = (ImageButton) findViewById(R.id.myBack);
		myAdd = (ImageButton) findViewById(R.id.myAdd);
		myClear = (ImageButton) findViewById(R.id.myClear);
		company = (RadioButton) findViewById(R.id.radiobutton_1);
		staff = (RadioButton) findViewById(R.id.radiobutton_2);
		
		//��ӵ�λ
		addcompanyCompanyname = (EditText) findViewById(R.id.addcompany_companyname);
		addcompanyCityname = (EditText) findViewById(R.id.addcompany_cityname);
		addcompanyAreaname = (EditText) findViewById(R.id.addcompany_areaname);
		addcompanyPostCode = (EditText) findViewById(R.id.addcompany_postCode);
		addcompanyOfficeName = (EditText) findViewById(R.id.addcompany_officeName);
		addcompanyFoxNumber = (EditText) findViewById(R.id.addcompany_faxNumber);
		addcompanyCompanyNOte = (EditText) findViewById(R.id.addcompany_companyNOte);
		
		//�����Ա
		staffName = (EditText) findViewById(R.id.staffName);
		staffbumen = (EditText) findViewById(R.id.staffbumen);
		staffgangwei = (EditText) findViewById(R.id.staffgangwei);
		staffPhone = (EditText) findViewById(R.id.staffPhone);
		staffOfficePhone = (EditText) findViewById(R.id.staffOfficePhone);
		staffHomePhone = (EditText) findViewById(R.id.staffHomePhone);
		staffHobby = (EditText) findViewById(R.id.staffHobby);
		staffNote = (EditText) findViewById(R.id.staffNote);
		
		company.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked == true){
					isCompanyUpdated = false;
					currenId = 0;
					myCompanyContent.setVisibility(View.VISIBLE);
					myStaffContent.setVisibility(View.GONE);
					System.out.println("aaaaaaa");
				}
			}
		});
		
		staff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked == true){
					currenId = 1;
					isUpdated = false;
					
					progressDialog = ProgressDialog.show(AddContact.this, "������Ա��Ϣ", "������Ա��Ϣ...", true, false);  
					myStaffContent.setVisibility(View.VISIBLE);
					myCompanyContent.setVisibility(View.GONE);
					
					new Thread(){  
			            @Override  
			        public void run(){     
			           try{  
			        	   getContactsData();
			           }catch(Exception e) {  
			               Log.d("zlq", "***************************"+e.toString());  
			          }  
			         }  
			       }.start(); 
				}
			}
		});
		
		myUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("click");
				if(currenId == 0){
					companyCheckErrorMessage = "";
					if(companyCheck() == true){
						progressDialog = ProgressDialog.show(AddContact.this, "��ӵ�λ��", "��ӵ�λ��...", true, false);  
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
						Toast.makeText(AddContact.this, companyCheckErrorMessage, Toast.LENGTH_SHORT).show();
					}
						
				}else{
					staffCheckErrorMessage = "";
					if(staffCheck() == true){
						progressDialog = ProgressDialog.show(AddContact.this, "�����Ա��", "�����Ա��...", true, false);  
						new Thread(){  
				            @Override  
				        public void run(){     
				           try{  
				        	   updateStaffData();
				        	   System.out.println("renyuan");
				           }catch(Exception e) {  
				               Log.d("zlq", "***************************"+e.toString());  
				          }  
				         }  
				       }.start(); 
					}else{
						Toast.makeText(AddContact.this, staffCheckErrorMessage, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		myBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		myAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currenId == 0){
					if(isCompanyUpdated == true){
						clearData();
					}else{
						companyCheckErrorMessage = "";
						if(companyCheck() == true){
							iscompanyClear = true;
							progressDialog = ProgressDialog.show(AddContact.this, "��ӵ�λ��", "��ӵ�λ��...", true, false);  
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
							Toast.makeText(AddContact.this, companyCheckErrorMessage, Toast.LENGTH_SHORT).show();
						}
					}
				}else{
					System.out.println("staffCheckErrorMessage"+staffCheckErrorMessage);
					if(isUpdated == true){
						clearData();
					}else{
						staffCheckErrorMessage = "";
						if(staffCheck() == true){
							isStaffClear = true;
							progressDialog = ProgressDialog.show(AddContact.this, "���Ա����", "���Ա����...", true, false);  
							new Thread(){  
					            @Override  
					        public void run(){     
					           try{  
					        	   System.out.println("renyuan");
									updateStaffData();
					           }catch(Exception e) {  
					               Log.d("zlq", "***************************"+e.toString());  
					          }  
					         }  
					       }.start(); 
						}else{
							Toast.makeText(AddContact.this, staffCheckErrorMessage, Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});
		myClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearData();
			}
		});
	}
	
	private void updateCompanyData(){
		//{"saveNewCompInfo":{"compName": ��λ���,"cityName": ���ڳ���, "address":��ϸ��ַ, "mailCode":�ʱ�, 
		//"officeNum":����绰, "faxNum":�����, "note":��ע},"token":����}
	  	String resultStr = "";
		JSONObject resultJSON;
			
		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'saveNewCompInfo':{'compName':'"+addcompanyCompanyname.getText().toString()+
					"', 'cityName':'"+addcompanyCityname.getText().toString()+
					"', 'address':'"+addcompanyAreaname.getText().toString()+
					"', 'mailCode':'"+addcompanyPostCode.getText().toString()+
					"', 'officeNum':'"+addcompanyOfficeName.getText().toString()+
					"', 'faxNum':'"+addcompanyFoxNumber.getText().toString()+
					"', 'note':'"+addcompanyCompanyNOte.getText().toString()+"'}," +
					"'token':'"+SystemConfig.token+"'}";
				
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
	
	private void updateStaffData(){
		//{"saveNewMemberInfo":{"memberName":��Ա����,"companyId":��λ����,"department":����,"post": ְλ,
		//"cellNum": �ֻ��, "officialNum":�칫�绰, "homeNum":��ͥ�绰, "hobby":��Ȥ����, "remark":��ע},"token":����}
	  	String resultStr = "";
		JSONObject resultJSON;
			
		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'saveNewMemberInfo':{'memberName':'"+staffName.getText().toString()+
					"', 'companyId':'"+companyArrayList.get(namePosition).get("compId")+
					"', 'department':'"+staffbumen.getText().toString()+
					"', 'post':'"+staffgangwei.getText().toString()+
					"', 'cellNum':'"+staffPhone.getText().toString()+
					"', 'officialNum':'"+staffOfficePhone.getText().toString()+
					"', 'homeNum':'"+staffHomePhone.getText().toString()+
					"', 'hobby':'"+staffHobby.getText().toString()+
					"', 'remark':'"+staffNote.getText().toString()+"'}," +
					"'token':'"+SystemConfig.token+"'}";
				
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
	
	private void getContactsData(){
    	//{"getCustList":{"userName":"1001"},"token":"402881ea34bc62fa0134bc8b856d0010"}
    	String resultStr = "";
		JSONObject resultJSON;
		
		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'getCustList':{'userName':'"+SystemConfig.userName+"'}, 'token':'"+SystemConfig.token+"'}";
			
			resultStr = JsonParser.getResponse(SystemConfig.URL, msgStr);
			System.out.println(resultStr);
			if(resultStr.equals("")){
				handler.sendEmptyMessage(4);//
			}else{
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if(result.equals("ok")){
					if(!resultJSON.getString("compNumber").equals("0")){
						companyArrayList.clear();
						JSONObject companyList = resultJSON.getJSONObject("myCustCompList");
						JSONArray projectArray = companyList.names();
						System.out.println("companyList"+projectArray.length());
						for(int i=0; i<projectArray.length(); i++){
							JSONObject temp = companyList.getJSONObject(projectArray.getString(i));
							HashMap<String, String> company = new HashMap<String, String>();
							company.put("cityName",temp.getString("cityName"));
							company.put("compName", temp.getString("compName"));
							company.put("compId", temp.getString("compId"));
							companyArrayList.add(company);
						}
					}
		
					handler.sendEmptyMessage(6);
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
	  
	  private void clearData(){
		addcompanyCompanyname.setText("");
		addcompanyCityname.setText("");
		addcompanyAreaname.setText("");
		addcompanyPostCode.setText("");
		addcompanyOfficeName.setText("");
		addcompanyFoxNumber.setText("");
		addcompanyCompanyNOte.setText("");
		staffName.setText("");
		staffbumen.setText("");
		staffgangwei.setText("");
		staffPhone.setText("");
		staffOfficePhone.setText("");
		staffHomePhone.setText("");
		staffHobby.setText("");
		staffNote.setText("");
		
		
	  }
}
