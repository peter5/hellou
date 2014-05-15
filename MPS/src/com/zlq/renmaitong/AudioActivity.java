package com.zlq.renmaitong;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.vov.vitamio.R;
import com.zlq.util.SystemConfig;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AudioActivity extends Activity {
 public static final String TAG = AudioActivity.class.getSimpleName();
 
 private MediaRecorder mediaRecord = null;
 private Button btnStart, btnStop;
 private TextView timeTV;
 private File audioFile = null;
 private MyOnClickListener listener = null;
 
 private static final int TAG_START = 0;
 private static final int TAG_STOP = 1;
 
 private String audioName = "";
 private Timer  mTimer;
 private int timeSize = 0;
 
 final Handler handler = new Handler(){     
     @Override  
	    public void handleMessage(Message msg) {     
	        if(msg.what == 1){  
	        	timeSize++;
	        	timeTV.setText("¼��"+SystemConfig.audiosecond+"�룬�Ѿ�¼��"+timeSize+"��");
	        	if(timeSize>Integer.parseInt(SystemConfig.audiosecond)){
	        		onStopRecord();
	        		mTimer.cancel();
	        		setResult(RESULT_OK, (new Intent()).setAction("Corky!"));
	        	    finish();
	        	}
	        }else if(msg.what == 2){
	        	 onStartRecord();
	        } 
	    }   
	};  
 @Override
 protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.my_audio);
	  
	  audioName = getIntent().getExtras().getString("audioName");
	  System.out.println(">>>>>>>>>>>>>>>audioName:"+audioName);
	  
	  mediaRecord = new MediaRecorder();
	  listener = new MyOnClickListener();
	  btnStart = (Button)findViewById(R.id.start_record);
	  timeTV = (TextView) findViewById(R.id.audioTime);
	  btnStart.setTag(TAG_START);
	  btnStart.setOnClickListener(listener);
	  btnStop = (Button)findViewById(R.id.stop_record);
	  btnStop.setTag(TAG_STOP);
	  btnStop.setOnClickListener(listener);
	  
	  btnStart.setEnabled(true);
	  btnStop.setEnabled(false);
	  
		TimerTask task = new TimerTask() {
	        public void run() {
	            handler.sendEmptyMessage(2);
	       }
	   };
	    Timer timer = new Timer(true);
	    timer.schedule(task, 300);//�ӳ�1���ִ��
 }
 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {// ����menu���¼�
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {// ���ط��ذ�ť�¼�
			mTimer.cancel();
    	    finish();
		}
		return true;
	}
 
 private void onStartRecord() {
	  btnStart.setEnabled(false);
	  btnStop.setEnabled(true);
	  btnStop.requestFocus();
	  try {
		  startRecord();
	  } catch (IllegalStateException e) {
		  Log.e(TAG, e.toString());
	  } catch (IOException e) {
		  Log.e(TAG, e.toString());
	  }
	  //5��ˢ��messageList
      mTimer = new Timer(true);
      TimerTask mTimerTask;
      mTimerTask = new TimerTask() {
			@Override
			public void run() {
				System.out.println("¼��ʱ��");
				  handler.sendEmptyMessage(1);//
			}
		};
		mTimer.schedule(mTimerTask, 1000, 1000);
 }
 
 private void startRecord() throws IllegalStateException, IOException {
	  mediaRecord.setAudioSource(MediaRecorder.AudioSource.MIC);//������ƵԴ
	  mediaRecord.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//������Ƶ�����ʽ
	  mediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//������Ƶ����
	  if(audioFile == null){
		   File audioFileDir = new File(Environment.getExternalStorageDirectory().getPath()+"/myImage/");
		   if(!audioFileDir.exists()){
			   audioFileDir.mkdir();
		   }
		   audioFile = new File(audioFileDir.getAbsoluteFile()+"/"+audioName);
		   if(audioFile.exists()){
			   audioFile.delete();
		   }
		   audioFile.createNewFile();
		   System.out.println("audioFile.getAbsolutePath():  "+audioFile.getAbsolutePath());
	  }
	  mediaRecord.setOutputFile(audioFile.getAbsolutePath());
	  mediaRecord.prepare();
	  mediaRecord.start();
 }

 private void onStopRecord() {
	  btnStart.setEnabled(true);
	  btnStop.setEnabled(false);
	  btnStart.requestFocus();
	  stopRecord();
 }
 
 private void stopRecord() {
	  mediaRecord.stop();
	  mediaRecord.release();
//	  processAudioFile();
 }

// private void processAudioFile() {
//	  ContentValues cv = new ContentValues();
//	  long currentTime = System.currentTimeMillis();
//	  
//	  cv.put(MediaStore.Audio.Media.TITLE, "audio" + audioFile.getName());
//	  cv.put(MediaStore.Audio.Media.DATE_ADDED, (int)currentTime/1000);
//	  cv.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gp");
//	  cv.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());
//	  
//	  Uri uri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cv);
//	  sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
// }

 class MyOnClickListener implements OnClickListener{
	  @Override
	  public void onClick(View view) {
		   switch ((Integer)view.getTag()) {
		   case TAG_START:
			    onStartRecord();
			    break;
		   case TAG_STOP:
			    onStopRecord();
			    break;
		   default:
			   break;
		   }
	  }
 }

}