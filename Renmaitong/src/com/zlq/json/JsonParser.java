package com.zlq.json;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonParser {

	
	public static String getResponse(String url, String json){
		System.out.println("请求"+json);
		System.out.println("请求"+url);
		String result = "";
		HttpPost request = new HttpPost(url);
		HttpResponse httpResponse;
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,5000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		ArrayList <NameValuePair> message  = new ArrayList<NameValuePair>();
		message.add(new BasicNameValuePair("msg", json));

		try {
			request.setEntity(new UrlEncodedFormEntity(message, HTTP.UTF_8));
			httpResponse = client.execute(request);
			result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		} catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}  
	
	//msg={'uploadfile':{'userName':'1001'}}
	public static String UpLoadRes(String url,String param,File bitmap ){
		
		System.out.println("param"+param);
		System.out.println("File"+bitmap.getName());
		String msgStr = "?msg="+URLEncoder.encode(param);
		url = url+msgStr;
		System.out.println("url"+url);
		String retSrc = "";
        HttpPost httpRequestHttpPost = new HttpPost(url);
		 MultipartEntity multipartEntity = new MultipartEntity(
		 HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(HTTP.UTF_8));
	     ContentBody contentBody = new FileBody(bitmap, "image/jpg");	
	     
	         try {
	 	    	multipartEntity.addPart("attach", contentBody);
	 	        httpRequestHttpPost.setEntity(multipartEntity);
	 	        HttpClient  httpClient = new DefaultHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpRequestHttpPost);
				retSrc = EntityUtils.toString(httpResponse.getEntity());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return retSrc;
			} catch (IOException e) {
				e.printStackTrace();
				return retSrc;
			}
		return retSrc;
	}
	

	
	
	public static void getTest2(){
		String strResult = null;
		try {
//			String httpUrl = "http://10.10.10.10:61002/userMessage/cJobConsultationUnread.json?data=688656&client_id=20012&view_id=268800";
			String httpUrl = "http://gzch306.vicp.cc:9072/pf/services/AndroidPhoneService.pt?msg={%22androidPhoneLogin%22:{%22passWord%22:%221234%22,%22userName%22:%221001%22,%22imsi%22:%2213813813800%22}}";
			// HttpGet连接对象
			HttpGet httpRequest = new HttpGet(httpUrl);
			// 取得HttpClient对象
			HttpClient httpclient = new DefaultHttpClient();
			// 请求HttpClient，取得HttpResponse
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			// 请求成功
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的字符串
				strResult = EntityUtils.toString(httpResponse.getEntity());
				System.out.println(strResult);
			} else {
				System.out.println("错误");
			}
		} catch (Exception e) {
		 
		}

	}
	
	public static void getTest(String urlStr, String jsonStr){
		 URL url;
		try {
			url = new URL(urlStr);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	         conn.setConnectTimeout(5000);
	         conn.setRequestMethod("POST");
	         conn.setDoOutput(true);
	         
	         String json = jsonStr;
	         OutputStream os = conn.getOutputStream();
	         os.write(json.getBytes());
	         os.flush();
	         os.close();
	         
	         if(conn.getResponseCode() == 200){
	                 InputStream is = conn.getInputStream();
	                 
	                 byte[] buf = new byte[1024];
	                 int len = 0;
	                 while((len=is.read(buf)) > 0){
	                         System.out.println(new String(buf, 0, len));
	                 }
	                 
	                 is.close();
	         }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
         
	}

	
	
	public static String getJson(String url, String json){
		HttpPost request = new HttpPost(url);
		String retSrc = "";
		try {
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(json);
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient().execute(request);
			// 得到应答的字符串，这也是一个 JSON 格式保存的数据
			retSrc = EntityUtils.toString(httpResponse.getEntity());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return retSrc;
		// 生成 JSON 对象
//		JSONObject result = new JSONObject( retSrc);
//		String token = result.get("token");
	}
	
	public static String getContent(String url){
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		try{
			HttpResponse response = client.execute(new HttpGet(url));
			System.out.println("getContentUrl"+url);
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
	
	
	public static String SendRequest(String adress_Http, String strJson) {

		  String returnLine = "";
		  try {

		   System.out.println("**************开始http通讯**************");
		   System.out.println("**************调用的接口地址为**************" + adress_Http);
		   System.out.println("**************请求发送的数据为**************" + strJson);
		   URL my_url = new URL(adress_Http);
		   HttpURLConnection connection = (HttpURLConnection) my_url.openConnection();
		   connection.setDoOutput(true);
		   connection.setDoInput(true);
		   connection.setRequestMethod("POST");
		   connection.setUseCaches(false);
		   connection.setInstanceFollowRedirects(true);
		   connection.setRequestProperty("Content-Type", "application/json");
		   connection.connect();
		   DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		   byte[] content = strJson.getBytes("utf-8");
		   out.write(content, 0, content.length);
		   out.flush();
		   out.close(); // flush and close
		   BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		   //StringBuilder builder = new StringBuilder();
		   String line = "";
		   System.out.println("Contents of post request start");
		   while ((line = reader.readLine()) != null) {
		    // line = new String(line.getBytes(), "utf-8");
		    returnLine += line;
		    System.out.println(line);
		   }

		   System.out.println("Contents of post request ends");
		   
		   reader.close();
		   connection.disconnect();
		   System.out.println("========返回的结果的为========" + returnLine);

		  } catch (Exception e) {
		   e.printStackTrace();
		  }

		  return returnLine;

		 }
}
