package com.zlq.adapter;

import io.vov.vitamio.R;
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


public class CompanyAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
    private TextView code, city, company;
    public static ArrayList<HashMap<String, String>> companyList = new ArrayList<HashMap<String, String>>();
     
    public CompanyAdapter(Context context, ArrayList<HashMap<String, String>> list){
    	
        this.mInflater = LayoutInflater.from(context);
        companyList = list; 
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
        convertView = mInflater.inflate(R.layout.my_list_item, null);
        code = (TextView)convertView.findViewById(R.id.myListCode);
        city = (TextView)convertView.findViewById(R.id.myListCity);
        company = (TextView)convertView.findViewById(R.id.myListCampany);
        
        code.setText((position+1)+""); 
        city.setText(companyList.get(position).get("cityName")); 
        company.setText(companyList.get(position).get("compName")); 
        return convertView;
    }

}