package com.example.choisemorepictures;

import java.util.ArrayList;

import com.example.logic.ImgFileListActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @QQ:794522712
 * @作者: kir~song
 * @日期: 2014-4-8
 * @欢迎: 欢迎与我交流.^_^
 * @使用方法: 亲选择的图片文件数据会在 ImgsActivity.sendfiles方法下进行获取,
 *          只需要在该方法里跳转亲所要求的界面即可
 */
public class MainActivity extends Activity {

	ListView listView;
	ArrayList<String> listfile=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView=(ListView) findViewById(R.id.listView1);
		Bundle bundle= getIntent().getExtras();
	
		if (bundle!=null) {
			if (bundle.getStringArrayList("files")!=null) {
				listfile= bundle.getStringArrayList("files");
				listView.setVisibility(View.VISIBLE);
				ArrayAdapter<String> arryAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listfile);
				listView.setAdapter(arryAdapter);
			}
		}
		
	}

	public void chise(View v){
		Intent intent = new Intent();
		intent.setClass(this,ImgFileListActivity.class);
		startActivity(intent);
	}
}
