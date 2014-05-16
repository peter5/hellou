package com.zlq.media;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/*
 * ͨ��Url�õ�BitMapͼƬ
 */
public class GetImage {

	public static Bitmap getImage(String imageUrl){
		Bitmap bitmap = null ;
	    //httpGet���Ӷ���  
	    HttpGet httpRequest = new HttpGet(imageUrl);  
	    //ȡ��HttpClient ����  
	    HttpClient httpclient = new DefaultHttpClient();  
	    try {  
	        //����httpClient ��ȡ��HttpRestponse  
	        HttpResponse httpResponse = httpclient.execute(httpRequest);  
	        if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){  
	            //ȡ��������?ȡ��HttpEntiy  
	            HttpEntity httpEntity = httpResponse.getEntity();  
	            //���һ��������? 
	            InputStream is = httpEntity.getContent();  
	            bitmap = BitmapFactory.decodeStream(is);  
	            is.close();  
	        }  
	          
	    } catch (ClientProtocolException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    }  
	    return bitmap;
	}
	
	
//	/**
//	* 放大缩小图片
//	*/
//	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
//	   int width = bitmap.getWidth();
//	   int height = bitmap.getHeight();
//	   Matrix matrix = new Matrix();
//	   float scaleWidht = ((float) w / width);//宽度缩放比例
//	   float scaleHeight = ((float) h / height);//高度缩放比例
//	   if(width!=w||height!=h)
//	   matrix.postScale(scaleWidht, scaleHeight);
//	  
//	   Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//	   return newbmp;
//	}
}
