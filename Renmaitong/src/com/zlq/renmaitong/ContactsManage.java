package com.zlq.renmaitong;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zlq.adapter.CompanyAdapter;
import com.zlq.adapter.SearchAdapter;
import com.zlq.adapter.StaffAdapter;
import com.zlq.json.JsonParser;
import com.zlq.util.MyDatabaseHelper;
import com.zlq.util.MyUtil;
import com.zlq.util.SystemConfig;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsManage extends Activity{

	private ImageButton informationReport, ContactsManage, SystemSetting, exitSystem;
	private ListView companyList, staffList, searchListView;
	private TextView companyHeader, staffHeader;
	private Button search;
	private EditText searchEdit;
	private LinearLayout contentLayout;
//	private TextView companyheaderNumber, staffHeaderNumber;
	
	private ProgressDialog progressDialog; 
	private String message ;
	
	public static ArrayList<HashMap<String, String>> companyArrayList = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> staffArrayList = new ArrayList<HashMap<String, String>>();
	private ArrayList<ArrayList<String>> allData = new ArrayList<ArrayList<String>>();
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(ContactsManage.this, "加载通讯录成功", Toast.LENGTH_SHORT).show();
		    	  
		    	 //删除以前的
		    	MyDatabaseHelper.deleteContacts(ContactsManage.this);
		   		//插入新的
		   		MyDatabaseHelper.insertContacts(ContactsManage.this, companyArrayList, staffArrayList);
		   		//监听
//		   		allData = MyDatabaseHelper.selectContacts(ContactsManage.this, "小");
//		   		System.out.println("模糊查询结果数据"+allData.size());
//		    	for(int i=0; i<allData.size(); i++){
//		    		for(int j=0; j<allData.get(i).size(); j++){
//		    			System.out.print(allData.get(i).get(j)+"  ");
//		    		}
//		    		System.out.println();
//		    	}   
		   		
		    	   System.out.println(companyArrayList.size()+"");
		    	   if(!companyArrayList.isEmpty()){
		    		   CompanyAdapter companyAdapter = new CompanyAdapter(ContactsManage.this, companyArrayList);
		    		   companyList.setAdapter(companyAdapter);
//		    		   companyheaderNumber.setText(companyArrayList.size()+"");
		    	   }else{
//		    		   companyheaderNumber.setText("");
		    	   }
		    	   
		    	   if(!staffArrayList.isEmpty()){
		    		   StaffAdapter staffAdapter = new StaffAdapter(ContactsManage.this, staffArrayList);
		    		   staffList.setAdapter(staffAdapter);
//		    		   staffHeaderNumber.setText(staffArrayList.size()+"");
		    	   }else{
//		    		   staffHeaderNumber.setText("");
		    	   }
		    		
		    		
		    		
		    		companyList.setOnItemClickListener(new OnItemClickListener() {
		    			@Override
		    			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		    				Intent intent = new Intent(ContactsManage.this, CompanyInformation.class);
		    				intent.putExtra("companyId", companyArrayList.get(arg2).get("compId"));
		    				intent.putExtra("cityName", companyArrayList.get(arg2).get("cityName"));
		    				intent.putExtra("compName", companyArrayList.get(arg2).get("compName"));
		    				startActivity(intent);
		    			}
		    		});
		    		staffList.setOnItemClickListener(new OnItemClickListener() {
		    			@Override
		    			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		    				Intent intent = new Intent(ContactsManage.this, StafInformation.class);
		    				intent.putExtra("memberId", staffArrayList.get(arg2).get("memberId"));
		    				intent.putExtra("compName", staffArrayList.get(arg2).get("compName"));
		    				intent.putExtra("memberName", staffArrayList.get(arg2).get("memberName"));
		    				intent.putExtra("post", staffArrayList.get(arg2).get("post"));
		    				startActivity(intent);
		    			}
		    		});
		       }else if(msg.what == 2){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(ContactsManage.this, message, Toast.LENGTH_LONG).show();
		       }else if(msg.what == 3){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(ContactsManage.this, "服务器返回值错误", Toast.LENGTH_LONG).show();
		       }else if(msg.what == 4){
		    	   progressDialog.dismiss();
		    	   Toast.makeText(ContactsManage.this, "服务器超时", Toast.LENGTH_LONG).show();
		       }
		}   
	};  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_constacts);
		
		intiView();
		toPage();
		
		if(companyArrayList.isEmpty() && staffArrayList.isEmpty() ){
			progressDialog = ProgressDialog.show(ContactsManage.this, "加载通讯录中...", "请稍候...", true, false);  
			new Thread(){  
	            @Override  
	        public void run(){     
	           try{  
		       		getContactsData();
		    		System.out.println(companyArrayList.size()+"======"+staffArrayList.size());
	           }catch(Exception e) {  
	               Log.d("zlq", "***************************"+e.toString());  
	          }  
	         }  
	       }.start();  
		}else{
		   		
		    	   System.out.println(companyArrayList.size()+"");
		    	   if(!companyArrayList.isEmpty()){
		    		   CompanyAdapter companyAdapter = new CompanyAdapter(ContactsManage.this, companyArrayList);
		    		   companyList.setAdapter(companyAdapter);
//		    		   companyheaderNumber.setText(companyArrayList.size()+"");
		    	   }else{
//		    		   companyheaderNumber.setText("");
		    	   }
		    	   
		    	   if(!staffArrayList.isEmpty()){
		    		   StaffAdapter staffAdapter = new StaffAdapter(ContactsManage.this, staffArrayList);
		    		   staffList.setAdapter(staffAdapter);
//		    		   staffHeaderNumber.setText(staffArrayList.size()+"");
		    	   }else{
//		    		   staffHeaderNumber.setText("");
		    	   }
		    		
		    		companyList.setOnItemClickListener(new OnItemClickListener() {
		    			@Override
		    			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		    				Intent intent = new Intent(ContactsManage.this, CompanyInformation.class);
		    				intent.putExtra("companyId", companyArrayList.get(arg2).get("compId"));
		    				intent.putExtra("cityName", companyArrayList.get(arg2).get("cityName"));
		    				intent.putExtra("compName", companyArrayList.get(arg2).get("compName"));
		    				startActivity(intent);
		    			}
		    		});
		    		staffList.setOnItemClickListener(new OnItemClickListener() {
		    			@Override
		    			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		    				Intent intent = new Intent(ContactsManage.this, StafInformation.class);
		    				intent.putExtra("memberId", staffArrayList.get(arg2).get("memberId"));
		    				intent.putExtra("compName", staffArrayList.get(arg2).get("compName"));
		    				intent.putExtra("memberName", staffArrayList.get(arg2).get("memberName"));
		    				intent.putExtra("post", staffArrayList.get(arg2).get("post"));
		    				startActivity(intent);
		    			}
		    		});
		}
	}
	
	@Override
	protected void onResume() {
		if(SystemConfig.isAddContacts == true){
			SystemConfig.isAddContacts = false;
			companyArrayList = new ArrayList<HashMap<String, String>>();
			staffArrayList = new ArrayList<HashMap<String, String>>();
			progressDialog = ProgressDialog.show(ContactsManage.this, "加载通讯录中...", "请稍候...", true, false);  
			new Thread(){  
	            @Override  
	        public void run(){     
	           try{  
		       		getContactsData();
		    		System.out.println(companyArrayList.size()+"======"+staffArrayList.size());
	           }catch(Exception e) {  
	               Log.d("zlq", "***************************"+e.toString());  
	          }  
	         }  
	       }.start();  
		}
	    super.onResume();
	}
	
	void intiView(){
		search = (Button) findViewById(R.id.contacts_search);
		informationReport = (ImageButton) findViewById(R.id.tabbar1);
		ContactsManage = (ImageButton) findViewById(R.id.tabbar2);
		SystemSetting = (ImageButton) findViewById(R.id.tabbar4);
		exitSystem = (ImageButton) findViewById(R.id.tabbar5);
		ContactsManage.setPressed(true);
		
		companyHeader = (TextView) findViewById(R.id.companyheader);
		staffHeader = (TextView) findViewById(R.id.staffHeader);
		
//		companyheaderNumber = (TextView) findViewById(R.id.companyheaderNumber);
//		staffHeaderNumber = (TextView) findViewById(R.id.staffHeaderNumber);
		contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
		searchEdit = (EditText) findViewById(R.id.input_search);
		searchEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().equals("")){
					contentLayout.setVisibility(View.VISIBLE);
					searchListView.setVisibility(View.GONE);
				}else{
					searchListView.setVisibility(View.VISIBLE);
					contentLayout.setVisibility(View.GONE);
					allData = MyDatabaseHelper.selectContacts(ContactsManage.this, s.toString());
			   		System.out.println("模糊查询结果数据"+allData.size());
			    	for(int i=0; i<allData.size(); i++){
			    		for(int j=0; j<allData.get(i).size(); j++){
			    			System.out.print(allData.get(i).get(j)+"  ");
			    		}
			    		System.out.println();
			    	}   
			    	SearchAdapter adapter = new SearchAdapter(ContactsManage.this, allData);
			    	searchListView.setAdapter(adapter);
			    	searchListView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							if(allData.get(position).get(0).equals("0")){
								Intent intent = new Intent(ContactsManage.this, CompanyInformation.class);
								intent.putExtra("companyId", allData.get(position).get(3));
			    				intent.putExtra("cityName", allData.get(position).get(1));
			    				intent.putExtra("compName", allData.get(position).get(2));
								startActivity(intent);
							}else{
								Intent intent = new Intent(ContactsManage.this, StafInformation.class);
			    				intent.putExtra("memberId", allData.get(position).get(4));
			    				intent.putExtra("compName", allData.get(position).get(1));
			    				intent.putExtra("memberName", allData.get(position).get(2));
			    				intent.putExtra("post", allData.get(position).get(3));
			    				startActivity(intent);
							}
						}
					});
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		companyHeader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("companyHeader");
				System.out.println(companyList.getVisibility());
				if(companyList.getVisibility() == View.VISIBLE){
					System.out.println("companyHeader VISIBLE");
					companyList.setVisibility(View.GONE);
				}else if(companyList.getVisibility() == View.GONE){
					System.out.println("companyHeader VISIBLE");
					companyList.setVisibility(View.VISIBLE);
					staffList.setVisibility(View.GONE);
				}
			}
		});
		
		staffHeader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("staffHeader");
				System.out.println(staffList.getVisibility());
				if(staffList.getVisibility() == View.VISIBLE){
					staffList.setVisibility(View.GONE);
				}else if(staffList.getVisibility() == View.GONE){
					staffList.setVisibility(View.VISIBLE);
					companyList.setVisibility(View.GONE);
				}
			}
		});
		companyList = (ListView) findViewById(R.id.my_company_list);
		companyList.setVisibility(View.GONE);
		staffList = (ListView) findViewById(R.id.my_staff_list);
		searchListView = (ListView) findViewById(R.id.searchListView);
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ContactsManage.this, AddContact.class));
			}
		});
	}
	
	void toPage(){
		informationReport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ContactsManage.this, InformationReport.class));
				finish();
			}
		});
		SystemSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ContactsManage.this, SystemSetting.class));
				finish();
			}
		});
		exitSystem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				leave();
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {// 拦截menu键事件
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 拦截返回按钮事件
			leave();
		}
		return true;
	}
	
	 /**离开程序**/
    private void leave(){
        new AlertDialog.Builder(ContactsManage.this)
        .setMessage("你确定要关闭吗？")
        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
            	ContactsManage.setPressed(true);
            }})
        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }})
        .show();
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
					if(!resultJSON.getString("memberNumber").equals("0")){
						JSONObject staffList = resultJSON.getJSONObject("myCustMemberList");
						JSONArray projectArray = staffList.names();
						for(int i=0; i<projectArray.length(); i++){
							JSONObject temp = staffList.getJSONObject(projectArray.getString(i));
							HashMap<String, String> staff = new HashMap<String, String>();
							staff.put("compName",temp.getString("compName"));
							staff.put("memberName", temp.getString("memberName"));
							staff.put("post", temp.getString("post"));
							staff.put("memberId", temp.getString("memberId"));
							staffArrayList.add(staff);
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
