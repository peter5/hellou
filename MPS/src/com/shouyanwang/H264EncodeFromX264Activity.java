package com.shouyanwang;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.vov.vitamio.R;


import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class H264EncodeFromX264Activity extends Activity implements Callback {
	
	final String TAG = H264EncodeFromX264Activity.class.getSimpleName();
	
	h264encoder encoder;
	long handle;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainaa);
        
        encoder = new h264encoder ();
        InitSurfaceView();
    }
    
    private void InitSurfaceView(){
    	SurfaceView surfaceView = (SurfaceView)this.findViewById(R.id.surfaceView);
    	SurfaceHolder surfaceHolder = surfaceView.getHolder();
    	surfaceHolder.addCallback(this);
    	surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    
    Camera.PreviewCallback previewCallBack = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Message message = Message.obtain(handler, DECODE, data);
			message.sendToTarget();
		}
	};

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			openCamera(holder);
			startPreview();
			setPrewDataGetHandler();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		encoder.destory(handle);
		isPreviewRunning = false;
	}
	
	private int previewWidth = 352;
	private int previewHeight = 288;
	private boolean isPreviewRunning = false;
	
	Camera mCamera;
	private void openCamera(SurfaceHolder holder) throws IOException{
		if(mCamera == null){
			mCamera = Camera.open();
			if(mCamera == null){
				throw new IOException ();
			}
			mCamera.setPreviewDisplay(holder);
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(previewWidth, previewHeight);
			mCamera.setParameters(parameters);
			
			handle=encoder.initEncoder(previewWidth, previewHeight);
		}
	}
	
	private void startPreview(){
		if(mCamera != null){
			mCamera.startPreview();
			isPreviewRunning = true;
		}
	}
	
	
	private void setPrewDataGetHandler(){
		if(isPreviewRunning){
			mCamera.setPreviewCallback(previewCallBack);
		}
	}
	
	
	private final int DECODE = 1;
	
	Handler handler = new Handler (){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case DECODE:
				Decode((byte[])msg.obj);
				break;
			}
		}
	};
	
	
	private byte[] out = new byte[20*1024];
	long start = 0;
	long end = 0;
	private void Decode(byte[] yuvData){
		start = System.currentTimeMillis();
		int result = encoder.encodeframe(handle, -1, yuvData, yuvData.length, out);
		end = System.currentTimeMillis();
		Log.e(TAG, "encode result:"+result+"--encode time:"+(end-start));
		if(result > 0){
			try {
				FileOutputStream file_out = new FileOutputStream ("/sdcard/x264_zhangkai.264",true);
				file_out.write(out,0,result);
				file_out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.setPrewDataGetHandler();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			isPreviewRunning = false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
    
    
    
    private void SaveH264(byte[] outBuf,int outlen){
    	try {
			FileOutputStream file_out = new FileOutputStream ("/sdcard/352x288.264");
			file_out.write(outBuf, 0, outlen);
			file_out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    private byte[] getYuvFromFile(String fileName){
    	
    	File file = new File (fileName);
    	int length = (int)file.length();
    	try {
			FileInputStream fileInput = new FileInputStream (file);
			byte[] yuvData = new byte[length];
			int read = 0;
			while(read < length){
				read += fileInput.read(yuvData, read, length-read);
			}
			fileInput.close();	
			return yuvData;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	
    	return null;
    }
}