package com.zlq.adapter;

import java.util.ArrayList;

import com.zlq.renmaitong.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class UserNameAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
    private TextView username;
    private ImageButton usenameclear;
    public static ArrayList<ArrayList<String>> usernameList = new ArrayList<ArrayList<String>>();
     
    public UserNameAdapter(Context context, ArrayList<ArrayList<String>> list){
    	
        this.mInflater = LayoutInflater.from(context);
        usernameList = list; 
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return usernameList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return usernameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.username_spinner_item, null);
        username = (TextView)convertView.findViewById(R.id.userPassword);
        usenameclear = (ImageButton)convertView.findViewById(R.id.userPassword_clear);
        
        username.setText(usernameList.get(position).get(0)); 
        usenameclear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("É¾³ýÓÃ»§Ãû");
			}
		});
//        username.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				System.out.println("aadfasdf");
//			}
//		});
        return convertView;
    }

}
