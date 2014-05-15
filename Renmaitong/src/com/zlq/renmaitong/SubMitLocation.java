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
	LocationListener mLocationListener = null;//create时注册此listener，Destroy时需要Remove
	Location myLocation;
	
	final Handler handler = new Handler(){     
		@Override  
	 	public void handleMessage(Message msg) {     
		       if(msg.what == 1){
//		         upPicture();  //上传照片 
		    	   progressDialog.setMessage("上传成功");
		    	   progressDialog.dismiss();
		    	   Toast.makeText(SubMitLocation.this, "上传成功！", Toast.LENGTH_SHORT).show();
		    	   SubMitLocation.this.finish();
		    	   if (test!= null ) {
		    		   test.delete();
				   }
		       }else if (msg.what == 2) {
		    	   progressDialog.setMessage("正在上传文字..");
		       }else if (msg.what == 3) {
				
		       }else if (msg.what == 4) {//失败
				progressDialog.dismiss();
				Toast.makeText(SubMitLocation.this, "上传失败！", Toast.LENGTH_SHORT).show();
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
		
        // 注册定位事件
        mLocationListener = new LocationListener(){
			@Override
			public void onLocationChanged(Location location) {
				if(location != null){
					String strLog = String.format("您当前的位置:\r\n" +
							"经度:%f\r\n" +
							"纬度:%f",
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
					Toast.makeText(SubMitLocation.this, "CX不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (_content_kf.equals("")) {
					Toast.makeText(SubMitLocation.this, "KF不能为空！",  Toast.LENGTH_SHORT).show();
					return;
				}
				if (_content_kh.equals("")) {
					Toast.makeText(SubMitLocation.this, "KH不能为空！",  Toast.LENGTH_SHORT).show();
					return;
				}
				progressDialog = ProgressDialog.show(SubMitLocation.this, null, "正在上传..",
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
	                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
	                    Toast.makeText(SubMitLocation.this, "SD卡不存在", Toast.LENGTH_SHORT).show();
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
//				// 限制最大输入行数 
//				if (lines > 3) { 
//				String str = s.toString(); 
//				int cursorStart = content_QT.getSelectionStart(); 
//				int cursorEnd = content_QT.getSelectionEnd(); 
//				if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) { 
//				str = str.substring(0, cursorStart-1) + str.substring(cursorStart); 
//				} else { 
//				str = str.substring(0, s.length()-1); 
//				} 
//				// setText会触发afterTextChanged的递归 
//				content_QT.setText(str);	
//				// setSelection用的索引不能使用str.length()否则会越界 
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
//				// 限制最大输入行数 
//				if (lines > 3) { 
//				String str = s.toString(); 
//				int cursorStart = content_QT.getSelectionStart(); 
//				int cursorEnd = content_QT.getSelectionEnd(); 
//				if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) { 
//				str = str.substring(0, cursorStart-1) + str.substring(cursorStart); 
//				} else { 
//				str = str.substring(0, s.length()-1); 
//				} 
//				// setText会触发afterTextChanged的递归 
//				content_QT.setText(str);	
//				// setSelection用的索引不能使用str.length()否则会越界 
//				content_QT.setSelection(content_QT.getText().length()); 
//				} 
			}
		};

	// 复制文件
	public void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}
	/**
	 * backup 备份到 "konruns-backup" 文件夹下
	 * 
	 * @param srcFileAbspath
	 *            源文件路径
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
	        		// 缩略显示
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
//	             progressDialog = ProgressDialog.show(InformationReport.this, "正在上传照片...", "请稍等...", true, true);  
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
		    imm.hideSoftInputFromWindow(content_CX.getWindowToken(), 0); //强制隐藏键盘
		    super.onDestroy();
//		    if (imm.isActive()) {  //隐藏软键盘
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