package com.zlq.media;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.zlq.util.SystemConfig;


public class UploadMedia {
	
	
	/**
	 * @param ֻ������ͨ����,���ô˷���
	 * @param urlString ��Ӧ��Php ҳ��
	 * @param params ��Ҫ���͵�������� �������õķ���
	 * @return
	 */
	public String communication(String urlString,Map<String, String> params){
		
		HttpClient client = new DefaultHttpClient();
		
		
		
		client.getConnectionManager().closeIdleConnections(20, TimeUnit.SECONDS);//20��
		
		String result="";
		
		String uploadUrl = SystemConfig.URL+"/";//new BingoApp().URLIN ���Ҷ�����ϴ�URL
		//http://192.168.10.9/bingo/Server/code
		String MULTIPART_FORM_DATA = "multipart/form-data"; 
		String BOUNDARY = "---------7d4a6d158c9"; //���ݷָ���
		
		if (!urlString.equals("")) {
			uploadUrl = uploadUrl+urlString;
		
		
		try {
			URL url = new URL(uploadUrl);  
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	        conn.setDoInput(true);//��������  
	        conn.setDoOutput(true);//�������  
	        conn.setUseCaches(false);//��ʹ��Cache  
	        conn.setConnectTimeout(6000);// 6�������ӳ�ʱ
	        conn.setReadTimeout(25000);// 25���Ӷ����ݳ�ʱ
	        conn.setRequestMethod("POST");            
	        conn.setRequestProperty("Connection", "Keep-Alive");  
	        conn.setRequestProperty("Charset", "UTF-8");  
	        conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
	        
	      
	        StringBuilder sb = new StringBuilder();  
	          
	        //�ϴ��ı��������֣���ʽ��ο�����  
	        for (Map.Entry<String, String> entry : params.entrySet()) {//�������ֶ�����  
	            sb.append("--");  
	            sb.append(BOUNDARY);  
	            sb.append("\r\n");  
	            sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");  
	            sb.append(entry.getValue());  
	            sb.append("\r\n");  
	        }  

	        
	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(sb.toString().getBytes());
	        dos.writeBytes("--" + BOUNDARY + "--\r\n");
		      dos.flush();

		      InputStream is = conn.getInputStream();
		      InputStreamReader isr = new InputStreamReader(is, "utf-8");
		      BufferedReader br = new BufferedReader(isr);
		      result = br.readLine();
	       
		      
		}catch (Exception e) {
			result = "{\"ret\":\"898\"}";
		}
	}
		return result;
		
	}
	
	/**
	 * @param ֻ������ͨ����,���ô˷���
	 * @param urlString ��Ӧ��Php ҳ�� 
	 * @param params ��Ҫ���͵�������� �������õķ���
	 * @paramimage ͼƬ�ֽ���������ļ��ֽ�����
	 * @paramimg ͼƬ����
	 * @return  Json 
	 */
	public String communication01(String urlString,Map<String, Object> params,byte[] image ,String img){
		String result="";
		
		String end = "\r\n";		
		String uploadUrl = SystemConfig.URL+"/";//new BingoApp().URLIN ���Ҷ�����ϴ�URL
		String MULTIPART_FORM_DATA = "multipart/form-data"; 
		String BOUNDARY = "---------7d4a6d158c9"; //���ݷָ���
		String imguri ="";
		Random random = new Random();
		int temp = random.nextInt();
		imguri = temp+ "sdfse"+".jpg";
		if (!urlString.equals("")) {
			uploadUrl = uploadUrl+urlString;
		
		try {
			URL url = new URL(uploadUrl);  
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	        conn.setDoInput(true);//��������  
	        conn.setDoOutput(true);//�������  
	        conn.setUseCaches(false);//��ʹ��Cache  
	        conn.setConnectTimeout(6000);// 6�������ӳ�ʱ
	        conn.setReadTimeout(6000);// 6���Ӷ����ݳ�ʱ
	        conn.setRequestMethod("POST");            
	        conn.setRequestProperty("Connection", "Keep-Alive");  
	        conn.setRequestProperty("Charset", "UTF-8");  
	        conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
	        
	        StringBuilder sb = new StringBuilder();  
	          
	        //�ϴ��ı��������֣���ʽ��ο�����  
	        for (Map.Entry<String, Object> entry : params.entrySet()) {//�������ֶ�����  
	            sb.append("--");  
	            sb.append(BOUNDARY);  
	            sb.append("\r\n");  
	            sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");  
	            sb.append(entry.getValue());  
	            sb.append("\r\n");  
	        }  

            sb.append("--");  
            sb.append(BOUNDARY);  
            sb.append("\r\n");  

	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(sb.toString().getBytes());
	        
	        if (!imguri.equals("")&&!imguri.equals(null)) {
	        	 dos.writeBytes("Content-Disposition: form-data; name=\""+img+"\"; filename=\"" + imguri + "\"" + "\r\n"+"Content-Type: image/jpeg\r\n\r\n");
				 
			     dos.write(image,0,image.length);
			    
			      dos.writeBytes(end);
			     
			
	        	dos.writeBytes("--" + BOUNDARY + "--\r\n");
		      dos.flush();

		      InputStream is = conn.getInputStream();
		      InputStreamReader isr = new InputStreamReader(is, "utf-8");
		      BufferedReader br = new BufferedReader(isr);
		      result = br.readLine();
	       } 
		}catch (Exception e) {
			result = "{\"ret\":\"898\"}";
		}
	}
		return result;
		
	}
	
	
	/**
	 * @param ֻ������ͨ����,���ô˷���
	 * @param urlString ��Ӧ��Php ҳ��
	 * @param params ��Ҫ���͵�������� �������õķ���
	 * @param imageuri ͼƬ���ļ��ֻ��ϵĵ�ַ ��:sdcard/photo/123.jpg
	 * @param img ͼƬ����
	 * @return Json
	 */
	public String communication02(String urlString,Map<String, Object> params,String  imageuri ,String img){
		String result="";
		
		String end = "\r\n";		
		String uploadUrl = SystemConfig.URL+"/";//new BingoApp().URLIN ���Ҷ�����ϴ�URL
		String MULTIPART_FORM_DATA = "multipart/form-data"; 
		String BOUNDARY = "---------7d4a6d158c9"; //���ݷָ���
		String imguri ="";
		if (!imageuri.equals("")) {
			imguri = imageuri.substring(imageuri.lastIndexOf("/") + 1);//���ͼƬ���ļ�����
		}
		
		
		
		
		if (!urlString.equals("")) {
			uploadUrl = uploadUrl+urlString;
		
		
		try {
			URL url = new URL(uploadUrl);  
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	        conn.setDoInput(true);//��������  
	        conn.setDoOutput(true);//�������  
	        conn.setUseCaches(false);//��ʹ��Cache  
	        conn.setConnectTimeout(6000);// 6�������ӳ�ʱ
	        conn.setReadTimeout(6000);// 6���Ӷ����ݳ�ʱ
	        conn.setRequestMethod("POST");            
	        conn.setRequestProperty("Connection", "Keep-Alive");  
	        conn.setRequestProperty("Charset", "UTF-8");  
	        conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
	        
	        StringBuilder sb = new StringBuilder();  
	          
	        //�ϴ��ı��������֣���ʽ��ο�����  
	        for (Map.Entry<String, Object> entry : params.entrySet()) {//�������ֶ�����  
	            sb.append("--");  
	            sb.append(BOUNDARY);  
	            sb.append("\r\n");  
	            sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");  
	            sb.append(entry.getValue());  
	            sb.append("\r\n");  
	        }  

            sb.append("--");  
            sb.append(BOUNDARY);  
            sb.append("\r\n");  

	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(sb.toString().getBytes());
	        
	        if (!imageuri.equals("")&&!imageuri.equals(null)) {
	        	 dos.writeBytes("Content-Disposition: form-data; name=\""+img+"\"; filename=\"" + imguri + "\"" + "\r\n"+"Content-Type: image/jpeg\r\n\r\n");
				  FileInputStream fis = new FileInputStream(imageuri);
			      byte[] buffer = new byte[1024]; // 8k
			      int count = 0;
			      while ((count = fis.read(buffer)) != -1)
			      {
			        dos.write(buffer, 0, count);
			      }
			      dos.writeBytes(end);
			      fis.close();
			}
	        	dos.writeBytes("--" + BOUNDARY + "--\r\n");
		      dos.flush();

		      InputStream is = conn.getInputStream();
		      InputStreamReader isr = new InputStreamReader(is, "utf-8");
		      BufferedReader br = new BufferedReader(isr);
		      result = br.readLine();
	        
		}catch (Exception e) {
			result = "{\"ret\":\"898\"}";
		}
	}
		return result;
		
	}
	
	
}
