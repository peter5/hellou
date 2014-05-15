package com.zlq.json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonParser2 implements IJsonParser{

	@Override
	public HashMap<Object, Object> xParser(String url) {
		// TODO Auto-generated method stub
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		try{
			String body = getContent(url);
			JSONObject obj = new JSONObject(body);
			JSONArray images = obj.getJSONArray("appModuleImageIds");
			String text = obj.getString("appModuleContent");
			String moduleId = obj.getString("appModuleId");
			
			map.put("moduleId", moduleId);
			map.put("text", text);
	        String[] imgs = new String[images.length()];
	        for( int i = 0 ; i<images.length() ; i++ ){
	        	imgs[i] = images.getString(i);
	        	map.put("img"+i, imgs[i]);
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
	
	public HashMap<Object, Object> xParser(JSONObject object) {
		// TODO Auto-generated method stub
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		try{
			JSONObject obj = object;
			JSONArray images = obj.getJSONArray("appModuleImageIds");
			String text = obj.getString("appModuleContent");
			String moduleId = obj.getString("appModuleId");
			
			map.put("moduleId", moduleId);
			map.put("text", text);
	        String[] imgs = new String[images.length()];
	        for( int i = 0 ; i<images.length() ; i++ ){
	        	imgs[i] = images.getString(i);
	        	map.put("img"+i, imgs[i]);
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public  ArrayList<HashMap<Object, Object>> yParser(String url) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<Object, Object>> list = new ArrayList<HashMap<Object, Object>>();
		try{
			String body = getContent(url);
			JSONObject obj = new JSONObject(body);
			JSONArray jsonArray = obj.getJSONArray("appLists");
			for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i); //ÿ����¼���ɼ���Object�������?     
                String imageId = item.getString("IMAGE_ID");     // ��ȡ�����Ӧ���?     
                String content = item.getString("CONTENT");      
                String Id = item.getString("ID");
                String xId = item.getString("Y_X_ID");
                String parentId = item.getString("PARENT_ID");
                String RelationId = item.getString("RELATION_ID");
                String zId = item.getString("Y_Z_ID");
     
                HashMap<Object, Object> map = new HashMap<Object, Object>();    
                map.put("imageId", imageId);      
                map.put("content", content); 
                map.put("Id", Id);      
                map.put("xId", xId); 
                map.put("parentId", parentId);      
                map.put("RelationId", RelationId); 
                map.put("zId", zId);
                list.add(map); 
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public HashMap<Object, Object> zParser(String url) {
		// TODO Auto-generated method stub
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		try{
			String body = getContent(url);
			JSONObject obj = new JSONObject(body);
			JSONArray images = obj.getJSONArray("appModuleImageIds");
			String text = obj.getString("appModuleContent");
			String moduleId = obj.getString("appModuleId");
			String title = obj.getString("appModuleTitle");
			
			map.put("moduleId", moduleId);
			map.put("title", title);
			map.put("text", text);
	        String[] imgs = new String[images.length()];
	        for( int i = 0 ; i<images.length() ; i++ ){
	        	imgs[i] = images.getString(i);
	        	map.put("img"+i, imgs[i]);
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
	
	private String getContent(String url){
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		try{
			HttpResponse response = client.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();
			if(entity != null){
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				String line = null;
				while((line = reader.readLine()) != null){
					sb.append(line+"\n");
				}
				reader.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return sb.toString();
	}

	@Override
	public HashMap<Object, Object> yParserX(String url) {
		// TODO Auto-generated method stub
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		try{
			String body = getContent(url);
			JSONObject obj = new JSONObject(body);
			JSONArray images = obj.getJSONArray("appYXImageIds");
			String text = obj.getString("appYXContent");
			String moduleId = obj.getString("appYXId");
			
			map.put("moduleId", moduleId);
			map.put("text", text);
	        String[] imgs = new String[images.length()];
	        for( int i = 0 ; i<images.length() ; i++ ){
	        	imgs[i] = images.getString(i);
	        	map.put("img"+i, imgs[i]);
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public HashMap<Object, Object> yParserZ(String url) {
		// TODO Auto-generated method stub
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		try{
			String body = getContent(url);
			JSONObject obj = new JSONObject(body);
			JSONArray images = obj.getJSONArray("appYZImageIds");
			String text = obj.getString("appYZContent");
			String moduleId = obj.getString("appYZId");
			String title = obj.getString("appYZTitle");
			
			map.put("moduleId", moduleId);
			map.put("title", title);
			map.put("text", text);
	        String[] imgs = new String[images.length()];
	        for( int i = 0 ; i<images.length() ; i++ ){
	        	imgs[i] = images.getString(i);
	        	map.put("img"+i, imgs[i]);
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public HashMap<String, String> ContactUs(String url) {
		// TODO Auto-generated method stub
		HashMap<String, String> map = new HashMap<String, String>();
		try{
			String body = getContent(url);
			JSONObject obj = new JSONObject(body);
			JSONArray address = obj.getJSONArray("appCompanyAddress");
			JSONArray phone = obj.getJSONArray("appCompanyTelephone");
			
			map.put("moduleId", obj.getString("appModuleId"));
			map.put("website", obj.getString("appCompanyWebsite"));
			map.put("person", obj.getString("appContactPerson"));
			map.put("fax", obj.getString("appFax"));
	        for( int i = 0 ; i<address.length() ; i++ ){
	        	map.put("address"+i, address.getString(i));
	        }
	        for(int i=0; i<phone.length(); i++){
	        	map.put("phone"+i, phone.getString(i));
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public ArrayList<String> baiduMap(String url) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		try{
			String body = getContent(url);
			JSONObject obj = new JSONObject(body);
			list.add(obj.getString("appModuleId"));
			list.add(obj.getString("appXX"));
			list.add(obj.getString("appYY"));
			list.add(obj.getString("address"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list; 
	}

	@Override
	public ArrayList<HashMap<String, String>> Recruitment(String url) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try{
			String body = getContent(url);
			JSONObject obj = new JSONObject(body);
			JSONArray jsonArray = obj.getJSONArray("appPostList");
			for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i); //ÿ����¼���ɼ���Object�������?     
                HashMap<String, String> map = new HashMap<String, String>();    
                map.put("name", item.getString("POST_NAME"));      
                map.put("desc", item.getString("POST_DESC")); 
                map.put("require", item.getString("POST_REQUIRE"));   
                map.put("id", item.getString("ID"));
                map.put("RELATION_ID", item.getString("RELATION_ID"));
                list.add(map); 
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list; 
	}

	@Override
	public HashMap<String, String> Partners(String url) {
		// TODO Auto-generated method stub
		HashMap<String, String> map = new HashMap<String, String>();
		try{
			String body = getContent(url);
			JSONObject obj = new JSONObject(body);
			JSONArray images = obj.getJSONArray("appModuleImageIds");
			
//			map.put("moduleId", obj.getString("appModuleId"));
	        for( int i = 0 ; i<images.length() ; i++ ){
	        	map.put("img"+i, images.getString(i));
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
	
	public  Map<String, String>  getADS(String url) {
		// TODO Auto-generated method stub
		Map<String, String> list = new HashMap<String, String>();
		try{
			String body = getContent(url);
			JSONObject obj = new JSONObject(body);
			list.put("AD_id", obj.getString("adsImageId"));
			list.put("AD_clicked_url", obj.getString("imageClickUrl"));
			list.put("AD_isShow", obj.getString("isShow"));
			list.put("AD_msg", obj.getString("msg"));
			list.put("AD_showTime", obj.getString("showTime"));
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list; 
	}

}
