package com.zlq.renmaitong;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import io.vov.vitamio.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
/**
 * Android�Դ��CameraӦ�ó��������ɺܶ๦�ܡ����ǵ��䲻������������Ҫ��ʱ��
 * ���ǿ��Զ����Լ���Camera��Android�ṩ��Camera������������ʵ���Լ���Camera��
 * ������Ӿ�������һ���Լ���Camera
 * ���ȣ���Manifest����Ҫ����Ȩ��<uses-permission android:name="android:permission.CAMERA"/>
 * ������Ҫ�������ȡ�����������������������SurfaceView��
 * ʹ��SurfaceView��ͬʱ�����ǻ���Ҫʹ�õ�SurfaceHolder��SurfaceHolder�൱��һ�������������Լ���
 * Surface�ϵı仯,ͨ�����ڲ���CallBack��ʵ�֡�
 * Ϊ�˿��Ի�ȡͼƬ��������Ҫʹ��Camera��takePicture����ͬʱ������Ҫʵ��Camera.PictureCallBack�࣬ʵ��onPictureTaken����
 * @author Administrator
 *
 */
public class Test3 extends Activity implements SurfaceHolder.Callback,Camera.PictureCallback{
	
	public static final int MAX_WIDTH = 200;
	public static final int MAX_HEIGHT = 200;
	
	private SurfaceView surfaceView;
	
	private Camera camera; //�����hardare��Camera����
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.testcamera2);
		surfaceView = (SurfaceView)this.findViewById(R.id.mSurfaceView1);
		surfaceView.setFocusable(true); 
		surfaceView.setFocusableInTouchMode(true);
		surfaceView.setClickable(true);
		surfaceView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				camera.takePicture(null, null, null, Test3.this);
				
			}
		});
		//SurfaceView�е�getHolder�������Ի�ȡ��һ��SurfaceHolderʵ��
		SurfaceHolder holder = surfaceView.getHolder();
		//Ϊ��ʵ����ƬԤ�����ܣ���Ҫ��SurfaceHolder����������ΪPUSH
		//����ͼ�������Camera�������?��ͼ�����Ƕ�����Surface��
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// ��Surface��������ʱ�򣬸÷��������ã�����������ʵ��Camera����
		//ͬʱ���Զ�Camera���ж���
		camera = Camera.open(); //��ȡCameraʵ��
	
		
		/**
		 * Camera�����к���һ���ڲ���Camera.Parameters.������Զ�Camera�����Խ��ж���
		 * ��Parameters��������ɺ���Ҫ����Camera.setParameters()��������Ӧ�����òŻ���Ч
		 * ���ڲ�ͬ���豸��Camera�������ǲ�ͬ�ģ�����������ʱ����Ҫ�����ж��豸��Ӧ�����ԣ��ټ�������
		 * �����ڵ���setEffects֮ǰ����ȵ���getSupportedColorEffects������豸��֧����ɫ���ԣ���ô�÷�����
		 * ����һ��null
		 */
		try {
			
			Camera.Parameters param = camera.getParameters();
			if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
				//���������
				param.set("orientation", "portrait");
				//��2.2���Ͽ���ʹ��
				//camera.setDisplayOrientation(90);
			}else{
				param.set("orientation", "landscape");
				//��2.2���Ͽ���ʹ��
				//camera.setDisplayOrientation(0);				
			}
			//���Ȼ�ȡϵͳ�豸֧�ֵ�������ɫ��Ч���и������ǵģ������ã���������
			List<String> colorEffects = param.getSupportedColorEffects();
			Iterator<String> colorItor = colorEffects.iterator();
			while(colorItor.hasNext()){
				String currColor = colorItor.next();
				if(currColor.equals(Camera.Parameters.EFFECT_SOLARIZE)){
					param.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
					break;
				}
			}
			//���������Ҫ�ٴε���setParameter����������Ч
			camera.setParameters(param);
			
			camera.setPreviewDisplay(holder);
			
			/**
			 * ����ʾ��Ԥ����������ʱ��ϣ������Ԥ����Size
			 * ���ǲ������Լ�ָ��һ��SIze����ָ��һ��Size��Ȼ��
			 * ��ȡϵͳ֧�ֵ�SIZE��Ȼ��ѡ��һ����ָ��SIZEС����ӽ���ָ��SIZE��һ��
			 * Camera.Size������Ǹ�SIZE��
			 * 
			 */
			int bestWidth = 0;
			int bestHeight = 0;
			
			List<Camera.Size> sizeList = param.getSupportedPreviewSizes();
			//���sizeListֻ��һ������Ҳû�б�Ҫ��ʲô�ˣ���Ϊ����һ������ѡ��
			if(sizeList.size() > 1){
				Iterator<Camera.Size> itor = sizeList.iterator();
				while(itor.hasNext()){
					Camera.Size cur = itor.next();
					if(cur.width > bestWidth && cur.height>bestHeight && cur.width <MAX_WIDTH && cur.height < MAX_HEIGHT){
						bestWidth = cur.width;
						bestHeight = cur.height;
					}
				}
				if(bestWidth != 0 && bestHeight != 0){
					param.setPreviewSize(bestWidth, bestHeight);
					//����ı���SIze�����ǻ�Ҫ����SurfaceView������Surface������ı��С������Camera��ͼ�������ܲ�
					surfaceView.setLayoutParams(new LinearLayout.LayoutParams(bestWidth, bestHeight));
				}
			}
			camera.setParameters(param);
		} catch (Exception e) {
			// �������쳣�����ͷ�Camera����
			camera.release();
		}
		
		//����Ԥ������
		camera.startPreview();
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// ��Surface����ٵ�ʱ�򣬸÷���������
		//��������Ҫ�ͷ�Camera��Դ
		camera.stopPreview();
		camera.release();
		
	}
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// data��һ��ԭʼ��JPEGͼ����ݣ�
		//���������ǿ��Դ洢ͼƬ������Ȼ���Բ���MediaStore
		//ע�Ᵽ��ͼƬ���ٴε���startPreview()�ص�Ԥ��
		Uri imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
		try {
			OutputStream os = this.getContentResolver().openOutputStream(imageUri);
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		camera.startPreview();
	}
	
	
	
}