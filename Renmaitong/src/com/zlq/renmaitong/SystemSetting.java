package com.zlq.renmaitong;

import com.zlq.util.MyUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class SystemSetting extends Activity {

	private ImageButton informationReport, ContactsManage, SystemSetting, exitSystem;
	private Button bingPhone, resetPassword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_systemsettings);
		
		intiView();
		toPage();
	}
	
	void intiView(){
		informationReport = (ImageButton) findViewById(R.id.tabbar1);
		ContactsManage = (ImageButton) findViewById(R.id.tabbar2);
		SystemSetting = (ImageButton) findViewById(R.id.tabbar4);
		SystemSetting.setPressed(true);
		exitSystem = (ImageButton) findViewById(R.id.tabbar5);
		bingPhone = (Button) findViewById(R.id.btnOK);
		resetPassword = (Button) findViewById(R.id.btnCancel);
	}
	
	void toPage(){
		ContactsManage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SystemSetting.this, ContactsManage.class));
				finish();
			}
		});
		exitSystem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SystemSetting.setSelected(true);
				leave();
			}
		});
		informationReport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SystemSetting.this, InformationReport.class));
				finish();
			}
		});
		bingPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SystemSetting.this, BingPhoneNumber.class));
			}
		});
		resetPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SystemSetting.this, ResetPassword.class));
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
        new AlertDialog.Builder(SystemSetting.this)
        .setMessage("你确定要关闭吗？")
        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
            	SystemSetting.setPressed(true);
            }})
        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }})
        .show();
    }
    
    @Override
    protected void onResume() {
    	SystemSetting.setPressed(true);
    	super.onResume();
    }
}
