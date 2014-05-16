package com.zlq.renmaitong;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.vov.vitamio.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Test4 extends Activity {
	private static final int RESULT_CAPTURE_IMAGE = 1;// �����requestCode
	private static final int REQUEST_CODE_TAKE_VIDEO = 2;// ����������requestCode
	private static final int RESULT_CAPTURE_RECORDER_SOUND = 3;// ¼����requestCode
	private String strImgPath = "";// ��Ƭ�ļ����·��
	private String strVideoPath = "";// ��Ƶ�ļ��ľ��·��
	private String strRecorderPath = "";// ¼���ļ��ľ��·��
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mycamera4);
		
		findViewById(R.id.btn1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cameraMethod() ;
			}
		});
		
		findViewById(R.id.btn2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				videoMethod();
			}
		});
		
		findViewById(R.id.btn3).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 soundRecorderMethod();
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RESULT_CAPTURE_IMAGE://����
			if (resultCode == RESULT_OK) {
			Toast.makeText(this, strImgPath, Toast.LENGTH_SHORT).show();
			}
			break;
		case REQUEST_CODE_TAKE_VIDEO://������Ƶ
			if (resultCode == RESULT_OK) {
			Uri uriVideo = data.getData();
			Cursor cursor=this.getContentResolver().query(uriVideo, null, null, null, null);
			if (cursor.moveToNext()) {
			
			strVideoPath = cursor.getString(cursor.getColumnIndex("_data"));
			Toast.makeText(this, strVideoPath, Toast.LENGTH_SHORT).show();
			}
			}
			break;
		case RESULT_CAPTURE_RECORDER_SOUND://¼��
			if (resultCode == RESULT_OK) {
			Uri uriRecorder = data.getData();
			Cursor cursor=this.getContentResolver().query(uriRecorder, null, null, null, null);
			if (cursor.moveToNext()) {
			
			strRecorderPath = cursor.getString(cursor.getColumnIndex("_data"));
			Toast.makeText(this, strRecorderPath, Toast.LENGTH_SHORT).show();
			}
			}
			break;
		}
	}
	
	private void cameraMethod() {
		Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		strImgPath = Environment.getExternalStorageDirectory().toString() + "/CONSDCGMPIC/";//�����Ƭ���ļ���
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";//��Ƭ����
		File out = new File(strImgPath);
		if (!out.exists()) {
		out.mkdirs();
		}
		out = new File(strImgPath, fileName);
		strImgPath = strImgPath + fileName;//����Ƭ�ľ��·��
		Uri uri = Uri.fromFile(out);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(imageCaptureIntent, RESULT_CAPTURE_IMAGE);
	}
	
	private void videoMethod() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30*1000);
		
		startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);
	}
	
	private void soundRecorderMethod() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/amr");
		startActivityForResult(intent, RESULT_CAPTURE_RECORDER_SOUND);
	}
	
	private void showToast(String text, int duration) {
		Toast.makeText(Test4.this, text, duration).show();
	}
}