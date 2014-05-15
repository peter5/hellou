package com.zlq.adapter;

import io.vov.vitamio.R;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class NameAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
    private TextView name;
    private ArrayList<HashMap<String, String>> nameList = null;
     
    public NameAdapter(Context context, ArrayList<HashMap<String, String>> list){
    	
        this.mInflater = LayoutInflater.from(context);
        nameList = new ArrayList<HashMap<String, String>>();
        nameList = list;
    }
    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.my_textview, null);
        name = (TextView)convertView.findViewById(R.id.nameTV);
        
        name.setText(nameList.get(position).get("dataName").toString());
        return convertView;
    }

}