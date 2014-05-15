package com.cn.rtmp;

import io.vov.vitamio.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends Activity{

	public static String url = "";
	public static String fileName = "";
	public static String projNumber = "";
	private EditText urlET, fileNameET;
	private Button save, record;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		urlET = (EditText) findViewById(R.id.urlET);
		fileNameET = (EditText) findViewById(R.id.fileNameET);
		save = (Button) findViewById(R.id.save);
		record = (Button) findViewById(R.id.record);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				write();
				Toast.makeText(Main.this, "����ɹ�", Toast.LENGTH_LONG).show();
			}
		});
		record.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println(url + "==="+fileName);
				read();
				startRecord();
				
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		read();
	}
	
	void write(){
		SharedPreferences settings = this.getSharedPreferences("shared_file", 0);
		//��ÿɱ༭����
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("url", urlET.getText().toString());
		editor.putString("fileName", fileNameET.getText().toString());
		editor.commit();
		System.out.println("����ɹ�");
	}
	
	void read(){
		SharedPreferences settings = this.getSharedPreferences("shared_file", 0);
		url = settings.getString("url", "no name");
		fileName = settings.getString("fileName", "");
		System.out.println("��ȡ�ɹ�");
		urlET.setText(url);
		fileNameET.setText(fileName);
	}
	
	 /**�뿪����**/
    private void startRecord(){
        new AlertDialog.Builder(Main.this)
        .setMessage("��ȷ����ʼ¼�ƣ������ַ��"+url+"  �ļ���"+fileName)
        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
            }})
        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                	startActivity(new Intent(Main.this, CameraVideoActivity.class));
                }})
        .show();
    }
	
	
}
