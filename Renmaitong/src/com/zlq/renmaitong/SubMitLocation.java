package com.zlq.renmaitong;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.LocationListener;
import com.zlq.json.JsonParser;
import com.zlq.util.MyUtil;
import com.zlq.util.PictuerCompress;
import com.zlq.util.SystemConfig;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SubMitLocation extends Activity {
	private TextView backView;
	private Button paizhao;
	private Button btn_submit;
	private EditText content_CX,content_KF,content_KH ,content_et_SW,content_et_XW;
	private String message;
	private String photopath;
	private ImageView iv_pic;
	File test;
	private  ProgressDialog progressDialog;
	BMapManager mBMapMan = null;
	LocationListener mLocationListener = null;//createʱע���listener��Destroyʱ��ҪRemove
	Location myLocation;
	
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
//		         upPicture();  //�ϴ���Ƭ 
		    	   progressDialog.setMessage("�ϴ��ɹ�");
		    	   progressDialog.dismiss();
		    	   Toast.makeText(SubMitLocation.this, "�ϴ��ɹ���", Toast.LENGTH_SHORT).show();
		    	   SubMitLocation.this.finish();
		    	   if (test!= null ) {
		    		   test.delete();
				   }
		       }else if (msg.what == 2) {
		    	   progressDialog.setMessage("�����ϴ�����..");
		       }else if (msg.what == 3) {
				
		       }else if (msg.what == 4) {//ʧ��
				progressDialog.dismiss();
				Toast.makeText(SubMitLocation.this, "�ϴ�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
		       }
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.copyofactivity_marketreport);
		
		initlayout();
		setListenners();
		
		mBMapMan = new BMapManager(this);
		mBMapMan.init("B2CC3DD73EAA572CAD1B4B81C84798F67C98C1C3", null);
		
        // ע�ᶨλ�¼�
        mLocationListener = new LocationListener(){
			@Override
			public void onLocationChanged(Location location) {
				if(location != null){
					String strLog = String.format("����ǰ��λ��:\r\n" +
							"����:%f\r\n" +
							"γ��:%f",
							location.getLongitude(), location.getLatitude());
					myLocation = location;
			        log(strLog+"==========================");
				}
			}
        };
		
	}

	void setListenners() {
		btn_submit.setOnClickListener(new OnClickListener() {
			private String _content_sw = "";
			private String _content_xw = "";
			@Override
			public void onClick(View v) {
				final String _content_cx = content_CX.getText().toString().trim();
				final String _content_kf = content_KF.getText().toString().trim();
				final String _content_kh = content_KH.getText().toString().trim();
				
				_content_sw = content_et_SW.getText().toString().trim();
				_content_sw = _content_sw.replaceAll("\\s+", " "); 
				_content_xw = content_et_XW.getText().toString().trim();
				_content_xw = _content_xw.replaceAll("\\s+", " "); 
				
				if (_content_cx.equals("")) {
					Toast.makeText(SubMitLocation.this, "CX����Ϊ�գ�", Toast.LENGTH_SHORT).show();
					return;
				}
				if (_content_kf.equals("")) {
					Toast.makeText(SubMitLocation.this, "KF����Ϊ�գ�",  Toast.LENGTH_SHORT).show();
					return;
				}
				if (_content_kh.equals("")) {
					Toast.makeText(SubMitLocation.this, "KH����Ϊ�գ�",  Toast.LENGTH_SHORT).show();
					return;
				}
				progressDialog = ProgressDialog.show(SubMitLocation.this, null, "�����ϴ�..",
	    				true, true);
				new Thread(){  
	                @Override  
	            public void run(){     
	               try{
	            	   log("photopath "+photopath);
	            	   if (test == null || !test.exists()) {
	            		   submit(_content_cx,_content_kf,_content_kh ,_content_sw,_content_xw);
	            		   log("test null");
					   }else {
						   upPicture(_content_cx,_content_kf,_content_kh ,_content_sw,_content_xw);
						   log("test exists");
					   }
	               }catch(Exception e) { 
	            	   e.printStackTrace();
	                   Log.d("zlq", "***************************"+e.toString());  
	              }  
	             }
	           }.start();  
			}
		});
		backView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SubMitLocation.this.finish();
			}
		});
		paizhao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String sdStatus = Environment.getExternalStorageState();
				File photoDir = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage1/");
	                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
	                    Toast.makeText(SubMitLocation.this, "SD��������", Toast.LENGTH_SHORT).show();
	                    return;
	                }
	                
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					log("photoDir.exists() is "+photoDir.exists());
					if(!photoDir.exists()){
						boolean b = photoDir.mkdirs();log("photoDir.mkdirs() is "+b);
						if (!b) {
							return;
						}
					}
				    test = new File(photoDir,"photo_"+System.currentTimeMillis()+".jpg");
				    log("test.exists() is "+test.exists());
				    if (!test.exists()) {
				    	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(test));
				    	photopath = test.getAbsolutePath();
				    	startActivityForResult(intent, 0);
					}
			}
		});
	}

	private void initlayout() {
		backView = (TextView) findViewById(R.id.market_back);
		paizhao = (Button) findViewById(R.id.market_paizhao);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		content_CX = (EditText) findViewById(R.id.market_et_cx);
		content_KF = (EditText) findViewById(R.id.market_et_kf);
		content_KH = (EditText) findViewById(R.id.market_et_kh);
		content_et_SW = (EditText) findViewById(R.id.market_et_sw);
		content_et_XW = (EditText) findViewById(R.id.market_et_xw);
		
		
		content_CX.setText("");
		content_KF.setText("");
		content_KH.setText("");
		content_et_SW.setText("");
		content_et_XW.setText("");
		
		content_CX.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
		content_KF.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
		content_CX.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
		content_et_SW.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
		content_et_XW.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
		
		iv_pic = (ImageView) findViewById(R.id.iv_picture);
		
//		content_QT.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//			}
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub 
//				int lines = content_QT.getLineCount(); 
//				// ��������������� 
//				if (lines > 3) { 
//				String str = s.toString(); 
//				int cursorStart = content_QT.getSelectionStart(); 
//				int cursorEnd = content_QT.getSelectionEnd(); 
//				if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) { 
//				str = str.substring(0, cursorStart-1) + str.substring(cursorStart); 
//				} else { 
//				str = str.substring(0, s.length()-1); 
//				} 
//				// setText�ᴥ��afterTextChanged�ĵݹ� 
//				content_QT.setText(str);	
//				// setSelection�õ���������ʹ��str.length()�����Խ�� 
//				content_QT.setSelection(content_QT.getText().length()); 
//				} 
//			}
//		});
		
		
	}

	 private TextWatcher textWatcher = new TextWatcher() {
		
		 @Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
//				int lines = content_QT.getLineCount(); 
//				// ��������������� 
//				if (lines > 3) { 
//				String str = s.toString(); 
//				int cursorStart = content_QT.getSelectionStart(); 
//				int cursorEnd = content_QT.getSelectionEnd(); 
//				if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) { 
//				str = str.substring(0, cursorStart-1) + str.substring(cursorStart); 
//				} else { 
//				str = str.substring(0, s.length()-1); 
//				} 
//				// setText�ᴥ��afterTextChanged�ĵݹ� 
//				content_QT.setText(str);	
//				// setSelection�õ���������ʹ��str.length()�����Խ�� 
//				content_QT.setSelection(content_QT.getText().length()); 
//				} 
			}
		};

	// �����ļ�
	public void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// �½��ļ����������������л���
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// �½��ļ���������������л���
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// ��������
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// ˢ�´˻���������
			outBuff.flush();
		} finally {
			// �ر���
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}
	/**
	 * backup ���ݵ� "konruns-backup" �ļ�����
	 * 
	 * @param srcFileAbspath
	 *            Դ�ļ�·��
	 */
	public void doBackup(String srcFileAbspath) {

		File file = new File(srcFileAbspath);
		File file_backup = new File(new StringBuilder(Environment
				.getExternalStorageDirectory().getAbsolutePath())
				.append(File.separator).append("konruns-backup").toString()
				+ "/" + file.getName());
		if (file.exists()) {
			try {
				copyFile(file, file_backup);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        log("requestCode is " +requestCode);
	        if(requestCode == 0){
	        	log("resultCode is " +resultCode);
	        	  if (resultCode == Activity.RESULT_OK) {
//	        		log(test.exists()+"   test.exists()");
//	        		test.exists();  
	        		// ������ʾ
//	  				BitmapFactory.Options options = new BitmapFactory.Options();
//	  				options.inJustDecodeBounds = true;
//	  				BitmapFactory.decodeFile(photopath, options);
//	  				int bili = (int) (options.outHeight / (float) 300);
//	  				if (bili <= 0) {
//	  					bili = 1;
//	  				}
//	  				options.inJustDecodeBounds = false;
//	  				options.inSampleSize = bili;
	  				
	  				DisplayMetrics dm = new DisplayMetrics();
	  				getWindowManager().getDefaultDisplay().getMetrics(dm);
	  				iv_pic.setImageBitmap(MyUtil.makeImg(dm.widthPixels-100, 300, BitmapFactory.decodeFile(photopath)));
	  				doBackup(photopath);
	        	  }
	        }
	   }
	private void exists() {
		
	}

	void upPicture(final String _content_cx,final String _content_kf,final String _content_kh,final String _content_sw,final String _content_xw){
		log("start upload picture ---");
	         if(test.exists()){
//	             progressDialog = ProgressDialog.show(InformationReport.this, "�����ϴ���Ƭ...", "���Ե�...", true, true);  
	             new Thread(){  
	       	            @Override  
	       	        public void run(){     
	       	           try{  
	       	        	   String resultStr = "";
	       	       			JSONObject resultJSON;
	       	        	   
	       		            File picture = new File(photopath);
	       		            picture = PictuerCompress.compFile(photopath);
	       		           	log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+"picture.length()"+picture.length());
	       		            resultStr = JsonParser.UpLoadRes(SystemConfig.Location_URL, "{'uploadfile':{'userName':'"+SystemConfig.userName+"'},'token':'"+SystemConfig.token+"'}", picture);
	            		        log("resultJSON.toString()"+resultStr);
	            		            
	            		        if(resultStr.equals("")){
	                    			handler.sendEmptyMessage(4);//
	                    		}else{
	                    			resultJSON = new JSONObject(resultStr);
	                    			String result = resultJSON.getString("result");
	                  				message = resultJSON.getString("message");
	                  				if(result.equals("ok")){
	                  					submit2(_content_cx,_content_kf,_content_kh,_content_sw,_content_xw);
	                  					handler.sendEmptyMessage(2);
	                  				}else if(result.equals("error")){
	                  					handler.sendEmptyMessage(4);
	                  				}
	                  			}
	       	           }catch(Exception e) {  
	       	               Log.d("zlq", "***************************"+e.toString()); 
	       	            handler.sendEmptyMessage(4);
	       	          }  
	       	         }  
	       	       }.start(); 
	         }else{
	       	  handler.sendEmptyMessage(4);
	         }
	}
	private void submit2(String _content_cx,String _content_kf,String _content_kh,String _content_sw,String _content_xw) {
		String resultStr = "";
		JSONObject resultJSON;
		
		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'submitLocation':{'userName':'"+SystemConfig.userName+
						"','forenoon':'"+_content_sw+"','afternoon':'"+_content_xw+"','CX':'"+_content_cx+"','KF':'"+_content_kf+"','KH':'"+_content_kh+"','photo_name':'"+test.getName()+"','yyy':'"+myLocation.getLatitude()+"','xxx':'"+myLocation.getLongitude()+"'},'token':'"+SystemConfig.token+"'}";
			resultStr = JsonParser.getResponse(SystemConfig.Location_URL, msgStr);
			log("resultStr is  "+resultStr);
			if(resultStr.equals("")){
				handler.sendEmptyMessage(4);//
			}else{
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if (resultJSON.has("count")) {
					String count = resultJSON.getString("count");
					MyUtil.saveJson2preference(this, "count", count);
				}
				if(result.equals("ok")){
					handler.sendEmptyMessage(1);   // ok
				}else if(result.equals("error")){
					handler.sendEmptyMessage(4);
				}
			}
		} catch (JSONException e) {
			handler.sendEmptyMessage(4);
			e.printStackTrace();
		}
   
	}
	private void submit(String _content_cx,String _content_kf,String _content_kh,String _content_sw,String _content_xw) {
		String resultStr = "";
		JSONObject resultJSON;
		
		String result = "";
		String msgStr = "";
		try {
			msgStr = "{'submitLocation':{'userName':'"+SystemConfig.userName+
						"','forenoon':'"+_content_sw+"','afternoon':'"+_content_xw+"','CX':'"+_content_cx+"','KF':'"+_content_kf+"','KH':'"+_content_kh+"','photo_name':'','yyy':'"+myLocation.getLatitude()+"','xxx':'"+myLocation.getLongitude()+"'},'token':'"+SystemConfig.token+"'}";
			resultStr = JsonParser.getResponse(SystemConfig.Location_URL, msgStr);
			log("resultStr is  "+resultStr);
			if(resultStr.equals("")){
				handler.sendEmptyMessage(4);//
			}else{
				resultJSON = new JSONObject(resultStr);
				result = resultJSON.getString("result");
				message = resultJSON.getString("message");
				if (resultJSON.has("count")) {
					String count = resultJSON.getString("count");
					MyUtil.saveJson2preference(this, "count", count);
				}
				if(result.equals("ok")){
					handler.sendEmptyMessage(1);   // ok
				}else if(result.equals("error")){
					handler.sendEmptyMessage(4);
				}
			}
		} catch (JSONException e) {
			handler.sendEmptyMessage(4);
			e.printStackTrace();
		}
   
	}
	 @Override
		protected void onDestroy() {
		   log("======================================onDestroy");
		    if (mBMapMan != null) {
		    	mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		        mBMapMan.destroy();
		        mBMapMan = null;
		    }
		    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(content_CX.getWindowToken(), 0); //ǿ�����ؼ���
		    super.onDestroy();
//		    if (imm.isActive()) {  //���������
//		    	imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//			}
		}
		@Override
		protected void onPause() {
			log("======================================onPause");
		    if (mBMapMan != null) {
		    	mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		        mBMapMan.stop();
		    }
		    super.onPause();
		}
		@Override
		protected void onResume() {
			log("======================================onResume");
		    if (mBMapMan != null) {
		    	mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		        mBMapMan.start();
		    }else{
		    	log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>MBmapMan is null");
		    }
		    super.onResume();
		}
		
		@Override
		protected void onStart() {
			log("======================================onStarty");
			super.onStart();
		}
		
		@Override
		protected void onStop() {
			log("======================================onStop");
			super.onStop();
		}
		
		@Override
		protected void onRestart() {
			log("======================================onRestart");
			super.onRestart();
		}
	void log(String msg){
		System.out.println("peter "+msg);
	}
}