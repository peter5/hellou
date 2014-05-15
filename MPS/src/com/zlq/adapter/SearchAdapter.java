package com.zlq.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import io.vov.vitamio.R;

public class SearchAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
    private TextView name, content;
    public static ArrayList<ArrayList<String>> companyList = new ArrayList<ArrayList<String>>();
     
    public SearchAdapter(Context context, ArrayList<ArrayList<String>> list){
    	
        this.mInflater = LayoutInflater.from(context);
        companyList = list; 
        System.out.println("search����");
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return companyList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return companyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.search_item, null);
        name = (TextView)convertView.findViewById(R.id.name);
        content = (TextView)convertView.findViewById(R.id.content);
        
        if(companyList.get(position).get(0).equals("0")){
        	name.setText(companyList.get(position).get(2));
        	content.setText("���У�"+companyList.get(position).get(1));
        }else{
        	name.setText(companyList.get(position).get(2));
        	content.setText("��λ��"+companyList.get(position).get(1)+
        			"   ְλ��"+companyList.get(position).get(3));
        }
        
        return convertView;
    }

    
    
    
    
    
    
    
    
    
    
}